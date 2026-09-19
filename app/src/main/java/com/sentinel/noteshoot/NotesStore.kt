package com.sentinel.noteshoot

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject

object NotesStore {

    private const val PREFS = "notes_store_prefs"
    private const val KEY_NOTES = "notes_json"
    private const val KEY_INITIALIZED = "initialized"

    private lateinit var appContext: Context

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    /** Call this once from Application.onCreate or MainActivity.onCreate */
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
            // First launch — seed with a welcome note
            _notes.value = listOf(
                Note(title = "Welcome", content = "This is your first note. Tap to edit.")
            )
            saveToDisk()
            prefs.edit().putBoolean(KEY_INITIALIZED, true).apply()
            return
        }

        val json = prefs.getString(KEY_NOTES, null) ?: "[]"
        _notes.value = parseJson(json)
    }

    private fun saveToDisk() {
        if (!::appContext.isInitialized) return
        val json = serializeJson(_notes.value)
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
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ---------- Public API ----------

    fun addNote(title: String, content: String): Note {
        val newNote = Note(title = title, content = content)
        _notes.update { current -> current + newNote }
        afterChange()
        return newNote
    }

    fun updateNote(id: String, title: String, content: String) {
        _notes.update { current ->
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

    fun deleteNote(id: String) {
        _notes.update { current -> current.filterNot { it.id == id } }
        afterChange()
    }

    fun getNote(id: String): Note? = _notes.value.find { it.id == id }

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