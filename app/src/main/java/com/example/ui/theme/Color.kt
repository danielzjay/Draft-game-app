package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Game Theme Palette — rebuilt to actually match "AmberGold"/"RedCrimson" by name, not just in
// name. The previous values here were pale Material-You pastel purples (#EADDFF, #D0BCFF) —
// completely disconnected from what the variable names claimed and from how a real mobile game
// UI (bold, saturated, high-contrast, beveled 3D buttons) is actually built. These are real,
// deep, saturated colors modeled on that reference style: rich gold with a highlight/shadow
// pair for bevel effects, deep crimson red, and a vivid violet — plus a near-black navy
// background instead of a flat grey, which is what makes those reference UIs read as "premium"
// rather than "default Android dark mode."
val DarkBg = Color(0xFF0C0E1A)              // Deep near-black navy background
val DarkSurface = Color(0xFF171A2E)         // Card/panel surface, one step up from bg
val DarkSurfaceVariant = Color(0xFF2A2F4A)  // Lighter container surface / borders

val AmberGold = Color(0xFFFFC738)           // Real gold — primary accent, CTAs, currency
val AmberGoldHighlight = Color(0xFFFFF3C4)  // Top-edge bevel highlight for gold buttons
val AmberGoldShadow = Color(0xFFB8790A)     // Bottom-edge bevel shadow for gold buttons

val RedCrimson = Color(0xFFE63946)          // Real crimson red — danger, Shadow Clan, defeat
val RedCrimsonDark = Color(0xFF8E1F28)      // Deep crimson shadow/bevel + dark accents

val VioletNeon = Color(0xFF8B5CF6)          // Vivid violet — Vanguard Order accent, highlights
val ShadowVioletDark = Color(0xFF241B3A)    // Deep violet panel surface (Vanguard-themed cards)

val TextWhite = Color(0xFFF5F3FF)           // High contrast light text
val TextGray = Color(0xFFB8B3CC)            // Supporting medium contrast text
val TextMuted = Color(0xFF6B6685)           // Disabled or muted labels

val HealthGreen = Color(0xFF4CAF50)         // Saturated health green
val ManaBlue = Color(0xFF3B82F6)            // Saturated mana/energy blue
val ShieldOrange = Color(0xFFFF9800)        // Saturated shield/warning orange
val GridDarkSquare = Color(0xFF171A2E)      // Aligned dark square for game board
val GridLightSquare = Color(0xFF2A2F4A)     // Aligned light square for game board
val GridHighlight = Color(0x66FFC738)       // Gold move highlight
val GridAttackHighlight = Color(0x88E63946) // Crimson warning highlight for attacks
