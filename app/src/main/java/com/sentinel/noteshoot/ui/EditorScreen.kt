package com.sentinel.noteshoot.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentinel.noteshoot.NotesStore
import com.sentinel.noteshoot.ui.theme.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    noteId: String?,
    onNavigateBack: () -> Unit
) {
    val theme = ThemeManager.active
    val existing = remember(noteId) { noteId?.let { NotesStore.getNote(it) } }
    val isEditing = existing != null

    var title by remember(noteId) { mutableStateOf(existing?.title ?: "") }
    var content by remember(noteId) { mutableStateOf(existing?.content ?: "") }

    val contentFocus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(noteId) {
        if (noteId != null) {
            kotlinx.coroutines.delay(180)
            contentFocus.requestFocus()
            keyboard?.show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "EDIT" else "NEW NOTE",
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace,
                        color = theme.fontsHeadings
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = theme.fontsPrimary
                        )
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = {
                            NotesStore.deleteNote(existing!!.id)
                            onNavigateBack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = theme.consoleError)
                        }
                    }
                    IconButton(onClick = {
                        if (title.isNotBlank() || content.isNotBlank()) {
                            if (isEditing) NotesStore.updateNote(existing!!.id, title, content)
                            else NotesStore.addNote(title, content)
                        }
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = theme.fontsPrimary)
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
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = fieldColors(theme)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .focusRequester(contentFocus),
                colors = fieldColors(theme)
            )
        }
    }
}

@Composable
private fun fieldColors(theme: com.sentinel.noteshoot.ui.theme.ThemeSpec) =
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = theme.fontsPrimary,
        unfocusedTextColor = theme.fontsPrimary,
        focusedBorderColor = theme.buttonPrimaryBg,
        unfocusedBorderColor = theme.bevelBorder,
        focusedLabelColor = theme.fontsSecondary,
        unfocusedLabelColor = theme.fontsSecondary,
        cursorColor = theme.buttonPrimaryBg,
    )