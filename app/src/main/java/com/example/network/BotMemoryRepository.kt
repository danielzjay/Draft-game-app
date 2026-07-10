package com.example.network

import com.example.data.BotMemoryEntry
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Syncs the local bot "opening book" (see BotMemoryEntry) with a shared collection so every
 * player's bots effectively learn from EVERY player's games, not just their own — the more
 * people play, the sharper the bots get for everyone.
 *
 * Conceptually this is "upload deadly moves to the manager/moves folder" — in practice a real,
 * structured Firestore collection is the right tool for this (atomic counters merging cleanly
 * across many concurrent players), rather than literally writing flat files to a PHP-hosted
 * folder, which has no safe way to merge concurrent writes from many devices at once. If you
 * specifically want the data mirrored to your file host too, the Firestore data here can be
 * exported to that PHP folder periodically — but Firestore has to be the live source of truth.
 *
 * Uses FieldValue.increment(), which is atomic server-side — many players syncing the same
 * (position, move) pair at once still add up correctly instead of overwriting each other, which
 * a plain read-modify-write (like the leaderboard's simple set()) would NOT get right here.
 */
object BotMemoryRepository {

    private val dbOrNull: FirebaseFirestore?
        get() = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }

    private const val COLLECTION = "bot_move_memory"

    /** Pushes local win/loss counts up as atomic increments, then clears what was sent (see caller). */
    suspend fun uploadDelta(entries: List<BotMemoryEntry>): Result<Unit> {
        val db = dbOrNull ?: return Result.failure(Exception("Firebase is not initialized."))
        if (entries.isEmpty()) return Result.success(Unit)
        return try {
            // Firestore batches cap at 500 writes; chunk defensively for large local histories.
            entries.chunked(400).forEach { chunk ->
                val batch = db.batch()
                chunk.forEach { entry ->
                    val docId = "${entry.positionHash}_${entry.moveKey}"
                    val ref = db.collection(COLLECTION).document(docId)
                    batch.set(
                        ref,
                        mapOf(
                            "positionHash" to entry.positionHash,
                            "moveKey" to entry.moveKey,
                            "wins" to FieldValue.increment(entry.wins.toLong()),
                            "losses" to FieldValue.increment(entry.losses.toLong()),
                            "totalGames" to FieldValue.increment((entry.wins + entry.losses).toLong())
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
                }
                batch.commit().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Bulk sync entry point: call this "whenever the user goes online" (e.g. right after sign-in,
     * or when entering a bot match while signed in). Downloads the most-played, most-informative
     * positions from everyone's combined games, so local bias lookups during actual play stay
     * fast (pure local Room reads — no network round-trip needed mid-move).
     */
    suspend fun downloadTopEntries(limit: Long = 500): List<BotMemoryEntry> {
        val db = dbOrNull ?: return emptyList()
        return try {
            db.collection(COLLECTION)
                .orderBy("totalGames", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limit)
                .get().await()
                .documents.mapNotNull { doc ->
                    val positionHash = doc.getString("positionHash") ?: return@mapNotNull null
                    val moveKey = doc.getString("moveKey") ?: return@mapNotNull null
                    val wins = doc.getLong("wins")?.toInt() ?: 0
                    val losses = doc.getLong("losses")?.toInt() ?: 0
                    BotMemoryEntry(positionHash, moveKey, wins, losses)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchBiasFor(positionHashes: List<String>): Map<String, BotMemoryEntry> {
        val db = dbOrNull ?: return emptyMap()
        if (positionHashes.isEmpty()) return emptyMap()
        return try {
            val result = mutableMapOf<String, BotMemoryEntry>()
            // Firestore whereIn caps at 30 values per query — chunk defensively.
            positionHashes.chunked(30).forEach { chunk ->
                val snap = db.collection(COLLECTION)
                    .whereIn("positionHash", chunk)
                    .get().await()
                snap.documents.forEach { doc ->
                    val positionHash = doc.getString("positionHash") ?: return@forEach
                    val moveKey = doc.getString("moveKey") ?: return@forEach
                    val wins = doc.getLong("wins")?.toInt() ?: 0
                    val losses = doc.getLong("losses")?.toInt() ?: 0
                    result["${positionHash}_$moveKey"] = BotMemoryEntry(positionHash, moveKey, wins, losses)
                }
            }
            result
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
