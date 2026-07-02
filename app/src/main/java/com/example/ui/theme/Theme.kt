package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AbyssalColorScheme = darkColorScheme(
    primary = RedCrimson,
    onPrimary = TextWhite,
    secondary = AmberGold,
    onSecondary = DarkBg,
    tertiary = VioletNeon,
    onTertiary = TextWhite,
    background = DarkBg,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextGray,
    error = RedCrimson,
    onError = TextWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme for consistency with game aesthetic
    dynamicColor: Boolean = false, // Disable dynamic colors to maintain a cohesive gaming brand
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AbyssalColorScheme,
        typography = Typography,
        content = content
    )
}
