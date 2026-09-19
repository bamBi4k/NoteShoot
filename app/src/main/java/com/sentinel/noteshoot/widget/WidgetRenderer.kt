package com.sentinel.noteshoot.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import androidx.compose.ui.graphics.toArgb
import com.sentinel.noteshoot.Note
import com.sentinel.noteshoot.ui.theme.ThemeManager

/**
 * Renders a note into a Bitmap suitable for the widget's ImageView.
 *
 * Font sizes scale with ThemeManager.fontScale so the widget reflects
 * the user's chosen base pt size. Padding, borders, and margins stay fixed
 * because the widget's physical dimensions are determined by the launcher.
 */
object WidgetRenderer {

    fun render(
        context: Context,
        note: Note?,
        widthPx: Int,
        heightPx: Int
    ): Bitmap {
        val theme = ThemeManager.active
        val density = context.resources.displayMetrics.density
        val scale = ThemeManager.fontScale  // 0.85 .. 1.38 (basePt / 13)

        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(theme.bgSurface.toArgb())
        drawBorder(canvas, widthPx, heightPx, theme.bevelBorder.toArgb())

        val pad = (14 * density).toInt()
        var y = pad.toFloat()

        // ---------- Header ----------
        val headerSize = 10f * density * scale
        val headerPaint = TextPaint().apply {
            isAntiAlias = true
            color = theme.fontsSecondary.toArgb()
            textSize = headerSize
            typeface = Typeface.MONOSPACE
            letterSpacing = 0.15f
        }
        canvas.drawText("● NOTE", pad.toFloat(), y + headerPaint.textSize, headerPaint)
        y += headerSize + (10 * density)

        // ---------- Title ----------
        val titleSize = 15f * density * scale
        val titlePaint = TextPaint().apply {
            isAntiAlias = true
            color = theme.fontsHeadings.toArgb()
            textSize = titleSize
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
        }
        val titleText = note?.safeTitle(60) ?: "No notes yet"
        val titleLines = ellipsize(titleText, titlePaint, widthPx - 2 * pad, maxLines = 1)
        titleLines.forEach { line ->
            canvas.drawText(line, pad.toFloat(), y + titlePaint.textSize, titlePaint)
            y += titleSize + (2 * density)
        }
        y += (6 * density)

        // ---------- Body ----------
        val bodySize = 12f * density * scale
        val bodyPaint = TextPaint().apply {
            isAntiAlias = true
            color = theme.fontsPrimary.toArgb()
            textSize = bodySize
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        }
        val bodyText = note?.content ?: "Tap + in the app to create one"

        // Footer height also scales so its glyph doesn't overlap body
        val footerSize = 10f * density * scale
        val footerHeight = footerSize + (4 * density)

        val bodyLines = wrapText(bodyText, bodyPaint, widthPx - 2 * pad)
        for (line in bodyLines) {
            if (y + bodyPaint.textSize > heightPx - footerHeight - pad) break
            canvas.drawText(line, pad.toFloat(), y + bodyPaint.textSize, bodyPaint)
            y += bodySize + (2 * density)
        }

        // ---------- Footer ----------
        if (note != null) {
            val footerPaint = TextPaint().apply {
                isAntiAlias = true
                color = theme.fontsSecondary.toArgb()
                textSize = footerSize
                typeface = Typeface.MONOSPACE
            }
            canvas.drawText(
                note.formattedTime(),
                pad.toFloat(),
                heightPx - pad.toFloat(),
                footerPaint
            )
        }

        return bitmap
    }

    // ---------- helpers ----------

    private fun drawBorder(canvas: Canvas, w: Int, h: Int, color: Int) {
        val paint = Paint().apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        canvas.drawRect(0f, 0f, w.toFloat() - 1, h.toFloat() - 1, paint)
    }

    private fun wrapText(text: String, paint: TextPaint, maxWidth: Int): List<String> {
        if (text.isEmpty()) return emptyList()
        val result = mutableListOf<String>()
        text.split('\n').forEach { paragraph ->
            if (paragraph.isEmpty()) {
                result.add("")
                return@forEach
            }
            var start = 0
            val end = paragraph.length
            while (start < end) {
                val count = paint.breakText(paragraph, start, end, true, maxWidth.toFloat(), null)
                if (count <= 0) break
                result.add(paragraph.substring(start, start + count).trimEnd())
                start += count
                while (start < end && paragraph[start] == ' ') start++
            }
        }
        return result
    }

    private fun ellipsize(text: String, paint: TextPaint, maxWidth: Int, maxLines: Int): List<String> {
        val result = mutableListOf<String>()
        var remaining = text
        repeat(maxLines) {
            val count = paint.breakText(remaining, 0, remaining.length, true, maxWidth.toFloat(), null)
            if (count <= 0) return result
            result.add(if (count < remaining.length) remaining.take(count).trimEnd() + "…" else remaining)
            remaining = remaining.drop(count)
            if (remaining.isEmpty()) return result
        }
        return result
    }
}