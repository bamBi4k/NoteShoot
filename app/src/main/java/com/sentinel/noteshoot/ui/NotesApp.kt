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

@Composable
fun NotesApp(initialNoteId: String? = null) {

    var currentScreen by remember {
        mutableStateOf<Screen>(
            if (initialNoteId != null) Screen.Editor(initialNoteId)
            else Screen.NotesList
        )
    }

    val activeTheme = com.sentinel.noteshoot.ui.theme.ThemeManager.active
    androidx.compose.runtime.LaunchedEffect(activeTheme.id) {
        // Optional: hook into a snackbar host here later
        android.util.Log.d("ThemeChange", "Active: ${activeTheme.displayName}")
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
                onOpenAbout = { currentScreen = Screen.About }
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
        }
    }
}

/** Depth heuristic for transition direction. */
private fun isForward(target: Screen, initial: Screen): Boolean = when {
    initial is Screen.NotesList && target !is Screen.NotesList -> true
    target is Screen.NotesList -> false
    else -> true
}