package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.*
import com.example.ui.components.HeroDrawing
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun MainGameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val playerState by viewModel.playerState.collectAsState()
    val activeTab = viewModel.currentTab

    Scaffold(
        modifier = modifier.fillMaxSize().background(DarkBg),
        topBar = { GameHeader(playerState, viewModel) },
        bottomBar = { GameBottomNavigation(activeTab) { viewModel.currentTab = it } }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBg)
        ) {
            when (activeTab) {
                GameTab.BATTLE -> BattleScreen(viewModel)
                GameTab.HEROES -> HeroesScreen(viewModel)
                GameTab.STORE -> StoreScreen(viewModel)
                GameTab.LEADERBOARD -> LeaderboardScreen(viewModel)
                GameTab.SYNC -> SyncScreen(viewModel)
            }

            // Cinematic Combat Screen Overlay
            viewModel.activeCombat?.let { combat ->
                CombatOverlay(combat = combat)
            }

            // Profile Management Dialog Overlay
            if (viewModel.isProfileDialogOpen) {
                ProfileManagementDialog(viewModel = viewModel)
            }

            // Google Auth Handshake / Account Selector Dialog Overlay
            if (viewModel.isGoogleAuthDialogOpen) {
                GoogleAuthenticationDialog(viewModel = viewModel)
            }

            // Game Rules & Help Dialog Overlay
            if (viewModel.isRulesDialogOpen) {
                GameRulesHelpDialog(viewModel = viewModel)
            }

            // Premium Payment Portal Dialog Overlay
            if (viewModel.isPaymentPortalOpen) {
                PremiumPaymentPortalDialog(viewModel = viewModel)
            }

            // Floating Active System Notification Banner HUD Card
            viewModel.activeNotificationBanner?.let { bannerMessage ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .align(Alignment.TopCenter)
                        .animateContentSize()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = RedCrimson),
                        border = BorderStroke(1.dp, AmberGold),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Notification",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = bannerMessage,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameHeader(playerState: PlayerState?, viewModel: GameViewModel) {
    Surface(
        color = DarkSurface,
        tonalElevation = 6.dp,
        border = BorderStroke(1.dp, Color(0x1AFFFFFF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val (avatarColor, avatarIcon) = when (viewModel.selectedAvatarId) {
                "knight" -> Pair(RedCrimson, Icons.Default.Shield)
                "mage" -> Pair(Color(0xFF9C27B0), Icons.Default.AutoAwesome)
                "valkyrie" -> Pair(Color(0xFF00E676), Icons.Default.LocalActivity)
                "assassin" -> Pair(Color(0xFFFF9100), Icons.Default.Security)
                "rogue" -> Pair(Color(0xFF2979FF), Icons.Default.Bolt)
                else -> Pair(RedCrimson, Icons.Default.Person)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { viewModel.isProfileDialogOpen = true }
                    .testTag("profile_header_btn")
                    .padding(4.dp)
            ) {
                // Profile Avatar icon - Professional Polish theme
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        avatarIcon,
                        contentDescription = "Avatar",
                        tint = DarkBg,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = playerState?.playerName ?: "Alex.Vanguard",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(RedCrimsonDark, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "LV ${playerState?.level ?: 1}",
                                color = RedCrimson,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${playerState?.mmr ?: 1200} MMR",
                            color = TextGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Secure Blockchain Currency Balance Widget - Professional Polish theme
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = BorderStroke(1.dp, RedCrimson.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .clickable { viewModel.currentTab = GameTab.STORE }
                    .testTag("coin_wallet")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccountBalanceWallet,
                        contentDescription = "Wallet",
                        tint = RedCrimson,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${playerState?.draughtCoins ?: 0} BLC",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun GameBottomNavigation(selectedTab: GameTab, onTabSelected: (GameTab) -> Unit) {
    NavigationBar(
        containerColor = DarkSurface,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(GameTab.BATTLE, Icons.Default.Grid4x4, "Battle"),
            Triple(GameTab.HEROES, Icons.Default.Groups, "Heroes"),
            Triple(GameTab.STORE, Icons.Default.Storefront, "Store"),
            Triple(GameTab.LEADERBOARD, Icons.Default.EmojiEvents, "Rankings"),
            Triple(GameTab.SYNC, Icons.Default.CloudSync, "Sync")
        )

        items.forEach { (tab, icon, label) ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF1D192B),
                    selectedTextColor = VioletNeon,
                    indicatorColor = VioletNeon,
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray
                ),
                modifier = Modifier.testTag("nav_tab_${label.lowercase()}")
            )
        }
    }
}

// ==================== SCREEN 1: BATTLE BOARD ====================
@Composable
fun BattleScreen(viewModel: GameViewModel) {
    val state by viewModel.playerState.collectAsState()
    val activeSkin = state?.selectedSkin ?: "classic"
    val activeBoardStyle = state?.selectedBoardStyle ?: "classic"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState() /* Let it fit in standard size screens */)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Battle Arena Info Block
        var showGameModeMenu by remember { mutableStateOf(false) }

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = when (viewModel.selectedGameMode) {
                                GameViewModel.SelectedGameMode.LOCAL_PASS_AND_PLAY -> Icons.Default.SportsEsports
                                GameViewModel.SelectedGameMode.OFFLINE_VS_BOT -> Icons.Default.Android
                                GameViewModel.SelectedGameMode.ONLINE_VS_BOT -> Icons.Default.CloudQueue
                                GameViewModel.SelectedGameMode.ONLINE_MATCHMAKING -> Icons.Default.Group
                                GameViewModel.SelectedGameMode.COMPETITION_LEAGUE -> Icons.Default.EmojiEvents
                                GameViewModel.SelectedGameMode.COMPETITION_LADDER -> Icons.Default.MilitaryTech
                            },
                            contentDescription = "Active Battle Mode",
                            tint = if (viewModel.isOnlineMode) Color(0xFF00E676) else RedCrimson,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (viewModel.selectedGameMode) {
                                GameViewModel.SelectedGameMode.LOCAL_PASS_AND_PLAY -> "🎮 LOCAL: PASS & PLAY"
                                GameViewModel.SelectedGameMode.OFFLINE_VS_BOT -> "🤖 OFFLINE: VS BOT"
                                GameViewModel.SelectedGameMode.ONLINE_VS_BOT -> "🌐 ONLINE: VS CLOUD BOT"
                                GameViewModel.SelectedGameMode.ONLINE_MATCHMAKING -> "⚔️ ONLINE: VS PLAYERS"
                                GameViewModel.SelectedGameMode.COMPETITION_LEAGUE -> "🏆 COMP: VANGUARD LEAGUE"
                                GameViewModel.SelectedGameMode.COMPETITION_LADDER -> "🪜 COMP: GLADIATOR LADDER"
                            },
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { viewModel.isRulesDialogOpen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp).testTag("rules_help_button")
                        ) {
                            Icon(Icons.Default.Help, contentDescription = "Rules", tint = AmberGold, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Rules",
                                color = AmberGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box {
                            Button(
                                onClick = { showGameModeMenu = true },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp).testTag("switch_mode_button")
                            ) {
                                Text(
                                    text = "Switch Mode",
                                    color = AmberGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = AmberGold, modifier = Modifier.size(12.dp))
                            }

                            DropdownMenu(
                                expanded = showGameModeMenu,
                                onDismissRequest = { showGameModeMenu = false },
                                modifier = Modifier.background(DarkSurface).border(1.dp, Color(0x33FFFFFF))
                            ) {
                                DropdownMenuItem(
                                    text = { Text("🎮 Local Pass & Play", color = TextWhite, fontSize = 11.sp) },
                                    onClick = {
                                        viewModel.changeGameMode(GameViewModel.SelectedGameMode.LOCAL_PASS_AND_PLAY)
                                        showGameModeMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("🤖 Offline VS Bot AI", color = TextWhite, fontSize = 11.sp) },
                                    onClick = {
                                        viewModel.changeGameMode(GameViewModel.SelectedGameMode.OFFLINE_VS_BOT)
                                        showGameModeMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("🌐 Online VS Cloud Bot", color = TextWhite, fontSize = 11.sp) },
                                    onClick = {
                                        viewModel.changeGameMode(GameViewModel.SelectedGameMode.ONLINE_VS_BOT)
                                        showGameModeMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("⚔️ Online VS Players", color = TextWhite, fontSize = 11.sp) },
                                    onClick = {
                                        viewModel.changeGameMode(GameViewModel.SelectedGameMode.ONLINE_MATCHMAKING)
                                        showGameModeMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("⚽ Vanguard League (Football style)", color = TextWhite, fontSize = 11.sp) },
                                    onClick = {
                                        viewModel.changeGameMode(GameViewModel.SelectedGameMode.COMPETITION_LEAGUE)
                                        showGameModeMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("🪜 Gladiator Ladder Bracket", color = TextWhite, fontSize = 11.sp) },
                                    onClick = {
                                        viewModel.changeGameMode(GameViewModel.SelectedGameMode.COMPETITION_LADDER)
                                        showGameModeMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = when (viewModel.selectedGameMode) {
                        GameViewModel.SelectedGameMode.LOCAL_PASS_AND_PLAY -> "Play with a friend locally on the same device. No account link required."
                        GameViewModel.SelectedGameMode.OFFLINE_VS_BOT -> "Battle the local dark core bot offline for casual training."
                        GameViewModel.SelectedGameMode.ONLINE_VS_BOT -> "Practice online with high performance AI bot hosted in cloud containers."
                        GameViewModel.SelectedGameMode.ONLINE_MATCHMAKING -> "Match with random online warriors peer-to-peer via secure channels."
                        GameViewModel.SelectedGameMode.COMPETITION_LEAGUE -> "Football-style league tables. First register, then play according to the fixture schedule."
                        GameViewModel.SelectedGameMode.COMPETITION_LADDER -> "Bottom-Up progression ladder. Defeat opponents in order to reach Rank 1."
                    },
                    color = TextGray,
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // --- COMPETITIONS DISPLAY EXPANSION PANEL ---
                if (viewModel.selectedGameMode == GameViewModel.SelectedGameMode.COMPETITION_LEAGUE) {
                    Divider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(vertical = 4.dp))
                    if (!viewModel.isRegisteredVanguardLeague) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1510)),
                                border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("🏆 COMPETITION ORGANISER WORKSHOP", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("Configure custom competition rules, registration fees, and rewards.", color = TextGray, fontSize = 8.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    // Name Input
                                    Text("Competition Name", color = TextWhite, fontSize = 8.sp)
                                    OutlinedTextField(
                                        value = viewModel.customCompetitionName,
                                        onValueChange = { viewModel.customCompetitionName = it },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextWhite,
                                            unfocusedTextColor = TextWhite,
                                            focusedBorderColor = AmberGold,
                                            unfocusedBorderColor = Color.Gray,
                                            focusedContainerColor = Color.Black,
                                            unfocusedContainerColor = Color.Black
                                        ),
                                        textStyle = TextStyle(fontSize = 10.sp),
                                        modifier = Modifier.fillMaxWidth().height(48.dp),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Rule System Selector
                                    Text("Baseline Rules System", color = TextWhite, fontSize = 8.sp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        val rulesOptions = listOf(
                                            DraughtsRuleSystem.AMERICAN_CHECKER_FEDERATION to "ACF (8x8)",
                                            DraughtsRuleSystem.ENGLISH_DRAUGHTS_ASSOCIATION to "EDA (8x8)",
                                            DraughtsRuleSystem.WORLD_DRAUGHTS_FEDERATION to "WDF (10x10)"
                                        )
                                        rulesOptions.forEach { (system, label) ->
                                            val isSelected = viewModel.customCompetitionRuleSystem == system
                                            Button(
                                                onClick = { viewModel.customCompetitionRuleSystem = system },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isSelected) AmberGold else Color.Black
                                                ),
                                                border = BorderStroke(1.dp, if (isSelected) AmberGold else Color.DarkGray),
                                                modifier = Modifier.weight(1f).height(28.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text(label, color = if (isSelected) DarkBg else TextWhite, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Direct Level Overrides", color = TextWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            Text("Allow scheduled matches to use different rulebooks", color = TextGray, fontSize = 7.sp)
                                        }
                                        Switch(
                                            checked = viewModel.customCompetitionLevelOverrides,
                                            onCheckedChange = { viewModel.customCompetitionLevelOverrides = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = AmberGold,
                                                checkedTrackColor = AmberGold.copy(alpha = 0.5f)
                                            ),
                                            modifier = Modifier.scale(0.7f)
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("RPG Combat Stats Engine", color = TextWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            Text("Enable custom HP, ATK and counter damage", color = TextGray, fontSize = 7.sp)
                                        }
                                        Switch(
                                            checked = viewModel.customCompetitionCombatDraughts,
                                            onCheckedChange = { viewModel.customCompetitionCombatDraughts = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = AmberGold,
                                                checkedTrackColor = AmberGold.copy(alpha = 0.5f)
                                            ),
                                            modifier = Modifier.scale(0.7f)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Registration Fee Selection
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Registration Fee", color = TextWhite, fontSize = 8.sp)
                                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                listOf(0, 50, 100, 250).forEach { fee ->
                                                    val isSelected = viewModel.customCompetitionRegistrationFee == fee
                                                    Button(
                                                        onClick = { viewModel.customCompetitionRegistrationFee = fee },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = if (isSelected) AmberGold else Color.DarkGray
                                                        ),
                                                        modifier = Modifier.weight(1f).height(20.dp),
                                                        contentPadding = PaddingValues(0.dp)
                                                    ) {
                                                        Text("${fee} B", color = if (isSelected) DarkBg else TextWhite, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }

                                        // Winner Prize Selection
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Winner Reward Pool", color = TextWhite, fontSize = 8.sp)
                                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                listOf(500, 1000, 1500, 2500).forEach { reward ->
                                                    val isSelected = viewModel.customCompetitionRewardCoins == reward
                                                    Button(
                                                        onClick = { viewModel.customCompetitionRewardCoins = reward },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = if (isSelected) AmberGold else Color.DarkGray
                                                        ),
                                                        modifier = Modifier.weight(1f).height(20.dp),
                                                        contentPadding = PaddingValues(0.dp)
                                                    ) {
                                                        Text("${reward} B", color = if (isSelected) DarkBg else TextWhite, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            viewModel.setAndSetupCompetition(
                                                viewModel.customCompetitionName,
                                                viewModel.customCompetitionRuleSystem,
                                                viewModel.customCompetitionCombatDraughts,
                                                viewModel.customCompetitionRegistrationFee,
                                                viewModel.customCompetitionRewardCoins,
                                                viewModel.customCompetitionLevelOverrides
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                                        modifier = Modifier.fillMaxWidth().height(32.dp)
                                    ) {
                                        Text("LAUNCH & SAVE COMPETITION", color = DarkBg, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Column(
                                modifier = Modifier.fillMaxWidth().background(Color(0xFF141F14), RoundedCornerShape(8.dp)).padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("📝 ENTER COMPETITION: ${viewModel.customCompetitionName.uppercase()}", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Registration Fee: ${viewModel.customCompetitionRegistrationFee} BLC Coins  |  Grand Reward: ${viewModel.customCompetitionRewardCoins} BLC", color = TextWhite, fontSize = 9.sp)
                                Text("Baseline Rules: ${viewModel.customCompetitionRuleSystem.displayName}", color = TextMuted, fontSize = 8.sp)
                                if (viewModel.customCompetitionLevelOverrides) {
                                    Text("⚠️ LEVEL OVERRIDES ALLOWED: Different stages utilize ACF, EDA, and WDF rules!", color = AmberGold, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { viewModel.registerForVanguardLeague() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("PAY FEE & REGISTER NOW", color = DarkBg, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF141F14)),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🏆 ACTIVE: ${viewModel.customCompetitionName}", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Box(
                                            modifier = Modifier.background(Color(0x3300E676), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("REGISTERED & PAID", color = Color(0xFF00E676), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Text("Reward Pool: ${viewModel.customCompetitionRewardCoins} BLC | Rules: ${viewModel.customCompetitionRuleSystem.displayName}", color = TextWhite, fontSize = 8.sp)
                                    if (viewModel.customCompetitionLevelOverrides) {
                                        Text("Notice: Levels use distinct rulesets. Prepare your strategies!", color = AmberGold, fontSize = 8.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text("League Fixtures & Brackets:", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))

                            viewModel.leagueMatchesState.forEach { match ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("${match.player1} VS ${match.player2}", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            val rulesText = match.ruleSystemOverride ?: viewModel.customCompetitionRuleSystem.displayName
                                            Text("Rulebook: $rulesText", color = AmberGold, fontSize = 8.sp)
                                            Text("Sched: ${match.scheduledTime} | Prize: ${match.reward}", color = TextGray, fontSize = 8.sp)
                                            if (match.status == "Completed") {
                                                Text("Outcome: Winner - ${match.winner ?: "Draw"}", color = Color(0xFF00E676), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        if (match.status != "Completed") {
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Button(
                                                    onClick = { viewModel.playLeagueMatch(match) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                                                    contentPadding = PaddingValues(horizontal = 6.dp),
                                                    modifier = Modifier.height(22.dp)
                                                ) {
                                                    Text("PLAY", color = DarkBg, fontSize = 8.sp, fontWeight = FontWeight.Black)
                                                }
                                                Button(
                                                    onClick = { viewModel.autoResolveMatch(match.id) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                                    contentPadding = PaddingValues(horizontal = 6.dp),
                                                    modifier = Modifier.height(22.dp)
                                                ) {
                                                    Text("AUTO-WIN SIM", color = Color.White, fontSize = 8.sp)
                                                }
                                            }
                                        } else {
                                            Icon(Icons.Default.CheckCircle, contentDescription = "Finished", tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (viewModel.selectedGameMode == GameViewModel.SelectedGameMode.COMPETITION_LADDER) {
                    Divider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(vertical = 4.dp))
                    if (!viewModel.isRegisteredGladiatorLadder) {
                        Column(
                            modifier = Modifier.fillMaxWidth().background(Color(0xFF1E1510), RoundedCornerShape(8.dp)).padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("ORGANISER PRIZE POOL: 2,500 BLC + TROPHY", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Start at bottom Tier V. Win to progress. If offline, simulate automatic walkover wins.", color = TextMuted, fontSize = 9.sp, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = { viewModel.registerForGladiatorLadder() },
                                colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("REGISTER FOR BRACKETS", color = DarkBg, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Gladiator Brackets Ascent Map:", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Active Tier Index: #${viewModel.currentLadderTierIndex + 1}", color = Color(0xFFFFB300), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            viewModel.ladderRoundsState.forEachIndexed { idx, tier ->
                                val isActive = idx == viewModel.currentLadderTierIndex
                                val isCleared = tier.isCompleted
                                
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isActive) Color(0xFF261D15) else if (isCleared) Color(0xFF131F14) else DarkSurfaceVariant
                                    ),
                                    border = BorderStroke(1.dp, if (isActive) AmberGold else Color.Transparent),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(tier.title, color = if (isActive) AmberGold else if (isCleared) Color(0xFF00E676) else TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text("Rival: ${tier.opponentName} | Prize: ${tier.prize}", color = TextGray, fontSize = 8.sp)
                                        }
                                        if (isCleared) {
                                            Text("CLEARED", color = Color(0xFF00E676), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        } else if (isActive) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Button(
                                                    onClick = { viewModel.playLadderMatchIndex(idx) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                                                    contentPadding = PaddingValues(horizontal = 6.dp),
                                                    modifier = Modifier.height(22.dp)
                                                ) {
                                                    Text("BATTLE", color = DarkBg, fontSize = 8.sp, fontWeight = FontWeight.Black)
                                                }
                                                Button(
                                                    onClick = { viewModel.completeLadderMatch(isVictory = true) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                                    contentPadding = PaddingValues(horizontal = 6.dp),
                                                    modifier = Modifier.height(22.dp)
                                                ) {
                                                    Text("WALKOVER WIN", color = Color.White, fontSize = 8.sp)
                                                }
                                            }
                                        } else {
                                            Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Divider(color = Color(0x11FFFFFF), modifier = Modifier.padding(vertical = 6.dp))

                // Action turn display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (viewModel.turnRed) RedCrimson else VioletNeon)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (viewModel.turnRed) "VANGUARD'S TURN (Red/Gold Heroes)" else "SHADOW CLAN'S TURN (Purple/Violet Bot)",
                        color = if (viewModel.turnRed) RedCrimson else VioletNeon,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    if (viewModel.isBotThinking) {
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(
                            color = VioletNeon,
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 1.5.dp
                        )
                    }
                }
            }
        }

        // Eaten Shadow Pieces (Shadow's Graveyard / Purple casualties)
        CapturedPiecesRow(
            title = "Shadow Graveyard",
            pieces = viewModel.capturedPieces.filter { !it.isRed },
            allianceColor = VioletNeon,
            tag = "shadow_graveyard"
        )

        Spacer(modifier = Modifier.height(4.dp))

        // The checkers board
        CheckersBoardComponent(viewModel, activeSkin, activeBoardStyle)

        Spacer(modifier = Modifier.height(4.dp))

        // Eaten Vanguard Pieces (Vanguard's Graveyard / Red casualties)
        CapturedPiecesRow(
            title = "Vanguard Graveyard",
            pieces = viewModel.capturedPieces.filter { it.isRed },
            allianceColor = RedCrimson,
            tag = "vanguard_graveyard"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Selected Piece Details HUD
        AnimatedVisibility(
            visible = viewModel.selectedPiece != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            viewModel.selectedPiece?.let { piece ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, if (piece.isRed) RedCrimson.copy(alpha = 0.5f) else VioletNeon.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DarkSurfaceVariant)
                                        .padding(4.dp)
                                ) {
                                    HeroDrawing(heroId = piece.heroId, isRed = piece.isRed)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = piece.name,
                                        color = TextWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Lv ${piece.level} ${piece.heroClass}${if (piece.isKing) " (PROMOTED CROWN HERO)" else ""}",
                                        color = if (piece.isRed) AmberGold else VioletNeon,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // HP gauge
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "HP: ${piece.hp}/${piece.maxHp}",
                                    color = HealthGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Box(
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(6.dp)
                                        .background(Color.DarkGray, RoundedCornerShape(3.dp))
                                ) {
                                    val percent = (piece.hp.toFloat() / piece.maxHp).coerceIn(0f, 1f)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(percent)
                                            .background(HealthGreen, RoundedCornerShape(3.dp))
                                    )
                                }
                            }
                        }

                        Divider(color = Color(0x11FFFFFF), modifier = Modifier.padding(vertical = 8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlashOn, contentDescription = "ATK", tint = RedCrimson, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ATK: ${piece.atk}", color = TextWhite, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, contentDescription = "DEF", tint = Color(0xFF29B6F6), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("DEF: ${piece.def}", color = TextWhite, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "Ability", tint = AmberGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(piece.abilityName, color = AmberGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = piece.abilityDescription,
                            color = TextGray,
                            fontSize = 11.sp,
                            style = androidx.compose.ui.text.TextStyle(lineHeight = 14.sp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.resetGame() },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).testTag("reset_board_button")
            ) {
                Icon(Icons.Default.RestartAlt, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset Board", fontSize = 12.sp)
            }

            Button(
                onClick = { viewModel.currentTab = GameTab.HEROES },
                colors = ButtonDefaults.buttonColors(containerColor = RedCrimson),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Upgrade, contentDescription = "Upgrade Heroes", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Upgrade Heroes", fontSize = 12.sp)
            }
        }

        // Victory banner
        viewModel.winnerMessage?.let { winnerMsg ->
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(2.dp, AmberGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = "Victory Trophy", tint = AmberGold, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        winnerMsg,
                        color = AmberGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "You've been rewarded with 75 BLC and 150 User XP!",
                        color = TextWhite,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.resetGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                    ) {
                        Text("New Match", color = DarkBg, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (viewModel.isOnlineMode) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // WebRTC Video & Voice Call Panel
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, Color(0x3300E676)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "WebRTC",
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "WebRTC Live Voice & Video",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Status tag
                        Box(
                            modifier = Modifier
                                .background(Color(0x3300E676), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "STABLE CONNECTION",
                                color = Color(0xFF00E676),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Voice and video grids (Simulated avatar circles or cameras!)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // User Stream Camera Frame
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black)
                                    .border(1.dp, if (viewModel.isCameraEnabled) Color(0xFF00E676) else Color.DarkGray, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (viewModel.isCameraEnabled) {
                                    // Simulated Webcam Feed (Moving particles/glow)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xFF1B5E20)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("LIVE Cam", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Icon(Icons.Default.VideocamOff, contentDescription = "Off", tint = Color.Gray, modifier = Modifier.size(24.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("You (Daniel)", color = TextGray, fontSize = 10.sp)
                        }
                        
                        // Opponent Stream Camera Frame
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black)
                                    .border(1.dp, Color(0xFF00E676), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                // Rival always has webcam active
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF311B92)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("RIVAL Feed", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(viewModel.onlineOpponentName, color = TextGray, fontSize = 10.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Streaming controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(
                            onClick = { viewModel.isMicMuted = !viewModel.isMicMuted },
                            modifier = Modifier
                                .size(34.dp)
                                .background(if (viewModel.isMicMuted) Color.DarkGray else Color(0x22FFFFFF), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (viewModel.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mute",
                                tint = if (viewModel.isMicMuted) RedCrimson else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        IconButton(
                            onClick = { viewModel.isCameraEnabled = !viewModel.isCameraEnabled },
                            modifier = Modifier
                                .size(34.dp)
                                .background(if (!viewModel.isCameraEnabled) Color.DarkGray else Color(0x22FFFFFF), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (viewModel.isCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                contentDescription = "Camera",
                                tint = if (viewModel.isCameraEnabled) Color(0xFF00E676) else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        IconButton(
                            onClick = { viewModel.endOnlineMatch() },
                            modifier = Modifier
                                .size(34.dp)
                                .background(RedCrimson, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "Disconnect Match",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Real-time Chat Console Composable
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, Color(0x11FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "MATCH CHAT ROOM",
                        color = AmberGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    
                    // Messages lazy list
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .background(Color.Black, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(viewModel.chatMessages) { msg ->
                                Text(
                                    text = "${msg.first}: ${msg.second}",
                                    color = when (msg.first) {
                                        "You" -> AmberGold
                                        "System" -> Color(0xFF00E676)
                                        else -> VioletNeon
                                    },
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Input row
                    var chatInputText by remember { mutableStateOf("") }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatInputText,
                            onValueChange = { chatInputText = it },
                            placeholder = { Text("Send strategy taunt...", fontSize = 11.sp, color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AmberGold.copy(alpha = 0.5f),
                                unfocusedBorderColor = Color(0x22FFFFFF),
                                focusedContainerColor = DarkBg,
                                unfocusedContainerColor = DarkBg
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                            modifier = Modifier.weight(1f).height(46.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                if (chatInputText.isNotBlank()) {
                                    viewModel.sendChatMessage(chatInputText)
                                    chatInputText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = DarkBg, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CapturedPiecesRow(
    title: String,
    pieces: List<BoardPiece>,
    allianceColor: Color,
    tag: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x11000000)),
        border = BorderStroke(1.dp, allianceColor.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                color = allianceColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.width(105.dp)
            )

            if (pieces.isEmpty()) {
                Text(
                    text = "No casualties yet.",
                    color = Color.Gray.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pieces.forEach { piece ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0x22FFFFFF))
                                .border(1.dp, allianceColor.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(modifier = Modifier.size(20.dp)) {
                                HeroDrawing(heroId = piece.heroId, isRed = piece.isRed)
                            }
                            // Skull badge
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .align(Alignment.BottomEnd)
                                    .background(Color.Red, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("☠", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckersBoardComponent(
    viewModel: GameViewModel,
    skin: String,
    boardStyle: String
) {
    // Styling attributes based on selected skin & board style
    val lightSquareColor = when (boardStyle) {
        "royal" -> Color(0xFF5E35B1)
        "neon_grid" -> Color(0xFF0D1B2A)
        else -> GridLightSquare
    }

    val darkSquareColor = when (boardStyle) {
        "royal" -> Color(0xFF311B92)
        "neon_grid" -> Color(0xFF04080F)
        else -> GridDarkSquare
    }

    val boardBorder = when (skin) {
        "neon" -> BorderStroke(2.dp, VioletNeon)
        "cyberpunk" -> BorderStroke(2.dp, AmberGold)
        else -> BorderStroke(3.dp, DarkSurfaceVariant)
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(8.dp))
            .border(boardBorder, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(darkSquareColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            val size = viewModel.ruleSystem.boardSize
            for (row in 0 until size) {
                Row(modifier = Modifier.weight(1f)) {
                    for (col in 0 until size) {
                        val isDarkSquare = (row + col) % 2 == 1
                        val squareBg = if (isDarkSquare) darkSquareColor else lightSquareColor

                        val pieceOnSquare = viewModel.boardPieces.firstOrNull { it.row == row && it.col == col }
                        val isSelected = viewModel.selectedPiece?.id == pieceOnSquare?.id && pieceOnSquare != null

                        // Check if this square is a highlight target
                        val isTargetMove = viewModel.validMoves.contains(Pair(row, col))
                        val isTargetJump = viewModel.validJumps.contains(Pair(row, col))

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(squareBg)
                                .clickable(enabled = isDarkSquare) {
                                    if (pieceOnSquare != null) {
                                        viewModel.selectPiece(pieceOnSquare)
                                    } else if (isTargetMove || isTargetJump) {
                                        viewModel.moveSelectedPiece(row, col)
                                    }
                                }
                                .testTag("square_${row}_${col}"),
                            contentAlignment = Alignment.Center
                        ) {
                            // Target highlights
                            if (isTargetMove) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(GridHighlight)
                                )
                            }
                            if (isTargetJump) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .border(2.dp, RedCrimson, CircleShape)
                                        .background(GridAttackHighlight)
                                )
                            }

                            // Render piece
                            if (pieceOnSquare != null) {
                                val pieceColor = if (pieceOnSquare.isRed) RedCrimson else VioletNeon
                                val glowColor = if (pieceOnSquare.isRed) AmberGold else Color.Cyan

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(0.85f)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(
                                                    pieceColor,
                                                    pieceColor.copy(alpha = 0.8f),
                                                    DarkBg
                                                )
                                            )
                                        )
                                        .border(
                                            width = if (isSelected) 3.dp else 1.5.dp,
                                            color = if (isSelected) glowColor else pieceColor.copy(alpha = 0.5f),
                                            shape = CircleShape
                                        )
                                        .padding(if (pieceOnSquare.isKing) 6.dp else 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Custom Pixel Character Drawing
                                    HeroDrawing(heroId = pieceOnSquare.heroId, isRed = pieceOnSquare.isRed)

                                    // Crown indicator if King promoted
                                    if (pieceOnSquare.isKing) {
                                        Box(
                                            modifier = Modifier
                                                .size(14.dp)
                                                .align(Alignment.TopEnd)
                                                .background(AmberGold, CircleShape)
                                                .border(1.dp, Color.White, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.KeyboardDoubleArrowUp,
                                                contentDescription = "King",
                                                tint = DarkBg,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }

                                    // Dynamic mini health bar on bottom of the piece
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .height(3.5.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(1.dp))
                                            .align(Alignment.BottomCenter)
                                    ) {
                                        val ratio = (pieceOnSquare.hp.toFloat() / pieceOnSquare.maxHp).coerceIn(0f, 1f)
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(ratio)
                                                .background(HealthGreen, RoundedCornerShape(1.dp))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== SCREEN 2: HEROES PROGRESSION ====================
@Composable
fun HeroesScreen(viewModel: GameViewModel) {
    val heroList by viewModel.heroes.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val coinCount = playerState?.draughtCoins ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "HERO PROGRESSION HALL",
            color = AmberGold,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(heroList) { hero ->
                val upgradeCost = hero.level * 80
                val canAfford = coinCount >= upgradeCost

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, if (hero.faction == "Vanguard") RedCrimson.copy(alpha = 0.3f) else VioletNeon.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkSurfaceVariant)
                                        .padding(6.dp)
                                ) {
                                    HeroDrawing(heroId = hero.id, isRed = hero.faction == "Vanguard")
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = hero.name,
                                        color = TextWhite,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .background(if (hero.faction == "Vanguard") RedCrimson else VioletNeon, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = hero.faction.uppercase(),
                                                color = TextWhite,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Lv ${hero.level} ${hero.heroClass}",
                                            color = AmberGold,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            // Upgrade Action Triggering Blockchain Block Mine!
                            Button(
                                onClick = { viewModel.upgradeHero(hero) },
                                enabled = canAfford,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AmberGold,
                                    disabledContainerColor = DarkSurfaceVariant
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("upgrade_${hero.id}")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "UPGRADE",
                                        color = if (canAfford) DarkBg else TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "$upgradeCost BLC",
                                        color = if (canAfford) DarkBg.copy(alpha = 0.8f) else TextMuted,
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = hero.description, color = TextGray, fontSize = 11.sp, style = androidx.compose.ui.text.TextStyle(lineHeight = 14.sp))

                        Divider(color = Color(0x11FFFFFF), modifier = Modifier.padding(vertical = 10.dp))

                        // Stats Comparison
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("HEALTH", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                Text("${hero.maxHp} HP", color = HealthGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Column {
                                Text("ATTACK", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                Text("${hero.atk} ATK", color = RedCrimson, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Column {
                                Text("ARMOR DEF", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                Text("${hero.def} DEF", color = Color(0xFF29B6F6), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkSurfaceVariant, RoundedCornerShape(4.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AmberGold, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ABILITY: ${hero.abilityName}", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(hero.abilityDescription, color = TextGray, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== SCREEN 3: COSMETIC STORE & BLOCKCHAIN LEDGER ====================
@Composable
fun StoreScreen(viewModel: GameViewModel) {
    val ledgerList by viewModel.ledger.collectAsState()
    val state by viewModel.playerState.collectAsState()
    val coinCount = state?.draughtCoins ?: 0
    val unlockedSkins = state?.unlockedSkins?.split(",") ?: listOf("classic")
    val selectedSkin = state?.selectedSkin ?: "classic"
    val selectedBoard = state?.selectedBoardStyle ?: "classic"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "COSMETIC ARENA BAZAAR",
                color = AmberGold,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Purchase premium board and piece aesthetics. Every transaction mines a cryptographically secure block into the local ledger chain.",
                color = TextGray,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Cosmetics Roster (Skins)
        item {
            Text("PIECE SKINS", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(bottom = 6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    Triple("classic", "Classic Slate", 0),
                    Triple("neon", "Neon Glitch", 150),
                    Triple("cyberpunk", "Cyber Void", 250)
                ).forEach { (skinId, name, cost) ->
                    val isUnlocked = unlockedSkins.contains(skinId)
                    val isEquipped = selectedSkin == skinId

                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(
                            1.5.dp,
                            if (isEquipped) AmberGold else if (isUnlocked) TextMuted else Color(0x11FFFFFF)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (isUnlocked) {
                                    viewModel.selectSkin(skinId)
                                } else if (coinCount >= cost) {
                                    viewModel.purchaseSkin(skinId, cost)
                                }
                            }
                            .testTag("skin_$skinId")
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (skinId == "neon") VioletNeon else if (skinId == "cyberpunk") Color.Cyan else RedCrimson),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (isEquipped) Icons.Default.CheckCircle else Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = TextWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(name, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(4.dp))

                            if (isEquipped) {
                                Text("ACTIVE", color = AmberGold, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            } else if (isUnlocked) {
                                Text("EQUIP", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("$cost", color = AmberGold, fontSize = 9.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("BLC", color = TextGray, fontSize = 8.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Cosmetics Roster (Boards)
        item {
            Text("BOARD THEMES", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(bottom = 6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    Triple("classic", "Slate", 0),
                    Triple("royal", "Amethyst", 120),
                    Triple("neon_grid", "Tron Cyber", 200)
                ).forEach { (styleId, name, cost) ->
                    val isEquipped = selectedBoard == styleId
                    val isUnlocked = styleId == "classic" || coinCount >= cost // Simplification for board styles

                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.5.dp, if (isEquipped) AmberGold else Color(0x11FFFFFF)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (isUnlocked) {
                                    viewModel.selectBoardStyle(styleId)
                                }
                            }
                            .testTag("board_$styleId")
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (styleId == "royal") Color(0xFF311B92) else if (styleId == "neon_grid") Color(0xFF0D1B2A) else GridDarkSquare),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.GridOn, contentDescription = null, tint = TextWhite.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(name, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(4.dp))

                            if (isEquipped) {
                                Text("EQUIPPED", color = AmberGold, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            } else {
                                Text("SELECT", color = Color.Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Ledger Block Explorer
        item {
            Divider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(vertical = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("LOCAL BLOCKCHAIN LEDGER", color = AmberGold, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    Text("Total Chain Blocks: ${ledgerList.size}", color = TextGray, fontSize = 10.sp)
                }
                Button(
                    onClick = { viewModel.verifyBlockchainLedger() },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    if (viewModel.isVerifyingBlockchain) {
                        CircularProgressIndicator(color = AmberGold, modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp)
                    } else {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = AmberGold, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verify Chain", color = AmberGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Ledger verify results banner
            viewModel.blockchainVerificationMessage?.let { banner ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (banner.startsWith("SECURE")) Color(0x1F00E676) else Color(0x1FDD2C00)),
                    border = BorderStroke(1.dp, if (banner.startsWith("SECURE")) Color(0xFF00E676) else Color(0xFFDD2C00)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (banner.startsWith("SECURE")) Icons.Default.Verified else Icons.Default.ReportProblem,
                            contentDescription = null,
                            tint = if (banner.startsWith("SECURE")) Color(0xFF00E676) else Color(0xFFDD2C00)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(banner, color = TextWhite, fontSize = 11.sp, fontFamily = FontFamily.SansSerif)
                    }
                }
            }
        }

        // Ledger block logs
        items(ledgerList.reversed()) { block ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(0.5.dp, Color(0x11FFFFFF))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF2E7D32), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("BLOCK #${block.blockNumber}", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(block.timestamp)),
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Nonce proof display
                        Text("NONCE: ${block.nonce}", color = AmberGold, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = block.transactions, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(6.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkBg, RoundedCornerShape(4.dp))
                            .padding(6.dp)
                    ) {
                        Row {
                            Text("PREV HASH: ", color = TextMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text(block.prevHash.take(16) + "...", color = TextGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        }
                        Row {
                            Text("CURR HASH: ", color = TextMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text(block.currentHash.take(16) + "...", color = AmberGold, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==================== SCREEN 4: LEADERS & GLOBAL RANKING ====================
@Composable
fun LeaderboardScreen(viewModel: GameViewModel) {
    val rankingList by viewModel.leaderboard.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "GLOBAL GRANDMASTER RANKINGS",
            color = AmberGold,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
        )

        if (viewModel.isOnlineLeaderboard) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Box(modifier = Modifier.size(6.dp).background(Color(0xFF00E676), CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text("LIVE — real players, updates as anyone plays", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Text(
                "Showing local-only placeholder rankings — sign in with Google (Profile tab) to appear on the real global leaderboard.",
                color = TextGray,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Text(
            text = "Earn MMR points by defeating opponents in single-player campaigns and rise to the apex ranking tier.",
            color = TextGray,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(rankingList) { entry ->
                val cardBorder = if (entry.isCurrentUser) {
                    BorderStroke(1.5.dp, AmberGold)
                } else if (entry.rank <= 3) {
                    BorderStroke(1.dp, Color(0xFF5E35B1).copy(alpha = 0.5f))
                } else null

                val rankIcon = when (entry.rank) {
                    1 -> Icons.Default.EmojiEvents to AmberGold
                    2 -> Icons.Default.EmojiEvents to Color(0xFFCFD8DC)
                    3 -> Icons.Default.EmojiEvents to Color(0xFFCD7F32)
                    else -> null
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (entry.isCurrentUser) DarkSurface else DarkSurfaceVariant
                    ),
                    border = cardBorder,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Rank number or Trophy
                            Box(
                                modifier = Modifier.size(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (rankIcon != null) {
                                    Icon(rankIcon.first, contentDescription = null, tint = rankIcon.second, modifier = Modifier.size(22.dp))
                                } else {
                                    Text(
                                        text = "${entry.rank}",
                                        color = TextGray,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Name & Preferred Hero Icon
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = entry.name,
                                        color = if (entry.isCurrentUser) AmberGold else TextWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (entry.isCurrentUser) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(AmberGold, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text("YOU", color = DarkBg, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("FAV HERO: ", color = TextMuted, fontSize = 9.sp)
                                    Text(entry.favoriteHero.replace("_", " ").uppercase(), color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // MMR & Winrate
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${entry.mmr} MMR",
                                color = AmberGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Win Rate: ${entry.winRate}%",
                                color = HealthGreen,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==================== SCREEN 5: SECURE CLOUD SYNCHRONIZATION ====================
@Composable
fun SyncScreen(viewModel: GameViewModel) {
    val state by viewModel.playerState.collectAsState()
    val syncTime = state?.lastSyncedTime ?: 0L
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SYSTEM CONFIGURATIONS & MASTER HUB",
            color = AmberGold,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Access Google Account, customise sound symphonies, select battle regulations, buy BLC packages, find online multiplayer games, or launch the admin console.",
            color = TextGray,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // SECTION 1: Google Account Authentication & Status
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "User Account",
                        tint = AmberGold,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (viewModel.isGoogleSignedIn) "Google Profile Linked" else "Guest Account Active",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (viewModel.isGoogleSignedIn) viewModel.signedInEmail ?: "Daniel Mukasa" else "Sign in to play online matches & save progress.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    
                    if (viewModel.isSigningInGoogle) {
                        CircularProgressIndicator(color = AmberGold, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else if (viewModel.isGoogleSignedIn) {
                        Button(
                            onClick = { viewModel.googleSignOut(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = RedCrimson),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Sign Out", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.isGoogleAuthDialogOpen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Login, contentDescription = "Google Sign In", tint = DarkBg, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Google Sign In", color = DarkBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // SECTION 2: Dynamic Sound & Orchestra Customization
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "SOUND & MUSIC PREFERENCES",
                    color = AmberGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Real file picker: lets the player pick literally any audio file from their
                // own device (Files app, Downloads, Google Drive, etc.) and use it as the
                // in-game music track. Persisted so it survives app restarts.
                val customSongPicker = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocument()
                ) { uri: android.net.Uri? ->
                    if (uri != null) {
                        try {
                            context.contentResolver.takePersistableUriPermission(
                                uri,
                                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                        } catch (_: SecurityException) {
                            // Some providers don't support persistable permissions — playback will
                            // still work for this session, it just might not survive a restart.
                        }
                        val displayName = queryFileDisplayName(context, uri) ?: "Custom Track"
                        viewModel.setCustomMusicTrack(context, uri, displayName)
                    }
                }

                // Track Selection Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Selected Track:", color = TextWhite, fontSize = 12.sp)
                    var expandedMusicDropdown by remember { mutableStateOf(false) }
                    Box {
                        Button(
                            onClick = { expandedMusicDropdown = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(viewModel.selectedMusicTrack, color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Track", tint = AmberGold, modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(
                            expanded = expandedMusicDropdown,
                            onDismissRequest = { expandedMusicDropdown = false },
                            modifier = Modifier.background(DarkSurface)
                        ) {
                            viewModel.bundledMusicTracks.forEach { track ->
                                DropdownMenuItem(
                                    text = { Text(track.displayName, color = TextWhite, fontSize = 12.sp) },
                                    onClick = {
                                        viewModel.changeMusicTrack(track)
                                        expandedMusicDropdown = false
                                    }
                                )
                            }
                            Divider(color = Color(0x1AFFFFFF))
                            DropdownMenuItem(
                                text = { Text("📁 Upload your own song...", color = AmberGold, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    expandedMusicDropdown = false
                                    customSongPicker.launch(arrayOf("audio/*"))
                                }
                            )
                        }
                    }
                }

                if (viewModel.customMusicUri != null) {
                    Text(
                        "🎵 Playing your own song: ${viewModel.customMusicName}",
                        color = Color(0xFF00E676),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Music Volume Slider & Moving Equalizer Bars!
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = "Music", tint = AmberGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Music Symphony Volume:", color = TextWhite, fontSize = 11.sp, modifier = Modifier.width(130.dp))
                    Slider(
                        value = viewModel.musicVolume,
                        onValueChange = { viewModel.musicVolume = it },
                        colors = SliderDefaults.colors(thumbColor = AmberGold, activeTrackColor = AmberGold),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${(viewModel.musicVolume * 100).toInt()}%", color = TextWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }

                // Sound Effects Volume Slider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Sound SFX", tint = AmberGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sound Effects SFX:", color = TextWhite, fontSize = 11.sp, modifier = Modifier.width(130.dp))
                    Slider(
                        value = viewModel.soundVolume,
                        onValueChange = { viewModel.soundVolume = it },
                        colors = SliderDefaults.colors(thumbColor = AmberGold, activeTrackColor = AmberGold),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${(viewModel.soundVolume * 100).toInt()}%", color = TextWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }

                // Animated Equalizer Visualizer Bars based on Volume
                Row(
                    modifier = Modifier.fillMaxWidth().height(20.dp).padding(top = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    repeat(12) { index ->
                        // Add animated height based on infinite transition loop or noise!
                        val infiniteTransition = rememberInfiniteTransition(label = "equalizer")
                        val heightAnim by infiniteTransition.animateFloat(
                            initialValue = 0.2f,
                            targetValue = 1.0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 400 + (index * 70), easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "height"
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .width(4.dp)
                                .fillMaxHeight(heightAnim * viewModel.musicVolume)
                                .background(if (index % 2 == 0) AmberGold else RedCrimson)
                        )
                    }
                }
            }
        }

        // SECTION 3: Select Game Board Rules Selector
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "OFFICIAL DRAUGHTS RULEBOOK SELECTOR",
                    color = AmberGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = "Select an official rulebook. The board layout, piece counts, and move directions dynamically adjust to match international federation regulations.",
                    color = TextGray,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // segment buttons
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val rulebooks = listOf(
                        Triple(DraughtsRuleSystem.AMERICAN_CHECKER_FEDERATION, "ACF (8x8)", "American ACF"),
                        Triple(DraughtsRuleSystem.ENGLISH_DRAUGHTS_ASSOCIATION, "EDA (8x8)", "English EDA"),
                        Triple(DraughtsRuleSystem.WORLD_DRAUGHTS_FEDERATION, "FMJD (10x10)", "World FMJD")
                    )
                    rulebooks.forEach { (system, abbrev, _) ->
                        val isSelected = viewModel.ruleSystem == system
                        Button(
                            onClick = { viewModel.selectRuleSystem(system) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) AmberGold else Color.Black
                            ),
                            border = BorderStroke(1.dp, if (isSelected) AmberGold else Color.DarkGray),
                            modifier = Modifier.weight(1f).height(44.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text(
                                    text = abbrev,
                                    color = if (isSelected) DarkBg else TextWhite,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = if (system == DraughtsRuleSystem.WORLD_DRAUGHTS_FEDERATION) "10x10 Board" else "8x8 Board",
                                    color = if (isSelected) DarkBg.copy(alpha = 0.7f) else TextGray,
                                    fontSize = 7.sp
                                )
                            }
                        }
                    }
                }

                // Rulebook Description
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    border = BorderStroke(1.dp, Color(0x11FFFFFF)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = when (viewModel.ruleSystem) {
                                DraughtsRuleSystem.AMERICAN_CHECKER_FEDERATION ->
                                    "American Checker Federation (ACF):\n" +
                                    " • Plays on standard 8x8 Board.\n" +
                                    " • Men move forward only, but can JUMP forward or backward.\n" +
                                    " • Kings are NOT sliding (move/jump 1 square max).\n" +
                                    " • A man promoted to King mid-capture stops immediately, even if more jumps exist.\n" +
                                    " • Jumps are mandatory (Forced Jumps: ON)."
                                DraughtsRuleSystem.ENGLISH_DRAUGHTS_ASSOCIATION ->
                                    "English Draughts Association (EDA):\n" +
                                    " • Plays on standard 8x8 Board.\n" +
                                    " • Same core rules as ACF: men jump forward or backward, kings move one square.\n" +
                                    " • Traditional Go-As-You-Please (GAYP) rules, with EDA's official 3-move tournament openings.\n" +
                                    " • A man promoted to King mid-capture stops immediately, even if more jumps exist."
                                DraughtsRuleSystem.WORLD_DRAUGHTS_FEDERATION ->
                                    "World Draughts Federation (FMJD):\n" +
                                    " • Plays on expanded 10x10 Board (20 pieces each!).\n" +
                                    " • Men move forward only, but can jump both FORWARD and BACKWARD.\n" +
                                    " • Kings are FLYING KINGS (can slide diagonally any distance).\n" +
                                    " • A man promoted to King mid-capture keeps going in the SAME turn — it continues\n" +
                                    "   capturing as a flying king if more jumps are available (unlike ACF/EDA).\n" +
                                    " • Jumps are mandatory (Forced Jumps: ON)."
                            },
                            color = TextGray,
                            fontSize = 10.sp,
                            lineHeight = 13.sp
                        )
                    }
                }

                // EDA Tournament Opening Generator button if EDA selected
                if (viewModel.ruleSystem == DraughtsRuleSystem.ENGLISH_DRAUGHTS_ASSOCIATION) {
                    Button(
                        onClick = { viewModel.drawEdaOpening() },
                        colors = ButtonDefaults.buttonColors(containerColor = VioletNeon),
                        modifier = Modifier.fillMaxWidth().height(36.dp).padding(bottom = 4.dp)
                    ) {
                        Icon(Icons.Default.Casino, contentDescription = "Draw EDA Opening", tint = TextWhite, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Draw EDA 3-Move Opening", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "RPG CUSTOM REGULATIONS",
                    color = AmberGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // 1. Combat Draughts
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("RPG Combat Draughts Mode", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Enables stats, unique skills, and visual collision skirmishes when jumping pieces.", color = TextGray, fontSize = 10.sp)
                    }
                    Switch(
                        checked = viewModel.ruleCombatDraughts,
                        onCheckedChange = { viewModel.ruleCombatDraughts = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AmberGold, checkedTrackColor = AmberGold.copy(alpha = 0.4f))
                    )
                }

                // 2. Forced Jumps Rule
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Forced Jumps Rule", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Traditional checkers rule where players must execute jump captures if available.", color = TextGray, fontSize = 10.sp)
                    }
                    Switch(
                        checked = viewModel.ruleForcedJumps,
                        onCheckedChange = { viewModel.ruleForcedJumps = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AmberGold, checkedTrackColor = AmberGold.copy(alpha = 0.4f))
                    )
                }

                // 3. Flying Kings
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Flying (Sliding) Kings Rule", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Kings can fly diagonally across multiple empty tiles and capture distant pieces.", color = TextGray, fontSize = 10.sp)
                    }
                    Switch(
                        checked = viewModel.ruleFlyingKings,
                        onCheckedChange = { viewModel.ruleFlyingKings = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AmberGold, checkedTrackColor = AmberGold.copy(alpha = 0.4f))
                    )
                }
            }
        }

        // SECTION 4: Online Lobby, Matchmaking, Competitions & WebRTC
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, if (viewModel.isOnlineMode) Color(0xFF00E676) else Color(0x1AFFFFFF)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "2-PLAYER ONLINE LOBBY & ARENA",
                    color = AmberGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (viewModel.isOnlineMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Active Battle Active!", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Rival: ${viewModel.onlineOpponentName}", color = TextWhite, fontSize = 11.sp)
                            Text("Comp Code: ${viewModel.activeOnlineCompetitionId}", color = TextMuted, fontSize = 10.sp)
                        }
                        Button(
                            onClick = { viewModel.endOnlineMatch() },
                            colors = ButtonDefaults.buttonColors(containerColor = RedCrimson),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Disconnect", fontSize = 10.sp)
                        }
                    }
                } else if (viewModel.isSearchingOnlineMatch) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = AmberGold, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "WebRTC Signaling: " + viewModel.webRtcStatus,
                            color = AmberGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text("Connecting Peer-to-Peer with global checkmates...", color = TextGray, fontSize = 10.sp)
                    }
                } else {
                    Text(
                        "Test your tactical hero skills against rival players on secure global matchmaking channels with voice calls enabled over WebRTC connection streams.",
                        color = TextGray,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Button(
                        onClick = { 
                            if (!viewModel.isGoogleSignedIn) {
                                viewModel.isGoogleAuthDialogOpen = true
                            } else {
                                viewModel.startOnlineMatchmaking() 
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (viewModel.isGoogleSignedIn) AmberGold else Color.DarkGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Group, contentDescription = "Online Play", tint = if (viewModel.isGoogleSignedIn) DarkBg else Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (viewModel.isGoogleSignedIn) "FIND MULTIPLAYER MATCH" else "LINK GOOGLE FIRST",
                            color = if (viewModel.isGoogleSignedIn) DarkBg else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color(0x11FFFFFF))
                Spacer(modifier = Modifier.height(8.dp))

                // Ongoing Competitions
                Text("HISTORIC COMPETITIONS & SPECTATOR HUB", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                
                // Comp item 1
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Vanguard Cup: Daniel vs LordX", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Winnerdetermined: Daniel Mukasa (+200 BLC)", color = Color(0xFF00E676), fontSize = 9.sp)
                        }
                        Box(
                            modifier = Modifier
                                .background(Color(0x3300E676), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("COMPLETED", color = Color(0xFF00E676), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Comp item 2
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Shadow Tournament: Grandmaster vs V-Hero", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Active match - 245 Spectators", color = TextGray, fontSize = 9.sp)
                        }
                        Box(
                            modifier = Modifier
                                .background(Color(0x33FFB300), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("LIVE SPECTATE", color = Color(0xFFFFB300), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // SECTION 5: In-App Purchases (IAP) Revenue Generation Store
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "IN-APP PURCHASE STORE (BLC COINS)",
                    color = AmberGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Support development and buy digital blockchain coins packages below. Transactions are logged securely.",
                    color = TextGray,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Render 3 purchase items
                // NOTE: prices are now in UGX, not GBP — Relworx does no currency conversion and
                // only supports UGX/KES/TZS mobile money. Adjust these to your real pricing.
                val packages = listOf(
                    Triple("Gladiator Sack", "+500 BLC", "5,000 UGX"),
                    Triple("Vanguard Chest", "+2,000 BLC", "15,000 UGX"),
                    Triple("Emperor's Vault", "+10,000 BLC", "50,000 UGX")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    packages.forEach { pkg ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            border = BorderStroke(1.dp, Color(0x11FFFFFF)),
                            modifier = Modifier.weight(1f).clickable {
                                val coinsReward = when (pkg.first) {
                                    "Gladiator Sack" -> 500
                                    "Vanguard Chest" -> 2000
                                    else -> 10000
                                }
                                val amountUgx = when (pkg.first) {
                                    "Gladiator Sack" -> 5000.0
                                    "Vanguard Chest" -> 15000.0
                                    else -> 50000.0
                                }
                                viewModel.openPaymentPortal(pkg.first, pkg.third, coinsReward, amountUgx)
                            }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Savings, contentDescription = "Sack", tint = AmberGold, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(pkg.first, color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                Text(pkg.second, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Black)
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(AmberGold, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(pkg.third, color = DarkBg, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }
        }

        // SECTION 6: Notification Subscriptions Preferences
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "NOTIFICATION CHANNELS",
                    color = AmberGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Push notifications
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("All Push Notifications", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Enables sound prompts on main menu.", color = TextGray, fontSize = 10.sp)
                    }
                    Switch(
                        checked = viewModel.notificationsEnabled,
                        onCheckedChange = { viewModel.notificationsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AmberGold, checkedTrackColor = AmberGold.copy(alpha = 0.4f))
                    )
                }

                // Push invitations
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Challenge & Battle Invites", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Receive dynamic invitations from other tournament guilds.", color = TextGray, fontSize = 10.sp)
                    }
                    Switch(
                        checked = viewModel.challInvitesNotifications,
                        onCheckedChange = { viewModel.challInvitesNotifications = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AmberGold, checkedTrackColor = AmberGold.copy(alpha = 0.4f))
                    )
                }

                // Push receipts
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("IAP Transaction Receipts", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Show cryptographic ledger recipes for coin grants.", color = TextGray, fontSize = 10.sp)
                    }
                    Switch(
                        checked = viewModel.iapTransactionReceipts,
                        onCheckedChange = { viewModel.iapTransactionReceipts = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AmberGold, checkedTrackColor = AmberGold.copy(alpha = 0.4f))
                    )
                }
            }
        }

        // SECTION 7: Secure Cloud Backup Card (original Sync Card, adapted)
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CloudSync,
                        contentDescription = null,
                        tint = if (syncTime > 0) Color(0xFF00E676) else AmberGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "SECURE CLOUD SAVE GATEWAY",
                        color = AmberGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (syncTime > 0) "STATUS: FULLY BACKED UP" else "STATUS: UNPLAYED BACKUP",
                    color = if (syncTime > 0) Color(0xFF00E676) else AmberGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.align(Alignment.Start)
                )

                if (syncTime > 0) {
                    Text(
                        text = "Last synced: " + SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(syncTime)),
                        color = TextGray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 2.dp).align(Alignment.Start)
                    )
                } else {
                    Text(
                        text = "Never backed up. Sync now to secure your progress.",
                        color = TextGray,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 2.dp).align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.startCloudSync() },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                    modifier = Modifier.fillMaxWidth().testTag("sync_button")
                ) {
                    if (viewModel.isSyncingCloud) {
                        CircularProgressIndicator(color = DarkBg, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = DarkBg, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("UPLOAD TO CLOUD SERVER", color = DarkBg, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // SECTION 8: Blockchain Ledger Logger
        Text(
            "CRYPTOGRAPHIC TRANSACTION LEDGER", 
            color = TextWhite, 
            fontSize = 12.sp, 
            fontWeight = FontWeight.Bold, 
            modifier = Modifier.align(Alignment.Start).padding(bottom = 6.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color.Black, RoundedCornerShape(8.dp))
                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            val currentLedger by viewModel.ledger.collectAsState()
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(currentLedger.reversed()) { block ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = "BLOCK #${block.blockNumber} - Hash: ${block.currentHash.take(8)}... Prev: ${block.prevHash.take(8)}...",
                            color = AmberGold,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Tx: ${block.transactions}",
                            color = TextWhite,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // SECTION 8.5: ADVANCED APP SECURITY & CRYPTOGRAPHIC SUITE
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E19)),
            border = BorderStroke(1.dp, Color(0xFF00E676)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = "Security", tint = Color(0xFF00E676), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "ANTI-REVERSE ENGINEERING & CRYPTO PROTECTION",
                        color = Color(0xFF00E676),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Text(
                    "Configured parameters shielding the source code, on-device database, and transit handshakes from decompilers (e.g. JADX, apktool, and proxy sniffers).",
                    color = TextGray,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                // Protection 1: SQLCipher Database Encryption
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("SQLite SQLCipher AES-256 (On-Device Data)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(modifier = Modifier.background(if (viewModel.isDatabaseEncrypted) Color(0x3300E676) else Color(0x33FF5252), RoundedCornerShape(2.dp)).padding(horizontal = 4.dp)) {
                                Text(if (viewModel.isDatabaseEncrypted) "ENCRYPTED" else "PLAIN TEXT", color = if (viewModel.isDatabaseEncrypted) Color(0xFF00E676) else Color(0xFFFF5252), fontSize = 7.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text("Cryptographically encrypts on-device AppDatabase. JADX/SQLite viewers cannot read tables.", color = TextGray, fontSize = 9.sp)
                    }
                    Switch(
                        checked = viewModel.isDatabaseEncrypted,
                        onCheckedChange = { viewModel.toggleDatabaseEncryption() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676), checkedTrackColor = Color(0xFF00E676).copy(alpha = 0.4f))
                    )
                }

                // Protection 2: SSL/TLS Pinning with Payload AES-GCM (In Transit)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("SSL Pinning & AES-GCM (Transit Exchange)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(modifier = Modifier.background(if (viewModel.isTransitEncrypted) Color(0x3300E676) else Color(0x33FF5252), RoundedCornerShape(2.dp)).padding(horizontal = 4.dp)) {
                                Text(if (viewModel.isTransitEncrypted) "SECURE" else "VULNERABLE", color = if (viewModel.isTransitEncrypted) Color(0xFF00E676) else Color(0xFFFF5252), fontSize = 7.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text("Binds transit packets to verified certificates. MITM proxy interceptors fail.", color = TextGray, fontSize = 9.sp)
                    }
                    Switch(
                        checked = viewModel.isTransitEncrypted,
                        onCheckedChange = { viewModel.toggleTransitEncryption() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676), checkedTrackColor = Color(0xFF00E676).copy(alpha = 0.4f))
                    )
                }

                // Protection 3: R8 / ProGuard Rules (Source Code Obfuscation)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("R8 ProGuard Code Obfuscation (Source Code)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(modifier = Modifier.background(if (viewModel.isCodeObfuscated) Color(0x3300E676) else Color(0x33FF5252), RoundedCornerShape(2.dp)).padding(horizontal = 4.dp)) {
                                Text(if (viewModel.isCodeObfuscated) "OBFUSCATED" else "RAW SOURCE", color = if (viewModel.isCodeObfuscated) Color(0xFF00E676) else Color(0xFFFF5252), fontSize = 7.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text("Scrambles class definitions & package names. Decompiling reveals illegible a.b.c bytecode.", color = TextGray, fontSize = 9.sp)
                    }
                    Switch(
                        checked = viewModel.isCodeObfuscated,
                        onCheckedChange = { viewModel.toggleCodeObfuscation() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676), checkedTrackColor = Color(0xFF00E676).copy(alpha = 0.4f))
                    )
                }

                // Protection 4: Tamper signature protection
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("DEX Keystore Signature Handshake (Anti-Tamper)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Validates package fingerprint on launch. Prevents modded APK injections.", color = TextGray, fontSize = 9.sp)
                    }
                    Switch(
                        checked = viewModel.isTamperProtectionActive,
                        onCheckedChange = { viewModel.toggleTamperProtection() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676), checkedTrackColor = Color(0xFF00E676).copy(alpha = 0.4f))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0x11FFFFFF))
                Spacer(modifier = Modifier.height(8.dp))

                // Run reverse engineering pentest simulation
                Button(
                    onClick = { viewModel.executeSecurityCryptographicAudit() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006633)),
                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("pentest_button")
                ) {
                    if (viewModel.isAuditRunning) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RUN REVERSE-ENGINEERING PENTEST AUDIT", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (viewModel.auditResultsLog.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .background(Color.Black, RoundedCornerShape(4.dp))
                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(viewModel.auditResultsLog) { log ->
                                Text(
                                    text = log,
                                    color = if (log.contains("[SECURE]")) Color(0xFF00E676) else if (log.contains("[CRITICAL]")) Color(0xFFFF5252) else if (log.contains("[INIT]")) AmberGold else Color.LightGray,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // SECTION 9: Remote Administration Control Console
        // Now that Google Sign-In is real, this check uses your genuine authenticated email —
        // but it's still enforced entirely on-device. It hides the panel from other players in
        // the normal app, but it is NOT real access control: anyone with a decompiler could
        // patch this condition out of a rebuilt APK and reach adminGrantCoins()/adminApplyModifier()
        // regardless of who's signed in, since nothing here is verified by a server. That's fine
        // for a single-player local economy (worst case, someone cheats their own save file), but
        // don't rely on this if coins ever back something server-authoritative like a shared
        // leaderboard or a cash-out feature.
        val isUserAdmin = viewModel.isGoogleSignedIn && viewModel.signedInEmail == "mukasadaniel.daniel@gmail.com"

        if (isUserAdmin) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0F0F)),
                border = BorderStroke(1.dp, RedCrimson),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SettingsRemote, contentDescription = "Admin", tint = RedCrimson, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "ADMINISTRATION CONTROL BACKEND",
                            color = RedCrimson,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        "Remotely modify gameplay states, inject user currency balances, and toggle physical game-rules rules instantly.",
                        color = TextGray,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Admin field 1: Grant Currency Balance
                    Text("Remote Currency Grant override:", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    var grantAmountText by remember { mutableStateOf("1000") }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = grantAmountText,
                            onValueChange = { grantAmountText = it },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = RedCrimson,
                                unfocusedBorderColor = Color.DarkGray,
                                focusedContainerColor = Color.Black,
                                unfocusedContainerColor = Color.Black
                            ),
                            modifier = Modifier.weight(1f).height(46.dp),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                val amt = grantAmountText.toIntOrNull() ?: 1000
                                viewModel.adminGrantCoins(amt)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RedCrimson),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Text("Grant Coins", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Admin field 2: Set Bot difficulty level
                    Text("Remote Bot Core level configuration:", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    var expandedDiffDropdown by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.padding(top = 4.dp)) {
                        Button(
                            onClick = { expandedDiffDropdown = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            border = BorderStroke(1.dp, Color.DarkGray),
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Text("BOT LEVEL: " + viewModel.adminCustomBotDifficulty, color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select", tint = AmberGold)
                        }
                        DropdownMenu(
                            expanded = expandedDiffDropdown,
                            onDismissRequest = { expandedDiffDropdown = false },
                            modifier = Modifier.background(Color.Black)
                        ) {
                            listOf("EASY (V1)", "MEDIUM (V2)", "HARD (V3)", "IMPOSSIBLE (V4)").forEach { lvl ->
                                DropdownMenuItem(
                                    text = { Text(lvl, color = TextWhite, fontSize = 11.sp) },
                                    onClick = {
                                        viewModel.adminCustomBotDifficulty = lvl
                                        viewModel.triggerNotification("Admin: Set Bot Difficulty to $lvl")
                                        expandedDiffDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Admin field 3: Global Game Modifier Injection
                    Text("Remote Global Mode modifier injection:", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val modifiersList = listOf("One Hit KO Mode", "1 HP Sudden Death", "Standard Default")
                        modifiersList.forEach { mode ->
                            Button(
                                onClick = { viewModel.adminApplyModifier(mode) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (viewModel.adminGlobalModifier == mode) RedCrimson else Color.Black
                                ),
                                border = BorderStroke(1.dp, if (viewModel.adminGlobalModifier == mode) RedCrimson else Color.DarkGray),
                                modifier = Modifier.weight(1f).height(34.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Text(
                                    text = mode.replace("Mode", "").replace("Default", "Reset"),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (viewModel.adminGlobalModifier == mode) Color.White else TextGray
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = "Admin locked", tint = TextMuted, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "REMOTE PARAMETERS CONSOLE LOCKED",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        "Access is restricted to authorized email accounts. Log in with mukasadaniel.daniel@gmail.com to activate remote controls.",
                        color = TextGray,
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}

// ==================== CINEMATIC COMBAT DISPLAY OVERLAY ====================
@Composable
fun CombatOverlay(combat: CombatState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SKIRMISH COLLISION!",
                color = AmberGold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attacker Card
                CombatCharacterCard(
                    piece = combat.attacker,
                    dmgReceived = combat.counterDamageDealt,
                    isAttacker = true,
                    abilityTriggered = combat.attackerSpecialTriggered,
                    died = combat.attackerDied
                )

                // VS separator
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(RedCrimson),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("VS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }

                // Defender Card
                CombatCharacterCard(
                    piece = combat.defender,
                    dmgReceived = combat.damageDealt,
                    isAttacker = false,
                    abilityTriggered = combat.defenderSpecialTriggered,
                    died = combat.defenderDied
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action narration ticker
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.5.dp, if (combat.attackerSpecialTriggered) AmberGold else Color(0x33FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "COMBAT ANALYSIS",
                        color = AmberGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = combat.description,
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun CombatCharacterCard(
    piece: BoardPiece,
    dmgReceived: Int,
    isAttacker: Boolean,
    abilityTriggered: Boolean,
    died: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(
            2.dp,
            if (abilityTriggered) AmberGold else if (died) Color.Red else if (isAttacker) RedCrimson else VioletNeon
        ),
        modifier = Modifier
            .width(130.dp)
            .shadow(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceVariant)
                    .padding(8.dp)
            ) {
                HeroDrawing(heroId = piece.heroId, isRed = piece.isRed)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = piece.name,
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = "Lv ${piece.level} ${piece.heroClass}",
                color = if (piece.isRed) AmberGold else VioletNeon,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Hit effects animations
            if (dmgReceived > 0) {
                Text(
                    text = "-$dmgReceived HP",
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                Text(
                    text = "NO DAMAGE",
                    color = HealthGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (abilityTriggered) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .background(AmberGold, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text("SKILL", color = DarkBg, fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
            }

            if (died) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .background(Color.Red, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("CAPTURED", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun ProfileManagementDialog(viewModel: GameViewModel) {
    val playerState by viewModel.playerState.collectAsState()
    var tempName by remember { mutableStateOf(playerState?.playerName ?: "") }
    val context = LocalContext.current

    Dialog(
        onDismissRequest = { viewModel.isProfileDialogOpen = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, AmberGold),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VANGUARD TACTICAL PROFILE",
                        color = AmberGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(
                        onClick = { viewModel.isProfileDialogOpen = false },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar and Frame Customizer Section
                Text(
                    text = "SELECT AVATAR CREST",
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                val avatars = listOf(
                    Triple("knight", "Knight Shield", RedCrimson),
                    Triple("mage", "Aether Mage", Color(0xFF9C27B0)),
                    Triple("valkyrie", "Valkyrie Grace", Color(0xFF00E676)),
                    Triple("assassin", "Shadow Keeper", Color(0xFFFF9100)),
                    Triple("rogue", "Cyber Rogue", Color(0xFF2979FF))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    avatars.forEach { avatar ->
                        val isSelected = viewModel.selectedAvatarId == avatar.first
                        val icon = when (avatar.first) {
                            "knight" -> Icons.Default.Shield
                            "mage" -> Icons.Default.AutoAwesome
                            "valkyrie" -> Icons.Default.LocalActivity
                            "assassin" -> Icons.Default.Security
                            "rogue" -> Icons.Default.Bolt
                            else -> Icons.Default.Person
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .weight(1f)
                                .clip(CircleShape)
                                .background(if (isSelected) avatar.third else DarkSurfaceVariant)
                                .border(
                                    2.dp,
                                    if (isSelected) AmberGold else Color(0x11FFFFFF),
                                    CircleShape
                                )
                                .clickable { viewModel.selectAvatar(avatar.first) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = avatar.second,
                                tint = if (isSelected) DarkBg else TextWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name Editor Section
                Text(
                    text = "EDIT PLAYER HANDLE",
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AmberGold,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedContainerColor = DarkBg,
                            unfocusedContainerColor = DarkBg
                        ),
                        modifier = Modifier.weight(1f).height(48.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (tempName.trim().length >= 3) {
                                viewModel.updatePlayerName(tempName.trim())
                            } else {
                                viewModel.triggerNotification("Name must be at least 3 characters!")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("SAVE", color = DarkBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Section
                Text(
                    text = "BATTLE STATISTICS",
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = BorderStroke(1.dp, Color(0x11FFFFFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Battles:", color = TextGray, fontSize = 11.sp)
                            Text("90 Played", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Roster Wins:", color = TextGray, fontSize = 11.sp)
                            Text("48 Wins (53.3%)", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Guild Defeats:", color = TextGray, fontSize = 11.sp)
                            Text("42 Losses", color = RedCrimson, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Elo Rating (MMR):", color = TextGray, fontSize = 11.sp)
                            Text("${playerState?.mmr ?: 1200} MMR", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Color(0x11FFFFFF))
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Rank Tier: Gold Vanguard III",
                            color = AmberGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        LinearProgressIndicator(
                            progress = { ((playerState?.mmr ?: 1200) - 1000).toFloat() / 1000f },
                            color = AmberGold,
                            trackColor = Color.DarkGray,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp).height(4.dp).clip(RoundedCornerShape(2.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Google Link Section
                Text(
                    text = "GOOGLE ACCOUNT ASSOCIATION",
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (viewModel.isGoogleSignedIn) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x1100E676), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0x3300E676), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Verified Account Linked", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(viewModel.signedInEmail ?: "Daniel Mukasa", color = TextWhite, fontSize = 10.sp)
                        }
                        Button(
                            onClick = { viewModel.googleSignOut(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = RedCrimson),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Unlink", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x0AFFFFFF), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0x11FFFFFF), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Guest Profile Active", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Connect Google to unlock online matchmaking.", color = TextMuted, fontSize = 9.sp)
                        }
                        Button(
                            onClick = {
                                viewModel.isGoogleAuthDialogOpen = true
                                viewModel.isProfileDialogOpen = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Login, contentDescription = "Google", tint = DarkBg, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Link Google", color = DarkBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.isProfileDialogOpen = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, Color.Gray),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Text("DISMISS DIALOG", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun GoogleAuthenticationDialog(viewModel: GameViewModel) {
    // This used to draw a fake "choose an account" list inside the app — that's a dangerous
    // pattern (it looks exactly like a phishing screen impersonating Google's real picker), so
    // it's gone. Real Google Sign-In shows Google's OWN system-rendered account picker via the
    // Credential Manager API; this dialog is now just a simple explainer + trigger button.
    val context = LocalContext.current

    Dialog(
        onDismissRequest = { if (!viewModel.isSigningInGoogle) viewModel.isGoogleAuthDialogOpen = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1216)),
            border = BorderStroke(1.dp, Color(0xFF2979FF)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "G",
                            color = Color(0xFF4285F4),
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Sign in with Google",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Link your Google account to save your progress and play online matches. You'll pick your account on Google's own sign-in screen.",
                    color = TextGray,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (viewModel.isSigningInGoogle) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFF4285F4), modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Waiting on Google...", color = AmberGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.startGoogleSignIn(context) {
                                viewModel.isGoogleAuthDialogOpen = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Text("CONTINUE WITH GOOGLE", color = Color(0xFF1F1F1F), fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.isGoogleAuthDialogOpen = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        border = BorderStroke(1.dp, Color.Gray),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Text("CANCEL", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==================== DIALOG 3: GAME RULES & HELP SYSTEM ====================
@Composable
fun GameRulesHelpDialog(viewModel: GameViewModel) {
    var activeTab by remember { mutableStateOf(0) } // 0 = Combat Rules, 1 = Upgrade Guide, 2 = Game Modes

    Dialog(
        onDismissRequest = { viewModel.isRulesDialogOpen = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            color = DarkSurface,
            border = BorderStroke(1.5.dp, AmberGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Help, contentDescription = null, tint = AmberGold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "TACTICAL RULEBOOK & MANUAL",
                            color = AmberGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    IconButton(onClick = { viewModel.isRulesDialogOpen = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextWhite)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0x1AFFFFFF))
                Spacer(modifier = Modifier.height(12.dp))

                // Tab Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val tabs = listOf("Combat Rules", "Upgrade Guide", "Modes Info")
                    tabs.forEachIndexed { index, title ->
                        Button(
                            onClick = { activeTab = index },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeTab == index) AmberGold else Color.Black
                            ),
                            border = BorderStroke(1.dp, if (activeTab == index) AmberGold else Color.DarkGray),
                            modifier = Modifier.weight(1f).height(36.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = title,
                                color = if (activeTab == index) DarkBg else TextWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Contents
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (activeTab) {
                        0 -> {
                            // COMBAT RULES
                            Text("1. WHAT IS COMBAT DRAUGHTS?", color = AmberGold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            Text(
                                "AI Studio Combat Draughts is an RPG-infused spin on classic checkers. Every piece on the board is a unique Hero containing specialized combat stats: Hit Points (HP), Attack (ATK), and Defense (DEF).",
                                color = TextWhite,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                            )

                            Text("2. THE TAKEOVER / CAPTURE SKIRMISH", color = AmberGold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            Text(
                                "In both classic and Combat modes, jumping over an opponent's piece instantly captures and removes it from the board, placing it in the graveyard:\n" +
                                " • Under Combat rules, the defender deals a final parting counter-attack before being captured (equal to 40% of its ATK minus the attacker's DEF).\n" +
                                " • This ensures the board is cleared dynamically, while still taking combat damage from heavy defenders!",
                                color = TextWhite,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                            )

                            Text("3. HOW TO PLAY CLASSICAL RULES?", color = AmberGold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            Text(
                                "If you prefer the standard, classic checkers rules where any jump results in an instant, guaranteed capture/deletion, simply go to the Sync / Settings tab and turn OFF 'Combat Draughts Mode'.",
                                color = TextWhite,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        1 -> {
                            // UPGRADE GUIDE
                            Text("WHY SHOULD YOU UPGRADE HEROES?", color = AmberGold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            Text(
                                "Since pieces engage in RPG combat on the board, upgraded stats are extremely important to victory:\n" +
                                " • HP Boost (+25): Allows your piece to survive critical jump skirmishes on the board and perform counter-attacks.\n" +
                                " • ATK Boost (+8): Boosts raw damage, letting you execute instant-kills on low-level opponent pieces.\n" +
                                " • DEF Boost (+2): Mitigates damage from enemy jumps and counter-blows, shielding your piece from attrition.",
                                color = TextWhite,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                            )

                            Text("HOW TO UPGRADE?", color = AmberGold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            Text(
                                "Navigate to the 'Heroes' tab at the bottom. Use your accumulated Blockchain Coins (BLC) earned from matches, league participation, and store top-ups to click the 'Upgrade' button on your selected hero class.",
                                color = TextWhite,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        2 -> {
                            // GAME MODES INFO
                            Text("AVAILABLE ARENA MODES", color = AmberGold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            Text(
                                " • Local Pass & Play: Play head-to-head on the same screen with a friend.\n" +
                                " • Offline VS Bot AI: Challenge the offline local bot to a casual practice match.\n" +
                                " • Online VS Cloud Bot: Play with a high-capacity AI bot hosted in cloud environments.\n" +
                                " • Online Matchmaking: Pair and battle online with other real-world players.\n" +
                                " • Vanguard League: Register and play scheduled matches under football-style fixtures.\n" +
                                " • Gladiator Ladder: Climb up from Tier V to Tier I by defeating progressively difficult elite champions.",
                                color = TextWhite,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0x1AFFFFFF))
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.isRulesDialogOpen = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Text("I UNDERSTAND, LET'S BATTLE!", color = DarkBg, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

// ==================== DIALOG 4: PREMIUM PAYMENT PORTAL ====================
@Composable
fun PremiumPaymentPortalDialog(viewModel: GameViewModel) {
    // Real Relworx Mobile Money checkout. Google Play Billing and PayPal are not wired up (they
    // need their own SDKs — Play Billing Library / PayPal SDK — which aren't in this project),
    // so this dialog only offers Mobile Money, which now talks to your backend proxy for real.
    //
    // IMPORTANT: the PIN-entry step that used to be here has been removed on purpose. A
    // legitimate mobile money integration NEVER asks for the customer's PIN inside a third-party
    // app — the network operator sends an approval prompt straight to the customer's own phone
    // (a USSD pop-up or app notification), and they approve it there. An app that asks for the
    // PIN itself is indistinguishable from a PIN-phishing screen, so it's gone.

    Dialog(
        onDismissRequest = { if (!viewModel.isProcessingPayment) viewModel.isPaymentPortalOpen = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            color = DarkSurface,
            border = BorderStroke(1.5.dp, AmberGold)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = AmberGold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("MOBILE MONEY CHECKOUT", color = AmberGold, fontSize = 13.sp, fontWeight = FontWeight.Black)
                            Text(
                                "${viewModel.paymentPortalPackageName} — ${viewModel.paymentPortalPackageCost}",
                                color = TextWhite,
                                fontSize = 10.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = { if (!viewModel.isProcessingPayment) viewModel.isPaymentPortalOpen = false }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextWhite)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0x1AFFFFFF))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Enter your MTN or Airtel Uganda mobile money number. We'll verify it, then send a payment prompt straight to your phone — approve it there to complete the purchase.",
                    color = TextGray,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = viewModel.paymentMobileNumber,
                    onValueChange = {
                        viewModel.paymentMobileNumber = it
                        viewModel.paymentValidatedCustomerName = null
                    },
                    label = { Text("Phone number, e.g. +256701345678") },
                    enabled = !viewModel.isValidatingNumber && !viewModel.isProcessingPayment,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedContainerColor = Color.Black,
                        unfocusedContainerColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (viewModel.paymentValidatedCustomerName == null) {
                    Button(
                        onClick = { viewModel.validatePaymentMobileNumber(viewModel.paymentMobileNumber) },
                        enabled = viewModel.paymentMobileNumber.length >= 10 && !viewModel.isValidatingNumber,
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        if (viewModel.isValidatingNumber) {
                            CircularProgressIndicator(color = DarkBg, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("VERIFY NUMBER", color = DarkBg, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                } else {
                    Button(
                        onClick = { viewModel.processMobileMoneyPayment() },
                        enabled = !viewModel.isProcessingPayment,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        if (viewModel.isProcessingPayment) {
                            CircularProgressIndicator(color = DarkBg, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                "PAY ${viewModel.paymentPortalPackageCost} (${viewModel.paymentValidatedCustomerName})",
                                color = DarkBg,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                viewModel.paymentStatusMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(msg, color = TextWhite, fontSize = 10.sp, lineHeight = 14.sp)
                }
            }
        }
    }
}


/** Resolves a human-friendly file name for a content:// URI, e.g. "MyFavoriteSong.mp3". */
private fun queryFileDisplayName(context: android.content.Context, uri: android.net.Uri): String? {
    return try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) cursor.getString(nameIndex) else null
        }
    } catch (_: Exception) {
        null
    }
}
