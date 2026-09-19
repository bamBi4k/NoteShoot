package com.sentinel.noteshoot.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Restore
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
import com.sentinel.noteshoot.ui.theme.themedBevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(onNavigateBack: () -> Unit) {
    val theme = ThemeManager.active
    val trashed by NotesStore.trashedNotes.collectAsState()
    var confirmEmpty by remember { mutableStateOf(false) }
    var purgeTarget by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "TRASH",
                        color = theme.fontsHeadings,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp
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
                    if (trashed.isNotEmpty()) {
                        IconButton(onClick = { confirmEmpty = true }) {
                            Icon(
                                Icons.Default.DeleteSweep,
                                contentDescription = "Empty trash",
                                tint = theme.consoleError
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.bgMantle)
            )
        },
        containerColor = theme.bgBase
    ) { padding ->
        if (trashed.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Trash is empty",
                    color = theme.fontsSecondary,
                    letterSpacing = 1.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(trashed, key = { it.id }) { note ->
                    TrashItem(
                        note = note,
                        onRestore = { NotesStore.restoreFromTrash(note.id) },
                        onPurge = { purgeTarget = note }
                    )
                }
            }
        }
    }

    if (confirmEmpty) {
        AlertDialog(
            onDismissRequest = { confirmEmpty = false },
            containerColor = theme.bgSurface,
            titleContentColor = theme.fontsHeadings,
            textContentColor = theme.fontsPrimary,
            title = { Text("Empty trash?") },
            text = { Text("All ${trashed.size} note(s) will be permanently deleted. This cannot be undone.", fontSize = 13.sp) },
            confirmButton = {
                TextButton(onClick = {
                    NotesStore.emptyTrash()
                    confirmEmpty = false
                }) {
                    Text("Empty", color = theme.consoleError, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmEmpty = false }) {
                    Text("Cancel", color = theme.fontsSecondary)
                }
            }
        )
    }

    purgeTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { purgeTarget = null },
            containerColor = theme.bgSurface,
            titleContentColor = theme.fontsHeadings,
            textContentColor = theme.fontsPrimary,
            title = { Text("Delete forever?") },
            text = {
                Text(
                    "\"${target.safeTitle(40)}\" will be permanently deleted.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    NotesStore.purge(target.id)
                    purgeTarget = null
                }) {
                    Text("Delete", color = theme.consoleError, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { purgeTarget = null }) {
                    Text("Cancel", color = theme.fontsSecondary)
                }
            }
        )
    }
}

@Composable
private fun TrashItem(
    note: Note,
    onRestore: () -> Unit,
    onPurge: () -> Unit
) {
    val theme = ThemeManager.active
    val shape = theme.cornerShape()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(theme.themedBevel(shape)),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = theme.bgSurface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.safeTitle(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = theme.fontsHeadings,
                    maxLines = 1
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = note.snippet(60),
                    maxLines = 1,
                    color = theme.fontsSecondary,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Deleted ${note.formattedDeletedTime()}",
                    fontSize = 10.sp,
                    color = theme.fontsSecondary.copy(alpha = 0.7f)
                )
            }
            IconButton(onClick = onRestore) {
                Icon(
                    Icons.Default.Restore,
                    contentDescription = "Restore",
                    tint = theme.accentGreen
                )
            }
            IconButton(onClick = onPurge) {
                Icon(
                    Icons.Default.DeleteForever,
                    contentDescription = "Delete forever",
                    tint = theme.consoleError
                )
            }
        }
    }
}