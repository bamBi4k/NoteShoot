package com.sentinel.noteshoot.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import com.sentinel.noteshoot.MainActivity
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
            Log.d(TAG, "updateWidget: id=$widgetId note='${note?.title}'")

            val views = RemoteViews(context.packageName, R.layout.note_widget)

            // Point the ListView at our service, giving it a unique data URI
            // so RemoteViewsService instances do not collide across widgets.
            val serviceIntent = Intent(context, NoteWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
            views.setRemoteAdapter(R.id.widget_list, serviceIntent)

            // Empty view — used when there are no notes
            views.setEmptyView(R.id.widget_list, R.id.widget_list)

            // Whole widget is tappable; a fill-in intent (per row) will override
            // this when the user taps a row.
            val templateIntent = Intent(context, WidgetNoteTapReceiver::class.java).apply {
                putExtra(NotesStoreConstants.EXTRA_NOTE_ID, note?.id)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            }
            val templatePi = PendingIntent.getBroadcast(
                context,
                widgetId,
                templateIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            views.setPendingIntentTemplate(R.id.widget_list, templatePi)

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

            // Bevel-aware ☰ button background
            val theme = ThemeManager.active
            val menuBgRes = if (theme.isBeveled) R.drawable.widget_menu_button_vgui
            else R.drawable.widget_menu_button_default
            views.setInt(R.id.widget_menu_button, "setBackgroundResource", menuBgRes)

            // Apply theme background to root
            views.setInt(
                R.id.widget_root,
                "setBackgroundColor",
                argb(theme.bgSurface)
            )

            // Force the ListView to rebuild
            manager.notifyAppWidgetViewDataChanged(widgetId, R.id.widget_list)
            manager.updateAppWidget(widgetId, views)
        }

        fun resolveNoteForWidget(context: Context, widgetId: Int): com.sentinel.noteshoot.Note? {
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
    }
}