package com.pmk.freeplayer.feature.player.domain.model

import com.pmk.freeplayer.feature.songs.domain.model.Song
import com.pmk.freeplayer.feature.statistics.domain.model.PlaySource

// feature/player/domain/model/QueueItem.kt — versión completa

data class QueueItem(
	val songId     : Long,
	val title      : String,
	val artist     : String,
	val albumName  : String?,
	val filePath   : String,
	val durationMs : Long,
	val coverUri   : String?,
	// ── Relational IDs — para Statistics ─────────────────────────
	val artistId   : Long?,
	val albumId    : Long?,
	val genreId    : Long?,
	val playlistId : Long? = null,     // null si no viene de una playlist
	// ── Contexto de reproducción — para Statistics ────────────────
	val source     : PlaySource = PlaySource.LIBRARY,
)

fun Song.toQueueItem(
	playlistId: Long? = null,
	source: PlaySource = PlaySource.LIBRARY,
): QueueItem = QueueItem(
	songId     = id,
	title      = title,
	artist     = artistName,
	albumName  = null,          // se puede enriquecer en el ViewModel si se necesita
	filePath   = filePath,
	durationMs = duration.millis,
	coverUri   = null,          // ídem
	artistId   = artistId,
	albumId    = albumId,
	genreId    = genreId,
	playlistId = playlistId,
	source     = source,
)