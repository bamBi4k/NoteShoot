package com.sentinel.noteshoot.ui.theme

import androidx.compose.ui.graphics.Color

enum class ThemeMode { DARK, LIGHT }

data class ThemeSpec(
    val id: String,
    val displayName: String,
    val mode: ThemeMode,

    // Typography — resolved at runtime to FontFamily
    val fontHeadingId: Int? = null,     // R.font.xxx or null = default
    val fontBodyId: Int? = null,
    val fontCodeId: Int? = null,

    // Fonts
    val fontsPrimary: Color,
    val fontsSecondary: Color,
    val fontsHeadings: Color,
    val fontsLinks: Color,
    val fontsCode: Color,

    // Backgrounds
    val bgBase: Color,
    val bgMantle: Color,
    val bgCrust: Color,
    val bgSurface: Color,
    val bgOverlay: Color,

    // Buttons
    val buttonBg: Color,
    val buttonText: Color,
    val buttonPrimaryBg: Color,
    val buttonPrimaryText: Color,
    val buttonHover: Color,
    val buttonActive: Color,
    val buttonDisabledBg: Color,
    val buttonDisabledText: Color,

    // Bevels
    val bevelHighlight: Color,
    val bevelShadow: Color,
    val bevelBorder: Color,

    // Console
    val consoleBg: Color,
    val consoleText: Color,
    val consolePrompt: Color,
    val consoleError: Color,
    val consoleWarning: Color,
    val consoleSuccess: Color,
    val consoleInfo: Color,

    // Accents
    val accentMauve: Color,
    val accentPink: Color,
    val accentPeach: Color,
    val accentYellow: Color,
    val accentGreen: Color,
    val accentTeal: Color,
    val accentSky: Color,
    val accentLavender: Color,
) {
    /** True when this theme uses sharp corners + bevels (VGUI-style). */
    val isBeveled: Boolean get() = id == "vgui"
}