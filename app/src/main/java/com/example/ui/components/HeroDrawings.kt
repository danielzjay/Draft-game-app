package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.*

@Composable
fun HeroDrawing(
    heroId: String,
    isRed: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val mainColor = if (isRed) Color(0xFFFF2B55) else Color(0xFF9D4EDD)
        val accentColor = if (isRed) Color(0xFFFFB300) else Color(0xFF00E676)

        when (heroId) {
            "knight" -> drawKnight(w, h, mainColor, accentColor)
            "mage" -> drawMage(w, h, mainColor, accentColor)
            "rogue" -> drawRogue(w, h, mainColor, accentColor)
            "valkyrie" -> drawValkyrie(w, h, mainColor, accentColor)
            "necromancer" -> drawNecromancer(w, h, mainColor, accentColor)
            "assassin" -> drawAssassin(w, h, mainColor, accentColor)
            "warlock" -> drawWarlock(w, h, mainColor, accentColor)
            "death_knight" -> drawDeathKnight(w, h, mainColor, accentColor)
            else -> {
                // Default fallback: crown or sphere
                drawCircle(color = mainColor, radius = w * 0.3f, center = Offset(w * 0.5f, h * 0.5f))
                drawCircle(color = accentColor, radius = w * 0.12f, center = Offset(w * 0.5f, h * 0.5f))
            }
        }
    }
}

// 🛡️ Knight: Draws a medieval helm with gold crest
private fun DrawScope.drawKnight(w: Float, h: Float, main: Color, accent: Color) {
    // Helm base
    drawRoundRect(
        color = Color(0xFF90A4AE),
        topLeft = Offset(w * 0.28f, h * 0.35f),
        size = Size(w * 0.44f, h * 0.45f),
        cornerRadius = CornerRadius(w * 0.08f)
    )
    // T-shaped visor gap
    drawRect(
        color = Color(0xFF263238),
        topLeft = Offset(w * 0.44f, h * 0.45f),
        size = Size(w * 0.12f, h * 0.25f)
    )
    drawRect(
        color = Color(0xFF263238),
        topLeft = Offset(w * 0.35f, h * 0.48f),
        size = Size(w * 0.3f, h * 0.08f)
    )
    // Gold plume/crest
    val path = Path().apply {
        moveTo(w * 0.5f, h * 0.35f)
        lineTo(w * 0.35f, h * 0.15f)
        lineTo(w * 0.5f, h * 0.22f)
        lineTo(w * 0.65f, h * 0.15f)
        close()
    }
    drawPath(path = path, color = accent)
}

// 🔮 Mage: Draws a blazing flame orb inside a magic crystal staff
private fun DrawScope.drawMage(w: Float, h: Float, main: Color, accent: Color) {
    // Staff rod
    drawLine(
        color = Color(0xFF8D6E63),
        start = Offset(w * 0.5f, h * 0.85f),
        end = Offset(w * 0.5f, h * 0.32f),
        strokeWidth = w * 0.08f
    )
    // Crystal base brackets
    drawCircle(
        color = Color(0xFFB0BEC5),
        radius = w * 0.18f,
        center = Offset(w * 0.5f, h * 0.35f),
        style = Stroke(width = w * 0.06f)
    )
    // Blazing Core
    drawCircle(
        color = main,
        radius = w * 0.12f,
        center = Offset(w * 0.5f, h * 0.35f)
    )
    drawCircle(
        color = accent,
        radius = w * 0.06f,
        center = Offset(w * 0.5f, h * 0.35f)
    )
}

// 🗡️ Rogue: Draws a poison-coated assassin dagger
private fun DrawScope.drawRogue(w: Float, h: Float, main: Color, accent: Color) {
    // Blade (angled pointing down-left)
    val blade = Path().apply {
        moveTo(w * 0.65f, h * 0.25f)
        lineTo(w * 0.25f, h * 0.65f)
        lineTo(w * 0.35f, h * 0.72f)
        lineTo(w * 0.72f, h * 0.35f)
        close()
    }
    drawPath(path = blade, color = Color(0xFFCFD8DC))

    // Poison Edge overlay
    val poison = Path().apply {
        moveTo(w * 0.65f, h * 0.25f)
        lineTo(w * 0.25f, h * 0.65f)
        lineTo(w * 0.32f, h * 0.68f)
        close()
    }
    drawPath(path = poison, color = Color(0xFF00E676)) // Neon poison green

    // Hilt and guard
    drawLine(
        color = Color(0xFFFFB300),
        start = Offset(w * 0.62f, h * 0.32f),
        end = Offset(w * 0.75f, h * 0.45f),
        strokeWidth = w * 0.08f
    )
    // Pommel jewel
    drawCircle(
        color = main,
        radius = w * 0.05f,
        center = Offset(w * 0.75f, h * 0.45f)
    )
}

// 👼 Valkyrie: Draws golden angelic wings and a shining halo
private fun DrawScope.drawValkyrie(w: Float, h: Float, main: Color, accent: Color) {
    // Wings left & right
    val leftWing = Path().apply {
        moveTo(w * 0.45f, h * 0.55f)
        lineTo(w * 0.15f, h * 0.35f)
        lineTo(w * 0.25f, h * 0.65f)
        lineTo(w * 0.42f, h * 0.62f)
        close()
    }
    val rightWing = Path().apply {
        moveTo(w * 0.55f, h * 0.55f)
        lineTo(w * 0.85f, h * 0.35f)
        lineTo(w * 0.75f, h * 0.65f)
        lineTo(w * 0.58f, h * 0.62f)
        close()
    }
    drawPath(path = leftWing, color = Color(0xFFECEFF1))
    drawPath(path = rightWing, color = Color(0xFFECEFF1))

    // Halo (floating above)
    drawOval(
        color = accent,
        topLeft = Offset(w * 0.32f, h * 0.2f),
        size = Size(w * 0.36f, h * 0.1f),
        style = Stroke(width = w * 0.05f)
    )

    // Center spear/shield core
    drawCircle(
        color = main,
        radius = w * 0.1f,
        center = Offset(w * 0.5f, h * 0.55f)
    )
}

// 💀 Necromancer: Draws a glowing skull of shadow energy
private fun DrawScope.drawNecromancer(w: Float, h: Float, main: Color, accent: Color) {
    // Skull head
    drawOval(
        color = Color(0xFFECEFF1),
        topLeft = Offset(w * 0.32f, h * 0.28f),
        size = Size(w * 0.36f, h * 0.34f)
    )
    // Jaw
    drawRect(
        color = Color(0xFFECEFF1),
        topLeft = Offset(w * 0.38f, h * 0.58f),
        size = Size(w * 0.24f, h * 0.14f)
    )
    // Eye holes (glowing green/purple)
    drawCircle(
        color = main,
        radius = w * 0.05f,
        center = Offset(w * 0.42f, h * 0.44f)
    )
    drawCircle(
        color = main,
        radius = w * 0.05f,
        center = Offset(w * 0.58f, h * 0.44f)
    )
    // Nose cavity
    val nose = Path().apply {
        moveTo(w * 0.5f, h * 0.49f)
        lineTo(w * 0.47f, h * 0.55f)
        lineTo(w * 0.53f, h * 0.55f)
        close()
    }
    drawPath(path = nose, color = Color(0xFF263238))
}

// ⚔️ Assassin: Draws dual crossed crimson ninja daggers
private fun DrawScope.drawAssassin(w: Float, h: Float, main: Color, accent: Color) {
    // First dagger
    drawLine(
        color = Color(0xFFFF2B55),
        start = Offset(w * 0.25f, h * 0.25f),
        end = Offset(w * 0.75f, h * 0.75f),
        strokeWidth = w * 0.06f
    )
    // Second dagger (crossed)
    drawLine(
        color = Color(0xFFFF2B55),
        start = Offset(w * 0.75f, h * 0.25f),
        end = Offset(w * 0.25f, h * 0.75f),
        strokeWidth = w * 0.06f
    )
    // Center glow point
    drawCircle(
        color = Color.White,
        radius = w * 0.06f,
        center = Offset(w * 0.5f, h * 0.5f)
    )
}

// 🔮 Warlock: Draws a demonic swirling abyssal eye
private fun DrawScope.drawWarlock(w: Float, h: Float, main: Color, accent: Color) {
    // Outer shadow ring
    drawCircle(
        color = ShadowVioletDark,
        radius = w * 0.25f,
        center = Offset(w * 0.5f, h * 0.5f)
    )
    // Demonic eye contour
    val eyePath = Path().apply {
        moveTo(w * 0.22f, h * 0.5f)
        quadraticTo(w * 0.5f, h * 0.25f, w * 0.78f, h * 0.5f)
        quadraticTo(w * 0.5f, h * 0.75f, w * 0.22f, h * 0.5f)
        close()
    }
    drawPath(path = eyePath, color = Color.Black)

    // Glowing pupil
    drawCircle(
        color = main,
        radius = w * 0.09f,
        center = Offset(w * 0.5f, h * 0.5f)
    )
    drawCircle(
        color = Color(0xFFFFEB3B), // Yellow center slit
        radius = w * 0.03f,
        center = Offset(w * 0.5f, h * 0.5f)
    )
}

// 🔱 Death Knight: Heavy runic iron armor with glowing frost gems
private fun DrawScope.drawDeathKnight(w: Float, h: Float, main: Color, accent: Color) {
    // Heavy breastplate outline
    val plate = Path().apply {
        moveTo(w * 0.3f, h * 0.3f)
        lineTo(w * 0.7f, h * 0.3f)
        lineTo(w * 0.8f, h * 0.65f)
        lineTo(w * 0.5f, h * 0.85f)
        lineTo(w * 0.2f, h * 0.65f)
        close()
    }
    drawPath(path = plate, color = Color(0xFF37474F))

    // Frost gems (glowing cyan / blue)
    drawCircle(
        color = Color(0xFF00E5FF),
        radius = w * 0.06f,
        center = Offset(w * 0.4f, h * 0.48f)
    )
    drawCircle(
        color = Color(0xFF00E5FF),
        radius = w * 0.06f,
        center = Offset(w * 0.6f, h * 0.48f)
    )

    // Center dark rune engraving
    drawLine(
        color = Color.Black,
        start = Offset(w * 0.5f, h * 0.35f),
        end = Offset(w * 0.5f, h * 0.7f),
        strokeWidth = w * 0.04f
    )
}
