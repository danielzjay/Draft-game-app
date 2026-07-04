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
    val customMusicName: String? = null
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

data class LeagueMatch(
    val id: String,
    val player1: String,
    val player2: String,
    val scheduledTime: String,
    val status: String, // "Scheduled", "Playing", "Completed", "Forfeited"
    val winner: String? = null,
    val reward: String = "150 BLC Coins",
    val ruleSystemOverride: String? = null
)

data class LadderTier(
    val rankIndex: Int, // 1 to 5 (Bottom to Top)
    val title: String, // e.g. "Bottom Qualifier", "Quarter-Finals", "Semi-Finals", "Vanguard Championship"
    val opponentName: String,
    val opponentHero: String,
    val isCompleted: Boolean,
    val winnerName: String? = null,
    val prize: String
)
