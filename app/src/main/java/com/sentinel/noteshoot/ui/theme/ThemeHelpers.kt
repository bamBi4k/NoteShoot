package com.sentinel.noteshoot.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

/** Soft bevel used by non-VGUI themes. */
fun Modifier.bevel(theme: ThemeSpec): Modifier =
    this.background(
        Brush.verticalGradient(
            colors = listOf(
                theme.bevelHighlight.copy(alpha = 0.08f),
                Color.Transparent,
                theme.bevelShadow.copy(alpha = 0.10f)
            )
        )
    )

/** Outer 1px border used by non-VGUI themes. */
fun Modifier.borderOnly(theme: ThemeSpec, shape: RoundedCornerShape): Modifier =
    this.border(1.dp, theme.bevelBorder, shape)

/** VGUI OUTSET — classic 3D "raised" border. */
fun Modifier.vguiOutset(theme: ThemeSpec): Modifier = this
    .drawWithContent {
        drawContent()
        val w = size.width
        val h = size.height
        val light = theme.bevelHighlight
        val dark = theme.bevelShadow

        drawLine(light, Offset(0f, 0f), Offset(w, 0f), 1f)
        drawLine(light, Offset(0f, 0f), Offset(0f, h), 1f)
        drawLine(dark, Offset(w - 1f, 0f), Offset(w - 1f, h), 1f)
        drawLine(dark, Offset(0f, h - 1f), Offset(w, h - 1f), 1f)
    }

/** VGUI INSET — pressed / content-well look. */
fun Modifier.vguiInset(theme: ThemeSpec): Modifier = this
    .drawWithContent {
        drawContent()
        val w = size.width
        val h = size.height
        val light = theme.bevelHighlight
        val dark = theme.bevelShadow

        drawLine(dark, Offset(0f, 0f), Offset(w, 0f), 1f)
        drawLine(dark, Offset(0f, 0f), Offset(0f, h), 1f)
        drawLine(light, Offset(w - 1f, 0f), Offset(w - 1f, h), 1f)
        drawLine(light, Offset(0f, h - 1f), Offset(w, h - 1f), 1f)
    }

/** Theme-aware bevel dispatcher — cards and buttons. */
fun ThemeSpec.themedBevel(shape: RoundedCornerShape = RoundedCornerShape(0.dp)): Modifier =
    if (isBeveled) Modifier.vguiOutset(this)
    else Modifier.borderOnly(this, shape)

/** Theme-aware inset for content wells. */
fun ThemeSpec.insetBevel(): Modifier =
    if (isBeveled) Modifier.vguiInset(this)
    else Modifier

/** Theme-aware surface brush — VGUI is flat, others get a soft gradient. */
fun ThemeSpec.surfaceBrush(): Brush =
    if (isBeveled) SolidColor(bgSurface)
    else Brush.verticalGradient(listOf(bgSurface, bgMantle))

/** Corner shape — VGUI is sharp, others are rounded. */
fun ThemeSpec.cornerShape(radius: Int = 12): RoundedCornerShape =
    if (isBeveled) RoundedCornerShape(0.dp)
    else RoundedCornerShape(radius.dp)