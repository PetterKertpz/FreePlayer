package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.enums.LyricsStatus

data class Lyrics(
	val id: Long,
	val songId: Long,
	
	// ═══════════════════════════════════════════════════════════════
	// CONTENT
	// ═══════════════════════════════════════════════════════════════
	val plainText: String,    // Always has content (even if raw LRC)
	val syncedText: String?,  // Raw LRC string "[00:12.50] Hello..."
	
	// ═══════════════════════════════════════════════════════════════
	// METADATA
	// ═══════════════════════════════════════════════════════════════
	val sourceUrl: String?,   // Where it was found (Genius URL, etc.)
	val language: String?,    // ISO code: "en", "es", etc.
	
	// ═══════════════════════════════════════════════════════════════
	// STATUS (Using unified LyricsStatus)
	// ═══════════════════════════════════════════════════════════════
	val status: LyricsStatus = LyricsStatus.FOUND_ONLINE
) {
	/**
	 * True if lyrics have timing info for karaoke mode
	 */
	val isSynced: Boolean
		get() = !syncedText.isNullOrBlank()
	
	/**
	 * True if we have any lyrics content
	 */
	val hasContent: Boolean
		get() = plainText.isNotBlank()
	
	/**
	 * Source display name for UI
	 */
	val sourceDisplayName: String
		get() = when (status) {
			LyricsStatus.FOUND_EMBEDDED -> "Embedded"
			LyricsStatus.FOUND_LOCAL -> "Local file"
			LyricsStatus.FOUND_ONLINE -> sourceUrl?.extractDomain() ?: "Online"
			else -> "Unknown"
		}
	
	companion object {
		/**
		 * Empty lyrics object for when none are found
		 */
		fun empty(songId: Long) = Lyrics(
			id = 0,
			songId = songId,
			plainText = "",
			syncedText = null,
			sourceUrl = null,
			language = null,
			status = LyricsStatus.NOT_FOUND
		)
	}
}

/**
 * Extract domain from URL for display
 */
private fun String.extractDomain(): String? {
	return try {
		val withoutProtocol = removePrefix("https://").removePrefix("http://")
		withoutProtocol.substringBefore("/").substringBefore("?")
	} catch (e: Exception) {
		null
	}
}