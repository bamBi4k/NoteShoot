package com.sentinel.noteshoot.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import com.sentinel.noteshoot.MainActivity
import com.sentinel.noteshoot.Note
import com.sentinel.noteshoot.NotesStore
import com.sentinel.noteshoot.NotesStoreConstants
import com.sentinel.noteshoot.R
import com.sentinel.noteshoot.ui.theme.ThemeManager

class NoteWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        NotesStore.init(context)
        ThemeManager.init(context)
        appWidgetIds.forEach { updateWidget(context, appWidgetManager, it) }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        NotesStore.init(context)
        ThemeManager.init(context)
        updateWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val prefs = context.getSharedPreferences(NotesStoreConstants.WIDGET_PREFS, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        appWidgetIds.forEach { id -> editor.remove(NotesStoreConstants.widgetNoteKey(id)) }
        editor.apply()
    }

    companion object {
        private const val TAG = "NoteWidget"

        fun updateWidget(
            context: Context,
            manager: AppWidgetManager,
            widgetId: Int
        ) {
            NotesStore.init(context)
            ThemeManager.init(context)

            val note = resolveNoteForWidget(context, widgetId)
            Log.d(TAG, "updateWidget: id=$widgetId note='${note?.title}' content='${note?.content}'")

            // Widget size in px
            val options = manager.getAppWidgetOptions(widgetId)
            val minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 180)
            val minHeightDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 120)
            val density = context.resources.displayMetrics.density
            val widthPx = (minWidthDp * density).toInt().coerceAtLeast(100)
            val heightPx = (minHeightDp * density).toInt().coerceAtLeast(100)
            Log.d(TAG, "size: ${widthPx}x${heightPx} (density=$density)")

            // Render
            val bitmap = WidgetRenderer.render(context, note, widthPx, heightPx)
            Log.d(TAG, "bitmap rendered: ${bitmap.width}x${bitmap.height}")

            val views = RemoteViews(context.packageName, R.layout.note_widget)
            views.setImageViewBitmap(R.id.widget_image, bitmap)

// Bevel-aware ☰ button background
            val activeTheme = ThemeManager.active
            val menuBgRes = if (activeTheme.isBeveled) {
                R.drawable.widget_menu_button_vgui
            } else {
                R.drawable.widget_menu_button_default
            }
            views.setInt(R.id.widget_menu_button, "setBackgroundResource", menuBgRes)

            // Tap anywhere → open the note
            val openIntent = Intent(context, MainActivity::class.java).apply {
                putExtra(NotesStoreConstants.EXTRA_NOTE_ID, note?.id)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openPi = PendingIntent.getActivity(
                context,
                widgetId,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, openPi)

            // ☰ button → picker
            val pickerIntent = Intent(context, WidgetPickerActivity::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pickerPi = PendingIntent.getActivity(
                context,
                widgetId + 1000,
                pickerIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_menu_button, pickerPi)

            manager.updateAppWidget(widgetId, views)
        }

        fun resolveNoteForWidget(context: Context, widgetId: Int): Note? {
            val prefs = context.getSharedPreferences(NotesStoreConstants.WIDGET_PREFS, Context.MODE_PRIVATE)
            val savedId = prefs.getString(NotesStoreConstants.widgetNoteKey(widgetId), null)
            if (savedId != null) {
                NotesStore.getNote(savedId)?.let { return it }
            }
            return NotesStore.notes.value.maxByOrNull { it.timestamp }
        }
    }
}