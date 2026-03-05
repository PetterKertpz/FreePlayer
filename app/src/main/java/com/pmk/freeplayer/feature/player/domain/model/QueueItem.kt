package com.pmk.freeplayer.feature.player.domain.model

import com.pmk.freeplayer.feature.statistics.domain.model.PlaySource

/**
 * Immutable snapshot of a song loaded into the queue.
 * Carries all display + stats data so the Player never needs to
 * query the DB while playing.
 */
data class QueueItem(
	val songId: Long,
	val title: String,
	val artistName: String,
	val albumName: String?,
	val filePath: String,
	val durationMs: Long,
	val coverUri: String?,
	val artistId: Long?,
	val albumId: Long?,
	val genreId: Long?,
	val playlistId: Long?,
	val source: PlaySource,
)