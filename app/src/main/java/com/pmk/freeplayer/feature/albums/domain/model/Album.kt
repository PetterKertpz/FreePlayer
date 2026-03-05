package com.pmk.freeplayer.feature.albums.domain.model

import com.pmk.freeplayer.core.common.utils.TrackDuration

data class Album(
	val id: Long,
	val title: String,
	
	// ── Relation ──────────────────────────────────────────────────
	val artistId: Long,
	val artistName: String,
	
	// ── Multimedia ────────────────────────────────────────────────
	val coverUri: String?,
	
	// ── Metadata ──────────────────────────────────────────────────
	val type: AlbumType,
	val genreId: Long?,
	val genreName: String?,
	val year: Int?,
	val dateAdded: Long,
	
	// ── Rich metadata ─────────────────────────────────────────────
	val description: String?,
	val producer: String?,
	val recordLabel: String?,
	
	// ── Structural cache ──────────────────────────────────────────
	val songCount: Int,
	val totalDuration: TrackDuration,
	
	// ── User preferences ──────────────────────────────────────────
	val rating: Float,
	val isFavorite: Boolean,
	
	// ── External references ───────────────────────────────────────
	val geniusId: String?,
	val spotifyId: String?,
) {
	val isLive: Boolean   get() = type == AlbumType.LIVE
	val isEp: Boolean     get() = type == AlbumType.EP
	val isSingle: Boolean get() = type == AlbumType.SINGLE
	val hasDescription: Boolean get() = !description.isNullOrBlank()
}