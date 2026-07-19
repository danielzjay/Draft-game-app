package com.example.data

import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

class GameRepository(private val appDao: AppDao) {
    val allHeroes: Flow<List<Hero>> = appDao.getAllHeroes()
    val playerState: Flow<PlayerState?> = appDao.getPlayerState()
    val ledger: Flow<List<BlockchainBlock>> = appDao.getLedger()
    val leaderboard: Flow<List<LeaderboardEntry>> = appDao.getLeaderboard()

    suspend fun updateHero(hero: Hero) {
        appDao.updateHero(hero)
    }

    suspend fun insertLeaderboard(entries: List<LeaderboardEntry>) {
        appDao.insertLeaderboard(entries)
    }

    suspend fun getBotMemoryBias(positionHash: String, moveKey: String): BotMemoryEntry? {
        return appDao.getBotMemoryEntry(positionHash, moveKey)
    }

    suspend fun recordBotMemoryOutcome(positionHash: String, moveKey: String, won: Boolean) {
        val existing = appDao.getBotMemoryEntry(positionHash, moveKey)
        val updated = if (existing != null) {
            existing.copy(
                wins = existing.wins + if (won) 1 else 0,
                losses = existing.losses + if (won) 0 else 1
            )
        } else {
            BotMemoryEntry(positionHash, moveKey, wins = if (won) 1 else 0, losses = if (won) 0 else 1)
        }
        appDao.upsertBotMemoryEntry(updated)
    }

    suspend fun getAllBotMemory(): List<BotMemoryEntry> = appDao.getAllBotMemory()

    suspend fun mergeBotMemoryEntries(entries: List<BotMemoryEntry>) {
        appDao.insertBotMemoryEntries(entries)
    }

    suspend fun getPlayerStateDirect(): PlayerState? {
        return appDao.getPlayerStateDirect()
    }

    suspend fun updatePlayerState(state: PlayerState) {
        appDao.insertPlayerState(state)
    }

    // Simple direct transaction logging without cryptographic mining loop
    suspend fun mineNewBlock(
        transactions: String,
        costCoins: Int = 0,
        earnCoins: Int = 0,
        difficultyPrefix: String = "00"
    ): BlockchainBlock {
        // Update Player State (adjust coins)
        val state = appDao.getPlayerStateDirect() ?: PlayerState()
        val nextCoins = (state.draughtCoins - costCoins + earnCoins).coerceAtLeast(0)
        appDao.updatePlayerState(state.copy(draughtCoins = nextCoins))

        // Query ledger to link block hashes
        val existingLedger = appDao.getLedgerDirect()
        val latestBlock = existingLedger.lastOrNull()
        val nextBlockNumber = (latestBlock?.blockNumber ?: -1) + 1
        val prevHash = latestBlock?.currentHash ?: "0000000000000000"

        val timestamp = System.currentTimeMillis()
        val minedBlock = BlockchainBlock(
            blockNumber = nextBlockNumber,
            timestamp = timestamp,
            transactions = transactions,
            nonce = 0L,
            prevHash = prevHash,
            currentHash = "0000000000000000"
        )

        appDao.insertBlock(minedBlock)
        return minedBlock
    }

    suspend fun restoreLedger(blocks: List<BlockchainBlock>) {
        appDao.clearLedger()
        appDao.insertBlocks(blocks)
    }

    private fun calculateHash(
        blockNumber: Int,
        timestamp: Long,
        transactions: String,
        nonce: Long,
        prevHash: String
    ): String {
        val input = "$blockNumber$timestamp$transactions$nonce$prevHash"
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
