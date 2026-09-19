package com.sentinel.noteshoot

object NotesStoreConstants {
    const val EXTRA_NOTE_ID = "com.sentinel.noteshoot.EXTRA_NOTE_ID"

    // Widget prefs
    const val WIDGET_PREFS = "widget_prefs"
    fun widgetNoteKey(widgetId: Int) = "widget_note_$widgetId"
}