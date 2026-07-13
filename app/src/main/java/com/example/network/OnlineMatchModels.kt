package com.example.network

/**
 * Real online play data models (Cloud Firestore). Deliberately scoped to CORE draughts state
 * (positions, kings, turn, rule system) — not the single-player hero HP/ability/combat layer,
 * since that layer depends on each player's own local hero roster and has RNG-driven outcomes
 * (crit chance, etc.) that would desync two independent devices if each simulated it separately.
 * Online matches are straight ACF/EDA/FMJD draughts under whichever rule system was agreed.
 */

data class OnlineBoardPiece(
    val id: String = "",
    val row: Int = 0,
    val col: Int = 0,
    val isRed: Boolean = true,
    val isKing: Boolean = false
)

object MatchStatus {
    const val ACTIVE = "active"
    const val FINISHED = "finished"
    const val FORFEITED = "forfeited"
}

data class OnlineMatch(
    val matchId: String = "",
    val player1Uid: String = "",
    val player1Name: String = "",
    val player2Uid: String = "",
    val player2Name: String = "",
    val ruleSystem: String = "AMERICAN_CHECKER_FEDERATION",
    val board: List<OnlineBoardPiece> = emptyList(),
    val turnUid: String = "",
    val status: String = MatchStatus.ACTIVE,
    val winnerUid: String? = null,
    // Casual matches never expire — either player can resume whenever they like, or just start
    // a new one instead. Competition matches are timed: if the player whose turn it is hasn't
    // moved within FORFEIT_WINDOW_MS, they forfeit that match on the next time anyone checks it.
    val isCompetition: Boolean = false,
    val tournamentId: String? = null,
    val fixtureId: String? = null,
    val lastMoveAt: Long = 0L,
    val createdAt: Long = 0L,
    val player1Song: String = "",
    val player2Song: String = ""
) {
    companion object {
        const val FORFEIT_WINDOW_MS = 2 * 60 * 60 * 1000L // 2 hours, competitions only
    }
}

data class QueueEntry(
    val uid: String = "",
    val name: String = "",
    val mmr: Int = 0,
    val ruleSystem: String = "AMERICAN_CHECKER_FEDERATION",
    val joinedAt: Long = 0L,
    val matchedMatchId: String? = null
)

object TournamentFormat {
    const val BRACKET = "BRACKET" // single-elimination
    const val LEAGUE = "LEAGUE"   // round-robin table
}

object TournamentStatus {
    const val REGISTERING = "registering"
    const val IN_PROGRESS = "in_progress"
    const val COMPLETED = "completed"
}

data class TournamentInfo(
    val tournamentId: String = "",
    val name: String = "",
    val organizerUid: String = "",
    val organizerName: String = "",
    // The organizer decides the format when creating the competition — never hardcoded.
    val format: String = TournamentFormat.BRACKET,
    val ruleSystem: String = "AMERICAN_CHECKER_FEDERATION",
    val status: String = TournamentStatus.REGISTERING,
    val registeredUids: List<String> = emptyList(),
    val registeredNames: Map<String, String> = emptyMap(),
    val startsAt: Long = 0L,
    val createdAt: Long = 0L,
    val roundCount: Int = 0,
    val winnerReward: Int = 100,
    val finalLoserReward: Int = 50,
    val semiFinalLoserReward: Int = 25
)

object FixtureStatus {
    const val SCHEDULED = "scheduled"
    const val IN_PROGRESS = "in_progress"
    const val COMPLETED = "completed"
    const val BYE = "bye" // odd player count — this round's free pass
}

data class TournamentFixture(
    val fixtureId: String = "",
    val tournamentId: String = "",
    val round: Int = 0,
    val player1Uid: String = "",
    val player1Name: String = "",
    val player2Uid: String = "",
    val player2Name: String = "",
    val matchId: String? = null,
    val scheduledAt: Long = 0L,
    val status: String = FixtureStatus.SCHEDULED,
    val winnerUid: String? = null
)

data class OnlinePlayerPresence(
    val uid: String = "",
    val name: String = "",
    val lastSeen: Long = 0L,
    val mmr: Int = 1000,
    val ruleSystem: String = "AMERICAN_CHECKER_FEDERATION",
    val inGame: Boolean = false
)

data class DirectChallenge(
    val requestId: String = "",
    val senderUid: String = "",
    val senderName: String = "",
    val receiverUid: String = "",
    val receiverName: String = "",
    val ruleSystem: String = "AMERICAN_CHECKER_FEDERATION",
    val status: String = "PENDING", // PENDING, ACCEPTED, DECLINED
    val matchId: String? = null,
    val timestamp: Long = 0L
)

