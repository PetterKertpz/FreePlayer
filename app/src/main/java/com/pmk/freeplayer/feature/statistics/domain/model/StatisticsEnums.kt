package com.pmk.freeplayer.feature.statistics.domain.model

enum class EntityType { SONG, ARTIST, ALBUM, GENRE, PLAYLIST }

/**
 * Context that triggered the play — powers recommendation signals.
 */
enum class PlaySource {
	LIBRARY,        // Tapped directly from song list
	ALBUM,          // Playing from album screen
	ARTIST,         // Playing from artist screen
	PLAYLIST,       // Inside a playlist
	SEARCH,         // From search results
	SHUFFLE,        // Global shuffle
	QUEUE,          // Manually added to queue
	RECOMMENDATION, // AI/auto-generated
}