package com.pmk.freeplayer.feature.statistics.data.mapper

import com.pmk.freeplayer.feature.statistics.data.local.entity.PlayEventEntity
import com.pmk.freeplayer.feature.statistics.data.local.entity.StatsAggregateEntity
import com.pmk.freeplayer.feature.statistics.domain.model.EntityStats
import com.pmk.freeplayer.feature.statistics.domain.model.PlayEvent

fun PlayEvent.toEntity(): PlayEventEntity = PlayEventEntity(
	eventId        = id,
	songId         = songId,
	artistId       = artistId,
	albumId        = albumId,
	genreId        = genreId,
	playlistId     = playlistId,
	playedAt       = playedAt,
	listenedMs     = listenedMs,
	songDurationMs = songDurationMs,
	completionRatio = completionRatio,
	source         = source,
	wasSkipped     = wasSkipped,
)

fun StatsAggregateEntity.toDomain(): EntityStats = EntityStats(
	entityId        = entityId,
	entityType      = entityType,
	playCount       = playCount,
	totalListenedMs = totalListenedMs,
	skipCount       = skipCount,
	lastPlayedAt    = lastPlayedAt,
	firstPlayedAt   = firstPlayedAt,
)