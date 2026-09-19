package com.sentinel.noteshoot.ui

sealed class Screen {
    object NotesList : Screen()
    data class Editor(val noteId: String?) : Screen()
    object Themes : Screen()
    object About : Screen()
}