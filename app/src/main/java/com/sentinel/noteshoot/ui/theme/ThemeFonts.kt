package com.sentinel.noteshoot.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object ThemeFonts {

    fun headingFor(spec: ThemeSpec): FontFamily {
        val id = spec.fontHeadingId
        return if (id != null) FontFamily(Font(id)) else FontFamily.Monospace
    }

    fun bodyFor(spec: ThemeSpec): FontFamily {
        val id = spec.fontBodyId
        return if (id != null) FontFamily(Font(id)) else FontFamily.Monospace
    }

    fun codeFor(spec: ThemeSpec): FontFamily {
        val id = spec.fontCodeId
        return if (id != null) FontFamily(Font(id)) else FontFamily.Monospace
    }

    /** Builds a Material 3 Typography using the active theme and current base pt. */
    fun typographyFor(spec: ThemeSpec, basePt: Float = ThemeManager.BASE_PT_DEFAULT): Typography {
        val heading = headingFor(spec)
        val body = bodyFor(spec)
        val scale = basePt / ThemeManager.BASE_PT_REFERENCE

        fun sz(base: Float) = (base * scale).sp

        return Typography(
            displayLarge = TextStyle(fontFamily = heading, fontWeight = FontWeight.Bold, fontSize = sz(32f)),
            displayMedium = TextStyle(fontFamily = heading, fontWeight = FontWeight.Bold, fontSize = sz(28f)),
            displaySmall = TextStyle(fontFamily = heading, fontWeight = FontWeight.Bold, fontSize = sz(24f)),

            headlineLarge = TextStyle(fontFamily = heading, fontWeight = FontWeight.SemiBold, fontSize = sz(22f)),
            headlineMedium = TextStyle(fontFamily = heading, fontWeight = FontWeight.SemiBold, fontSize = sz(20f)),
            headlineSmall = TextStyle(fontFamily = heading, fontWeight = FontWeight.SemiBold, fontSize = sz(18f)),

            titleLarge = TextStyle(fontFamily = heading, fontWeight = FontWeight.SemiBold, fontSize = sz(18f)),
            titleMedium = TextStyle(fontFamily = body, fontWeight = FontWeight.Medium, fontSize = sz(16f)),
            titleSmall = TextStyle(fontFamily = body, fontWeight = FontWeight.Medium, fontSize = sz(14f)),

            bodyLarge = TextStyle(fontFamily = body, fontWeight = FontWeight.Normal, fontSize = sz(15f)),
            bodyMedium = TextStyle(fontFamily = body, fontWeight = FontWeight.Normal, fontSize = sz(13f)),
            bodySmall = TextStyle(fontFamily = body, fontWeight = FontWeight.Normal, fontSize = sz(12f)),

            labelLarge = TextStyle(fontFamily = body, fontWeight = FontWeight.Medium, fontSize = sz(13f)),
            labelMedium = TextStyle(fontFamily = body, fontWeight = FontWeight.Medium, fontSize = sz(11f)),
            labelSmall = TextStyle(fontFamily = body, fontWeight = FontWeight.Medium, fontSize = sz(10f)),
        )
    }
}