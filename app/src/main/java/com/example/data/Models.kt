package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heroes")
data class Hero(
    @PrimaryKey val id: String, // e.g. "knight", "mage", "rogue", "valkyrie", "necromancer", "assassin", "warlock", "death_knight"
    val name: String,
    val faction: String, // "Vanguard" or "Shadow"
    val heroClass: String, // "Knight", "Mage", "Rogue", "Valkyrie", etc.
    val level: Int = 1,
    val xp: Int = 0,
    val xpNeeded: Int = 100,
    val hp: Int,
    val maxHp: Int,
    val atk: Int,
    val def: Int,
    val description: String,
    val isUnlocked: Boolean = true,
    val activeAbilityId: String,
    val abilityName: String,
    val abilityDescription: String
)

@Entity(tableName = "player_state")
data class PlayerState(
    @PrimaryKey val id: Int = 1,
    val playerName: String = "Grandmaster Checkers",
    val draughtCoins: Int = 250, // Currency
    val xp: Int = 0,
    val level: Int = 1,
    val mmr: Int = 1200, // Leaderboard Rating
    val selectedSkin: String = "classic", // "classic", "neon", "cyberpunk"
    val selectedBoardStyle: String = "classic", // "classic", "royal", "neon_grid"
    val unlockedSkins: String = "classic", // Comma-separated
    val lastSyncedTime: Long = 0L,
    val syncAccount: String = "",
    val customMusicUri: String? = null, // content:// URI of a user-picked song, if any
    val customMusicName: String? = null,
    val tagline: String = "Tactical Overlord",
    val phoneNumber: String = "",
    val countryCode: String = "+256",
    val photoUri: String? = null
)

@Entity(tableName = "blockchain_ledger")
data class BlockchainBlock(
    @PrimaryKey val blockNumber: Int,
    val timestamp: Long,
    val transactions: String, // JSON or descriptive string
    val nonce: Long,
    val prevHash: String,
    val currentHash: String
)

@Entity(tableName = "leaderboard")
data class LeaderboardEntry(
    @PrimaryKey val rank: Int,
    val name: String,
    val mmr: Int,
    val winRate: Double,
    val favoriteHero: String,
    val isCurrentUser: Boolean = false
)

/**
 * Bot "memory" — a simple, real opening/position book. Key = a canonical snapshot of the board
 * (piece positions, colors, king status) at the moment a move was made, combined with the exact
 * move played from it. Every time a bot game ends, every move the BOT made gets one win or one
 * loss added here for its (position, move) pair. Over many games this starts to bias the bot
 * toward moves that have actually won before from a given shape of board, and away from ones
 * that have lost — on top of (not instead of) the real minimax search, which still does the
 * tactical heavy lifting. See BotMemoryRepository for how this syncs with other players' data.
 */
@Entity(tableName = "bot_memory", primaryKeys = ["positionHash", "moveKey"])
data class BotMemoryEntry(
    val positionHash: String,
    val moveKey: String, // "fromRow,fromCol-toRow,toCol"
    val wins: Int = 0,
    val losses: Int = 0
)
