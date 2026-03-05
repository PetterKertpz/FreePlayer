package com.pmk.freeplayer.feature.playlists.domain.model

/**
 * System-managed playlist types.
 * RECENTLY_PLAYED and MOST_PLAYED are statistics-backed: their song lists
 * are resolved at read time via GetRecentlyPlayedUseCase / GetMostPlayedUseCase
 * from feature/statistics, not stored in playlist_song_join.
 */
enum class SystemPlaylistType {
	FAVORITES,
	RECENTLY_ADDED,
	RECENTLY_PLAYED,  // read from stats_aggregates ORDER BY last_played_at DESC
	MOST_PLAYED,      // read from stats_aggregates ORDER BY play_count DESC
	;
	
	companion object {
		fun from(value: String?): SystemPlaylistType? =
			entries.find { it.name.equals(value, ignoreCase = true) }
	}
}