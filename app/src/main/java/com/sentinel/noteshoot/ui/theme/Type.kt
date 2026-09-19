package com.sentinel.noteshoot.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Use monospace to match the "Mono" theme vibe
private val Mono = FontFamily.Monospace

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 32.sp),
    displayMedium = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 28.sp),
    displaySmall = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 24.sp),

    headlineLarge = TextStyle(fontFamily = Mono, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    headlineMedium = TextStyle(fontFamily = Mono, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    headlineSmall = TextStyle(fontFamily = Mono, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),

    titleLarge = TextStyle(fontFamily = Mono, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    titleMedium = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    titleSmall = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 14.sp),

    bodyLarge = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Normal, fontSize = 15.sp),
    bodyMedium = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Normal, fontSize = 13.sp),
    bodySmall = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Normal, fontSize = 12.sp),

    labelLarge = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 13.sp),
    labelMedium = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 11.sp),
    labelSmall = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 10.sp),
)