package com.sentinel.noteshoot.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentinel.noteshoot.MainActivity
import com.sentinel.noteshoot.Note
import com.sentinel.noteshoot.NotesStore
import com.sentinel.noteshoot.NotesStoreConstants
import com.sentinel.noteshoot.ui.theme.NoteShootTheme
import com.sentinel.noteshoot.ui.theme.ThemeManager
import com.sentinel.noteshoot.ui.theme.cornerShape


class WidgetPickerActivity : ComponentActivity() {

    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        widgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish(); return
        }

        NotesStore.init(applicationContext)
        ThemeManager.init(applicationContext)

        window.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            attributes = attributes.apply { dimAmount = 0.55f }
            setGravity(Gravity.TOP or Gravity.END)
        }

        setContent {
            NoteShootTheme {
                WidgetPickerContent(
                    onPick = { noteId -> bindNote(noteId) },
                    onEdit = { note -> openEditor(note) },
                    onDelete = { note -> deleteNote(note) },
                    onDismiss = { finish() }
                )
            }
        }
    }

    private fun bindNote(noteId: String?) {
        val prefs = getSharedPreferences(NotesStoreConstants.WIDGET_PREFS, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        if (noteId == null) editor.remove(NotesStoreConstants.widgetNoteKey(widgetId))
        else editor.putString(NotesStoreConstants.widgetNoteKey(widgetId), noteId)
        editor.apply()

        NoteWidget.updateWidget(
            applicationContext,
            AppWidgetManager.getInstance(applicationContext),
            widgetId
        )
        finish()
    }

    private fun openEditor(note: Note) {
        val i = Intent(this, MainActivity::class.java).apply {
            putExtra(NotesStoreConstants.EXTRA_NOTE_ID, note.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(i)
        finish()
    }

    private fun deleteNote(note: Note) {
        NotesStore.deleteNote(note.id)
    }
}

@Composable
private fun WidgetPickerContent(
    onPick: (String?) -> Unit,
    onEdit: (Note) -> Unit,
    onDelete: (Note) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = ThemeManager.active
    val notes by NotesStore.notes.collectAsState()

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = tween(180), label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(180), label = "alpha"
    )

    val shape = theme.cornerShape(14)

    Surface(
        modifier = Modifier
            .padding(12.dp)
            .widthIn(min = 260.dp, max = 320.dp)
            .heightIn(max = 480.dp)
            .scale(scale)
            .alpha(alpha),
        color = theme.bgSurface,
        shape = shape,
        tonalElevation = 12.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 10.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SWAP NOTE",
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = theme.fontsSecondary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "✕",
                    fontSize = 16.sp,
                    color = theme.fontsSecondary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(onClick = onDismiss)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(Modifier.height(6.dp))
            HorizontalDivider(color = theme.bevelBorder)

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    PickerRow(
                        note = null,
                        displayTitle = "★ Latest note",
                        displaySubtitle = "Auto — most recently edited",
                        onPick = { onPick(null) },
                        onEdit = null,
                        onDelete = null
                    )
                    HorizontalDivider(color = theme.bevelBorder)
                }
                items(
                    notes.sortedByDescending { it.timestamp },
                    key = { it.id }
                ) { note ->
                    PickerRow(
                        note = note,
                        displayTitle = note.safeTitle(40),
                        displaySubtitle = note.snippet(50),
                        onPick = { onPick(note.id) },
                        onEdit = { onEdit(note) },
                        onDelete = { onDelete(note) }
                    )
                    HorizontalDivider(color = theme.bevelBorder)
                }
            }
        }
    }
}

@Composable
private fun PickerRow(
    note: Note?,
    displayTitle: String,
    displaySubtitle: String,
    onPick: () -> Unit,
    onEdit: (() -> Unit)?,
    onDelete: (() -> Unit)?
) {
    val theme = ThemeManager.active
    var confirmDelete by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayTitle,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = theme.fontsPrimary,
                    maxLines = 1
                )
                if (displaySubtitle.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = displaySubtitle,
                        fontSize = 11.sp,
                        color = theme.fontsSecondary,
                        maxLines = 1
                    )
                }
            }
            if (onEdit != null) {
                IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = theme.fontsSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            if (onDelete != null) {
                IconButton(onClick = { confirmDelete = true }, modifier = Modifier.size(34.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = theme.consoleError,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        if (confirmDelete && note != null) {
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.consoleError.copy(alpha = 0.18f))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Delete this note?",
                    fontSize = 12.sp,
                    color = theme.fontsPrimary,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { confirmDelete = false }) {
                    Text("No", fontSize = 12.sp, color = theme.fontsPrimary)
                }
                TextButton(onClick = {
                    confirmDelete = false
                    onDelete?.invoke()
                }) {
                    Text("Delete", fontSize = 12.sp, color = theme.consoleError)
                }
            }
        }
    }
}