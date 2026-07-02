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

    suspend fun getPlayerStateDirect(): PlayerState? {
        return appDao.getPlayerStateDirect()
    }

    suspend fun updatePlayerState(state: PlayerState) {
        appDao.updatePlayerState(state)
    }

    // Interactive Blockchain Miner with customizable transactions
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
        val prevHash = latestBlock?.currentHash ?: "0000000000000000000000000000000000000000000000000000000000000000"

        val timestamp = System.currentTimeMillis()
        var nonce = 0L
        var finalHash = ""

        // Cryptographic proof-of-work: hash must start with '00' (difficulty prefix)
        while (true) {
            finalHash = calculateHash(nextBlockNumber, timestamp, transactions, nonce, prevHash)
            if (finalHash.startsWith(difficultyPrefix)) {
                break
            }
            nonce++
            if (nonce > 15000) { // Safeguard to prevent infinite looping
                break
            }
        }

        val minedBlock = BlockchainBlock(
            blockNumber = nextBlockNumber,
            timestamp = timestamp,
            transactions = transactions,
            nonce = nonce,
            prevHash = prevHash,
            currentHash = finalHash
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
