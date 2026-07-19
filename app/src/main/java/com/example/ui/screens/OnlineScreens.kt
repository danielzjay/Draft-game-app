package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import com.example.ui.components.GameButton
import com.example.ui.components.GameDangerButton
import com.example.ui.components.GamePanel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.network.FixtureStatus
import com.example.network.MatchStatus
import com.example.network.OnlineBoardPiece
import com.example.network.OnlineMatch
import com.example.network.TournamentFixture
import com.example.network.TournamentFormat
import com.example.network.TournamentInfo
import com.example.network.TournamentStatus
import com.example.ui.GameViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ==================== REAL ONLINE 1v1: LOBBY + LIVE MATCH ====================
@Composable
fun RealOnlineMatchScreen(viewModel: GameViewModel) {
    val match = viewModel.activeOnlineMatch

    if (match != null) {
        LiveOnlineMatchBoard(viewModel, match)
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Public, contentDescription = null, tint = AmberGold, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text("REAL ONLINE MATCHMAKING", color = AmberGold, fontSize = 16.sp, fontWeight = FontWeight.Black)
        Text(
            "Get paired with another real signed-in player under ${viewModel.ruleSystem.name.replace('_', ' ')} rules.",
            color = TextGray, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
        )

        if (viewModel.myUid == null) {
            Text("You need to sign in to play online.", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(bottom = 10.dp))
            Button(
                onClick = { viewModel.isGoogleAuthDialogOpen = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth(0.8f).height(44.dp)
            ) {
                Text("SIGN IN WITH GOOGLE", color = Color(0xFF1F1F1F), fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
            return@Column
        }

        viewModel.onlineMatchError?.let {
            Text(it, color = Color(0xFFFF6E6E), fontSize = 10.sp, modifier = Modifier.padding(bottom = 10.dp))
        }

        if (viewModel.isSearchingRealMatch) {
            CircularProgressIndicator(color = AmberGold, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text("Searching for an opponent...", color = TextWhite, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = { viewModel.cancelRealMatchmaking() }) {
                Text("Cancel", color = TextGray)
            }

            if (viewModel.waitingPlayers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text("Or challenge someone directly:", color = TextGray, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.heightIn(max = 220.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(viewModel.waitingPlayers) { p ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = BorderStroke(1.dp, Color(0x33FFC107)),
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.challengeWaitingPlayer(p) }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(p.name, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("MMR ${p.mmr} • waiting", color = TextGray, fontSize = 9.sp)
                                }
                                Text("Challenge", color = AmberGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (viewModel.canOfferBotFallback) {
                Spacer(modifier = Modifier.height(20.dp))
                Text("Nobody's available right now.", color = TextMuted, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(
                    onClick = { viewModel.fallBackToBotMatch() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberGold),
                    border = BorderStroke(1.dp, AmberGold)
                ) {
                    Text("Practice Offline Instead", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E19)),
                border = BorderStroke(1.dp, Color(0xFF00E676)),
                modifier = Modifier.fillMaxWidth(0.85f).padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "ONLINE 1v1 MATCH RULES",
                        color = Color(0xFF00E676),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "• Match Entry Fee: 50 Coins (500 UGX equivalent)\n" +
                        "• Winner's Payout: 75 Coins (150% return)\n" +
                        "• Loser's Comfort: 2 Coins (3% back of fee)\n" +
                        "• Requirement: You must have at least 50 coins to play. This does not apply to free tournament competitions.",
                        color = TextWhite,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }

            GameButton(
                text = "FIND A REAL OPPONENT",
                onClick = { viewModel.startRealMatchmaking() },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}

@Composable
private fun LiveOnlineMatchBoard(viewModel: GameViewModel, match: OnlineMatch) {
    val myUid = viewModel.myUid
    val amIRed = match.player1Uid == myUid
    val myTurn = match.turnUid == myUid
    val opponentName = if (amIRed) match.player2Name else match.player1Name

    LaunchedEffect(match.status, match.winnerUid) {
        if (match.status != MatchStatus.ACTIVE && match.fixtureId != null) {
            viewModel.completeFixtureIfNeeded(match)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("vs $opponentName", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                if (match.isCompetition) {
                    Text("Competition match — 2hr forfeit clock", color = Color(0xFFFFB74D), fontSize = 9.sp)
                }
            }
            if (match.status == MatchStatus.ACTIVE) {
                Box(
                    modifier = Modifier
                        .background(if (myTurn) Color(0xFF2E7D32) else Color(0xFF616161), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(if (myTurn) "YOUR TURN" else "THEIR TURN", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        val context = androidx.compose.ui.platform.LocalContext.current
        val opponentSong = if (amIRed) match.player2Song else match.player1Song

        if (match.status == MatchStatus.ACTIVE && opponentSong.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, if (viewModel.isStreamingOpponentMusic) AmberGold else Color(0x33FFC107)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (viewModel.isStreamingOpponentMusic) Icons.Default.Headset else Icons.Default.MusicNote,
                            contentDescription = "Music",
                            tint = if (viewModel.isStreamingOpponentMusic) AmberGold else TextGray,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = if (viewModel.isStreamingOpponentMusic) "STREAMING OPPONENT'S TRACK" else "OPPONENT'S SOUNDTRACK",
                                color = if (viewModel.isStreamingOpponentMusic) AmberGold else TextGray,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = opponentSong,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Button(
                        onClick = {
                            if (viewModel.isStreamingOpponentMusic) {
                                viewModel.stopStreamingOpponentMusic()
                            } else {
                                viewModel.startStreamingOpponentMusic(opponentSong, context)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.isStreamingOpponentMusic) Color(0xFFEF5350) else AmberGold,
                            contentColor = if (viewModel.isStreamingOpponentMusic) Color.White else DarkBg
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(
                            text = if (viewModel.isStreamingOpponentMusic) "Stop Stream" else "Stream live 🎧",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (match.status != MatchStatus.ACTIVE) {
            val youWon = match.winnerUid == myUid
            Card(
                colors = CardDefaults.cardColors(containerColor = if (youWon) Color(0xFF1B3A1F) else Color(0xFF3A1B1B)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (youWon) "VICTORY!" else "DEFEAT",
                        color = if (youWon) Color(0xFF66BB6A) else Color(0xFFEF5350),
                        fontSize = 16.sp, fontWeight = FontWeight.Black
                    )
                    if (match.status == MatchStatus.FORFEITED) {
                        Text("(won by forfeit — opponent's clock ran out)", color = TextGray, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.leaveRealMatch() }, colors = ButtonDefaults.buttonColors(containerColor = AmberGold)) {
                        Text("Back to Lobby", color = DarkBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        SimpleOnlineBoard(viewModel, match, amIRed)

        if (match.status == MatchStatus.ACTIVE) {
            Spacer(modifier = Modifier.height(12.dp))
            GameDangerButton(
                text = "Resign",
                onClick = { viewModel.resignRealMatch() },
                modifier = Modifier.fillMaxWidth(),
                height = 44.dp
            )
        }
    }
}

/**
 * A deliberately plain board for real online matches — no hero art, no HP/combat overlay, just
 * positions and kings. Real PvP here is straight draughts under the chosen federation's rules;
 * see OnlineMatchRepository for why the single-player RPG combat layer doesn't apply online.
 */
@Composable
private fun SimpleOnlineBoard(viewModel: GameViewModel, match: OnlineMatch, amIRed: Boolean) {
    val size = if (match.ruleSystem == "WORLD_DRAUGHTS_FEDERATION") 10 else 8
    val onlineMandatoryJumps = remember(match.board, match.turnUid) {
        viewModel.getOnlinePiecesWithMandatoryJumps(match)
    }
    val selectedPieceObj = remember(viewModel.selectedOnlineSquare, match.board) {
        viewModel.selectedOnlineSquare?.let { (r, c) ->
            match.board.firstOrNull { it.row == r && it.col == c }
        }
    }
    val onlineMovesAndJumps = remember(selectedPieceObj, match.board) {
        selectedPieceObj?.let { viewModel.onlineMovesAndJumps(it, match.board) } ?: Pair(emptyList(), emptyList())
    }

    val infiniteTransition = rememberInfiniteTransition(label = "online_mandatory_jump_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .border(BorderStroke(3.dp, DarkSurfaceVariant), RoundedCornerShape(8.dp))
            .background(GridDarkSquare)
    ) {
        // Flip the board so the local player's pieces are always at the bottom, regardless of color
        val displayRows = if (amIRed) (0 until size) else (size - 1 downTo 0)
        Column(modifier = Modifier.fillMaxSize()) {
            for (row in displayRows) {
                Row(modifier = Modifier.weight(1f)) {
                    val displayCols = if (amIRed) (0 until size) else (size - 1 downTo 0)
                    for (col in displayCols) {
                        val isDark = (row + col) % 2 == 1
                        val bg = if (isDark) GridDarkSquare else GridLightSquare
                        val piece = match.board.firstOrNull { it.row == row && it.col == col }
                        val isSelected = viewModel.selectedOnlineSquare == Pair(row, col)
                        val isTargetMove = onlineMovesAndJumps.first.contains(Pair(row, col))
                        val isTargetJump = onlineMovesAndJumps.second.contains(Pair(row, col))
                        val isMandatoryJump = piece != null && onlineMandatoryJumps.contains(piece.id)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(bg)
                                .clickable(enabled = isDark) { viewModel.tapOnlineSquare(row, col) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(modifier = Modifier.fillMaxSize().background(Color(0x33FFEB3B)))
                            }
                            if (isTargetMove) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(GridHighlight)
                                )
                            }
                            if (isTargetJump) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .border(1.5.dp, RedCrimson, CircleShape)
                                        .background(GridAttackHighlight)
                                )
                            }
                            if (piece != null) {
                                OnlinePieceView(
                                    piece, 
                                    isMandatoryJump = isMandatoryJump,
                                    pulseScale = pulseScale,
                                    pulseAlpha = pulseAlpha
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnlinePieceView(
    piece: OnlineBoardPiece, 
    isMandatoryJump: Boolean = false,
    pulseScale: Float = 1.0f,
    pulseAlpha: Float = 0.0f
) {
    val color = if (piece.isRed) Color(0xFFD32F2F) else Color(0xFF303030)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (isMandatoryJump) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                        alpha = pulseAlpha
                    }
                    .border(2.5.dp, Color(0xFFFF1744), CircleShape)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                    BorderStroke(
                        if (isMandatoryJump) 3.dp else 2.dp,
                        if (isMandatoryJump) Color(0xFFFF1744) else (if (piece.isKing) AmberGold else Color(0x33FFFFFF))
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (piece.isKing) {
                Icon(Icons.Default.Star, contentDescription = "King", tint = AmberGold, modifier = Modifier.size(16.dp))
            }
            if (isMandatoryJump) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.TopStart)
                        .background(Color(0xFFFF1744), CircleShape)
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PriorityHigh,
                        contentDescription = "Must Capture",
                        tint = Color.White,
                        modifier = Modifier.size(9.dp)
                    )
                }
            }
        }
    }
}

// ==================== TOURNAMENTS: BROWSER ====================
@Composable
fun TournamentBrowserScreen(viewModel: GameViewModel, formatFilter: String?) {
    LaunchedEffect(Unit) { viewModel.loadOpenTournaments() }

    if (viewModel.currentTournament != null) {
        TournamentDetailScreen(viewModel)
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("COMPETITIONS", color = AmberGold, fontSize = 16.sp, fontWeight = FontWeight.Black)
            IconButton(onClick = { viewModel.isCreateTournamentDialogOpen = true }) {
                Icon(Icons.Default.Add, contentDescription = "Create", tint = AmberGold)
            }
        }
        Text(
            "Organizers pick Bracket or League when creating a competition.",
            color = TextGray, fontSize = 10.sp, modifier = Modifier.padding(bottom = 8.dp)
        )

        if (viewModel.myUid == null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x332979FF)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sign in to register or organize", color = TextWhite, fontSize = 10.sp, modifier = Modifier.weight(1f))
                    Button(
                        onClick = { viewModel.isGoogleAuthDialogOpen = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Sign In", color = Color(0xFF1F1F1F), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        val filtered = if (formatFilter == null) viewModel.openTournaments else viewModel.openTournaments.filter { it.format == formatFilter }
        if (filtered.isEmpty()) {
            Text("No open competitions yet. Tap + to organize one.", color = TextMuted, fontSize = 11.sp)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered) { t ->
                TournamentRow(t) { viewModel.openTournament(t.tournamentId) }
            }
        }
    }

    if (viewModel.isCreateTournamentDialogOpen) {
        CreateTournamentDialog(viewModel, defaultFormat = formatFilter ?: TournamentFormat.BRACKET)
    }
}

@Composable
private fun TournamentRow(t: TournamentInfo, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, Color(0x33FFC107)),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(t.name, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(if (t.format == TournamentFormat.LEAGUE) Color(0xFF2E7D32) else Color(0xFF5E35B1), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(if (t.format == TournamentFormat.LEAGUE) "LEAGUE" else "BRACKET", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text("by ${t.organizerName} • ${t.registeredUids.size} registered", color = TextGray, fontSize = 10.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGray)
        }
    }
}

@Composable
private fun CreateTournamentDialog(viewModel: GameViewModel, defaultFormat: String) {
    var name by remember { mutableStateOf("") }
    var format by remember { mutableStateOf(defaultFormat) }
    var daysFromNow by remember { mutableStateOf("1") }
    var ruleSystemSelected by remember { mutableStateOf(com.example.ui.DraughtsRuleSystem.AMERICAN_CHECKER_FEDERATION) }
    var winnerRewardStr by remember { mutableStateOf("100") }
    var finalLoserRewardStr by remember { mutableStateOf("50") }
    var semiFinalLoserRewardStr by remember { mutableStateOf("25") }

    val scrollState = androidx.compose.foundation.rememberScrollState()

    Dialog(onDismissRequest = { viewModel.isCreateTournamentDialogOpen = false }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                Text("ORGANIZE A COMPETITION", color = AmberGold, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Competition name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Rule System:", color = TextGray, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(4.dp))
                val onlineRules = com.example.ui.DraughtsRuleSystem.values()
                val chunkedOnlineRules = onlineRules.toList().chunked(2)
                chunkedOnlineRules.forEach { rowRules ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowRules.forEach { rule ->
                            val isSelected = ruleSystemSelected == rule
                            val label = when (rule) {
                                com.example.ui.DraughtsRuleSystem.AMERICAN_CHECKER_FEDERATION -> "ACF (American 8x8)"
                                com.example.ui.DraughtsRuleSystem.ENGLISH_DRAUGHTS_ASSOCIATION -> "EDA (English 8x8)"
                                com.example.ui.DraughtsRuleSystem.WORLD_DRAUGHTS_FEDERATION -> "FMJD (Int'l 10x10)"
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { ruleSystemSelected = rule },
                                label = { Text(label, fontSize = 11.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AmberGold.copy(alpha = 0.2f),
                                    selectedLabelColor = AmberGold
                                )
                            )
                        }
                        if (rowRules.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                Text("Format (you decide):", color = TextGray, fontSize = 10.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = format == TournamentFormat.BRACKET,
                        onClick = { format = TournamentFormat.BRACKET },
                        label = { Text("Bracket") }
                    )
                    FilterChip(
                        selected = format == TournamentFormat.LEAGUE,
                        onClick = { format = TournamentFormat.LEAGUE },
                        label = { Text("League") }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = daysFromNow, onValueChange = { daysFromNow = it.filter { c -> c.isDigit() } },
                    label = { Text("Starts in how many days?") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Set Tournament Rewards (BLC):", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = winnerRewardStr, onValueChange = { winnerRewardStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Top Win Reward (1st)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = finalLoserRewardStr, onValueChange = { finalLoserRewardStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Final Loser Reward (2nd)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = semiFinalLoserRewardStr, onValueChange = { semiFinalLoserRewardStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Semi-Final Loser Reward (3rd/4th)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val days = daysFromNow.toIntOrNull() ?: 1
                            val startsAt = System.currentTimeMillis() + days * 24L * 60 * 60 * 1000
                            val winRew = winnerRewardStr.toIntOrNull() ?: 100
                            val loseRew = finalLoserRewardStr.toIntOrNull() ?: 50
                            val semiRew = semiFinalLoserRewardStr.toIntOrNull() ?: 25
                            if (name.isNotBlank()) {
                                viewModel.createTournament(
                                    name = name, 
                                    format = format, 
                                    startsAt = startsAt,
                                    ruleSystemName = ruleSystemSelected.name,
                                    winnerReward = winRew,
                                    finalLoserReward = loseRew,
                                    semiFinalLoserReward = semiRew
                                )
                                viewModel.isCreateTournamentDialogOpen = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                    ) {
                        Text("Create", color = DarkBg, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(onClick = { viewModel.isCreateTournamentDialogOpen = false }) {
                        Text("Cancel", color = TextGray)
                    }
                }
            }
        }
    }
}

// ==================== TOURNAMENTS: DETAIL / BRACKET VIEW ====================
@Composable
fun TournamentDetailScreen(viewModel: GameViewModel) {
    val t = viewModel.currentTournament ?: return
    val myUid = viewModel.myUid
    val isOrganizer = t.organizerUid == myUid
    val isRegistered = t.registeredUids.contains(myUid)
    val dateFmt = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.currentTournament = null; viewModel.currentFixtures = emptyList() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Column {
                Text(t.name, color = AmberGold, fontSize = 15.sp, fontWeight = FontWeight.Black)
                Text("${t.format} • starts ${dateFmt.format(Date(t.startsAt))}", color = TextGray, fontSize = 10.sp)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (t.status == TournamentStatus.REGISTERING) {
            Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("${t.registeredUids.size} players registered", color = TextWhite, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (!isRegistered) {
                        Button(onClick = { viewModel.registerForTournament() }, colors = ButtonDefaults.buttonColors(containerColor = AmberGold)) {
                            Text("Register", color = DarkBg, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text("You're registered ✓", color = Color(0xFF66BB6A), fontSize = 11.sp)
                    }
                    if (isOrganizer) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.startTournament() },
                            enabled = t.registeredUids.size >= 2,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Text("Close Registration & Start", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val rounds = viewModel.currentFixtures.groupBy { it.round }.toSortedMap()
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            rounds.forEach { (round, fixtures) ->
                item {
                    Text("Round $round", color = AmberGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                items(fixtures) { fixture ->
                    FixtureRow(fixture, myUid, dateFmt) { viewModel.startFixture(fixture) }
                }
            }
        }
    }
}

@Composable
private fun FixtureRow(fixture: TournamentFixture, myUid: String?, dateFmt: SimpleDateFormat, onPlay: () -> Unit) {
    val involvesMe = fixture.player1Uid == myUid || fixture.player2Uid == myUid
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = if (involvesMe) BorderStroke(1.dp, AmberGold) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    if (fixture.status == FixtureStatus.BYE) "${fixture.player1Name} — BYE" else "${fixture.player1Name} vs ${fixture.player2Name}",
                    color = TextWhite, fontSize = 12.sp
                )
                Text(dateFmt.format(Date(fixture.scheduledAt)), color = TextGray, fontSize = 9.sp)
            }
            when (fixture.status) {
                FixtureStatus.COMPLETED -> Text("Done", color = Color(0xFF66BB6A), fontSize = 10.sp)
                FixtureStatus.BYE -> Text("Bye", color = TextMuted, fontSize = 10.sp)
                FixtureStatus.IN_PROGRESS -> Text("Live", color = Color(0xFFFFB74D), fontSize = 10.sp)
                else -> if (involvesMe) {
                    Button(onClick = onPlay, colors = ButtonDefaults.buttonColors(containerColor = AmberGold), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                        Text("Play", color = DarkBg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
