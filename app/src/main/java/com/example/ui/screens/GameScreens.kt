package com.example.ui.screens

// Forced reload of the preview to fix freezing issues

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.data.*
import com.example.ui.*
import com.example.ui.DraughtsRuleSystem
import com.example.audio.SoundManager
import kotlinx.coroutines.launch
import com.example.ui.components.HeroDrawing
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun SplashScreenComponent(viewModel: GameViewModel) {
    LaunchedEffect(Unit) {
        delay(3000)
        viewModel.showSplashScreen = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07070A)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_splash_screen_1783691857912),
            contentDescription = "Splash background",
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .align(Alignment.Center)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(2.dp, AmberGold),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                modifier = Modifier
                    .size(110.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_app_icon_1783691822115),
                    contentDescription = "App Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "DRAUGHTS COMBAT",
                color = AmberGold,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "V5.0 • THE PRESENCE UPDATE",
                color = TextWhite.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = AmberGold,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "LOADING TACTICAL ARENA...",
                color = TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
fun AppBackgroundWrapper(
    activeScreen: AppScreen = AppScreen.MAIN_MENU,
    content: @Composable BoxScope.() -> Unit
) {
    val bgRes = when (activeScreen) {
        AppScreen.STORE -> R.drawable.img_store_bg_1783856402223
        AppScreen.GAME_BOT, AppScreen.GAME_LOCAL_PVP, AppScreen.GAME_ONLINE_PVP -> R.drawable.img_battle_bg_1783856416447
        AppScreen.RANKING -> R.drawable.img_rankings_bg_1783856430416
        else -> R.drawable.img_tactical_bg_1783856365354
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07070A))
    ) {
        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            alpha = 0.45f
        )
        // Radiant dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color(0xFF07070A).copy(alpha = 0.9f)
                        )
                    )
                )
        )
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            content()
        }
    }
}

@Composable
fun SubScreenHeader(
    title: String,
    coins: Int,
    onBackClick: () -> Unit
) {
    Surface(
        color = Color(0x990F111A),
        border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0x33FFFFFF), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = AmberGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Coin balance indicator in header
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x33FFC107)),
                border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = "Coins",
                        tint = AmberGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$coins BLC",
                        color = AmberGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun MainMenuScreen(viewModel: GameViewModel) {
    val playerState by viewModel.playerState.collectAsState()
    val name = playerState?.playerName ?: "Grandmaster Checkers"
    val lvl = playerState?.level ?: 1
    val coins = playerState?.draughtCoins ?: 250

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Large Game Logo with tactical sci-fi HUD frame elements
        Box(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "DRAUGHTS COMBAT",
                    color = AmberGold,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = AmberGold.copy(alpha = 0.5f),
                            blurRadius = 8f,
                            offset = androidx.compose.ui.geometry.Offset(0f, 0f)
                        )
                    )
                )
                Text(
                    text = "TACTICAL ARENA PORTAL",
                    color = TextWhite.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Profile Card Hero with tactical ID card glow and linear gradients
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(AmberGold, AmberGold.copy(alpha = 0.2f))
                )
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.isProfileDialogOpen = true }
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(DarkSurface, Color(0xFF0F111A))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (avatarColor, avatarIcon) = when (viewModel.selectedAvatarId) {
                    "knight" -> Pair(RedCrimson, Icons.Default.Shield)
                    "mage" -> Pair(Color(0xFF9C27B0), Icons.Default.AutoAwesome)
                    "valkyrie" -> Pair(Color(0xFF00E676), Icons.Default.LocalActivity)
                    "assassin" -> Pair(Color(0xFFFF9100), Icons.Default.Security)
                    "rogue" -> Pair(Color(0xFF2979FF), Icons.Default.Bolt)
                    else -> Pair(RedCrimson, Icons.Default.Person)
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(avatarColor)
                        .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val photo = playerState?.photoUri
                    if (photo != null) {
                        coil.compose.AsyncImage(
                            model = photo,
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Icon(
                            avatarIcon,
                            contentDescription = "Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = playerState?.tagline ?: "Tactical Overlord",
                        color = TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                    Text(
                        text = "RANK TIER: ELITE III (Lv $lvl)",
                        color = AmberGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x1AFFC738))
                        .border(1.dp, AmberGold.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$coins",
                        color = AmberGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "BLC BAL",
                        color = TextGray,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Primary Game Mode Options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Offline Arena Card
            MainMenuCard(
                title = "OFFLINE ARENA",
                subtitle = "Bots & Local 1v1",
                icon = Icons.Default.SportsEsports,
                iconColor = RedCrimson,
                modifier = Modifier.weight(1f)
            ) {
                viewModel.navigateTo(AppScreen.OFFLINE_MENU)
            }

            // Online Arena Card
            MainMenuCard(
                title = "PLAY ONLINE",
                subtitle = "Matchmaking & Cups",
                icon = Icons.Default.Public,
                iconColor = Color(0xFF00E676),
                modifier = Modifier.weight(1f)
            ) {
                viewModel.navigateTo(AppScreen.ONLINE_MENU)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun MainMenuCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.linearGradient(
                colors = listOf(iconColor.copy(alpha = 0.8f), Color(0x11FFFFFF))
            )
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .height(130.dp)
            .clickable(onClick = onClick)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkSurface, Color(0xFF0F111A))
                ),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.15f))
                        .border(1.dp, iconColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Cybernetic accent bar
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(3.dp)
                        .clip(CircleShape)
                        .background(iconColor)
                )
            }

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = subtitle,
                    color = TextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun UtilityMenuRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x880F111A)),
        border = BorderStroke(1.dp, Color(0x16FFFFFF)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = TextGray, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun OfflineMenuScreen(viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "CHOOSE OFFLINE COMBAT MODE",
            color = AmberGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MainMenuCard(
                title = "SHADOW CAMPAIGN",
                subtitle = "Grandmaster solo battles",
                icon = Icons.Default.SmartToy,
                iconColor = AmberGold,
                modifier = Modifier.weight(1f)
            ) {
                viewModel.changeGameMode(GameViewModel.SelectedGameMode.OFFLINE_VS_BOT)
                viewModel.navigateTo(AppScreen.GAME_SETUP)
            }

            MainMenuCard(
                title = "PASS & PLAY",
                subtitle = "Local 1v1 screen",
                icon = Icons.Default.SportsEsports,
                iconColor = RedCrimson,
                modifier = Modifier.weight(1f)
            ) {
                viewModel.changeGameMode(GameViewModel.SelectedGameMode.LOCAL_PASS_AND_PLAY)
                viewModel.navigateTo(AppScreen.GAME_SETUP)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MainMenuCard(
                title = "LEADERS & RANKS",
                subtitle = "Campaign rankings",
                icon = Icons.Default.EmojiEvents,
                iconColor = Color(0xFF2979FF),
                modifier = Modifier.weight(1f)
            ) {
                viewModel.isOnlineRankingMode = false
                viewModel.navigateTo(AppScreen.RANKING)
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun MainMenuRow(
    title: String,
    subtitle: String,
    borderColor: Color,
    backgroundColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.2.dp, Brush.linearGradient(
            listOf(borderColor, borderColor.copy(alpha = 0.2f))
        )),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                Brush.linearGradient(
                    colors = listOf(backgroundColor, Color(0xFF0F111A))
                ),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(borderColor.copy(alpha = 0.12f))
                        .border(1.dp, borderColor.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = borderColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    color = TextGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextGray.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun OnlineMenuScreen(viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "CHOOSE ONLINE MULTIPLAYER MODE",
            color = AmberGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MainMenuCard(
                title = "MATCHMAKING",
                subtitle = "Live 1v1 online",
                icon = Icons.Default.Bolt,
                iconColor = Color(0xFF00E676),
                modifier = Modifier.weight(1f)
            ) {
                viewModel.changeGameMode(GameViewModel.SelectedGameMode.ONLINE_MATCHMAKING)
                viewModel.navigateTo(AppScreen.GAME_SETUP)
            }

            MainMenuCard(
                title = "WORLD TOURNYS",
                subtitle = "Cup league brackets",
                icon = Icons.Default.EmojiEvents,
                iconColor = AmberGold,
                modifier = Modifier.weight(1f)
            ) {
                viewModel.changeGameMode(GameViewModel.SelectedGameMode.COMPETITIONS)
                viewModel.navigateTo(AppScreen.GAME_SETUP)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MainMenuCard(
                title = "GLOBAL RANKS",
                subtitle = "Live player leaderboard",
                icon = Icons.Default.Star,
                iconColor = Color(0xFF9C27B0),
                modifier = Modifier.weight(1f)
            ) {
                viewModel.isOnlineRankingMode = true
                viewModel.navigateTo(AppScreen.RANKING)
            }

            MainMenuCard(
                title = "CHALLENGES",
                subtitle = "Invite online players",
                icon = Icons.Default.Send,
                iconColor = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            ) {
                viewModel.navigateTo(AppScreen.ONLINE_CHALLENGES)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun GameSetupScreen(viewModel: GameViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var currentStep by remember { mutableStateOf(1) }
    val gameMode = viewModel.selectedGameMode

    // Fetch waiting players on online setup screen
    LaunchedEffect(key1 = gameMode) {
        if (gameMode == GameViewModel.SelectedGameMode.ONLINE_MATCHMAKING) {
            viewModel.startRealMatchmaking()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step Indicator Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TACTICAL BATTLE REGULATOR",
                        color = AmberGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (currentStep == 1) "STEP 1: SELECT DRAUGHTS RULEBOOK" else "STEP 2: ENEMY & MATCH DETAILS",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Navigation buttons for steps
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { currentStep = 1 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentStep == 1) AmberGold else DarkSurfaceVariant
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Step 1", color = if (currentStep == 1) Color.Black else TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { currentStep = 2 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentStep == 2) AmberGold else DarkSurfaceVariant
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Step 2", color = if (currentStep == 2) Color.Black else TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (currentStep == 1) {
            // STEP 1: Select Rule System
            Text(
                "SELECT DRAUGHTS COMPAT COMPACT RULES",
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            val rules = DraughtsRuleSystem.values()
            val chunkedRules = rules.toList().chunked(2)
            chunkedRules.forEach { rowRules ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowRules.forEach { rule ->
                        val isSelected = viewModel.ruleSystem == rule
                        val ruleIcon = when (rule) {
                            DraughtsRuleSystem.AMERICAN_CHECKER_FEDERATION -> Icons.Default.Flag
                            DraughtsRuleSystem.ENGLISH_DRAUGHTS_ASSOCIATION -> Icons.Default.Book
                            DraughtsRuleSystem.WORLD_DRAUGHTS_FEDERATION -> Icons.Default.Public
                        }
                        val ruleColor = when (rule) {
                            DraughtsRuleSystem.AMERICAN_CHECKER_FEDERATION -> RedCrimson
                            DraughtsRuleSystem.ENGLISH_DRAUGHTS_ASSOCIATION -> AmberGold
                            DraughtsRuleSystem.WORLD_DRAUGHTS_FEDERATION -> Color(0xFF2979FF)
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            border = BorderStroke(
                                width = 1.2.dp,
                                brush = Brush.linearGradient(
                                    colors = if (isSelected) {
                                        listOf(ruleColor, ruleColor.copy(alpha = 0.15f))
                                    } else {
                                        listOf(Color(0x33FFFFFF), Color(0x05FFFFFF))
                                    }
                                )
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(160.dp)
                                .clickable { 
                                    viewModel.selectRuleSystem(rule)
                                    viewModel.playSfx(SoundManager.Sfx.NOTIFICATION)
                                }
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = if (isSelected) {
                                            listOf(ruleColor.copy(alpha = 0.12f), Color(0xFF0F111A))
                                        } else {
                                            listOf(DarkSurface, Color(0xFF0F111A))
                                        }
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ruleColor.copy(alpha = 0.15f))
                                            .border(1.dp, ruleColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = ruleIcon,
                                            contentDescription = null,
                                            tint = ruleColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = ruleColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = rule.displayName,
                                        color = if (isSelected) ruleColor else TextWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = when (rule) {
                                            DraughtsRuleSystem.AMERICAN_CHECKER_FEDERATION -> "ACF 8x8 checkers. Jumps mandatory. Classic rules."
                                            DraughtsRuleSystem.ENGLISH_DRAUGHTS_ASSOCIATION -> "EDA 8x8. Features official 3-move opening database."
                                            DraughtsRuleSystem.WORLD_DRAUGHTS_FEDERATION -> "FMJD International 10x10. Flying kings, majority captures."
                                        },
                                        color = TextGray,
                                        fontSize = 10.sp,
                                        lineHeight = 13.sp,
                                        maxLines = 3,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                    if (rowRules.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { currentStep = 2 },
                colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("NEXT STEP: SET MATCH DETAILS", color = Color.Black, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            }

        } else {
            // STEP 2: Custom details per game mode
            when (gameMode) {
                GameViewModel.SelectedGameMode.OFFLINE_VS_BOT -> {
                    Text("🔮 OFFLINE CAMPAIGN REGULATIONS", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
                        border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("SELECT CAMPAIGN DIFFICULTY:", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            
                            // Difficulty selection chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                GameViewModel.BotDifficulty.values().forEach { diff ->
                                    val isSelected = viewModel.currentBotPersona.difficulty == diff
                                    Button(
                                        onClick = {
                                            val pool = viewModel.botPersonaPool.filter { it.difficulty == diff }
                                            if (pool.isNotEmpty()) {
                                                viewModel.currentBotPersona = pool.random()
                                            }
                                            viewModel.playSfx(SoundManager.Sfx.NOTIFICATION)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) {
                                                when (diff) {
                                                    GameViewModel.BotDifficulty.EASY -> Color(0xFF00E676)
                                                    GameViewModel.BotDifficulty.MEDIUM -> AmberGold
                                                    GameViewModel.BotDifficulty.HARD -> RedCrimson
                                                }
                                            } else DarkSurfaceVariant
                                        ),
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(
                                            text = diff.name,
                                            color = if (isSelected) Color.Black else TextWhite,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Divider(color = Color(0x1AFFFFFF))

                            // Opponent Profile card
                            Text("ASSIGNED OPPONENT PROFILE:", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x33FFD700)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = viewModel.currentBotPersona.name.take(2).uppercase(),
                                        color = AmberGold,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(viewModel.currentBotPersona.name.replace("_", " "), color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("MMR Rating: ${viewModel.currentBotPersona.baseMmr} | Rank: ${viewModel.currentBotPersona.difficulty}", color = TextGray, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.navigateTo(AppScreen.GAME_BOT)
                            viewModel.playSfx(SoundManager.Sfx.UNLOCK)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("ENGAGE IN COMBAT", color = Color.Black, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                }

                GameViewModel.SelectedGameMode.LOCAL_PASS_AND_PLAY -> {
                    Text("🎮 PASS & PLAY PLAYER ENLISTMENT", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
                        border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Player 1 input
                            OutlinedTextField(
                                value = viewModel.localPlayer1Name,
                                onValueChange = { viewModel.localPlayer1Name = it },
                                label = { Text("Player 1 Name (Red Vanguard)", color = AmberGold, fontSize = 10.sp) },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = RedCrimson) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = RedCrimson,
                                    unfocusedBorderColor = Color(0x33FFFFFF),
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Player 2 input
                            OutlinedTextField(
                                value = viewModel.localPlayer2Name,
                                onValueChange = { viewModel.localPlayer2Name = it },
                                label = { Text("Player 2 Name (Purple Shadow)", color = AmberGold, fontSize = 10.sp) },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = VioletNeon) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VioletNeon,
                                    unfocusedBorderColor = Color(0x33FFFFFF),
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.navigateTo(AppScreen.GAME_LOCAL_PVP)
                            viewModel.playSfx(SoundManager.Sfx.UNLOCK)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("ENGAGE LOCAL PASS & PLAY", color = Color.Black, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                }

                GameViewModel.SelectedGameMode.ONLINE_MATCHMAKING, GameViewModel.SelectedGameMode.COMPETITIONS -> {
                    Text("⚔️ ONLINE LOBBY & WAITING PLAYERS", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
                        border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("PLAYERS CURRENTLY WAITING:", color = AmberGold, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                Text("${viewModel.waitingPlayers.size} Active", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            if (viewModel.waitingPlayers.isEmpty()) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0x1AFFFFFF)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "No players are currently waiting in the lobby for ${viewModel.ruleSystem.name.replace("_", " ")}. Click the 'RANDOM MATCHMAKING' button below to host a room and wait for others!",
                                        color = TextGray,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(12.dp),
                                        lineHeight = 15.sp
                                    )
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    viewModel.waitingPlayers.forEach { op ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0x22FFFFFF), RoundedCornerShape(8.dp))
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0x1A00E676)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(op.name, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    Text("Rating: ${op.mmr} MMR | ${op.ruleSystem.replace("_", " ")}", color = TextGray, fontSize = 9.sp)
                                                }
                                            }
                                            Button(
                                                onClick = {
                                                    viewModel.challengeWaitingPlayer(op)
                                                    viewModel.navigateTo(AppScreen.GAME_ONLINE_PVP)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                                contentPadding = PaddingValues(horizontal = 10.dp),
                                                modifier = Modifier.height(26.dp)
                                            ) {
                                                Text("CHALLENGE", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Main matchmaking search button
                    Button(
                        onClick = {
                            viewModel.navigateTo(AppScreen.GAME_ONLINE_PVP)
                            viewModel.playSfx(SoundManager.Sfx.UNLOCK)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("HOST ROOM / FIND RANDOM MATCH", color = Color.Black, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Public, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OfflineBotGameScreen(viewModel: GameViewModel) {
    val state by viewModel.playerState.collectAsState()
    val activeSkin = state?.selectedSkin ?: "classic"
    val activeBoardStyle = state?.selectedBoardStyle ?: "classic"
    val bot = viewModel.currentBotPersona

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Active Bot info banner
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
            border = BorderStroke(1.2.dp, AmberGold.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x1AFFC107)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = AmberGold, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "VS ${bot.name.replace("_", " ").uppercase()}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Rank: ${bot.difficulty} • MMR: ${bot.baseMmr}",
                        color = TextGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { viewModel.rollNewBotPersona() },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = AmberGold, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Change Opponent", color = AmberGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Active Turn Status Bar
        val isMyTurn = viewModel.turnRed
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isMyTurn) Color(0x1100E676) else Color(0x11FF1744)
            ),
            border = BorderStroke(
                1.dp, 
                if (isMyTurn) Color(0xFF00E676).copy(alpha = 0.4f) else Color(0xFFFF1744).copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isMyTurn) Color(0xFF00E676) else Color(0xFFFF1744))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMyTurn) "YOUR TURN (VANGUARD)" else "${bot.name.replace("_", " ").uppercase()}'S TURN (SHADOW)",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Captured Pieces (Shadow's Graveyard / Purple casualties)
        CapturedPiecesRow(
            title = "Shadow Graveyard",
            pieces = viewModel.capturedPieces.filter { !it.isRed },
            allianceColor = VioletNeon,
            tag = "shadow_graveyard"
        )

        // Checkers Board
        CheckersBoardComponent(viewModel, activeSkin, activeBoardStyle)

        // Captured Pieces (Vanguard's Graveyard / Red casualties)
        CapturedPiecesRow(
            title = "Vanguard Graveyard",
            pieces = viewModel.capturedPieces.filter { it.isRed },
            allianceColor = RedCrimson,
            tag = "vanguard_graveyard"
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Selected Piece Details HUD
        SelectedPieceHUD(viewModel)

        Spacer(modifier = Modifier.height(4.dp))

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.resetGame() },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.RestartAlt, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset Board", fontSize = 11.sp)
            }

            Button(
                onClick = { viewModel.navigateTo(AppScreen.STORE) },
                colors = ButtonDefaults.buttonColors(containerColor = RedCrimson),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Storefront, contentDescription = "Store", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Store", fontSize = 11.sp)
            }
        }

        // Victory banner
        VictoryBannerHUD(viewModel)
    }
}

@Composable
fun SelectedPieceHUD(viewModel: GameViewModel) {
    AnimatedVisibility(
        visible = viewModel.selectedPiece != null,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        viewModel.selectedPiece?.let { piece ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
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
                        style = TextStyle(lineHeight = 14.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun VictoryBannerHUD(viewModel: GameViewModel) {
    viewModel.winnerMessage?.let { winnerMsg ->
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
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
}

@Composable
fun OfflineLocalGameScreen(viewModel: GameViewModel) {
    val state by viewModel.playerState.collectAsState()
    val activeSkin = state?.selectedSkin ?: "classic"
    val activeBoardStyle = state?.selectedBoardStyle ?: "classic"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Mode Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
            border = BorderStroke(1.2.dp, RedCrimson.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x1AFF1744)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SportsEsports, contentDescription = null, tint = RedCrimson, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "LOCAL ARENA: PASS & PLAY",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Play 1v1 on this same device. Switch sides after each turn.",
                        color = TextGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Active Turn Status Bar
        val turnRed = viewModel.turnRed
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (turnRed) Color(0x11FF1744) else Color(0x119C27B0)
            ),
            border = BorderStroke(
                1.dp, 
                if (turnRed) Color(0xFFFF1744).copy(alpha = 0.4f) else Color(0xFF9C27B0).copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (turnRed) Color(0xFFFF1744) else Color(0xFF9C27B0))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (turnRed) "VANGUARD'S TURN (RED)" else "SHADOW'S TURN (PURPLE)",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Captured Pieces (Shadow's Graveyard / Purple casualties)
        CapturedPiecesRow(
            title = "Shadow Graveyard",
            pieces = viewModel.capturedPieces.filter { !it.isRed },
            allianceColor = VioletNeon,
            tag = "shadow_graveyard"
        )

        // Checkers Board
        CheckersBoardComponent(viewModel, activeSkin, activeBoardStyle)

        // Captured Pieces (Vanguard's Graveyard / Red casualties)
        CapturedPiecesRow(
            title = "Vanguard Graveyard",
            pieces = viewModel.capturedPieces.filter { it.isRed },
            allianceColor = RedCrimson,
            tag = "vanguard_graveyard"
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Selected Piece Details HUD
        SelectedPieceHUD(viewModel)

        Spacer(modifier = Modifier.height(4.dp))

        // Reset Board Action
        Button(
            onClick = { viewModel.resetGame() },
            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.RestartAlt, contentDescription = "Reset", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Reset Local Board", fontSize = 12.sp)
        }

        // Victory banner
        VictoryBannerHUD(viewModel)
    }
}

@Composable
fun OnlinePvPGameScreen(viewModel: GameViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        RealOnlineMatchScreen(viewModel)
    }
}

@Composable
fun OnlineCompetitionsScreen(viewModel: GameViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        TournamentBrowserScreen(viewModel, null)
    }
}

@Composable
fun WalletsScreen(viewModel: GameViewModel) {
    val playerState by viewModel.playerState.collectAsState()
    val coins = playerState?.draughtCoins ?: 250
    val isPremium = viewModel.isPremiumUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Coin Wallet Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
            border = BorderStroke(1.5.dp, AmberGold),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Paid,
                    contentDescription = "Wallet Balance",
                    tint = AmberGold,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "TOTAL DRAUGHT COIN BALANCE",
                    color = TextGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "$coins BLC",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "100% Secure Cryptographic Ledger Balance",
                    color = Color(0xFF00E676),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.isPaymentPortalOpen = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = DarkBg, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Top Up Coins", color = DarkBg, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { viewModel.triggerNotification("Cryptographic transfer logic requires a linked Google wallet.") },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Send BLC", color = AmberGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Membership Card Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
            border = BorderStroke(1.dp, if (isPremium) AmberGold else Color(0x33FFFFFF)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = if (isPremium) AmberGold else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PREMIUM MEMBERSHIP STATUS",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    if (isPremium) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0x33FFC107)),
                            border = BorderStroke(0.5.dp, AmberGold)
                        ) {
                            Text(
                                "ACTIVE GOLD",
                                color = AmberGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0x16FFFFFF)),
                            border = BorderStroke(0.5.dp, Color.Gray)
                        ) {
                            Text(
                                "INACTIVE",
                                color = Color.Gray,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isPremium) "Congratulations! You have unlocked unlimited online tournament entries, all visual board designs, double XP rewards, and ad-free priority match lobbies!" else "Unlock professional tournament access, rare champion tokens, double daily coin rewards, and ad-free lobbies.",
                    color = TextGray,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )

                if (!isPremium) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { viewModel.isPaymentPortalOpen = true },
                        colors = ButtonDefaults.buttonColors(containerColor = RedCrimson),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Get Premium Gold Upgrade", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Ledger List Header
        Text(
            text = "RECENT LEDGER TRANSACTIONS",
            color = AmberGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )

        // Ledger list (mock/real ledger statements)
        val ledger = listOf(
            Triple("Daily Login Reward", "+50 BLC", "SUCCESS"),
            Triple("Premium Gold Upgrade Attempt", "0 BLC", "PENDING_OAUTH"),
            Triple("Campaign Opponent Defeated", "+75 BLC", "SUCCESS"),
            Triple("Skin Store: Dark Forest Board Purchase", "-120 BLC", "SUCCESS")
        )

        ledger.forEach { (title, amt, status) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x66000000)),
                border = BorderStroke(1.dp, Color(0x11FFFFFF)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(status, color = if (status == "SUCCESS") Color(0xFF00E676) else AmberGold, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                    Text(
                        amt,
                        color = if (amt.startsWith("+")) Color(0xFF00E676) else RedCrimson,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun StoreScreenWrapper(viewModel: GameViewModel) {
    StoreScreen(viewModel)
}

@Composable
fun RankingsScreen(viewModel: GameViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Toggle rank type info banner
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
            border = BorderStroke(1.dp, if (viewModel.isOnlineRankingMode) Color(0xFF00E676).copy(alpha = 0.5f) else Color(0xFF2979FF).copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (viewModel.isOnlineRankingMode) Icons.Default.Public else Icons.Default.Leaderboard,
                    contentDescription = null,
                    tint = if (viewModel.isOnlineRankingMode) Color(0xFF00E676) else Color(0xFF2979FF),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (viewModel.isOnlineRankingMode) "GLOBAL ONLINE GRANDMASTERS" else "LOCAL OFFLINE CAMPAIGN RANKS",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (viewModel.isOnlineRankingMode) "Live global server stats. Updates dynamically." else "Local campaign high scores and opponent training levels.",
                        color = TextGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            // Render the existing beautifully optimized LeaderboardScreen
            LeaderboardScreen(viewModel)
        }
    }
}

@Composable
fun SettingsScreen(viewModel: GameViewModel) {
    val playerState by viewModel.playerState.collectAsState()
    val name = playerState?.playerName ?: "Grandmaster Checkers"
    val context = androidx.compose.ui.platform.LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Profile Name editor
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "PLAYER DESIGNATION",
                    color = AmberGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                var tempName by remember { mutableStateOf(name) }
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Display Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = Color.DarkGray
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (tempName.isNotBlank()) {
                            viewModel.updatePlayerName(tempName.trim())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save Name", color = DarkBg, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        // Section 2: Account sync
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "CLOUD ACCOUNT SYNCHRONIZATION",
                    color = AmberGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (viewModel.isGoogleSignedIn) "Your progression is securely backed up online to ${viewModel.signedInEmail ?: "your Google account"}." else "You are currently playing as a local Guest. Link your Google account to back up progression and participate in matchmaking.",
                    color = TextGray,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (viewModel.isGoogleSignedIn) {
                    Button(
                        onClick = { viewModel.googleSignOut(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = RedCrimson),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Unlink & Sign Out", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                } else {
                    Button(
                        onClick = { viewModel.isGoogleAuthDialogOpen = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null, tint = DarkBg, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Link Google Account", color = DarkBg, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Section 3: Official Draughts Rulebook Selector
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "OFFICIAL DRAUGHTS RULEBOOK SELECTOR",
                    color = AmberGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Select an official rulebook. The board layout, piece counts, and move directions dynamically adjust to match international federation regulations.",
                    color = TextGray,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                val rulebooks = listOf(
                    Triple(DraughtsRuleSystem.AMERICAN_CHECKER_FEDERATION, "ACF (8x8)", "American ACF"),
                    Triple(DraughtsRuleSystem.ENGLISH_DRAUGHTS_ASSOCIATION, "EDA (8x8)", "English EDA"),
                    Triple(DraughtsRuleSystem.WORLD_DRAUGHTS_FEDERATION, "FMJD (10x10)", "World FMJD")
                )
                rulebooks.forEach { (system, abbrev, desc) ->
                    val isSelected = viewModel.ruleSystem == system
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectRuleSystem(system) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.selectRuleSystem(system) },
                            colors = RadioButtonDefaults.colors(selectedColor = AmberGold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(abbrev, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(desc, color = TextGray, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Section 4: RPG Regulations & Switches
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "CUSTOM COMBAT SETTINGS",
                    color = AmberGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Combat Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
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

                Spacer(modifier = Modifier.height(10.dp))

                // Forced Jumps Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
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

                Spacer(modifier = Modifier.height(10.dp))

                // Flying Kings Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
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

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun MusicSettingsScreen(viewModel: GameViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val customSongPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<android.net.Uri>? ->
        if (!uris.isNullOrEmpty()) {
            viewModel.setCustomMusicPlaylist(context, uris)
        }
    }

    val singleSongPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {}
            val displayName = queryFileDisplayName(context, uri) ?: "Custom Track"
            viewModel.setCustomMusicTrack(context, uri, displayName)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER: Remixed Studio Console Display
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xDD0C0E17)),
            border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.4f)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "ORCHESTRA DJ REMIX STUDIO",
                        color = Color(0xFF00E676),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (com.example.audio.AutoDJEngine.isTransitioning) {
                        "MIXING ACTIVE: transitioning with ${com.example.audio.AutoDJEngine.crossfadeCurveType.name} curve"
                    } else {
                        "DECK ${if (com.example.audio.AutoDJEngine.currentDeck == 0) "A" else "B"} ONLINE • SYNC ENGINE ARMED"
                    },
                    color = TextWhite.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // CARD 1: Dual-Deck DJ Console
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // DECK A (Left Deck)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (com.example.audio.AutoDJEngine.currentDeck == 0) Color(0xAA131722) else Color(0x770F111A)
                ),
                border = BorderStroke(
                    1.dp,
                    if (com.example.audio.AutoDJEngine.currentDeck == 0) Color(0xFF00E676) else Color(0x33FFFFFF)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("DECK A", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (com.example.audio.AutoDJEngine.currentDeck == 0) Color(0xFF00E676) else Color(0x44FFFFFF)
                                )
                        )
                    }

                    // Spinning Vinyl Graphic
                    val isDeckAPlaying = (com.example.audio.AutoDJEngine.currentDeck == 0 && !com.example.audio.AutoDJEngine.isTransitioning) ||
                            (com.example.audio.AutoDJEngine.isTransitioning && com.example.audio.AutoDJEngine.crossfaderPosition < 0.5f)
                    RotatingVinylDisc(isPlaying = isDeckAPlaying)

                    Text(
                        text = com.example.audio.AutoDJEngine.deckATrackName,
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = String.format("%.1f BPM", com.example.audio.AutoDJEngine.deckABpm),
                        color = Color(0xFF00E676),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    // Nudge controls row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = { com.example.audio.AutoDJEngine.nudgePitch(0, false) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x22FFFFFF)),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.weight(1f).height(24.dp)
                        ) {
                            Text("NUDGE -", color = TextWhite, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { com.example.audio.AutoDJEngine.nudgePitch(0, true) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x22FFFFFF)),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.weight(1f).height(24.dp)
                        ) {
                            Text("NUDGE +", color = TextWhite, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (com.example.audio.AutoDJEngine.deckAPitchShift != 0f) {
                        Text(
                            text = String.format("Nudge: %+.1f%%", com.example.audio.AutoDJEngine.deckAPitchShift * 100),
                            color = AmberGold,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = { com.example.audio.AutoDJEngine.triggerScratchEffect() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x1A00E676)),
                            border = BorderStroke(0.5.dp, Color(0xFF00E676)),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.weight(1f).height(26.dp)
                        ) {
                            Text("SCRATCH", color = Color(0xFF00E676), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { com.example.audio.AutoDJEngine.syncTempos() },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold.copy(alpha = 0.1f)),
                            border = BorderStroke(0.5.dp, AmberGold),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.weight(1f).height(26.dp)
                        ) {
                            Text("SYNC BPM", color = AmberGold, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // DECK B (Right Deck)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (com.example.audio.AutoDJEngine.currentDeck == 1) Color(0xAA131722) else Color(0x770F111A)
                ),
                border = BorderStroke(
                    1.dp,
                    if (com.example.audio.AutoDJEngine.currentDeck == 1) Color(0xFF00E676) else Color(0x33FFFFFF)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("DECK B", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (com.example.audio.AutoDJEngine.currentDeck == 1) Color(0xFF00E676) else Color(0x44FFFFFF)
                                )
                        )
                    }

                    // Spinning Vinyl Graphic
                    val isDeckBPlaying = (com.example.audio.AutoDJEngine.currentDeck == 1 && !com.example.audio.AutoDJEngine.isTransitioning) ||
                            (com.example.audio.AutoDJEngine.isTransitioning && com.example.audio.AutoDJEngine.crossfaderPosition > -0.5f)
                    RotatingVinylDisc(isPlaying = isDeckBPlaying)

                    Text(
                        text = com.example.audio.AutoDJEngine.deckBTrackName,
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = String.format("%.1f BPM", com.example.audio.AutoDJEngine.deckBBpm),
                        color = Color(0xFF00E676),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    // Nudge controls row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = { com.example.audio.AutoDJEngine.nudgePitch(1, false) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x22FFFFFF)),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.weight(1f).height(24.dp)
                        ) {
                            Text("NUDGE -", color = TextWhite, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { com.example.audio.AutoDJEngine.nudgePitch(1, true) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x22FFFFFF)),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.weight(1f).height(24.dp)
                        ) {
                            Text("NUDGE +", color = TextWhite, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (com.example.audio.AutoDJEngine.deckBPitchShift != 0f) {
                        Text(
                            text = String.format("Nudge: %+.1f%%", com.example.audio.AutoDJEngine.deckBPitchShift * 100),
                            color = AmberGold,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = { com.example.audio.AutoDJEngine.triggerScratchEffect() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x1A00E676)),
                            border = BorderStroke(0.5.dp, Color(0xFF00E676)),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.weight(1f).height(26.dp)
                        ) {
                            Text("SCRATCH", color = Color(0xFF00E676), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { com.example.audio.AutoDJEngine.syncTempos() },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold.copy(alpha = 0.1f)),
                            border = BorderStroke(0.5.dp, AmberGold),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.weight(1f).height(26.dp)
                        ) {
                            Text("SYNC BPM", color = AmberGold, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // PHYSICAL MIXER: Crossfader Panel
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("DECK A (L)", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("PHYSICAL CROSSFADER BLEND", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    Text("DECK B (R)", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Slider(
                    value = com.example.audio.AutoDJEngine.crossfaderPosition,
                    onValueChange = { com.example.audio.AutoDJEngine.updateCrossfader(it) },
                    valueRange = -1.0f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF00E676),
                        activeTrackColor = Color(0xFF00E676).copy(alpha = 0.5f),
                        inactiveTrackColor = Color(0xFF1F2232)
                    )
                )

                // Blend label details
                val percentA = ((1.0f - com.example.audio.AutoDJEngine.crossfaderPosition) / 2.0f * 100).toInt()
                val percentB = 100 - percentA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Blend: $percentA% A", color = if (percentA > 50) Color(0xFF00E676) else TextWhite, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("Blend: $percentB% B", color = if (percentB > 50) Color(0xFF00E676) else TextWhite, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }

                Divider(color = Color(0x1AFFFFFF))

                // MANUAL BASS SWAP EQ TACTILE CUT
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Simulated Bass Swap (EQ Low-Cut)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Instantly swaps bass-frequency dominance to maintain extreme audio clarity.", color = TextGray, fontSize = 9.sp)
                    }
                    Switch(
                        checked = com.example.audio.AutoDJEngine.bassSwapActive,
                        onCheckedChange = { com.example.audio.AutoDJEngine.bassSwapActive = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF00E676),
                            checkedTrackColor = Color(0x4D00E676)
                        )
                    )
                }
            }
        }

        // CARD 2: Transitions & Mixing Setup
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
            border = BorderStroke(1.dp, Color(0x2200E676)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "AUTOMATIC TRANSITION CONFIGURATION",
                    color = AmberGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )

                // Auto Mix Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Continuous Auto-Mix Queue", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Detects song end and automatically triggers seamless crossfade mixing.", color = TextGray, fontSize = 9.sp)
                    }
                    Switch(
                        checked = com.example.audio.AutoDJEngine.autoMixEnabled,
                        onCheckedChange = { com.example.audio.AutoDJEngine.autoMixEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF00E676),
                            checkedTrackColor = Color(0x4D00E676)
                        )
                    )
                }

                // BPM Sync Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto BPM Beat-Sync Mode", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Matches playback speed/tempo perfectly during transition, then slurs back.", color = TextGray, fontSize = 9.sp)
                    }
                    Switch(
                        checked = com.example.audio.AutoDJEngine.syncModeEnabled,
                        onCheckedChange = { com.example.audio.AutoDJEngine.syncModeEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF00E676),
                            checkedTrackColor = Color(0x4D00E676)
                        )
                    )
                }

                Divider(color = Color(0x1AFFFFFF))

                // Transition curve type selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Crossfade Curve Type:", color = TextWhite, fontSize = 11.sp)
                    var expandedCurveDropdown by remember { mutableStateOf(false) }
                    Box {
                        Button(
                            onClick = { expandedCurveDropdown = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(com.example.audio.AutoDJEngine.crossfadeCurveType.name, color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(
                            expanded = expandedCurveDropdown,
                            onDismissRequest = { expandedCurveDropdown = false },
                            modifier = Modifier.background(DarkSurface)
                        ) {
                            com.example.audio.AutoDJEngine.CurveType.entries.forEach { curve ->
                                DropdownMenuItem(
                                    text = { Text(curve.name, color = TextWhite, fontSize = 12.sp) },
                                    onClick = {
                                        com.example.audio.AutoDJEngine.crossfadeCurveType = curve
                                        expandedCurveDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Transition Duration Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Crossfade Transition Time:", color = TextWhite, fontSize = 11.sp)
                        Text(
                            text = String.format("%.0fs", com.example.audio.AutoDJEngine.transitionDurationSeconds),
                            color = AmberGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = com.example.audio.AutoDJEngine.transitionDurationSeconds,
                        onValueChange = { com.example.audio.AutoDJEngine.transitionDurationSeconds = it },
                        valueRange = 3.0f..20.0f,
                        colors = SliderDefaults.colors(thumbColor = AmberGold, activeTrackColor = AmberGold)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Giant Trigger seamless remix button
                Button(
                    onClick = { com.example.audio.AutoDJEngine.triggerManualTransition() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    enabled = !com.example.audio.AutoDJEngine.isTransitioning,
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = null,
                        tint = DarkBg,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "TRIGGER AUTO-MIX REMIX SEAMLESSLY",
                        color = DarkBg,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // PLAYLIST & MUSIC SOURCE LOADER
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "MUSIC STORAGE & PLAYLIST QUEUE",
                        color = AmberGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    Button(
                        onClick = { customSongPicker.launch(arrayOf("audio/*")) },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold.copy(alpha = 0.15f)),
                        border = BorderStroke(1.dp, AmberGold),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = AmberGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Load Songs", color = AmberGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Dropdown to select bundled tracks or upload single song
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Select Active Symphony:", color = TextWhite, fontSize = 12.sp)
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
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
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
                                text = { Text("📁 Upload Single Song...", color = AmberGold, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    expandedMusicDropdown = false
                                    singleSongPicker.launch(arrayOf("audio/*"))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🎵 Upload Multiple Songs...", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    expandedMusicDropdown = false
                                    customSongPicker.launch(arrayOf("audio/*"))
                                }
                            )
                        }
                    }
                }

                if (viewModel.customMusicPlaylistUris.isNotEmpty()) {
                    Text("PLAYLIST QUEUE:", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0x33000000)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            viewModel.customMusicPlaylistNames.forEachIndexed { idx, name ->
                                val isCurrentTrackPlaying = com.example.audio.AutoDJEngine.deckATrackName == name ||
                                        com.example.audio.AutoDJEngine.deckBTrackName == name
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isCurrentTrackPlaying) Icons.Default.VolumeUp else Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = if (isCurrentTrackPlaying) Color(0xFF00E676) else TextGray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${idx + 1}. $name",
                                        color = if (isCurrentTrackPlaying) Color(0xFF00E676) else TextWhite,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isCurrentTrackPlaying) {
                                        Text("Playing", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        "No custom songs in playlist queue. Upload multiple tracks above to enable continuous back-to-back playback.",
                        color = TextGray,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // VOLUME & SYMPHONY EQUALIZER CARD
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "VOLUME SYMPHONY CONTROLS",
                    color = AmberGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Music Volume
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = AmberGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Music Track:", color = TextWhite, fontSize = 11.sp, modifier = Modifier.width(100.dp))
                    Slider(
                        value = viewModel.musicVolume,
                        onValueChange = { viewModel.musicVolume = it },
                        colors = SliderDefaults.colors(thumbColor = AmberGold, activeTrackColor = AmberGold),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${(viewModel.musicVolume * 100).toInt()}%", color = TextWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }

                // SFX Volume
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = AmberGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sound SFX:", color = TextWhite, fontSize = 11.sp, modifier = Modifier.width(100.dp))
                    Slider(
                        value = viewModel.soundVolume,
                        onValueChange = { viewModel.soundVolume = it },
                        colors = SliderDefaults.colors(thumbColor = AmberGold, activeTrackColor = AmberGold),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${(viewModel.soundVolume * 100).toInt()}%", color = TextWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Equalizer Visualizer Bars!
                Row(
                    modifier = Modifier.fillMaxWidth().height(24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    repeat(16) { index ->
                        val infiniteTransition = rememberInfiniteTransition(label = "music_equalizer")
                        val heightAnim by infiniteTransition.animateFloat(
                            initialValue = 0.1f,
                            targetValue = 1.0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 350 + (index * 60), easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "height"
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .width(5.dp)
                                .fillMaxHeight(heightAnim * viewModel.musicVolume)
                                .background(if (index % 2 == 0) AmberGold else RedCrimson)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RotatingVinylDisc(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val currentRotation = if (isPlaying) rotation else 0f

    androidx.compose.foundation.Canvas(modifier = modifier.size(72.dp)) {
        val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f

        // Draw vinyl record disc (Deep Charcoal/Black)
        drawCircle(color = Color(0xFF14151B), radius = radius)
        
        // Concentric audio groove rings
        for (i in 1..4) {
            drawCircle(
                color = Color(0x22FFFFFF),
                radius = radius * (0.35f + i * 0.13f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
            )
        }

        // Central sticker (glowing gold)
        drawCircle(
            color = Color(0xFFFFD700),
            radius = radius * 0.32f
        )
        
        // Inner record label ring
        drawCircle(
            color = Color(0xFF0F111A),
            radius = radius * 0.25f
        )

        // Spindle hole
        drawCircle(
            color = Color(0xFF000000),
            radius = radius * 0.08f
        )

        // Rotation needle/marker to visualize rotation
        drawContext.transform.rotate(currentRotation, center)
        drawLine(
            color = Color(0x99FFFFFF),
            start = center,
            end = androidx.compose.ui.geometry.Offset(center.x, center.y - radius * 0.85f),
            strokeWidth = 3f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        drawContext.transform.rotate(-currentRotation, center)
    }
}

@Composable
fun NotificationsScreen(viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Configuration Switches
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "COMMUNICATIONS CONFIGURATION",
                    color = AmberGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("System Toast Notifications", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Show in-game drop-down announcement cards.", color = TextGray, fontSize = 10.sp)
                    }
                    Switch(
                        checked = viewModel.notificationsEnabled,
                        onCheckedChange = { viewModel.notificationsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AmberGold, checkedTrackColor = AmberGold.copy(alpha = 0.4f))
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Challenge Invites Alerts", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Get notified when online users offer direct challenges.", color = TextGray, fontSize = 10.sp)
                    }
                    Switch(
                        checked = viewModel.challInvitesNotifications,
                        onCheckedChange = { viewModel.challInvitesNotifications = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AmberGold, checkedTrackColor = AmberGold.copy(alpha = 0.4f))
                    )
                }
            }
        }

        // Notification Log list
        Text(
            text = "SYSTEM ANNOUNCEMENTS & LOG",
            color = AmberGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )

        if (viewModel.notificationHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0x33FFFFFF), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("No notifications or alert logs yet.", color = TextGray, fontSize = 11.sp)
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                viewModel.notificationHistory.reversed().forEach { msg ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0x66000000)),
                        border = BorderStroke(1.dp, Color(0x11FFFFFF)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x1AFFC107)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = msg,
                                color = TextWhite,
                                fontSize = 11.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DraggableChatWidget(viewModel: GameViewModel) {
    var chatOffsetX by remember { mutableStateOf(0f) }
    var chatOffsetY by remember { mutableStateOf(0f) }
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(chatOffsetX.roundToInt(), chatOffsetY.roundToInt()) }
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        chatOffsetX += dragAmount.x
                        chatOffsetY += dragAmount.y
                    }
                }
                .width(if (isExpanded) 280.dp else 56.dp)
                .height(if (isExpanded) 320.dp else 56.dp)
                .background(
                    color = DarkSurface.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(if (isExpanded) 16.dp else 28.dp)
                )
                .border(
                    width = 1.5.dp,
                    color = AmberGold,
                    shape = RoundedCornerShape(if (isExpanded) 16.dp else 28.dp)
                )
                .clickable {
                    if (!isExpanded) isExpanded = true
                }
        ) {
            if (!isExpanded) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Chat",
                    tint = AmberGold,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tactical Chat", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = { isExpanded = false },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray, modifier = Modifier.size(14.dp))
                        }
                    }

                    Divider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(vertical = 6.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        val listState = rememberLazyListState()
                        LaunchedEffect(viewModel.chatMessages.size) {
                            if (viewModel.chatMessages.isNotEmpty()) {
                                listState.animateScrollToItem(viewModel.chatMessages.size - 1)
                            }
                        }
                        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                            items(viewModel.chatMessages) { msg ->
                                Text(
                                    text = "${msg.first}: ${msg.second}",
                                    color = when (msg.first) {
                                        "You" -> AmberGold
                                        "System" -> Color(0xFF00E676)
                                        else -> VioletNeon
                                    },
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    var textInput by remember { mutableStateOf("") }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            textStyle = TextStyle(color = TextWhite, fontSize = 12.sp),
                            cursorBrush = SolidColor(AmberGold),
                            modifier = Modifier
                                .weight(1f)
                                .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) { innerTextField ->
                            if (textInput.isEmpty()) {
                                Text("Type message...", color = TextGray, fontSize = 11.sp)
                            }
                            innerTextField()
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (textInput.isNotBlank()) {
                                    viewModel.sendChatMessage(textInput)
                                    textInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .background(AmberGold, CircleShape)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = DarkBg, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopGameAppBar(
    title: String,
    viewModel: GameViewModel,
    onMenuClick: () -> Unit,
    showBackButton: Boolean,
    onBackClick: () -> Unit
) {
    val unreadCount = viewModel.appNotifications.count { !it.isRead }
    var expandedNotifications by remember { mutableStateOf(false) }

    Surface(
        color = Color(0x990F111A),
        border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0x1AFFFFFF), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = AmberGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (showBackButton) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0x1AFFFFFF), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AmberGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Notification Bell with badge
            Box {
                IconButton(
                    onClick = { expandedNotifications = true },
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0x1AFFFFFF), CircleShape)
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(
                                    containerColor = RedCrimson,
                                    contentColor = Color.White
                                ) {
                                    Text(unreadCount.toString(), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = AmberGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Bell dropdown popup
                DropdownMenu(
                    expanded = expandedNotifications,
                    onDismissRequest = { expandedNotifications = false },
                    modifier = Modifier
                        .width(280.dp)
                        .background(DarkSurface)
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = "COMMS & IN-GAME ALERTS",
                        color = AmberGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Divider(color = Color(0x1AFFFFFF))

                    if (viewModel.appNotifications.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No notifications yet.", color = TextGray, fontSize = 11.sp) },
                            onClick = { expandedNotifications = false }
                        )
                    } else {
                        // Take the last 5 notifications to keep the list clean
                        viewModel.appNotifications.takeLast(5).reversed().forEach { notification ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = notification.message,
                                            color = if (notification.isRead) TextGray else TextWhite,
                                            fontSize = 11.sp,
                                            lineHeight = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (notification.isRead) "Read" else "New • Click to view",
                                            color = if (notification.isRead) TextGray else Color(0xFF00E676),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                },
                                onClick = {
                                    notification.isRead = true
                                    expandedNotifications = false
                                    viewModel.navigateTo(notification.targetScreen)
                                    viewModel.playSfx(SoundManager.Sfx.NOTIFICATION)
                                }
                            )
                            Divider(color = Color(0x11FFFFFF))
                        }
                    }

                    DropdownMenuItem(
                        text = {
                            Text(
                                "View All Alerts",
                                color = AmberGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        onClick = {
                            expandedNotifications = false
                            viewModel.navigateTo(AppScreen.NOTIFICATIONS)
                            viewModel.playSfx(SoundManager.Sfx.NOTIFICATION)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GameBottomNavigation(
    currentScreen: AppScreen,
    onTabSelected: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xEE0F111A),
        tonalElevation = 8.dp,
        modifier = Modifier.height(60.dp)
    ) {
        val tabs = listOf(
            Triple(AppScreen.MAIN_MENU, Icons.Default.Home, "Home"),
            Triple(AppScreen.OFFLINE_MENU, Icons.Default.SportsEsports, "Offline"),
            Triple(AppScreen.ONLINE_MENU, Icons.Default.Public, "Online"),
            Triple(AppScreen.RANKING, Icons.Default.EmojiEvents, "Leaders")
        )

        tabs.forEach { (screen, icon, label) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) AmberGold else TextGray,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        color = if (isSelected) AmberGold else TextGray,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0x22FFD700)
                )
            )
        }
    }
}

@Composable
fun DrawerContent(
    viewModel: GameViewModel,
    onItemClick: (AppScreen) -> Unit,
    onProfileClick: () -> Unit
) {
    val playerState by viewModel.playerState.collectAsState()

    ModalDrawerSheet(
        drawerContainerColor = Color(0xEE080911),
        drawerContentColor = TextWhite,
        modifier = Modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Drawer Header Profile Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x33FFC107)),
                border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onProfileClick)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFC107)),
                        contentAlignment = Alignment.Center
                    ) {
                        val photo = playerState?.photoUri
                        if (photo != null) {
                            coil.compose.AsyncImage(
                                model = photo,
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = (playerState?.playerName ?: "P").take(2).uppercase(),
                                color = AmberGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = playerState?.playerName ?: "Elite Commander",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = AmberGold, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Lvl ${playerState?.level ?: 1}",
                                color = AmberGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = AmberGold, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${playerState?.draughtCoins ?: 250} Coins",
                                color = TextWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Text(
                "VANGUARD SECURE NAVIGATION",
                color = AmberGold,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Navigation items in sidebar
            val menuItems = listOf(
                Triple(AppScreen.MAIN_MENU, Icons.Default.Home, "Home Dashboard"),
                Triple(AppScreen.OFFLINE_MENU, Icons.Default.SportsEsports, "Offline Arenas"),
                Triple(AppScreen.ONLINE_MENU, Icons.Default.Public, "Online Gateway"),
                Triple(AppScreen.RANKING, Icons.Default.EmojiEvents, "Grandmaster Leaders"),
                Triple(AppScreen.WALLETS, Icons.Default.AccountBalanceWallet, "Ledger Coin Wallet"),
                Triple(AppScreen.STORE, Icons.Default.Storefront, "Skins & Stores"),
                Triple(AppScreen.MUSIC_SETTINGS, Icons.Default.MusicNote, "Sound & Remix Studio"),
                Triple(AppScreen.NOTIFICATIONS, Icons.Default.NotificationsActive, "Comms & Alerts"),
                Triple(AppScreen.SETTINGS, Icons.Default.Settings, "System Regulations")
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                menuItems.forEach { (screen, icon, label) ->
                    val isSelected = viewModel.currentScreen == screen
                    NavigationDrawerItem(
                        icon = { Icon(icon, contentDescription = null, tint = if (isSelected) AmberGold else TextGray) },
                        label = { Text(label, color = if (isSelected) AmberGold else TextWhite, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        selected = isSelected,
                        onClick = { onItemClick(screen) },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            selectedContainerColor = Color(0x1AFFD700)
                        ),
                        modifier = Modifier.height(40.dp)
                    )
                }
            }

            Divider(color = Color(0x1AFFFFFF))
            
            // App branding bottom
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "VANGUARD TACTICAL SYSTEM",
                    color = TextGray,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "v6.0.4 - Secure Platform",
                    color = TextGray,
                    fontSize = 8.sp
                )
            }
        }
    }
}

@Composable
fun MainGameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    if (viewModel.showSplashScreen) {
        SplashScreenComponent(viewModel = viewModel)
        return
    }

    val playerState by viewModel.playerState.collectAsState()
    val activeScreen = viewModel.currentScreen
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    AppBackgroundWrapper(activeScreen = activeScreen) {
        Box(modifier = Modifier.fillMaxSize()) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    DrawerContent(
                        viewModel = viewModel,
                        onItemClick = { screen ->
                            coroutineScope.launch { drawerState.close() }
                            viewModel.navigateTo(screen)
                            viewModel.playSfx(SoundManager.Sfx.NOTIFICATION)
                        },
                        onProfileClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.isProfileDialogOpen = true
                            viewModel.playSfx(SoundManager.Sfx.NOTIFICATION)
                        }
                    )
                }
            ) {
                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        TopGameAppBar(
                            title = when (activeScreen) {
                                AppScreen.MAIN_MENU -> "DRAUGHTS COMBAT"
                                AppScreen.OFFLINE_MENU -> "OFFLINE ARENA"
                                AppScreen.ONLINE_MENU -> "MULTIPLAYER GATEWAY"
                                AppScreen.GAME_SETUP -> "MATCH REGULATION SETUP"
                                AppScreen.GAME_BOT -> "CAMPAIGN: VS ${viewModel.currentBotPersona.name.replace("_", " ").uppercase()}"
                                AppScreen.GAME_LOCAL_PVP -> "LOCAL PASS & PLAY"
                                AppScreen.GAME_ONLINE_PVP -> "ONLINE 1V1 COMBAT"
                                AppScreen.ONLINE_COMPETITIONS -> "CHAMPIONSHIP CUPS"
                                AppScreen.WALLETS -> "SECURE LEDGER COIN WALLET"
                                AppScreen.STORE -> "TACTICAL SKIN STORE"
                                AppScreen.SETTINGS -> "SYSTEM REGULATIONS"
                                AppScreen.MUSIC_SETTINGS -> "ORCHESTRA SOUND STUDIO"
                                AppScreen.NOTIFICATIONS -> "COMMS & ALERTS"
                                AppScreen.RANKING -> "GRANDMASTER LEADERBOARD"
                                AppScreen.ONLINE_CHALLENGES -> "DIRECT CHALLENGE LOBBY"
                            },
                            viewModel = viewModel,
                            onMenuClick = {
                                coroutineScope.launch { drawerState.open() }
                                viewModel.playSfx(SoundManager.Sfx.NOTIFICATION)
                            },
                            showBackButton = when (activeScreen) {
                                AppScreen.MAIN_MENU, AppScreen.OFFLINE_MENU, AppScreen.ONLINE_MENU, AppScreen.RANKING -> false
                                else -> true
                            },
                            onBackClick = {
                                viewModel.navigateBack()
                                viewModel.playSfx(SoundManager.Sfx.NOTIFICATION)
                            }
                        )
                    },
                    bottomBar = {
                        GameBottomNavigation(
                            currentScreen = activeScreen,
                            onTabSelected = { screen ->
                                viewModel.navigateTo(screen)
                                viewModel.playSfx(SoundManager.Sfx.NOTIFICATION)
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (activeScreen) {
                            AppScreen.MAIN_MENU -> MainMenuScreen(viewModel)
                            AppScreen.OFFLINE_MENU -> OfflineMenuScreen(viewModel)
                            AppScreen.ONLINE_MENU -> OnlineMenuScreen(viewModel)
                            AppScreen.GAME_SETUP -> GameSetupScreen(viewModel)
                            AppScreen.GAME_BOT -> OfflineBotGameScreen(viewModel)
                            AppScreen.GAME_LOCAL_PVP -> OfflineLocalGameScreen(viewModel)
                            AppScreen.GAME_ONLINE_PVP -> OnlinePvPGameScreen(viewModel)
                            AppScreen.ONLINE_COMPETITIONS -> OnlineCompetitionsScreen(viewModel)
                            AppScreen.WALLETS -> WalletsScreen(viewModel)
                            AppScreen.STORE -> StoreScreenWrapper(viewModel)
                            AppScreen.SETTINGS -> SettingsScreen(viewModel)
                            AppScreen.MUSIC_SETTINGS -> MusicSettingsScreen(viewModel)
                            AppScreen.NOTIFICATIONS -> NotificationsScreen(viewModel)
                            AppScreen.RANKING -> RankingsScreen(viewModel)
                            AppScreen.ONLINE_CHALLENGES -> OnlineChallengesScreen(viewModel)
                        }
                    }
                }
            }

            // --- TOP MOST OVERLAYS & ALERTS LAYER (NEVER COVERED BY BOTTOM NAV OR DRAWERS) ---
            // Dragging chat overlay button in bots or online match mode
            if (activeScreen == AppScreen.GAME_BOT || activeScreen == AppScreen.GAME_ONLINE_PVP) {
                DraggableChatWidget(viewModel = viewModel)
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
                    val photo = playerState?.photoUri
                    if (photo != null) {
                        coil.compose.AsyncImage(
                            model = photo,
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Icon(
                            avatarIcon,
                            contentDescription = "Avatar",
                            tint = DarkBg,
                            modifier = Modifier.size(22.dp)
                        )
                    }
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
        tonalElevation = 0.dp,
        modifier = Modifier.border(
            androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.25f))
        )
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
                    selectedIconColor = DarkBg,
                    selectedTextColor = AmberGold,
                    indicatorColor = AmberGold,
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
                                GameViewModel.SelectedGameMode.ONLINE_MATCHMAKING -> Icons.Default.Group
                                GameViewModel.SelectedGameMode.COMPETITIONS -> Icons.Default.EmojiEvents
                            },
                            contentDescription = "Active Battle Mode",
                            tint = if (viewModel.isOnlineMode) Color(0xFF00E676) else RedCrimson,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (viewModel.selectedGameMode) {
                                GameViewModel.SelectedGameMode.LOCAL_PASS_AND_PLAY -> "🎮 LOCAL: PASS & PLAY"
                                GameViewModel.SelectedGameMode.OFFLINE_VS_BOT -> "🔮 OFFLINE: CAMPAIGN"
                                GameViewModel.SelectedGameMode.ONLINE_MATCHMAKING -> "⚔️ ONLINE: VS PLAYERS"
                                GameViewModel.SelectedGameMode.COMPETITIONS -> "🏆 COMPETITIONS"
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
                                    text = { Text("🤖 One vs Bot (Offline)", color = TextWhite, fontSize = 11.sp) },
                                    onClick = {
                                        viewModel.changeGameMode(GameViewModel.SelectedGameMode.OFFLINE_VS_BOT)
                                        showGameModeMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("🎮 One vs One (Offline)", color = TextWhite, fontSize = 11.sp) },
                                    onClick = {
                                        viewModel.changeGameMode(GameViewModel.SelectedGameMode.LOCAL_PASS_AND_PLAY)
                                        showGameModeMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("⚔️ One vs One (Online)", color = TextWhite, fontSize = 11.sp) },
                                    onClick = {
                                        viewModel.changeGameMode(GameViewModel.SelectedGameMode.ONLINE_MATCHMAKING)
                                        showGameModeMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("🏆 Tournaments / Competitions", color = TextWhite, fontSize = 11.sp) },
                                    onClick = {
                                        viewModel.changeGameMode(GameViewModel.SelectedGameMode.COMPETITIONS)
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
                        GameViewModel.SelectedGameMode.OFFLINE_VS_BOT -> "Battle the shadow commanders offline for grandmaster training."
                        GameViewModel.SelectedGameMode.LOCAL_PASS_AND_PLAY -> "Play with a friend locally on the same device. No account link required."
                        GameViewModel.SelectedGameMode.ONLINE_MATCHMAKING -> "Real matchmaking — get paired with another real signed-in player."
                        GameViewModel.SelectedGameMode.COMPETITIONS -> "Real tournaments organized by real players — bracket or league, whichever the organizer picks."
                    },
                    color = TextGray,
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // These two modes have real implementations (Firestore-backed matchmaking and
                // tournaments — see OnlineScreens.kt) — hand off to them here, which skips past
                // the local board below entirely for these modes.
                when (viewModel.selectedGameMode) {
                    GameViewModel.SelectedGameMode.ONLINE_MATCHMAKING -> {
                        com.example.ui.screens.RealOnlineMatchScreen(viewModel)
                        return@Column
                    }
                    GameViewModel.SelectedGameMode.COMPETITIONS -> {
                        com.example.ui.screens.TournamentBrowserScreen(viewModel, formatFilter = null)
                        return@Column
                    }
                    else -> {}
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
            
            // Honest disclosure: this is an AI opponent dressed up as an "online rival," not a
            // real networked match — there is no real-time video/voice calling here (there
            // never was a working implementation behind it, despite what the UI used to claim).
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, Color(0x33FFC107)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = AmberGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Playing an AI arena rival — real cross-device multiplayer isn't built yet.",
                        color = TextGray,
                        fontSize = 10.sp
                    )
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
        "neon" -> BorderStroke(2.5.dp, Brush.linearGradient(listOf(VioletNeon, Color.Cyan)))
        "cyberpunk" -> BorderStroke(2.5.dp, Brush.linearGradient(listOf(AmberGold, RedCrimson)))
        else -> BorderStroke(3.dp, Brush.linearGradient(listOf(DarkSurfaceVariant, DarkBg)))
    }

    val mandatoryJumpPieceIds = remember(viewModel.boardPieces, viewModel.turnRed) {
        viewModel.getPiecesWithMandatoryJumps()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "mandatory_jump_pulse")
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
                        val isMandatoryJump = pieceOnSquare != null && mandatoryJumpPieceIds.contains(pieceOnSquare.id)

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

                                if (isMandatoryJump) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize(0.85f)
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
                                            width = if (isSelected) 3.dp else (if (isMandatoryJump) 3.dp else 1.5.dp),
                                            color = if (isSelected) glowColor else (if (isMandatoryJump) Color(0xFFFF1744) else pieceColor.copy(alpha = 0.5f)),
                                            shape = CircleShape
                                        )
                                        .padding(if (pieceOnSquare.isKing) 6.dp else 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Custom Pixel Character Drawing
                                    HeroDrawing(heroId = pieceOnSquare.heroId, isRed = pieceOnSquare.isRed)

                                    // Mandatory Capture badge overlay (alert icon)
                                    if (isMandatoryJump) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .align(Alignment.TopStart)
                                                .background(Color(0xFFFF1744), CircleShape)
                                                .border(1.dp, Color.White, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.PriorityHigh,
                                                contentDescription = "Must Capture",
                                                tint = Color.White,
                                                modifier = Modifier.size(11.dp)
                                            )
                                        }
                                    }

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
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        border = BorderStroke(
                            1.2.dp,
                            if (isEquipped) Brush.linearGradient(listOf(AmberGold, AmberGold.copy(alpha = 0.2f)))
                            else if (isUnlocked) SolidColor(TextMuted)
                            else SolidColor(Color(0x11FFFFFF))
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
                            .background(
                                Brush.verticalGradient(listOf(DarkSurface, Color(0xFF0F111A))),
                                shape = RoundedCornerShape(12.dp)
                            )
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
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        border = BorderStroke(
                            1.2.dp,
                            if (isEquipped) Brush.linearGradient(listOf(AmberGold, AmberGold.copy(alpha = 0.2f)))
                            else SolidColor(Color(0x11FFFFFF))
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (isUnlocked) {
                                    viewModel.selectBoardStyle(styleId)
                                }
                            }
                            .background(
                                Brush.verticalGradient(listOf(DarkSurface, Color(0xFF0F111A))),
                                shape = RoundedCornerShape(12.dp)
                            )
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
                            text = if (viewModel.isGoogleSignedIn) "Cloud Profile Linked" else "Guest Account Active",
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
                    
                    if (viewModel.isSigningInEmail) {
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
                            Icon(Icons.Default.Login, contentDescription = "Sign In", tint = DarkBg, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sign In", color = DarkBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                                    " • Majority Capture Rule: if you have a choice between capture sequences, you MUST\n" +
                                    "   play whichever one captures the most pieces — shorter captures are blocked.\n" +
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
                            text = "Status: " + viewModel.webRtcStatus,
                            color = AmberGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text("Finding an arena rival...", color = TextGray, fontSize = 10.sp)
                    }
                } else {
                    Text(
                        "Test your tactical hero skills against an AI arena rival — real cross-device multiplayer isn't built yet, so this matches you with AI, not another person.",
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
                            text = if (viewModel.isGoogleSignedIn) "FIND MULTIPLAYER MATCH" else "LINK ACCOUNT FIRST",
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

        // Admin tooling now lives entirely outside the app, at awishworldgroup.xyz/Manager/ —
        // that's the right call: nothing that grants currency or flips game rules should be
        // reachable inside a client APK, since anyone with a decompiler can patch around an
        // on-device check regardless of how it's gated. This button is just a convenience link,
        // still gated to your real signed-in email — it has no admin powers of its own.
        if (viewModel.isGoogleSignedIn && viewModel.signedInEmail == "mukasadaniel.daniel@gmail.com") {
            OutlinedButton(
                onClick = {
                    val intent = android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse("https://awishworldgroup.xyz/Manager/")
                    )
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberGold),
                border = BorderStroke(1.dp, AmberGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Admin Panel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
    var tempTagline by remember { mutableStateOf(playerState?.tagline ?: "Tactical Overlord") }
    var tempPhoneNumber by remember { mutableStateOf(playerState?.phoneNumber ?: "") }
    var tempCountryCode by remember { mutableStateOf(playerState?.countryCode ?: "+256") }
    var tempPhotoUri by remember { mutableStateOf(playerState?.photoUri) }
    var showCountryDropdown by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val countryCodes = listOf(
        Pair("🇺🇬", "+256"),
        Pair("🇺🇸", "+1"),
        Pair("🇬🇧", "+44"),
        Pair("🇰🇪", "+254"),
        Pair("🇳🇬", "+234"),
        Pair("🇮🇳", "+91"),
        Pair("🇿🇦", "+27"),
        Pair("🇩🇪", "+49"),
        Pair("🇫🇷", "+33"),
        Pair("🇨🇦", "+1")
    )

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            tempPhotoUri = uri.toString()
        }
    }

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

                // Profile Photo Section - Dynamic & Modern
                Text(
                    text = "PROFILE PHOTO",
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(DarkBg)
                        .border(2.dp, AmberGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (tempPhotoUri != null) {
                        coil.compose.AsyncImage(
                            model = tempPhotoUri,
                            contentDescription = "Profile Photo Preview",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        // Fallback icon based on selected avatar
                        val fallbackIcon = when (viewModel.selectedAvatarId) {
                            "knight" -> Icons.Default.Shield
                            "mage" -> Icons.Default.AutoAwesome
                            "valkyrie" -> Icons.Default.LocalActivity
                            "assassin" -> Icons.Default.Security
                            "rogue" -> Icons.Default.Bolt
                            else -> Icons.Default.Person
                        }
                        Icon(
                            fallbackIcon,
                            contentDescription = "Fallback Avatar",
                            tint = AmberGold,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Button(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("SELECT PHOTO", color = DarkBg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    if (tempPhotoUri != null) {
                        Button(
                            onClick = { tempPhotoUri = null },
                            colors = ButtonDefaults.buttonColors(containerColor = RedCrimson),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("CLEAR", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
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
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Tagline
                Text(
                    text = "CUSTOM TAGLINE",
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = tempTagline,
                    onValueChange = { tempTagline = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedContainerColor = DarkBg,
                        unfocusedContainerColor = DarkBg
                    ),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                    placeholder = { Text("e.g. Tactical Overlord", color = Color.Gray, fontSize = 13.sp) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Phone number and Country code
                Text(
                    text = "PHONE NUMBER & COUNTRY CODE",
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
                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            .background(DarkBg, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                            .clickable { showCountryDropdown = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val currentCountry = countryCodes.find { it.second == tempCountryCode }
                            Text(
                                text = "${currentCountry?.first ?: "🇺🇬"} $tempCountryCode",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = AmberGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showCountryDropdown,
                            onDismissRequest = { showCountryDropdown = false },
                            modifier = Modifier.background(DarkSurface)
                        ) {
                            countryCodes.forEach { country ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${country.first}  ${country.second}",
                                            color = Color.White,
                                            fontSize = 13.sp
                                        )
                                    },
                                    onClick = {
                                        tempCountryCode = country.second
                                        showCountryDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = tempPhoneNumber,
                        onValueChange = { tempPhoneNumber = it },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AmberGold,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedContainerColor = DarkBg,
                            unfocusedContainerColor = DarkBg
                        ),
                        modifier = Modifier.weight(1f).height(48.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                        placeholder = { Text("700000000", color = Color.Gray, fontSize = 13.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (tempName.trim().length >= 3) {
                            viewModel.updateFullProfile(
                                newName = tempName.trim(),
                                newTagline = tempTagline.trim(),
                                newPhoneNumber = tempPhoneNumber.trim(),
                                newCountryCode = tempCountryCode,
                                newPhotoUri = tempPhotoUri
                            )
                        } else {
                            viewModel.triggerNotification("Name must be at least 3 characters!")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Save", tint = DarkBg, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SAVE PROFILE CHANGES", color = DarkBg, fontSize = 12.sp, fontWeight = FontWeight.Black)
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

                // Cloud Account Link Section
                Text(
                    text = "CLOUD ACCOUNT ASSOCIATION",
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
                            Text("Connect your email account to unlock online matchmaking.", color = TextMuted, fontSize = 9.sp)
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
                            Icon(Icons.Default.Login, contentDescription = "Account", tint = DarkBg, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Link Account", color = DarkBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf(0) } // 0 = Google Auth, 1 = Email Auth
    var showPassword by remember { mutableStateOf(false) }
    var termsCheckboxChecked by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { 
            if (!viewModel.isSigningInGoogle && !viewModel.isSigningInEmail) {
                viewModel.isGoogleAuthDialogOpen = false 
                viewModel.showTermsOverlay = false
            }
        },
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
            if (viewModel.showTermsOverlay) {
                // TERMS & CONDITIONS REDIRECT VIEW
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Terms & Privacy Policy",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "To keep Draughts Combat fair, secure, and integrated, you must review and accept our guidelines before authenticating your account.",
                        color = TextGray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF07090C)),
                        border = BorderStroke(1.dp, Color(0x332979FF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                Text(
                                    text = "📄 1. UNIFIED AUTHENTICATION SERVICES",
                                    color = AmberGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Your gameplay profile, leaderboard ranking points, local hero card inventories, and Draughts Coins (BLC) are synced securely to Google Firebase databases.",
                                    color = TextGray,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            }
                            item {
                                Text(
                                    text = "🔐 2. DATA CONSENT & PRIVACY",
                                    color = AmberGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "We collect your verified email, display profile name, and unique authentication UID. We never sell, rent, or distribute your email or game data to any third party.",
                                    color = TextGray,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            }
                            item {
                                Text(
                                    text = "⚡ 3. COIN MIGRATION & RECOUPING",
                                    color = AmberGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "By creating an account, local guest coins are permanently merged and uploaded to your cloud ledger. Any duplicate guest profiles will be unified under your cloud identity.",
                                    color = TextGray,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            }
                            item {
                                Text(
                                    text = "🛡️ 4. ANTI-TAMPERING & FAIR PLAY",
                                    color = AmberGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Modifying SQLite local database footprints, manipulating memory, executing decompilation hacks, or spamming network packets will activate automatic app security bans. All operations are auditable.",
                                    color = TextGray,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = termsCheckboxChecked,
                            onCheckedChange = { termsCheckboxChecked = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AmberGold,
                                uncheckedColor = Color.Gray,
                                checkmarkColor = DarkBg
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "I read and accept the Terms & Privacy Policy.",
                            color = TextWhite,
                            fontSize = 11.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { 
                                viewModel.showTermsOverlay = false
                                viewModel.pendingAuthAction = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = BorderStroke(1.dp, Color.Gray),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Text("BACK", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.isTermsAccepted = true
                                viewModel.showTermsOverlay = false
                                val action = viewModel.pendingAuthAction
                                viewModel.pendingAuthAction = null
                                action?.invoke()
                            },
                            enabled = termsCheckboxChecked,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberGold,
                                disabledContainerColor = Color.Gray
                            ),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Text("ACCEPT & PROCEED", color = DarkBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // AUTH SELECTION & FORM VIEW (Google vs Email)
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header / Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF2979FF),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Account Integration",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // EMAIL AUTHENTICATION VIEW DIRECTLY
                    Text(
                        text = if (viewModel.isEmailRegisterMode) 
                            "Create a new secure game account using your email address and a password." 
                        else 
                            "Sign in with your registered email and password to sync and load your profile.",
                        color = TextGray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Email Field
                    OutlinedTextField(
                        value = viewModel.emailInput,
                        onValueChange = { viewModel.emailInput = it },
                        label = { Text("Email Address", color = Color.Gray, fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2979FF),
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Password Field
                    OutlinedTextField(
                        value = viewModel.passwordInput,
                        onValueChange = { viewModel.passwordInput = it },
                        label = { Text("Password", color = Color.Gray, fontSize = 11.sp) },
                        singleLine = true,
                        visualTransformation = if (showPassword) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(
                                onClick = { showPassword = !showPassword },
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = if (showPassword) "HIDE" else "SHOW",
                                    color = AmberGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2979FF),
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mode Switch Row (Sign In vs Register)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (viewModel.isEmailRegisterMode) "Already have an account?" else "Don't have an account?",
                            color = TextGray,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        TextButton(
                            onClick = { 
                                viewModel.isEmailRegisterMode = !viewModel.isEmailRegisterMode
                                viewModel.emailAuthError = null
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = if (viewModel.isEmailRegisterMode) "Sign In" else "Create Account",
                                color = AmberGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (viewModel.isSigningInEmail) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = Color(0xFF2979FF), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Authenticating...", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        viewModel.emailAuthError?.let { err ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0x33FF5252)),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                            ) {
                                Text(
                                    text = err,
                                    color = Color(0xFFFFCDD2),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val triggerLogin = {
                                    viewModel.startEmailAuth(context) {
                                        viewModel.isGoogleAuthDialogOpen = false
                                    }
                                }
                                if (!viewModel.isTermsAccepted) {
                                    viewModel.pendingAuthAction = triggerLogin
                                    viewModel.showTermsOverlay = true
                                } else {
                                    triggerLogin()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF)),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text(
                                text = if (viewModel.isEmailRegisterMode) "CREATE SECURE ACCOUNT" else "AUTHENTICATE EMAIL",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.isGoogleAuthDialogOpen = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = BorderStroke(1.dp, Color.Gray),
                            modifier = Modifier.fillMaxWidth().height(40.dp)
                        ) {
                            Text("CANCEL", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // DIAGNOSTICS & TRACE CONSOLE (shared by Google and Email auth flows)
                    if (viewModel.googleSignInTraceLogs.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "DIAGNOSTICS & TRACE CONSOLE",
                                color = AmberGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                            TextButton(
                                onClick = {
                                    val logText = viewModel.googleSignInTraceLogs.joinToString("\n")
                                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(logText))
                                    viewModel.triggerNotification("Trace logs copied to clipboard!")
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy entire log payload",
                                    tint = AmberGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("COPY ALL LOGS", color = AmberGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF07090C)),
                            border = BorderStroke(1.dp, Color(0x332979FF)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                        ) {
                            androidx.compose.foundation.text.selection.SelectionContainer {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize().padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(viewModel.googleSignInTraceLogs) { logLine ->
                                        val textColor = when {
                                            logLine.startsWith("[FATAL]") || logLine.startsWith("[ERROR]") -> Color(0xFFFF5252)
                                            logLine.contains("[SUCCESS]") -> Color(0xFF4CAF50)
                                            logLine.startsWith("[TRACE]") -> Color(0xFFB0BEC5)
                                            else -> Color(0xFF42A5F5)
                                        }
                                        Text(
                                            text = logLine,
                                            color = textColor,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            lineHeight = 12.sp
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

@Composable
fun OnlineChallengesScreen(viewModel: GameViewModel) {
    val onlinePlayers = viewModel.onlinePresencePlayers
    val incoming = viewModel.incomingChallenges
    val outgoing = viewModel.outgoingChallenges
    val isSigned = viewModel.isGoogleSignedIn

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!isSigned) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
                border = BorderStroke(1.dp, Color(0x33FF9800)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "SIGN IN REQUIRED",
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "You are currently playing as a Guest. Direct online challenges are only available when signed into a Google Account.",
                        color = TextWhite,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            return@Column
        }

        // 1. Incoming Challenges Section
        if (incoming.isNotEmpty()) {
            Text(
                text = "INCOMING CHALLENGE REQUESTS",
                color = Color(0xFF00E676),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            incoming.forEach { challenge ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF171A2E)),
                    border = BorderStroke(1.5.dp, Color(0xFF00E676)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "⚔️ CHALLENGE FROM ${challenge.senderName}",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Mode: ${challenge.ruleSystem.replace('_', ' ')}",
                                color = TextGray,
                                fontSize = 10.sp
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.respondToIncomingChallenge(challenge.requestId, false, challenge.ruleSystem) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("DECLINE", color = TextWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.respondToIncomingChallenge(challenge.requestId, true, challenge.ruleSystem) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("ACCEPT", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 2. Outgoing Challenges Section
        if (outgoing.isNotEmpty()) {
            Text(
                text = "YOUR ACTIVE SENT REQUESTS",
                color = AmberGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            outgoing.forEach { challenge ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
                    border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Challenge to ${challenge.receiverName}",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Status: ${challenge.status} | Mode: ${challenge.ruleSystem.replace('_', ' ')}",
                                color = TextGray,
                                fontSize = 10.sp
                            )
                        }
                        CircularProgressIndicator(
                            color = AmberGold,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }

        // 3. All Online Players Section
        Text(
            text = "ALL ONLINE PLAYERS",
            color = Color(0xFFFF9800),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )

        if (onlinePlayers.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xAA0F111A)),
                border = BorderStroke(1.dp, Color(0x11FFFFFF)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Public, contentDescription = null, tint = TextGray, modifier = Modifier.size(36.dp))
                        Text(
                            text = "No other players are currently online.",
                            color = TextGray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Invite friends or wait for grandmasters to connect!",
                            color = TextGray.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            onlinePlayers.forEach { player ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF171A2E)),
                    border = BorderStroke(1.dp, if (player.inGame) Color(0x22FFFFFF) else Color(0x44FF9800)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Online Indicator
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        color = if (player.inGame) Color(0xFFE53935) else Color(0xFF00E676),
                                        shape = CircleShape
                                    )
                            )
                            Column {
                                Text(
                                    text = player.name,
                                    color = TextWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Rating: ${player.mmr} MMR | Mode: ${player.ruleSystem.replace('_', ' ')}",
                                    color = TextGray,
                                    fontSize = 10.sp
                                )
                                if (player.inGame) {
                                    Text(
                                        text = "CURRENTLY IN BATTLE ⚔️",
                                        color = Color(0xFFE53935),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                } else {
                                    Text(
                                        text = "READY FOR BATTLE 🎮",
                                        color = Color(0xFF00E676),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                        Button(
                            onClick = { viewModel.sendDirectChallenge(player.uid, player.name) },
                            enabled = !player.inGame,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800),
                                disabledContainerColor = Color(0x33FFFFFF)
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = if (player.inGame) "IN GAME" else "CHALLENGE",
                                color = if (player.inGame) TextGray else Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
