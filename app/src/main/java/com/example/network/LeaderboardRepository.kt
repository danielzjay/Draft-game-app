package com.example.network

import com.example.data.LeaderboardEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * A real, shared online leaderboard using Cloud Firestore — every player who signs in with
 * Google sees the same live-updating list, instead of the old hardcoded fake names that were
 * seeded once into each player's own local database.
 *
 * REQUIRED SETUP (one-time, in the Firebase Console):
 *  1. Build → Firestore Database → Create database (production mode is fine).
 *  2. Rules tab → paste the rules from /server/firestore.rules in this project, then Publish.
 *     Those rules make sure a player can only ever write to their OWN leaderboard entry (keyed
 *     by their Firebase Auth uid), so nobody can edit someone else's score directly in Firestore.
 *     (They could still submit an inflated score for THEMSELVES from a modified client — no
 *     client-only leaderboard can fully prevent that. See the note in the ViewModel.)
 */
object LeaderboardRepository {

    private val dbOrNull: FirebaseFirestore?
        get() = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }

    private const val COLLECTION = "leaderboard"

    /** Live top-N entries, ordered by MMR descending. Updates in real time as anyone plays. */
    fun observeTopEntries(limit: Long = 50): Flow<List<LeaderboardEntry>> {
        val firestore = dbOrNull ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        return callbackFlow {
            val registration = firestore.collection(COLLECTION)
                .orderBy("mmr", Query.Direction.DESCENDING)
                .limit(limit)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    val currentUid = try { FirebaseAuth.getInstance().currentUser?.uid } catch (e: Exception) { null }
                    val entries = snapshot.documents.mapIndexedNotNull { index, doc ->
                        val name = doc.getString("name") ?: return@mapIndexedNotNull null
                        val mmr = doc.getLong("mmr")?.toInt() ?: return@mapIndexedNotNull null
                        val winRate = doc.getDouble("winRate") ?: 0.0
                        val favoriteHero = doc.getString("favoriteHero") ?: "knight"
                        val isBot = doc.getBoolean("isBot") ?: false
                        LeaderboardEntry(
                            rank = index + 1,
                            name = name,
                            mmr = mmr,
                            winRate = winRate,
                            favoriteHero = favoriteHero,
                            isCurrentUser = doc.id == currentUid,
                            isBot = isBot
                        )
                    }
                    trySend(entries)
                }
            awaitClose { registration.remove() }
        }
    }

    /** Pushes the signed-in player's latest stats up so everyone else's leaderboard updates too. */
    suspend fun submitScore(name: String, mmr: Int, winRate: Double, favoriteHero: String): Result<Unit> {
        val firestore = dbOrNull ?: return Result.failure(Exception("Firebase is not initialized."))
        val uid = try { FirebaseAuth.getInstance().currentUser?.uid } catch (e: Exception) { null }
            ?: return Result.failure(Exception("Sign in with Google to appear on the global leaderboard."))
        return try {
            val data = mapOf(
                "name" to name,
                "mmr" to mmr,
                "winRate" to winRate,
                "favoriteHero" to favoriteHero,
                "isBot" to false,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection(COLLECTION).document(uid).set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Records a result for a BOT persona (see GameViewModel.BotPersona) on the same shared
     * leaderboard, clearly tagged isBot=true so the UI can badge it as CPU — real players and
     * bots are never presented as indistinguishable. Since bots have no real Firebase Auth
     * account of their own, any signed-in player can nudge a bot's score after playing it, but
     * only by a small fixed amount per game (enforced both here and in firestore.rules) so no
     * single client can arbitrarily set a bot's rank. This is a reasonable bound for what's a
     * cosmetic/flavor leaderboard entry, not a real competitive record — it is NOT the same
     * level of integrity as a real player's own Firebase-Auth-owned entry.
     */
    suspend fun reportBotMatchResult(botName: String, baseMmr: Int, botWon: Boolean): Result<Unit> {
        val firestore = dbOrNull ?: return Result.failure(Exception("Firebase is not initialized."))
        if (try { FirebaseAuth.getInstance().currentUser?.uid } catch (e: Exception) { null } == null) {
            return Result.failure(Exception("Sign in with Google for bot results to reflect on the shared leaderboard."))
        }
        return try {
            val docId = "bot_" + botName.lowercase().replace(Regex("[^a-z0-9]+"), "_")
            val docRef = firestore.collection(COLLECTION).document(docId)
            val snapshot = docRef.get().await()
            val currentMmr = if (snapshot.exists()) (snapshot.getLong("mmr")?.toInt() ?: baseMmr) else baseMmr
            val delta = if (botWon) 12 else -12
            val newMmr = (currentMmr + delta).coerceIn(0, 5000)
            val data = mapOf(
                "name" to botName,
                "mmr" to newMmr,
                "winRate" to (snapshot.getDouble("winRate") ?: 50.0),
                "favoriteHero" to (snapshot.getString("favoriteHero") ?: "knight"),
                "isBot" to true,
                "updatedAt" to System.currentTimeMillis()
            )
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
