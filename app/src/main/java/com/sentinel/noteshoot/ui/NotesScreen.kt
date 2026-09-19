package com.sentinel.noteshoot.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentinel.noteshoot.Note
import com.sentinel.noteshoot.NotesStore
import com.sentinel.noteshoot.ui.theme.ThemeManager
import com.sentinel.noteshoot.ui.theme.cornerShape
import com.sentinel.noteshoot.ui.theme.surfaceBrush
import com.sentinel.noteshoot.ui.theme.themedBevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onNoteClick: (String) -> Unit,
    onAddNoteClick: () -> Unit,
    onOpenThemes: () -> Unit,
    onOpenAbout: () -> Unit
) {
    val theme = ThemeManager.active
    val notes by NotesStore.notes.collectAsState()
    var menuOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "NOTES",
                        color = theme.fontsHeadings,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp
                    )
                },
                actions = {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = theme.fontsPrimary)
                    }
                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false },
                        modifier = Modifier.background(theme.bgOverlay)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Themes", color = theme.fontsPrimary) },
                            onClick = { menuOpen = false; onOpenThemes() }
                        )
                        DropdownMenuItem(
                            text = { Text("About", color = theme.fontsPrimary) },
                            onClick = { menuOpen = false; onOpenAbout() }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = theme.bgMantle,
                    titleContentColor = theme.fontsHeadings
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNoteClick,
                containerColor = theme.buttonPrimaryBg,
                contentColor = theme.buttonPrimaryText
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        },
        containerColor = theme.bgBase
    ) { paddingValues ->
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No notes yet.\nTap + to create one.",
                    color = theme.fontsSecondary,
                    letterSpacing = 1.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes, key = { it.id }) { note ->
                    NoteItem(
                        note = note,
                        onClick = { onNoteClick(note.id) },
                        onDelete = { NotesStore.deleteNote(note.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, onClick: () -> Unit, onDelete: () -> Unit) {
    val theme = ThemeManager.active
    val shape = theme.cornerShape()

    val baseModifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }

    val beveledModifier = baseModifier.then(theme.themedBevel(shape))

    Card(
        modifier = beveledModifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = theme.bgSurface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.safeTitle(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = theme.fontsHeadings
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.snippet(90),
                    maxLines = 2,
                    color = theme.fontsSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = note.formattedTime(),
                    fontSize = 11.sp,
                    color = theme.fontsSecondary.copy(alpha = 0.7f)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = theme.consoleError
                )
            }
        }
    }
}