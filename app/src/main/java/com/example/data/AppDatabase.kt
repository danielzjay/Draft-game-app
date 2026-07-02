package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

@Database(
    entities = [Hero::class, PlayerState::class, BlockchainBlock::class, LeaderboardEntry::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "draughts_combat_database"
                )
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        fun calculateHash(
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

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.appDao())
                }
            }
        }

        suspend fun populateDatabase(appDao: AppDao) {
            // Seed initial player state
            val initialState = PlayerState(
                id = 1,
                playerName = "Grandmaster Checkers",
                draughtCoins = 350,
                xp = 0,
                level = 1,
                mmr = 1200,
                selectedSkin = "classic",
                selectedBoardStyle = "classic",
                unlockedSkins = "classic",
                lastSyncedTime = 0L,
                syncAccount = ""
            )
            appDao.insertPlayerState(initialState)

            // Seed initial heroes
            val initialHeroes = listOf(
                Hero(
                    id = "knight",
                    name = "Sir Roland",
                    faction = "Vanguard",
                    heroClass = "Knight",
                    level = 1,
                    xp = 0,
                    xpNeeded = 100,
                    hp = 120,
                    maxHp = 120,
                    atk = 25,
                    def = 10,
                    description = "A heavily armored champion. Excels at withstanding enemy assaults.",
                    activeAbilityId = "shield_bash",
                    abilityName = "Shield Bash",
                    abilityDescription = "On jump capture, block 40% of counterattack damage."
                ),
                Hero(
                    id = "mage",
                    name = "Aurelia Fireweaver",
                    faction = "Vanguard",
                    heroClass = "Mage",
                    level = 1,
                    xp = 0,
                    xpNeeded = 100,
                    hp = 80,
                    maxHp = 80,
                    atk = 40,
                    def = 2,
                    description = "An archmage of the eternal flame. Low defense but commands immense magical attack power.",
                    activeAbilityId = "fireball",
                    abilityName = "Flame Strike",
                    abilityDescription = "Deals 20 bonus spell damage on jump captures."
                ),
                Hero(
                    id = "rogue",
                    name = "Kaelen Swiftblade",
                    faction = "Vanguard",
                    heroClass = "Rogue",
                    level = 1,
                    xp = 0,
                    xpNeeded = 100,
                    hp = 95,
                    maxHp = 95,
                    atk = 32,
                    def = 5,
                    description = "A stealthy rogue who strikes with poison. Pierces enemy armor.",
                    activeAbilityId = "poison_strike",
                    abilityName = "Poison Strike",
                    abilityDescription = "Attack ignores enemy defense, dealing 10 additional piercing damage."
                ),
                Hero(
                    id = "valkyrie",
                    name = "Freya Shieldmaiden",
                    faction = "Vanguard",
                    heroClass = "Valkyrie",
                    level = 1,
                    xp = 0,
                    xpNeeded = 100,
                    hp = 110,
                    maxHp = 110,
                    atk = 28,
                    def = 8,
                    description = "A holy warrior. Combines balanced combat statistics with divine healing.",
                    activeAbilityId = "heal_shield",
                    abilityName = "Holy Aegis",
                    abilityDescription = "Heals self for 20 HP upon capturing an enemy piece."
                ),
                // Shadow Faction
                Hero(
                    id = "necromancer",
                    name = "Malakor Darkborne",
                    faction = "Shadow",
                    heroClass = "Necromancer",
                    level = 1,
                    xp = 0,
                    xpNeeded = 100,
                    hp = 85,
                    maxHp = 85,
                    atk = 38,
                    def = 4,
                    description = "An undead sorcerer who steals vital energy from the fallen.",
                    activeAbilityId = "soul_siphon",
                    abilityName = "Soul Siphon",
                    abilityDescription = "Drains 15 HP from the captured enemy, healing yourself."
                ),
                Hero(
                    id = "assassin",
                    name = "Vesper Shadowblade",
                    faction = "Shadow",
                    heroClass = "Assassin",
                    level = 1,
                    xp = 0,
                    xpNeeded = 100,
                    hp = 80,
                    maxHp = 80,
                    atk = 45,
                    def = 3,
                    description = "A quiet, deadly assassin of the night. High critical attack multiplier.",
                    activeAbilityId = "critical_strike",
                    abilityName = "Critical Strike",
                    abilityDescription = "30% chance to execute a double damage critical strike on captures."
                ),
                Hero(
                    id = "warlock",
                    name = "Xanatos Torment",
                    faction = "Shadow",
                    heroClass = "Warlock",
                    level = 1,
                    xp = 0,
                    xpNeeded = 100,
                    hp = 95,
                    maxHp = 95,
                    atk = 35,
                    def = 6,
                    description = "A dark warlock who commands ancient abyssal curses.",
                    activeAbilityId = "dark_curse",
                    abilityName = "Abyssal Curse",
                    abilityDescription = "Reduces target's defense by 5 before striking."
                ),
                Hero(
                    id = "death_knight",
                    name = "Lord Karr",
                    faction = "Shadow",
                    heroClass = "Death Knight",
                    level = 1,
                    xp = 0,
                    xpNeeded = 100,
                    hp = 130,
                    maxHp = 130,
                    atk = 22,
                    def = 12,
                    description = "An unstoppable, undead juggernaut of physical power.",
                    activeAbilityId = "reapers_guard",
                    abilityName = "Reaper's Guard",
                    abilityDescription = "Gains +1 permanent defense per capture in the active match."
                )
            )
            appDao.insertHeroes(initialHeroes)

            // Seed Blockchain genesis block
            val genesisTime = System.currentTimeMillis()
            val genesisTx = "[SYSTEM] Genesis Block - Blockchain (BLC) Ledger Initialized"
            val prevHash = "0000000000000000000000000000000000000000000000000000000000000000"
            val genesisHash = calculateHash(0, genesisTime, genesisTx, 42L, prevHash)
            appDao.insertBlock(
                BlockchainBlock(
                    blockNumber = 0,
                    timestamp = genesisTime,
                    transactions = genesisTx,
                    nonce = 42L,
                    prevHash = prevHash,
                    currentHash = genesisHash
                )
            )

            // Seed Leaderboard entries
            val initialLeaderboard = listOf(
                LeaderboardEntry(1, "DraughtsOverlord", 2450, 78.5, "death_knight"),
                LeaderboardEntry(2, "PixelTactician", 2280, 71.2, "mage"),
                LeaderboardEntry(3, "BlockMaster", 2100, 68.9, "knight"),
                LeaderboardEntry(4, "ValkyrieVixen", 1980, 65.0, "valkyrie"),
                LeaderboardEntry(5, "RogueShadow", 1850, 62.4, "rogue"),
                LeaderboardEntry(6, "WarlockWhisper", 1720, 59.1, "warlock"),
                LeaderboardEntry(7, "Slayer3000", 1600, 57.3, "assassin"),
                LeaderboardEntry(8, "CheckerKing", 1490, 54.2, "knight"),
                LeaderboardEntry(9, "Grandmaster Checkers", 1200, 50.0, "valkyrie", isCurrentUser = true),
                LeaderboardEntry(10, "NoobBuster", 1050, 42.1, "necromancer")
            )
            appDao.insertLeaderboard(initialLeaderboard)
        }
    }
}
