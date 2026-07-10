package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldHighlight
import com.example.ui.theme.AmberGoldShadow
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.RedCrimson
import com.example.ui.theme.RedCrimsonDark

/**
 * HOW THESE REFERENCE-STYLE GAME UI KITS ARE ACTUALLY BUILT (what this file replicates without
 * needing any image assets):
 *
 * 1. BEVELED 3D BUTTON — a rounded shape filled with a top-to-bottom gradient (light → mid-tone
 *    → darker), plus a bright glossy highlight sliver across just the top portion. That
 *    combination is what reads as a "raised, glossy button cap" rather than a flat rectangle.
 *    A drop shadow underneath sells the elevation.
 * 2. GRADIENT PANEL — same idea at a bigger scale: a card background that's subtly lighter at
 *    the top than the bottom, plus a saturated accent-colored border, is what makes these panels
 *    look like distinct "premium" surfaces instead of flat Material cards.
 * 3. ICON BADGES — a small rounded/circular container with its own gradient fill and a colored
 *    ring border around it, used consistently for currency icons, achievement icons, portraits.
 * 4. PROGRESS BARS — a gradient fill plus a lighter glossy stripe across just the top portion of
 *    the filled section, same bevel trick as the buttons, applied to a pill shape.
 */

/** A beveled, gold, 3D-looking primary action button — the workhorse CTA style from the references. */
@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null,
    baseColor: Color = AmberGold,
    highlight: Color = AmberGoldHighlight,
    shadowColor: Color = AmberGoldShadow,
    textColor: Color = DarkBg,
    height: Dp = 52.dp,
    shape: Shape = RoundedCornerShape(14.dp)
) {
    val contentAlpha = if (enabled) 1f else 0.45f
    Box(
        modifier = modifier
            .height(height)
            .shadow(elevation = if (enabled) 6.dp else 0.dp, shape = shape, clip = false)
            .clip(shape)
            .background(Brush.verticalGradient(colors = listOf(highlight, baseColor, shadowColor)))
            .border(1.dp, Color.Black.copy(alpha = 0.25f), shape)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glossy top highlight sliver — the "raised cap" look
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height * 0.4f)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0f))
                    ),
                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
                )
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.alpha(contentAlpha)) {
            icon?.invoke()
            Text(
                text = text,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

/** Same beveled treatment, but for a secondary/danger action (crimson) — resign, delete, decline. */
@Composable
fun GameDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 48.dp
) {
    GameButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        baseColor = RedCrimson,
        highlight = Color(0xFFFF8A80),
        shadowColor = RedCrimsonDark,
        textColor = Color.White,
        height = height
    )
}

/** A gradient panel/card with an accent border and soft elevation — the "premium surface" look. */
@Composable
fun GamePanel(
    modifier: Modifier = Modifier,
    accentColor: Color = AmberGold,
    topColor: Color = DarkSurfaceVariant,
    bottomColor: Color = DarkSurface,
    shape: Shape = RoundedCornerShape(16.dp),
    contentPadding: PaddingValues = PaddingValues(14.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation = 4.dp, shape = shape, clip = false)
            .clip(shape)
            .background(Brush.verticalGradient(colors = listOf(topColor, bottomColor)))
            .border(1.5.dp, accentColor.copy(alpha = 0.5f), shape)
            .padding(contentPadding)
    ) {
        content()
    }
}

/** A small icon badge — rounded container with gradient fill and a colored ring, for currency/achievement icons. */
@Composable
fun GameIconBadge(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    ringColor: Color = AmberGold,
    topColor: Color = Color(0xFF3A3F5C),
    bottomColor: Color = Color(0xFF20233A),
    shape: Shape = RoundedCornerShape(12.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .height(size)
            .background(Brush.verticalGradient(colors = listOf(topColor, bottomColor)), shape)
            .border(2.dp, ringColor, shape),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/** A gradient progress/stat bar with a glossy highlight stripe, matching the reference sliders. */
@Composable
fun GameProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    trackColor: Color = DarkSurfaceVariant,
    fillStart: Color = AmberGoldHighlight,
    fillEnd: Color = AmberGold,
    height: Dp = 14.dp
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(trackColor)
            .border(1.dp, Color.Black.copy(alpha = 0.3f), shape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(height)
                .background(Brush.horizontalGradient(colors = listOf(fillStart, fillEnd)), shape)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(height * 0.4f)
                .background(
                    Brush.verticalGradient(colors = listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0f))),
                    RoundedCornerShape(topStart = 50f, topEnd = 50f)
                )
        )
    }
}
