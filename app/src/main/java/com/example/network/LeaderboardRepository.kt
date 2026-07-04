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
                        LeaderboardEntry(
                            rank = index + 1,
                            name = name,
                            mmr = mmr,
                            winRate = winRate,
                            favoriteHero = favoriteHero,
                            isCurrentUser = doc.id == currentUid
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
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection(COLLECTION).document(uid).set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
