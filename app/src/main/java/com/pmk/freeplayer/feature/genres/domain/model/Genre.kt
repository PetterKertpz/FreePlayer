package com.pmk.freeplayer.feature.genres.domain.model

import java.util.Locale

data class Genre(
	val id: Long,
	val name: String,
	
	// ── Visual ────────────────────────────────────────────────────
	val description: String?,
	val hexColor: String?,
	val iconUri: String?,
	
	// ── Structural cache ──────────────────────────────────────────
	val songCount: Int,     // FIX: kept — structural count, not behavioral statistic
	val artistCount: Int,   // FIX: kept — structural count
	val albumCount: Int,    // FIX: kept — structural count
	
	// ── Extra metadata ────────────────────────────────────────────
	val originDecade: String?,
	val originCountry: String?,
) {
	val displayName: String
		get() = if (name.isBlank()) "Unknown"
		else name.trim().lowercase().split(" ").joinToString(" ") { word ->
			word.replaceFirstChar {
				if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
			}
		}
	
	val hasContent: Boolean get() = songCount > 0 || artistCount > 0 || albumCount > 0
	
	companion object {
		val UNKNOWN = Genre(
			id            = -1L,
			name          = "Unknown",
			description   = null,
			hexColor      = "#808080",
			iconUri       = null,
			songCount     = 0,
			artistCount   = 0,
			albumCount    = 0,
			originDecade  = null,
			originCountry = null,
		)
	}
}