package com.sentinel.noteshoot.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

sealed class Screen {
    object NotesList : Screen()
    data class Editor(val noteId: String?) : Screen()
    object Themes : Screen()
    object About : Screen()
    object Trash : Screen()
}

@Composable
fun NotesApp(initialNoteId: String? = null) {

    var currentScreen by remember {
        mutableStateOf<Screen>(
            if (initialNoteId != null) Screen.Editor(initialNoteId)
            else Screen.NotesList
        )
    }

    LaunchedEffect(initialNoteId) {
        if (initialNoteId != null) {
            currentScreen = Screen.Editor(initialNoteId)
        }
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            val duration = 220
            val forward = isForward(targetState, initialState)

            if (forward) {
                (slideInHorizontally(tween(duration)) { it / 2 } + fadeIn(tween(duration))) togetherWith
                        (slideOutHorizontally(tween(duration)) { -it / 4 } + fadeOut(tween(duration)))
            } else {
                (slideInHorizontally(tween(duration)) { -it / 4 } + fadeIn(tween(duration))) togetherWith
                        (slideOutHorizontally(tween(duration)) { it / 2 } + fadeOut(tween(duration)))
            }
        },
        label = "screen-transition"
    ) { screen ->
        when (screen) {
            is Screen.NotesList -> NotesScreen(
                onNoteClick = { id -> currentScreen = Screen.Editor(id) },
                onAddNoteClick = { currentScreen = Screen.Editor(null) },
                onOpenThemes = { currentScreen = Screen.Themes },
                onOpenAbout = { currentScreen = Screen.About },
                onOpenTrash = { currentScreen = Screen.Trash }
            )
            is Screen.Editor -> EditorScreen(
                noteId = screen.noteId,
                onNavigateBack = { currentScreen = Screen.NotesList }
            )
            is Screen.Themes -> ThemePickerScreen(
                onNavigateBack = { currentScreen = Screen.NotesList }
            )
            is Screen.About -> AboutScreen(
                onNavigateBack = { currentScreen = Screen.NotesList }
            )
            is Screen.Trash -> TrashScreen(
                onNavigateBack = { currentScreen = Screen.NotesList }
            )
        }
    }
}

private fun isForward(target: Screen, initial: Screen): Boolean = when {
    initial is Screen.NotesList && target !is Screen.NotesList -> true
    target is Screen.NotesList -> false
    else -> true
}