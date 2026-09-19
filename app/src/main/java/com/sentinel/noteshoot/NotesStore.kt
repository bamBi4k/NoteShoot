package com.sentinel.noteshoot

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.launch

object NotesStore {

    private const val PREFS = "notes_store_prefs"
    private const val KEY_NOTES = "notes_json"
    private const val KEY_INITIALIZED = "initialized"

    private lateinit var appContext: Context

    /** All notes, including trashed ones. */
    private val _allNotes = MutableStateFlow<List<Note>>(emptyList())

    /** Active notes only (not trashed), sorted: pinned first, then most recent. */
    val notes: StateFlow<List<Note>> = _allNotes
        .map { list ->
            list.filter { !it.isTrashed }
                .sortedWith(
                    compareByDescending<Note> { it.pinned }
                        .thenByDescending { it.timestamp }
                )
        }
        .let { flow ->
            // Wrap the flow so it's stable across recompositions
            val backing = MutableStateFlow<List<Note>>(emptyList())
            kotlinx.coroutines.MainScope().launch {
                flow.collect { backing.value = it }
            }
            backing.asStateFlow()
        }

    /** Trashed notes only, most recently deleted first. */
    val trashedNotes: StateFlow<List<Note>> = _allNotes
        .map { list ->
            list.filter { it.isTrashed }
                .sortedByDescending { it.deletedAt ?: 0L }
        }
        .let { flow ->
            val backing = MutableStateFlow<List<Note>>(emptyList())
            kotlinx.coroutines.MainScope().launch {
                flow.collect { backing.value = it }
            }
            backing.asStateFlow()
        }

    fun init(context: Context) {
        if (::appContext.isInitialized) return
        appContext = context.applicationContext
        loadFromDisk()
    }

    // ---------- Persistence ----------

    private fun loadFromDisk() {
        val prefs = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val initialized = prefs.getBoolean(KEY_INITIALIZED, false)

        if (!initialized) {
            _allNotes.value = listOf(
                Note(title = "Welcome", content = "This is your first note. Tap to edit.")
            )
            saveToDisk()
            prefs.edit().putBoolean(KEY_INITIALIZED, true).apply()
            return
        }

        val json = prefs.getString(KEY_NOTES, null) ?: "[]"
        _allNotes.value = parseJson(json)
    }

    private fun saveToDisk() {
        if (!::appContext.isInitialized) return
        val json = serializeJson(_allNotes.value)
        appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_NOTES, json)
            .apply()
    }

    private fun serializeJson(notes: List<Note>): String {
        val arr = JSONArray()
        notes.forEach { n ->
            val obj = JSONObject()
            obj.put("id", n.id)
            obj.put("title", n.title)
            obj.put("content", n.content)
            obj.put("timestamp", n.timestamp)
            obj.put("pinned", n.pinned)
            n.deletedAt?.let { obj.put("deletedAt", it) }
            arr.put(obj)
        }
        return arr.toString()
    }

    private fun parseJson(json: String): List<Note> {
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                Note(
                    id = obj.getString("id"),
                    title = obj.optString("title", ""),
                    content = obj.optString("content", ""),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    pinned = obj.optBoolean("pinned", false),
                    deletedAt = if (obj.has("deletedAt")) obj.optLong("deletedAt") else null
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ---------- Public API ----------

    fun addNote(title: String, content: String): Note {
        val newNote = Note(title = title, content = content)
        _allNotes.update { current -> current + newNote }
        afterChange()
        return newNote
    }

    fun updateNote(id: String, title: String, content: String) {
        _allNotes.update { current ->
            current.map { n ->
                if (n.id == id) n.copy(
                    title = title,
                    content = content,
                    timestamp = System.currentTimeMillis()
                ) else n
            }
        }
        afterChange()
    }

    fun togglePin(id: String) {
        _allNotes.update { current ->
            current.map { n ->
                if (n.id == id) n.copy(pinned = !n.pinned) else n
            }
        }
        afterChange()
    }

    /** Soft delete: moves to trash. */
    fun moveToTrash(ids: Collection<String>) {
        val now = System.currentTimeMillis()
        val idSet = ids.toSet()
        _allNotes.update { current ->
            current.map { n ->
                if (n.id in idSet && !n.isTrashed) n.copy(deletedAt = now, pinned = false)
                else n
            }
        }
        afterChange()
    }

    /** Convenience for single delete. */
    fun moveToTrash(id: String) = moveToTrash(listOf(id))

    /** Restore from trash. */
    fun restoreFromTrash(ids: Collection<String>) {
        val idSet = ids.toSet()
        _allNotes.update { current ->
            current.map { n ->
                if (n.id in idSet && n.isTrashed) n.copy(deletedAt = null)
                else n
            }
        }
        afterChange()
    }

    fun restoreFromTrash(id: String) = restoreFromTrash(listOf(id))

    /** Permanently remove specific notes (used by trash screen). */
    fun purge(ids: Collection<String>) {
        val idSet = ids.toSet()
        _allNotes.update { current -> current.filterNot { it.id in idSet } }
        afterChange()
    }

    fun purge(id: String) = purge(listOf(id))

    /** Permanently remove every trashed note. */
    fun emptyTrash() {
        _allNotes.update { current -> current.filterNot { it.isTrashed } }
        afterChange()
    }

    fun getNote(id: String): Note? = _allNotes.value.find { it.id == id }

    /** Case-insensitive search across active notes. */
    fun search(query: String): List<Note> {
        if (query.isBlank()) return notes.value
        return notes.value.filter { it.matches(query) }
    }

    // ---------- Widget Sync ----------

    private fun afterChange() {
        saveToDisk()
        notifyWidgets()
    }

    private fun notifyWidgets() {
        if (!::appContext.isInitialized) return
        val intent = Intent(appContext, com.sentinel.noteshoot.widget.NoteWidget::class.java).apply {
            action = android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val manager = android.appwidget.AppWidgetManager.getInstance(appContext)
        val ids = manager.getAppWidgetIds(
            android.content.ComponentName(appContext, com.sentinel.noteshoot.widget.NoteWidget::class.java)
        )
        intent.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        appContext.sendBroadcast(intent)
    }
}