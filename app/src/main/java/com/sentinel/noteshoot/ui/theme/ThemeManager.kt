package com.sentinel.noteshoot.ui.theme

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sentinel.noteshoot.widget.NoteWidget

object ThemeManager {

    private const val PREFS = "theme_prefs"
    private const val KEY_ACTIVE = "active_theme_id"
    private const val KEY_UNLOCKED = "unlocked_theme_ids"
    private const val KEY_EGG = "easter_egg_hits"
    private const val KEY_BASE_PT = "base_pt"

    private const val DEFAULT_THEME = "mono_dark"
    private const val VGUI = "vgui"
    private const val EGG_THRESHOLD = 10

    /** Base font size in points. Everything scales relative to this. */
    const val BASE_PT_MIN = 11f
    const val BASE_PT_MAX = 18f
    const val BASE_PT_DEFAULT = 13f

    /** The pt value that all layout sizes were originally designed around. */
    const val BASE_PT_REFERENCE = 13f

    private lateinit var appContext: Context

    var active by mutableStateOf(ThemeCatalog.monoDark)
        private set

    var unlockedIds by mutableStateOf(setOf("mono_dark", "mono_light"))
        private set

    var eggHits by mutableStateOf(0)
        private set

    /** Base font size in points. Range: 11–18. */
    var basePt by mutableStateOf(BASE_PT_DEFAULT)
        private set

    /** Convenience: scale factor relative to the reference pt. */
    val fontScale: Float
        get() = basePt / BASE_PT_REFERENCE

    fun init(context: Context) {
        if (::appContext.isInitialized) return
        appContext = context.applicationContext

        val prefs = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val savedId = prefs.getString(KEY_ACTIVE, DEFAULT_THEME) ?: DEFAULT_THEME
        val unlocked = prefs.getStringSet(KEY_UNLOCKED, null)
            ?.toSet()
            ?: setOf("mono_dark", "mono_light")
        val hits = prefs.getInt(KEY_EGG, 0)
        val pt = prefs.getFloat(KEY_BASE_PT, BASE_PT_DEFAULT)

        unlockedIds = unlocked
        eggHits = hits
        basePt = pt.coerceIn(BASE_PT_MIN, BASE_PT_MAX)
        active = if (savedId in unlocked) ThemeCatalog.byId(savedId)
        else ThemeCatalog.byId(DEFAULT_THEME)
    }

    fun setTheme(theme: ThemeSpec) {
        if (theme.id !in unlockedIds) return
        active = theme
        persistActive(theme.id)
        refreshWidgets()
    }

    /** Update base font size in points. Clamped to valid range. */
    fun updateBasePt(pt: Float) {
        val clamped = pt.coerceIn(BASE_PT_MIN, BASE_PT_MAX)
        basePt = clamped
        prefs().edit().putFloat(KEY_BASE_PT, clamped).apply()
        refreshWidgets()  // <-- must be here
    }

    fun registerEasterEggHit(): Boolean {
        val newHits = eggHits + 1
        eggHits = newHits
        persistEggHits(newHits)

        if (newHits >= EGG_THRESHOLD && VGUI !in unlockedIds) {
            unlockedIds = unlockedIds + VGUI
            persistUnlocked()
            return true
        }
        return false
    }

    fun resetEasterEggHits() {
        eggHits = 0
        persistEggHits(0)
    }

    private fun refreshWidgets() {
        if (!::appContext.isInitialized) return
        val intent = Intent(appContext, NoteWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val manager = AppWidgetManager.getInstance(appContext)
        val ids = manager.getAppWidgetIds(
            ComponentName(appContext, NoteWidget::class.java)
        )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        appContext.sendBroadcast(intent)
    }

    private fun persistActive(id: String) {
        prefs().edit().putString(KEY_ACTIVE, id).apply()
    }

    private fun persistUnlocked() {
        prefs().edit().putStringSet(KEY_UNLOCKED, unlockedIds).apply()
    }

    private fun persistEggHits(n: Int) {
        prefs().edit().putInt(KEY_EGG, n).apply()
    }

    private fun prefs() =
        appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}