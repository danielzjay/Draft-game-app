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
 * Structured, schedulable competitions on top of OnlineMatchRepository. The organizer (whoever
 * creates the tournament) picks the format explicitly — bracket or league — nothing is hardcoded.
 * Each round's matches are flagged isCompetition=true, so OnlineMatchRepository's 2-hour forfeit
 * clock applies to them (casual 1v1 matches outside a tournament never expire).
 */
object TournamentRepository {

    private val dbOrNull: FirebaseFirestore?
        get() = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }

    private const val TOURNAMENTS = "tournaments"
    private const val FIXTURES = "tournament_fixtures"
    private const val ROUND_INTERVAL_MS = 24 * 60 * 60 * 1000L // one round per day by default

    private fun currentUid(): String? = try { FirebaseAuth.getInstance().currentUser?.uid } catch (e: Exception) { null }

    suspend fun createTournament(
        name: String, 
        format: String, 
        ruleSystem: String, 
        organizerName: String, 
        startsAt: Long,
        winnerReward: Int,
        finalLoserReward: Int,
        semiFinalLoserReward: Int
    ): Result<String> {
        val db = dbOrNull ?: return Result.failure(Exception("Firebase is not initialized."))
        val uid = currentUid() ?: return Result.failure(Exception("Sign in with Google to organize a competition."))
        return try {
            val tournamentId = db.collection(TOURNAMENTS).document().id
            val data = mapOf(
                "tournamentId" to tournamentId,
                "name" to name,
                "organizerUid" to uid,
                "organizerName" to organizerName,
                "format" to format, // organizer's explicit choice — BRACKET or LEAGUE
                "ruleSystem" to ruleSystem,
                "status" to TournamentStatus.REGISTERING,
                "registeredUids" to listOf(uid),
                "registeredNames" to mapOf(uid to organizerName),
                "startsAt" to startsAt,
                "createdAt" to System.currentTimeMillis(),
                "roundCount" to 0,
                "winnerReward" to winnerReward,
                "finalLoserReward" to finalLoserReward,
                "semiFinalLoserReward" to semiFinalLoserReward
            )
            db.collection(TOURNAMENTS).document(tournamentId).set(data).await()
            Result.success(tournamentId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(tournamentId: String, name: String): Result<Unit> {
        val db = dbOrNull ?: return Result.failure(Exception("Firebase is not initialized."))
        val uid = currentUid() ?: return Result.failure(Exception("Sign in with Google to register."))
        return try {
            val ref = db.collection(TOURNAMENTS).document(tournamentId)
            val snap = ref.get().await()
            val uids = (snap.get("registeredUids") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            if (uid in uids) return Result.success(Unit)
            val names = (snap.get("registeredNames") as? Map<*, *>)?.mapNotNull { (k, v) ->
                if (k is String && v is String) k to v else null
            }?.toMap() ?: emptyMap()
            ref.update(
                mapOf(
                    "registeredUids" to uids + uid,
                    "registeredNames" to names + (uid to name)
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeTournament(tournamentId: String): Flow<TournamentInfo?> {
        val db = dbOrNull ?: return flowOf(null)
        return callbackFlow {
            val reg = db.collection(TOURNAMENTS).document(tournamentId).addSnapshotListener { snap, _ ->
                trySend(snap?.let { docToTournament(it.id, it.data) })
            }
            awaitClose { reg.remove() }
        }
    }

    fun observeOpenTournaments(): Flow<List<TournamentInfo>> {
        val db = dbOrNull ?: return flowOf(emptyList())
        return callbackFlow {
            val reg = db.collection(TOURNAMENTS)
                .whereEqualTo("status", TournamentStatus.REGISTERING)
                .addSnapshotListener { snap, _ ->
                    val list = snap?.documents?.mapNotNull { docToTournament(it.id, it.data) }
                        ?.sortedBy { it.startsAt }
                        ?.take(30) ?: emptyList()
                    trySend(list)
                }
            awaitClose { reg.remove() }
        }
    }

    fun observeFixtures(tournamentId: String): Flow<List<TournamentFixture>> {
        val db = dbOrNull ?: return flowOf(emptyList())
        return callbackFlow {
            val reg = db.collection(FIXTURES)
                .whereEqualTo("tournamentId", tournamentId)
                .addSnapshotListener { snap, _ ->
                    val list = snap?.documents?.mapNotNull { docToFixture(it.id, it.data) }
                        ?.sortedBy { it.round } ?: emptyList()
                    trySend(list)
                }
            awaitClose { reg.remove() }
        }
    }

    /**
     * Organizer-triggered: closes registration and generates the full fixture list up front for
     * whichever format they picked. Bracket = single-elimination (byes handed out if the entry
     * count isn't a power of 2). League = full round-robin, everyone plays everyone once.
     * Each round is scheduled ROUND_INTERVAL_MS apart from the tournament's startsAt.
     */
    suspend fun generateFixturesAndStart(tournament: TournamentInfo): Result<Unit> {
        val db = dbOrNull ?: return Result.failure(Exception("Firebase is not initialized."))
        if (currentUid() != tournament.organizerUid) {
            return Result.failure(Exception("Only the organizer can start the competition."))
        }
        val players = tournament.registeredUids
        if (players.size < 2) return Result.failure(Exception("Need at least 2 registered players to start."))

        return try {
            val fixtures = if (tournament.format == TournamentFormat.LEAGUE) {
                buildLeagueFixtures(tournament, players)
            } else {
                buildBracketFixtures(tournament, players)
            }

            val batch = db.batch()
            fixtures.forEach { f ->
                val ref = db.collection(FIXTURES).document()
                batch.set(ref, fixtureToMap(f.copy(fixtureId = ref.id)))
            }
            val roundCount = fixtures.maxOfOrNull { it.round } ?: 1
            batch.update(
                db.collection(TOURNAMENTS).document(tournament.tournamentId),
                mapOf("status" to TournamentStatus.IN_PROGRESS, "roundCount" to roundCount)
            )
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildBracketFixtures(t: TournamentInfo, players: List<String>): List<TournamentFixture> {
        val shuffled = players.shuffled()
        val names = t.registeredNames
        val fixtures = mutableListOf<TournamentFixture>()
        var round1 = mutableListOf<TournamentFixture>()
        var i = 0
        while (i < shuffled.size) {
            if (i + 1 < shuffled.size) {
                round1.add(
                    TournamentFixture(
                        tournamentId = t.tournamentId, round = 1,
                        player1Uid = shuffled[i], player1Name = names[shuffled[i]] ?: "Player",
                        player2Uid = shuffled[i + 1], player2Name = names[shuffled[i + 1]] ?: "Player",
                        scheduledAt = t.startsAt, status = FixtureStatus.SCHEDULED
                    )
                )
            } else {
                // Odd one out gets a bye — automatically advances this round
                round1.add(
                    TournamentFixture(
                        tournamentId = t.tournamentId, round = 1,
                        player1Uid = shuffled[i], player1Name = names[shuffled[i]] ?: "Player",
                        player2Uid = "", player2Name = "",
                        scheduledAt = t.startsAt, status = FixtureStatus.BYE, winnerUid = shuffled[i]
                    )
                )
            }
            i += 2
        }
        fixtures.addAll(round1)
        // Only round 1 is generated up front — later rounds depend on round 1's real results, so
        // they're created by advanceBracketRound() once each round finishes (see below).
        return fixtures
    }

    private fun buildLeagueFixtures(t: TournamentInfo, players: List<String>): List<TournamentFixture> {
        val names = t.registeredNames
        val fixtures = mutableListOf<TournamentFixture>()
        var round = 1
        for (i in players.indices) {
            for (j in i + 1 until players.size) {
                fixtures.add(
                    TournamentFixture(
                        tournamentId = t.tournamentId, round = round,
                        player1Uid = players[i], player1Name = names[players[i]] ?: "Player",
                        player2Uid = players[j], player2Name = names[players[j]] ?: "Player",
                        scheduledAt = t.startsAt + (round - 1) * ROUND_INTERVAL_MS,
                        status = FixtureStatus.SCHEDULED
                    )
                )
                round++ // simple spread: one fixture per "round slot" so nobody double-books a day
            }
        }
        return fixtures
    }

    /**
     * Call once a bracket round's fixtures are all completed to generate the next round from the
     * winners. Not needed for League, since all its fixtures are generated up front.
     */
    suspend fun advanceBracketRound(tournamentId: String, completedRound: Int): Result<Unit> {
        val db = dbOrNull ?: return Result.failure(Exception("Firebase is not initialized."))
        return try {
            val roundFixtures = db.collection(FIXTURES)
                .whereEqualTo("tournamentId", tournamentId)
                .whereEqualTo("round", completedRound)
                .get().await().documents.mapNotNull { docToFixture(it.id, it.data) }

            if (roundFixtures.any { it.status != FixtureStatus.COMPLETED && it.status != FixtureStatus.BYE }) {
                return Result.failure(Exception("Round $completedRound isn't finished yet."))
            }
            val winners = roundFixtures.mapNotNull { it.winnerUid }
            if (winners.size <= 1) {
                // Tournament is over
                db.collection(TOURNAMENTS).document(tournamentId).update("status", TournamentStatus.COMPLETED).await()
                return Result.success(Unit)
            }

            val tSnap = db.collection(TOURNAMENTS).document(tournamentId).get().await()
            val t = docToTournament(tournamentId, tSnap.data) ?: return Result.failure(Exception("Tournament not found."))
            val names = t.registeredNames
            val nextRound = completedRound + 1
            val shuffled = winners.shuffled()
            val batch = db.batch()
            var i = 0
            while (i < shuffled.size) {
                val ref = db.collection(FIXTURES).document()
                val fixture = if (i + 1 < shuffled.size) {
                    TournamentFixture(
                        fixtureId = ref.id, tournamentId = tournamentId, round = nextRound,
                        player1Uid = shuffled[i], player1Name = names[shuffled[i]] ?: "Player",
                        player2Uid = shuffled[i + 1], player2Name = names[shuffled[i + 1]] ?: "Player",
                        scheduledAt = t.startsAt + (nextRound - 1) * ROUND_INTERVAL_MS,
                        status = FixtureStatus.SCHEDULED
                    )
                } else {
                    TournamentFixture(
                        fixtureId = ref.id, tournamentId = tournamentId, round = nextRound,
                        player1Uid = shuffled[i], player1Name = names[shuffled[i]] ?: "Player",
                        player2Uid = "", player2Name = "",
                        scheduledAt = t.startsAt + (nextRound - 1) * ROUND_INTERVAL_MS,
                        status = FixtureStatus.BYE, winnerUid = shuffled[i]
                    )
                }
                batch.set(ref, fixtureToMap(fixture))
                i += 2
            }
            batch.update(db.collection(TOURNAMENTS).document(tournamentId), "roundCount", nextRound)
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun startFixtureMatch(fixture: TournamentFixture, ruleSystem: String): Result<String> {
        val db = dbOrNull ?: return Result.failure(Exception("Firebase is not initialized."))
        return try {
            val matchId = db.collection("online_matches").document().id
            val startingBoard = buildStartingBoardForFixture(ruleSystem)
            val match = mapOf(
                "matchId" to matchId,
                "player1Uid" to fixture.player1Uid, "player1Name" to fixture.player1Name,
                "player2Uid" to fixture.player2Uid, "player2Name" to fixture.player2Name,
                "ruleSystem" to ruleSystem,
                "board" to startingBoard,
                "turnUid" to fixture.player1Uid,
                "status" to MatchStatus.ACTIVE,
                "winnerUid" to null,
                "isCompetition" to true, // 2-hour forfeit clock applies
                "tournamentId" to fixture.tournamentId,
                "fixtureId" to fixture.fixtureId,
                "lastMoveAt" to System.currentTimeMillis(),
                "createdAt" to System.currentTimeMillis()
            )
            db.collection("online_matches").document(matchId).set(match).await()
            db.collection(FIXTURES).document(fixture.fixtureId).update(
                mapOf("matchId" to matchId, "status" to FixtureStatus.IN_PROGRESS)
            ).await()
            Result.success(matchId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeFixture(fixtureId: String, winnerUid: String): Result<Unit> {
        val db = dbOrNull ?: return Result.failure(Exception("Firebase is not initialized."))
        return try {
            db.collection(FIXTURES).document(fixtureId).update(
                mapOf("status" to FixtureStatus.COMPLETED, "winnerUid" to winnerUid)
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildStartingBoardForFixture(ruleSystem: String): List<Map<String, Any?>> {
        val boardSize = if (ruleSystem == "WORLD_DRAUGHTS_FEDERATION") 10 else 8
        val backRows = if (boardSize == 10) 4 else 3
        val pieces = mutableListOf<Map<String, Any?>>()
        var idCounter = 0
        for (row in 0 until backRows) {
            for (col in 0 until boardSize) {
                if ((row + col) % 2 == 1) {
                    pieces.add(mapOf("id" to "p2_${idCounter++}", "row" to row, "col" to col, "isRed" to false, "isKing" to false))
                }
            }
        }
        idCounter = 0
        for (row in (boardSize - backRows) until boardSize) {
            for (col in 0 until boardSize) {
                if ((row + col) % 2 == 1) {
                    pieces.add(mapOf("id" to "p1_${idCounter++}", "row" to row, "col" to col, "isRed" to true, "isKing" to false))
                }
            }
        }
        return pieces
    }

    private fun fixtureToMap(f: TournamentFixture) = mapOf(
        "fixtureId" to f.fixtureId, "tournamentId" to f.tournamentId, "round" to f.round,
        "player1Uid" to f.player1Uid, "player1Name" to f.player1Name,
        "player2Uid" to f.player2Uid, "player2Name" to f.player2Name,
        "matchId" to f.matchId, "scheduledAt" to f.scheduledAt, "status" to f.status,
        "winnerUid" to f.winnerUid
    )

    @Suppress("UNCHECKED_CAST")
    private fun docToTournament(id: String, data: Map<String, Any?>?): TournamentInfo? {
        if (data == null) return null
        val names = (data["registeredNames"] as? Map<String, Any?>)?.mapNotNull { (k, v) ->
            if (v is String) k to v else null
        }?.toMap() ?: emptyMap()
        return TournamentInfo(
            tournamentId = id,
            name = data["name"] as? String ?: "",
            organizerUid = data["organizerUid"] as? String ?: "",
            organizerName = data["organizerName"] as? String ?: "",
            format = data["format"] as? String ?: TournamentFormat.BRACKET,
            ruleSystem = data["ruleSystem"] as? String ?: "AMERICAN_CHECKER_FEDERATION",
            status = data["status"] as? String ?: TournamentStatus.REGISTERING,
            registeredUids = (data["registeredUids"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            registeredNames = names,
            startsAt = (data["startsAt"] as? Long) ?: 0L,
            createdAt = (data["createdAt"] as? Long) ?: 0L,
            roundCount = (data["roundCount"] as? Long)?.toInt() ?: 0,
            winnerReward = (data["winnerReward"] as? Long)?.toInt() ?: 100,
            finalLoserReward = (data["finalLoserReward"] as? Long)?.toInt() ?: 50,
            semiFinalLoserReward = (data["semiFinalLoserReward"] as? Long)?.toInt() ?: 25
        )
    }

    private fun docToFixture(id: String, data: Map<String, Any?>?): TournamentFixture? {
        if (data == null) return null
        return TournamentFixture(
            fixtureId = id,
            tournamentId = data["tournamentId"] as? String ?: "",
            round = (data["round"] as? Long)?.toInt() ?: 0,
            player1Uid = data["player1Uid"] as? String ?: "",
            player1Name = data["player1Name"] as? String ?: "",
            player2Uid = data["player2Uid"] as? String ?: "",
            player2Name = data["player2Name"] as? String ?: "",
            matchId = data["matchId"] as? String,
            scheduledAt = (data["scheduledAt"] as? Long) ?: 0L,
            status = data["status"] as? String ?: FixtureStatus.SCHEDULED,
            winnerUid = data["winnerUid"] as? String
        )
    }
}
