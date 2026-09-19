package com.sentinel.noteshoot

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val pinned: Boolean = false,
    /** Null = active. Non-null = in trash (timestamp of when deleted). */
    val deletedAt: Long? = null
) {

    val isTrashed: Boolean get() = deletedAt != null

    fun formattedTime(): String {
        val fmt = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
        return fmt.format(Date(timestamp))
    }

    fun formattedDeletedTime(): String {
        val fmt = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
        return fmt.format(Date(deletedAt ?: timestamp))
    }

    /** Codepoint-safe snippet. Never splits an emoji in half. */
    fun snippet(maxCodepoints: Int = 80): String {
        val cps = content.codePoints().toArray()
        if (cps.size <= maxCodepoints) return content
        val truncated = String(cps, 0, maxCodepoints).trimEnd()
        return "$truncated…"
    }

    /** Codepoint-safe title trim. */
    fun safeTitle(maxCodepoints: Int = 60): String {
        if (title.isBlank()) return "(untitled)"
        val cps = title.codePoints().toArray()
        if (cps.size <= maxCodepoints) return title
        return String(cps, 0, maxCodepoints).trimEnd() + "…"
    }

    /** True if title or content contains the query (case-insensitive). */
    fun matches(query: String): Boolean {
        if (query.isBlank()) return true
        val q = query.trim()
        return title.contains(q, ignoreCase = true)
                || content.contains(q, ignoreCase = true)
    }
}