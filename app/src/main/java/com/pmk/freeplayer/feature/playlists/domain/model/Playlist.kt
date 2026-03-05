package com.pmk.freeplayer.feature.playlists.domain.model

import com.pmk.freeplayer.core.common.utils.TrackDuration

data class Playlist(
	val id: Long,
	val name: String,
	val description: String?,
	val coverUri: String?,
	val hexColor: String?,
	
	// ── Type ──────────────────────────────────────────────────────
	val isSystem: Boolean,
	val systemType: SystemPlaylistType?,  // FIX: enum, not String
	val isPinned: Boolean,
	
	// ── Structural cache ──────────────────────────────────────────
	// FIX: playCount removed — delegated to feature/statistics
	val songCount: Int,
	val totalDuration: TrackDuration,
	
	// ── Timestamps ────────────────────────────────────────────────
	val createdAt: Long,
	val updatedAt: Long,
) {
	val hasCustomCover: Boolean get() = !coverUri.isNullOrBlank()
	val isEmpty: Boolean        get() = songCount == 0
	val isNotEmpty: Boolean     get() = songCount > 0
	
	/** Statistics-backed playlists don't store songs in playlist_song_join. */
	val isStatisticsBacked: Boolean
		get() = systemType == SystemPlaylistType.RECENTLY_PLAYED ||
				systemType == SystemPlaylistType.MOST_PLAYED
}