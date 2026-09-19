package com.sentinel.noteshoot.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sentinel.noteshoot.MainActivity
import com.sentinel.noteshoot.NotesStore
import com.sentinel.noteshoot.NotesStoreConstants
import com.sentinel.noteshoot.ui.theme.ThemeManager

/**
 * Handles widget row taps.
 *
 * A broadcast is used instead of an activity intent template because some
 * launchers drop fill-in intent extras on activity templates. By routing
 * through a receiver, we can always resolve the widget's bound note at tap
 * time and guarantee the editor opens the right note.
 */
class WidgetNoteTapReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotesStore.init(context)
        ThemeManager.init(context)

        val widgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        var noteId = intent.getStringExtra(NotesStoreConstants.EXTRA_NOTE_ID)

        // If the fill-in intent extras were dropped, fall back to the widget's
        // currently bound note.
        if (noteId == null && widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            noteId = NoteWidget.resolveNoteForWidget(context, widgetId)?.id
        }

        val open = Intent(context, MainActivity::class.java).apply {
            putExtra(NotesStoreConstants.EXTRA_NOTE_ID, noteId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(open)
    }
}