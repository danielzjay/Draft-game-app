package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Hero Operations
    @Query("SELECT * FROM heroes ORDER BY id ASC")
    fun getAllHeroes(): Flow<List<Hero>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeroes(heroes: List<Hero>)

    @Update
    suspend fun updateHero(hero: Hero)

    // Player State Operations
    @Query("SELECT * FROM player_state WHERE id = 1 LIMIT 1")
    fun getPlayerState(): Flow<PlayerState?>

    @Query("SELECT * FROM player_state WHERE id = 1 LIMIT 1")
    suspend fun getPlayerStateDirect(): PlayerState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerState(state: PlayerState)

    @Update
    suspend fun updatePlayerState(state: PlayerState)

    // Blockchain Ledger Operations
    @Query("SELECT * FROM blockchain_ledger ORDER BY blockNumber ASC")
    fun getLedger(): Flow<List<BlockchainBlock>>

    @Query("SELECT * FROM blockchain_ledger ORDER BY blockNumber ASC")
    suspend fun getLedgerDirect(): List<BlockchainBlock>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: BlockchainBlock)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlocks(blocks: List<BlockchainBlock>)

    @Query("DELETE FROM blockchain_ledger")
    suspend fun clearLedger()

    // Leaderboard Operations
    @Query("SELECT * FROM leaderboard ORDER BY rank ASC")
    fun getLeaderboard(): Flow<List<LeaderboardEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboard(entries: List<LeaderboardEntry>)

    // Bot Memory Operations
    @Query("SELECT * FROM bot_memory WHERE positionHash = :positionHash AND moveKey = :moveKey LIMIT 1")
    suspend fun getBotMemoryEntry(positionHash: String, moveKey: String): BotMemoryEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBotMemoryEntry(entry: BotMemoryEntry)

    @Query("SELECT * FROM bot_memory")
    suspend fun getAllBotMemory(): List<BotMemoryEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBotMemoryEntries(entries: List<BotMemoryEntry>)
}
