package com.sentinel.noteshoot.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.sentinel.noteshoot.Note
import com.sentinel.noteshoot.NotesStore
import com.sentinel.noteshoot.NotesStoreConstants
import com.sentinel.noteshoot.R
import com.sentinel.noteshoot.ui.theme.ThemeManager

class NoteWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        NoteWidgetFactory(applicationContext, intent)
}

class NoteWidgetFactory(
    private val context: Context,
    intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private val widgetId: Int =
        intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

    private var rows: List<Row> = emptyList()

    override fun onCreate() { rebuild() }
    override fun onDataSetChanged() { rebuild() }
    override fun onDestroy() { rows = emptyList() }

    override fun getCount(): Int = rows.size

    override fun getViewAt(position: Int): RemoteViews {
        val row = rows[position]
        val views = RemoteViews(context.packageName, R.layout.widget_note_row)

        if (row.style != 0) {
            val span = SpannableString(row.text)
            span.setSpan(StyleSpan(row.style), 0, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            views.setTextViewText(R.id.widget_row_text, span)
        } else {
            views.setTextViewText(R.id.widget_row_text, row.text)
        }
        views.setTextColor(R.id.widget_row_text, row.color)

        // Every row carries its note ID via fill-in intent. Even blank spacer
        // rows carry the note ID, so tapping anywhere inside the ListView opens
        // the widget's note in the editor.
        val fillIn = Intent().apply {
            putExtra(NotesStoreConstants.EXTRA_NOTE_ID, row.noteId)
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
        views.setOnClickFillInIntent(R.id.widget_row_text, fillIn)

        return views
    }

    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = false

    private fun rebuild() {
        NotesStore.init(context)
        ThemeManager.init(context)

        val note = resolveNote()
        val theme = ThemeManager.active
        val rowColor = argb(theme.fontsPrimary)
        val dimColor = argb(theme.fontsSecondary)
        val headColor = argb(theme.fontsHeadings)

        rows = buildList {
            if (note == null) {
                add(Row(text = "No notes yet", color = dimColor, style = 0, noteId = null))
                add(Row(text = "Tap + in the app to create one", color = dimColor, style = 0, noteId = null))
                return@buildList
            }

            add(Row(text = note.safeTitle(80), color = headColor, style = android.graphics.Typeface.BOLD, noteId = note.id))
            add(Row(text = "", color = rowColor, style = 0, noteId = note.id))

            note.content.split('\n').forEach { line ->
                add(Row(text = if (line.isEmpty()) " " else line, color = rowColor, style = 0, noteId = note.id))
            }

            add(Row(text = "", color = rowColor, style = 0, noteId = note.id))
            add(Row(text = note.formattedTime(), color = dimColor, style = android.graphics.Typeface.ITALIC, noteId = note.id))
        }
    }

    private fun resolveNote(): Note? {
        val prefs = context.getSharedPreferences(NotesStoreConstants.WIDGET_PREFS, Context.MODE_PRIVATE)
        val savedId = prefs.getString(NotesStoreConstants.widgetNoteKey(widgetId), null)
        if (savedId != null) {
            NotesStore.getNote(savedId)?.let { if (!it.isTrashed) return it }
        }
        return NotesStore.notes.value.maxByOrNull { it.timestamp }
    }

    private fun argb(color: androidx.compose.ui.graphics.Color): Int =
        android.graphics.Color.argb(
            (color.alpha * 255).toInt(),
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt()
        )

    private data class Row(
        val text: String,
        val color: Int,
        val style: Int,
        val noteId: String?
    )
}