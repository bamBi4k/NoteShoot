package com.sentinel.noteshoot.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentinel.noteshoot.NotesStore
import com.sentinel.noteshoot.NotesStoreConstants
import com.sentinel.noteshoot.ui.theme.NoteShootTheme
import com.sentinel.noteshoot.ui.theme.ThemeManager

class NoteWidgetConfigActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        NotesStore.init(applicationContext)
        ThemeManager.init(applicationContext)

        setContent {
            NoteShootTheme {
                WidgetConfigScreen(
                    onNoteSelected = { noteId -> saveAndFinish(noteId) },
                    onCancel = { finish() }
                )
            }
        }
    }

    private fun saveAndFinish(noteId: String?) {
        if (noteId != null) {
            getSharedPreferences(NotesStoreConstants.WIDGET_PREFS, MODE_PRIVATE)
                .edit()
                .putString(NotesStoreConstants.widgetNoteKey(appWidgetId), noteId)
                .apply()
        }
        NoteWidget.updateWidget(
            applicationContext,
            AppWidgetManager.getInstance(applicationContext),
            appWidgetId
        )
        val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, result)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WidgetConfigScreen(
    onNoteSelected: (String?) -> Unit,
    onCancel: () -> Unit
) {
    val theme = ThemeManager.active
    val notes by NotesStore.notes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose a note", color = theme.fontsHeadings) },
                navigationIcon = {
                    TextButton(onClick = onCancel) {
                        Text("Cancel", color = theme.fontsPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = theme.bgMantle,
                    titleContentColor = theme.fontsHeadings
                )
            )
        },
        containerColor = theme.bgBase
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ConfigRow(
                title = "★ Latest note",
                subtitle = "Auto-shows the most recently edited note",
                onClick = { onNoteSelected(null) }
            )
            HorizontalDivider(color = theme.bevelBorder)

            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No notes yet.", color = theme.fontsSecondary)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(notes.sortedByDescending { it.timestamp }, key = { it.id }) { note ->
                        ConfigRow(
                            title = note.safeTitle(),
                            subtitle = note.snippet(60),
                            onClick = { onNoteSelected(note.id) }
                        )
                        HorizontalDivider(color = theme.bevelBorder)
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfigRow(title: String, subtitle: String, onClick: () -> Unit) {
    val theme = ThemeManager.active
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = theme.fontsPrimary
            )
            if (subtitle.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = theme.fontsSecondary
                )
            }
        }
        Text("›", fontSize = 20.sp, color = theme.fontsSecondary)
    }
}