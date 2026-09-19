package com.sentinel.noteshoot

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sentinel.noteshoot.ui.NotesApp
import com.sentinel.noteshoot.ui.theme.NoteShootTheme
import com.sentinel.noteshoot.ui.theme.ThemeManager

class MainActivity : ComponentActivity() {

    private var requestedNoteId by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotesStore.init(applicationContext)
        ThemeManager.init(applicationContext)

        requestedNoteId = extractNoteId(intent)

        setContent {
            NoteShootTheme {
                NotesApp(initialNoteId = requestedNoteId)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        requestedNoteId = extractNoteId(intent)
    }

    private fun extractNoteId(intent: Intent?): String? =
        intent?.getStringExtra(NotesStoreConstants.EXTRA_NOTE_ID)
}