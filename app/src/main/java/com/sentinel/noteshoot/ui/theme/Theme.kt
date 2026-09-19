package com.sentinel.noteshoot.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun NoteShootTheme(
    content: @Composable () -> Unit
) {
    val spec = ThemeManager.active
    val basePt = ThemeManager.basePt

    val scheme = if (spec.mode == ThemeMode.LIGHT) {
        lightColorScheme(
            primary = spec.buttonPrimaryBg,
            onPrimary = spec.buttonPrimaryText,
            primaryContainer = spec.bgOverlay,
            onPrimaryContainer = spec.fontsHeadings,
            secondary = spec.accentMauve,
            onSecondary = spec.bgBase,
            secondaryContainer = spec.buttonBg,
            onSecondaryContainer = spec.fontsPrimary,
            background = spec.bgBase,
            onBackground = spec.fontsPrimary,
            surface = spec.bgSurface,
            onSurface = spec.fontsPrimary,
            surfaceVariant = spec.bgOverlay,
            onSurfaceVariant = spec.fontsSecondary,
            outline = spec.bevelBorder,
            outlineVariant = spec.bevelShadow,
            error = spec.consoleError,
            onError = spec.bgBase,
            errorContainer = spec.bgOverlay,
            onErrorContainer = spec.consoleError,
            inverseSurface = spec.fontsPrimary,
            inverseOnSurface = spec.bgBase,
        )
    } else {
        darkColorScheme(
            primary = spec.buttonPrimaryBg,
            onPrimary = spec.buttonPrimaryText,
            primaryContainer = spec.bgOverlay,
            onPrimaryContainer = spec.fontsHeadings,
            secondary = spec.accentMauve,
            onSecondary = spec.bgBase,
            secondaryContainer = spec.buttonBg,
            onSecondaryContainer = spec.fontsPrimary,
            background = spec.bgBase,
            onBackground = spec.fontsPrimary,
            surface = spec.bgSurface,
            onSurface = spec.fontsPrimary,
            surfaceVariant = spec.bgOverlay,
            onSurfaceVariant = spec.fontsSecondary,
            outline = spec.bevelBorder,
            outlineVariant = spec.bevelShadow,
            error = spec.consoleError,
            onError = spec.bgBase,
            errorContainer = spec.bgOverlay,
            onErrorContainer = spec.consoleError,
            inverseSurface = spec.fontsPrimary,
            inverseOnSurface = spec.bgBase,
        )
    }

    MaterialTheme(
        colorScheme = scheme,
        typography = ThemeFonts.typographyFor(spec, basePt),
        content = content
    )
}