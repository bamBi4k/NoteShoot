package com.sentinel.noteshoot.ui.theme

import androidx.compose.ui.graphics.Color

object ThemeCatalog {

    val monoDark = ThemeSpec(
        id = "mono_dark",
        displayName = "Mono Dark",
        mode = ThemeMode.DARK,

        fontsPrimary   = Color(0xFFE8E8E8),
        fontsSecondary = Color(0xFF9A9A9A),
        fontsHeadings  = Color(0xFFFFFFFF),
        fontsLinks     = Color(0xFFD0D0D0),
        fontsCode      = Color(0xFFB0B0B0),

        bgBase    = Color(0xFF0A0A0A),
        bgMantle  = Color(0xFF141414),
        bgCrust   = Color(0xFF000000),
        bgSurface = Color(0xFF1A1A1A),
        bgOverlay = Color(0xFF2A2A2A),

        buttonBg           = Color(0xFF2A2A2A),
        buttonText         = Color(0xFFE8E8E8),
        buttonPrimaryBg    = Color(0xFFE8E8E8),
        buttonPrimaryText  = Color(0xFF0A0A0A),
        buttonHover        = Color(0xFFCFCFCF),
        buttonActive       = Color(0xFFFFFFFF),
        buttonDisabledBg   = Color(0xFF1F1F1F),
        buttonDisabledText = Color(0xFF5A5A5A),

        bevelHighlight = Color(0xFF3A3A3A),
        bevelShadow    = Color(0xFF000000),
        bevelBorder    = Color(0xFF2E2E2E),

        consoleBg      = Color(0xFF141414),
        consoleText    = Color(0xFFE8E8E8),
        consolePrompt  = Color(0xFFCFCFCF),
        consoleError   = Color(0xFFFF6B6B),
        consoleWarning = Color(0xFFE8C878),
        consoleSuccess = Color(0xFF8FD98F),
        consoleInfo    = Color(0xFFCFCFCF),

        accentMauve    = Color(0xFFB8B8B8),
        accentPink     = Color(0xFFC0C0C0),
        accentPeach    = Color(0xFFAAAAAA),
        accentYellow   = Color(0xFFD0D0D0),
        accentGreen    = Color(0xFF909090),
        accentTeal     = Color(0xFFA0A0A0),
        accentSky      = Color(0xFFB0B0B0),
        accentLavender = Color(0xFFBFBFBF),
    )

    val monoLight = ThemeSpec(
        id = "mono_light",
        displayName = "Mono Light",
        mode = ThemeMode.LIGHT,

        fontsPrimary   = Color(0xFF1A1A1A),
        fontsSecondary = Color(0xFF555555),
        fontsHeadings  = Color(0xFF000000),
        fontsLinks     = Color(0xFF2E2E2E),
        fontsCode      = Color(0xFF333333),

        bgBase    = Color(0xFFFFFFFF),
        bgMantle  = Color(0xFFF5F5F5),
        bgCrust   = Color(0xFFEEEEEE),
        bgSurface = Color(0xFFFAFAFA),
        bgOverlay = Color(0xFFE8E8E8),

        buttonBg           = Color(0xFFEFEFEF),
        buttonText         = Color(0xFF1A1A1A),
        buttonPrimaryBg    = Color(0xFF1A1A1A),
        buttonPrimaryText  = Color(0xFFFFFFFF),
        buttonHover        = Color(0xFFD8D8D8),
        buttonActive       = Color(0xFFCFCFCF),
        buttonDisabledBg   = Color(0xFFF0F0F0),
        buttonDisabledText = Color(0xFFAAAAAA),

        bevelHighlight = Color(0xFFFFFFFF),
        bevelShadow    = Color(0xFFB8B8B8),
        bevelBorder    = Color(0xFFD0D0D0),

        consoleBg      = Color(0xFFF5F5F5),
        consoleText    = Color(0xFF1A1A1A),
        consolePrompt  = Color(0xFF555555),
        consoleError   = Color(0xFFC03030),
        consoleWarning = Color(0xFFAA7700),
        consoleSuccess = Color(0xFF2E8B2E),
        consoleInfo    = Color(0xFF444444),

        accentMauve    = Color(0xFF888888),
        accentPink     = Color(0xFF777777),
        accentPeach    = Color(0xFF999999),
        accentYellow   = Color(0xFF666666),
        accentGreen    = Color(0xFF557755),
        accentTeal     = Color(0xFF667777),
        accentSky      = Color(0xFF778899),
        accentLavender = Color(0xFF9988AA),
    )

    val vgui = ThemeSpec(
        id = "vgui",
        displayName = "VGUI",
        mode = ThemeMode.DARK,

        fontHeadingId = com.sentinel.noteshoot.R.font.platelet,
        fontBodyId    = null,  // default sans for readability
        fontCodeId    = null,  // default mono

        fontsPrimary   = Color(0xFFDEDFD6),
        fontsSecondary = Color(0xFFD8DED3),
        fontsHeadings  = Color(0xFFFFFFFF),
        fontsLinks     = Color(0xFFC4B550),
        fontsCode      = Color(0xFFC4B550),

        bgBase    = Color(0xFF4A5942),
        bgMantle  = Color(0xFF444F3C),
        bgCrust   = Color(0xFF3E4637),
        bgSurface = Color(0xFF4A5942),
        bgOverlay = Color(0xFF5A6A50),

        buttonBg           = Color(0xFF4A5942),
        buttonText         = Color(0xFFDEDFD6),
        buttonPrimaryBg    = Color(0xFF615820),
        buttonPrimaryText  = Color(0xFFC4B550),
        buttonHover        = Color(0xFF958831),
        buttonActive       = Color(0xFF3E4637),
        buttonDisabledBg   = Color(0xFF4A5942),
        buttonDisabledText = Color(0xFF292C21),

        bevelHighlight = Color(0xFF8C9284),
        bevelShadow    = Color(0xFF292C21),
        bevelBorder    = Color(0xFF292C21),

        consoleBg      = Color(0xFF000000),
        consoleText    = Color(0xFFD4D4D4),
        consolePrompt  = Color(0xFFDCDCAA),
        consoleError   = Color(0xFFE05252),
        consoleWarning = Color(0xFFE8A838),
        consoleSuccess = Color(0xFF3DBA6B),
        consoleInfo    = Color(0xFFA0AA95),

        accentMauve    = Color(0xFFA0AA95),
        accentPink     = Color(0xFFC4B550),
        accentPeach    = Color(0xFF958831),
        accentYellow   = Color(0xFFC4B550),
        accentGreen    = Color(0xFF3DBA6B),
        accentTeal     = Color(0xFF7F8C7F),
        accentSky      = Color(0xFFA0AA95),
        accentLavender = Color(0xFF8C9284),
    )

    /** Themes available for selection, in picker order. */
    val all: List<ThemeSpec> = listOf(monoDark, monoLight, vgui)

    fun byId(id: String): ThemeSpec =
        all.firstOrNull { it.id == id } ?: monoDark
}