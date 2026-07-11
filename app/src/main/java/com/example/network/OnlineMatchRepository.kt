package com.example.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

/**
 * Real online 1v1 matchmaking and live move sync via Cloud Firestore.
 *
 * HOW PAIRING WORKS (and its real limitation):
 * Firestore transactions can't run an arbitrary query internally, so exact-fair pairing across
 * many simultaneous players would normally need a Cloud Function. This client-only version uses
 * a simpler deterministic rule instead: whoever's uid sorts alphabetically FIRST is the one who
 * performs the actual "claim and create the match" write; the other side just watches its own
 * queue entry for a matchId to appear. That avoids two clients both creating duplicate matches
 * for the same pairing, but it's still possible (if rare) for a third player to grab one side of
 * a pairing in the small window between "found a candidate" and "wrote the match" — if that
 * happens the loser of that race just keeps searching. Fine for moderate traffic; a real
 * high-traffic matchmaker would want a Cloud Function doing this server-side instead.
 *
 * ANTI-CHEAT NOTE (same caveat as the payment/leaderboard work): moves are trusted from
 * whichever client just played and pushed the resulting board. There is no server-side rules
 * validation yet — a modified client could push an illegal board. Worth knowing, not fixed here.
 */
object OnlineMatchRepository {

    private val dbOrNull: FirebaseFirestore?
        get() = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }

    private const val QUEUE = "matchmaking_queue"
    private const val MATCHES = "online_matches"

    private fun currentUid(): String? = try { FirebaseAuth.getInstance().currentUser?.uid } catch (e: Exception) { null }

    suspend fun joinQueue(name: String, mmr: Int, ruleSystem: String): Result<Unit> {
        val db = dbOrNull ?: return Result.failure(Exception("Firebase is not initialized."))
        val uid = currentUid() ?: return Result.failure(Exception("Sign in with Google to find an online match."))
        return try {
            val entry = mapOf(
                "uid" to uid, "name" to name, "mmr" to mmr, "ruleSystem" to ruleSystem,
                "joinedAt" to System.currentTimeMillis(), "matchedMatchId" to null
            )
            db.collection(QUEUE).document(uid).set(entry).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun leaveQueue() {
        val db = dbOrNull ?: return
        val uid = currentUid() ?: return
        try { db.collection(QUEUE).document(uid).delete().await() } catch (_: Exception) {}
    }

    /** Watches your own queue entry; emits a matchId once someone (possibly you) pairs you up. */
    fun observeOwnQueueEntry(): Flow<String?> {
        val db = dbOrNull ?: return flowOf(null)
        val uid = currentUid() ?: return flowOf(null)
        return callbackFlow {
            val reg = db.collection(QUEUE).document(uid).addSnapshotListener { snap, _ ->
                trySend(snap?.getString("matchedMatchId"))
            }
            awaitClose { reg.remove() }
        }
    }

    /**
     * Call repeatedly (e.g. every 3s) while searching. Looks for another waiting player with the
     * same rule system; if found AND our uid sorts first, claims them and creates the match.
     * Returns the matchId if a match was created THIS call, else null (keep polling).
     */
    /** Live list of everyone currently waiting for a match under this rule system, for a manual "Challenge" list — a fallback for when automatic pairing hasn't found anyone yet. */
    fun observeWaitingPlayers(ruleSystem: String): Flow<List<QueueEntry>> {
        val db = dbOrNull ?: return flowOf(emptyList())
        val selfUid = currentUid()
        return callbackFlow {
            val reg = db.collection(QUEUE)
                .whereEqualTo("ruleSystem", ruleSystem)
                .orderBy("joinedAt", Query.Direction.ASCENDING)
                .limit(30)
                .addSnapshotListener { snap, _ ->
                    val list = snap?.documents
                        ?.filter { it.id != selfUid && it.getString("matchedMatchId") == null }
                        ?.mapNotNull { doc ->
                            QueueEntry(
                                uid = doc.id,
                                name = doc.getString("name") ?: "Player",
                                mmr = doc.getLong("mmr")?.toInt() ?: 1000,
                                ruleSystem = doc.getString("ruleSystem") ?: ruleSystem,
                                joinedAt = doc.getLong("joinedAt") ?: 0L
                            )
                        } ?: emptyList()
                    trySend(list)
                }
            awaitClose { reg.remove() }
        }
    }

    /** Live list of all waiting players across all rules, for global presence notifications. */
    fun observeAllWaitingPlayers(): Flow<List<QueueEntry>> {
        val db = dbOrNull ?: return flowOf(emptyList())
        val selfUid = currentUid()
        return callbackFlow {
            val reg = db.collection(QUEUE)
                .addSnapshotListener { snap, _ ->
                    val list = snap?.documents
                        ?.filter { it.id != selfUid && it.getString("matchedMatchId") == null }
                        ?.mapNotNull { doc ->
                            QueueEntry(
                                uid = doc.id,
                                name = doc.getString("name") ?: "Player",
                                mmr = doc.getLong("mmr")?.toInt() ?: 1000,
                                ruleSystem = doc.getString("ruleSystem") ?: "AMERICAN_CHECKER_FEDERATION",
                                joinedAt = doc.getLong("joinedAt") ?: 0L
                            )
                        } ?: emptyList()
                    trySend(list)
                }
            awaitClose { reg.remove() }
        }
    }

    /** Directly pairs with a SPECIFIC waiting player picked from the list, instead of waiting on automatic matching. */
    suspend fun challengePlayer(opponent: QueueEntry, ruleSystem: String): String? {
        val db = dbOrNull ?: return null
        val selfUid = currentUid() ?: return null
        return try {
            val selfSnap = db.collection(QUEUE).document(selfUid).get().await()
            if (!selfSnap.exists() || selfSnap.getString("matchedMatchId") != null) return null
            val opponentSnap = db.collection(QUEUE).document(opponent.uid).get().await()
            if (!opponentSnap.exists() || opponentSnap.getString("matchedMatchId") != null) return null

            val matchId = db.collection(MATCHES).document().id
            val selfName = selfSnap.getString("name") ?: "Player"
            val startingBoard = buildStartingBoard(ruleSystem)
            val match = mapOf(
                "matchId" to matchId,
                "player1Uid" to selfUid, "player1Name" to selfName,
                "player2Uid" to opponent.uid, "player2Name" to opponent.name,
                "ruleSystem" to ruleSystem,
                "board" to startingBoard.map { pieceToMap(it) },
                "turnUid" to selfUid,
                "status" to MatchStatus.ACTIVE,
                "winnerUid" to null,
                "isCompetition" to false,
                "tournamentId" to null,
                "fixtureId" to null,
                "lastMoveAt" to System.currentTimeMillis(),
                "createdAt" to System.currentTimeMillis()
            )
            db.collection(MATCHES).document(matchId).set(match).await()
            db.collection(QUEUE).document(selfUid).update("matchedMatchId", matchId).await()
            db.collection(QUEUE).document(opponent.uid).update("matchedMatchId", matchId).await()
            matchId
        } catch (e: Exception) {
            null
        }
    }

    suspend fun attemptPairing(ruleSystem: String): String? {
        val db = dbOrNull ?: return null
        val selfUid = currentUid() ?: return null
        return try {
            val candidates = db.collection(QUEUE)
                .whereEqualTo("ruleSystem", ruleSystem)
                .orderBy("joinedAt", Query.Direction.ASCENDING)
                .limit(10)
                .get().await()
                .documents
                .filter { it.id != selfUid && it.getString("matchedMatchId") == null }

            val opponent = candidates.firstOrNull() ?: return null
            if (selfUid > opponent.id) return null // only the alphabetically-first uid claims

            val selfSnap = db.collection(QUEUE).document(selfUid).get().await()
            if (!selfSnap.exists() || selfSnap.getString("matchedMatchId") != null) return null

            val matchId = db.collection(MATCHES).document().id
            val selfName = selfSnap.getString("name") ?: "Player"
            val oppName = opponent.getString("name") ?: "Player"

            val startingBoard = buildStartingBoard(ruleSystem)
            val match = mapOf(
                "matchId" to matchId,
                "player1Uid" to selfUid, "player1Name" to selfName,
                "player2Uid" to opponent.id, "player2Name" to oppName,
                "ruleSystem" to ruleSystem,
                "board" to startingBoard.map { pieceToMap(it) },
                "turnUid" to selfUid, // red (player1) always starts, matching offline convention
                "status" to MatchStatus.ACTIVE,
                "winnerUid" to null,
                "isCompetition" to false,
                "tournamentId" to null,
                "fixtureId" to null,
                "lastMoveAt" to System.currentTimeMillis(),
                "createdAt" to System.currentTimeMillis()
            )

            db.collection(MATCHES).document(matchId).set(match).await()
            db.collection(QUEUE).document(selfUid).update("matchedMatchId", matchId).await()
            db.collection(QUEUE).document(opponent.id).update("matchedMatchId", matchId).await()
            matchId
        } catch (e: Exception) {
            null
        }
    }

    fun observeMatch(matchId: String): Flow<OnlineMatch?> {
        val db = dbOrNull ?: return flowOf(null)
        return callbackFlow {
            val reg = db.collection(MATCHES).document(matchId).addSnapshotListener { snap, _ ->
                trySend(snap?.let { docToMatch(it.id, it.data) })
            }
            awaitClose { reg.remove() }
        }
    }

    suspend fun pushMove(matchId: String, newBoard: List<OnlineBoardPiece>, nextTurnUid: String): Result<Unit> {
        val db = dbOrNull ?: return Result.failure(Exception("Firebase is not initialized."))
        return try {
            db.collection(MATCHES).document(matchId).update(
                mapOf(
                    "board" to newBoard.map { pieceToMap(it) },
                    "turnUid" to nextTurnUid,
                    "lastMoveAt" to System.currentTimeMillis()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun finishMatch(matchId: String, winnerUid: String, forfeited: Boolean = false): Result<Unit> {
        val db = dbOrNull ?: return Result.failure(Exception("Firebase is not initialized."))
        return try {
            db.collection(MATCHES).document(matchId).update(
                mapOf(
                    "status" to if (forfeited) MatchStatus.FORFEITED else MatchStatus.FINISHED,
                    "winnerUid" to winnerUid
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Competitions only: if the player on the clock hasn't moved within the forfeit window,
     * whoever's turn it WASN'T wins by forfeit. Casual matches never expire — this simply does
     * nothing for them. Call opportunistically whenever a match is opened/observed; there's no
     * server-side cron here, so the timeout is only actually applied once somebody checks.
     */
    suspend fun checkAndApplyTimeout(match: OnlineMatch): OnlineMatch {
        if (!match.isCompetition || match.status != MatchStatus.ACTIVE) return match
        val elapsed = System.currentTimeMillis() - match.lastMoveAt
        if (elapsed < OnlineMatch.FORFEIT_WINDOW_MS) return match
        val winner = if (match.turnUid == match.player1Uid) match.player2Uid else match.player1Uid
        finishMatch(match.matchId, winner, forfeited = true)
        return match.copy(status = MatchStatus.FORFEITED, winnerUid = winner)
    }

    private fun pieceToMap(p: OnlineBoardPiece) = mapOf(
        "id" to p.id, "row" to p.row, "col" to p.col, "isRed" to p.isRed, "isKing" to p.isKing
    )

    @Suppress("UNCHECKED_CAST")
    private fun docToMatch(id: String, data: Map<String, Any?>?): OnlineMatch? {
        if (data == null) return null
        val boardRaw = data["board"] as? List<Map<String, Any?>> ?: emptyList()
        val board = boardRaw.map {
            OnlineBoardPiece(
                id = it["id"] as? String ?: "",
                row = (it["row"] as? Long)?.toInt() ?: 0,
                col = (it["col"] as? Long)?.toInt() ?: 0,
                isRed = it["isRed"] as? Boolean ?: true,
                isKing = it["isKing"] as? Boolean ?: false
            )
        }
        return OnlineMatch(
            matchId = id,
            player1Uid = data["player1Uid"] as? String ?: "",
            player1Name = data["player1Name"] as? String ?: "",
            player2Uid = data["player2Uid"] as? String ?: "",
            player2Name = data["player2Name"] as? String ?: "",
            ruleSystem = data["ruleSystem"] as? String ?: "AMERICAN_CHECKER_FEDERATION",
            board = board,
            turnUid = data["turnUid"] as? String ?: "",
            status = data["status"] as? String ?: MatchStatus.ACTIVE,
            winnerUid = data["winnerUid"] as? String,
            isCompetition = data["isCompetition"] as? Boolean ?: false,
            tournamentId = data["tournamentId"] as? String,
            fixtureId = data["fixtureId"] as? String,
            lastMoveAt = (data["lastMoveAt"] as? Long) ?: 0L,
            createdAt = (data["createdAt"] as? Long) ?: 0L
        )
    }

    /** Standard starting layout, sized/populated per rule system (8x8/12 men or 10x10/20 men). */
    private fun buildStartingBoard(ruleSystem: String): List<OnlineBoardPiece> {
        val boardSize = if (ruleSystem == "WORLD_DRAUGHTS_FEDERATION") 10 else 8
        val backRows = if (boardSize == 10) 4 else 3
        val pieces = mutableListOf<OnlineBoardPiece>()
        var idCounter = 0
        for (row in 0 until backRows) {
            for (col in 0 until boardSize) {
                if ((row + col) % 2 == 1) {
                    pieces.add(OnlineBoardPiece(id = "p2_${idCounter++}", row = row, col = col, isRed = false))
                }
            }
        }
        idCounter = 0
        for (row in (boardSize - backRows) until boardSize) {
            for (col in 0 until boardSize) {
                if ((row + col) % 2 == 1) {
                    pieces.add(OnlineBoardPiece(id = "p1_${idCounter++}", row = row, col = col, isRed = true))
                }
            }
        }
        return pieces
    }
}
