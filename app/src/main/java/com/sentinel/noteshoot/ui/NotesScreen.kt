package com.sentinel.noteshoot.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentinel.noteshoot.Note
import com.sentinel.noteshoot.NotesStore
import com.sentinel.noteshoot.ui.theme.ThemeManager
import com.sentinel.noteshoot.ui.theme.cornerShape
import com.sentinel.noteshoot.ui.theme.themedBevel

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun NotesScreen(
    onNoteClick: (String) -> Unit,
    onAddNoteClick: () -> Unit,
    onOpenThemes: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenTrash: () -> Unit
) {
    val theme = ThemeManager.active
    val context = LocalContext.current
    val allActiveNotes by NotesStore.notes.collectAsState()

    var menuOpen by remember { mutableStateOf(false) }
    var searchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val searchFocus = remember { FocusRequester() }

    // Selection mode
    var selectionMode by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateListOf<String>() }

    // Filtered list: search takes priority
    val notes = remember(allActiveNotes, searchQuery, searchActive) {
        if (searchActive && searchQuery.isNotBlank()) {
            allActiveNotes.filter { it.matches(searchQuery) }
        } else allActiveNotes
    }

    // Exit selection mode if nothing remains selected
    LaunchedEffect(selectedIds.size) {
        if (selectionMode && selectedIds.isEmpty()) selectionMode = false
    }

    // Auto-focus search field when activated
    LaunchedEffect(searchActive) {
        if (searchActive) {
            kotlinx.coroutines.delay(120)
            searchFocus.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            if (selectionMode) {
                TopAppBar(
                    title = {
                        Text(
                            "${selectedIds.size} selected",
                            color = theme.fontsHeadings,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            selectionMode = false
                            selectedIds.clear()
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Cancel",
                                tint = theme.fontsPrimary
                            )
                        }
                    },
                    actions = {
                        val selectedNotes = allActiveNotes.filter { it.id in selectedIds }

                        // Pin toggle — if any selected note is unpinned, pin all; else unpin all
                        if (selectedNotes.isNotEmpty()) {
                            val anyUnpinned = selectedNotes.any { !it.pinned }
                            IconButton(onClick = {
                                selectedNotes.forEach { note ->
                                    val shouldPin = anyUnpinned
                                    if (note.pinned != shouldPin) {
                                        NotesStore.togglePin(note.id)
                                    }
                                }
                                selectedIds.clear()
                                selectionMode = false
                            }) {
                                Icon(
                                    Icons.Default.PushPin,
                                    contentDescription = if (anyUnpinned) "Pin" else "Unpin",
                                    tint = theme.fontsPrimary
                                )
                            }

                            IconButton(onClick = {
                                copyNotesToClipboard(context, selectedNotes)
                                selectedIds.clear()
                                selectionMode = false
                            }) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = theme.fontsPrimary
                                )
                            }

                            IconButton(onClick = {
                                shareNotes(context, selectedNotes)
                                selectedIds.clear()
                                selectionMode = false
                            }) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = theme.fontsPrimary
                                )
                            }
                        }

                        IconButton(onClick = {
                            if (selectedIds.size == notes.size) selectedIds.clear()
                            else {
                                selectedIds.clear()
                                selectedIds.addAll(notes.map { it.id })
                            }
                        }) {
                            Icon(
                                Icons.Default.SelectAll,
                                contentDescription = "Select all",
                                tint = theme.fontsPrimary
                            )
                        }

                        IconButton(onClick = {
                            NotesStore.moveToTrash(selectedIds.toList())
                            selectedIds.clear()
                            selectionMode = false
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Move to trash",
                                tint = theme.consoleError
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.bgMantle)
                )
            } else if (searchActive) {
                TopAppBar(
                    title = {
                        SearchField(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            focusRequester = searchFocus,
                            onClose = {
                                searchActive = false
                                searchQuery = ""
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            searchActive = false
                            searchQuery = ""
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = theme.fontsPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.bgMantle)
                )
            } else {
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
                        IconButton(onClick = { searchActive = true }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = theme.fontsPrimary
                            )
                        }
                        IconButton(onClick = { menuOpen = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = theme.fontsPrimary
                            )
                        }
                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false },
                            modifier = Modifier.background(theme.bgOverlay)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Trash", color = theme.fontsPrimary) },
                                onClick = { menuOpen = false; onOpenTrash() }
                            )
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.bgMantle)
                )
            }
        },
        floatingActionButton = {
            if (!selectionMode && !searchActive) {
                FloatingActionButton(
                    onClick = onAddNoteClick,
                    containerColor = theme.buttonPrimaryBg,
                    contentColor = theme.buttonPrimaryText
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Note")
                }
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
                    text = when {
                        searchActive && searchQuery.isNotBlank() -> "No matches for \"$searchQuery\""
                        searchActive -> "Type to search notes"
                        else -> "No notes yet.\nTap + to create one."
                    },
                    color = theme.fontsSecondary,
                    letterSpacing = 1.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                    val isSelected = note.id in selectedIds
                    NoteItem(
                        note = note,
                        selectionMode = selectionMode,
                        selected = isSelected,
                        onClick = {
                            if (selectionMode) {
                                // Toggle selection
                                if (isSelected) selectedIds.remove(note.id)
                                else selectedIds.add(note.id)
                            } else {
                                // Open the note
                                onNoteClick(note.id)
                            }
                        },
                        onLongClick = {
                            if (!selectionMode) {
                                // Enter selection mode with this note selected
                                selectionMode = true
                                if (note.id !in selectedIds) selectedIds.add(note.id)
                            } else {
                                // Already in selection mode — toggle this note
                                if (isSelected) selectedIds.remove(note.id)
                                else selectedIds.add(note.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    note: Note,
    selectionMode: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val theme = ThemeManager.active
    val shape = theme.cornerShape()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .then(theme.themedBevel(shape)),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) theme.bgOverlay else theme.bgSurface
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectionMode) {
                SelectionIndicator(selected = selected)
                Spacer(Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (note.pinned) {
                        Icon(
                            Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = theme.accentPink,
                            modifier = Modifier
                                .size(14.dp)
                                .padding(end = 2.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(
                        text = note.safeTitle(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = theme.fontsHeadings,
                        maxLines = 1
                    )
                }
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
        }
    }
}

@Composable
private fun SelectionIndicator(selected: Boolean) {
    val theme = ThemeManager.active
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(if (selected) theme.buttonPrimaryBg else theme.bgOverlay),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                tint = theme.buttonPrimaryText,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onClose: () -> Unit
) {
    val theme = ThemeManager.active
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(
            color = theme.fontsPrimary,
            fontSize = 16.sp
        ),
        cursorBrush = androidx.compose.ui.graphics.SolidColor(theme.buttonPrimaryBg),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            "Search notes…",
                            color = theme.fontsSecondary,
                            fontSize = 16.sp
                        )
                    }
                    inner()
                }
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = theme.fontsSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    )
}

// ------- bulk actions -------

private fun copyNotesToClipboard(context: Context, notes: List<Note>) {
    if (notes.isEmpty()) return
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val text = notes.joinToString("\n\n---\n\n") { note ->
        if (note.title.isBlank()) note.content else "${note.title}\n\n${note.content}"
    }
    cm.setPrimaryClip(ClipData.newPlainText("Notes", text))
}

private fun shareNotes(context: Context, notes: List<Note>) {
    if (notes.isEmpty()) return
    val text = notes.joinToString("\n\n---\n\n") { note ->
        if (note.title.isBlank()) note.content else "${note.title}\n\n${note.content}"
    }
    val subject = if (notes.size == 1) notes[0].title else "${notes.size} notes"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share"))
}