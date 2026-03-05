package com.pmk.freeplayer.feature.playlists.data.mapper

import com.pmk.freeplayer.core.common.utils.TrackDuration
import com.pmk.freeplayer.feature.playlists.data.local.entity.PlaylistEntity
import com.pmk.freeplayer.feature.playlists.domain.model.Playlist
import com.pmk.freeplayer.feature.playlists.domain.model.SystemPlaylistType

// ── Entity → Domain ───────────────────────────────────────────────────────────

fun PlaylistEntity.toDomain(): Playlist = Playlist(
	id            = playlistId,
	name          = name,
	description   = description,
	coverUri      = coverPath,
	hexColor      = hexColor,
	isSystem      = isSystem,
	systemType    = systemType,
	isPinned      = isPinned,
	songCount     = songCount,
	totalDuration = TrackDuration(totalDurationMs),
	createdAt     = createdAt,
	updatedAt     = updatedAt,
)

fun List<PlaylistEntity>.toDomain(): List<Playlist> = map { it.toDomain() }

// ── Domain → Entity (read-modify-write) ──────────────────────────────────────

/**
 * Preserves system fields (createdAt, isSystem, systemType, structural counts)
 * not present in the domain model.
 * Structural counts are updated by refreshStats(), never by domain writes.
 */
fun Playlist.toEntity(original: PlaylistEntity, now: Long): PlaylistEntity = original.copy(
	name          = name,
	description   = description,
	coverPath     = coverUri,
	hexColor      = hexColor,
	isPinned      = isPinned,
	// isSystem, systemType, songCount, totalDurationMs, createdAt preserved from original
	updatedAt     = now,
)

// ── Factories ─────────────────────────────────────────────────────────────────

fun createUserPlaylistEntity(name: String, description: String? = null, now: Long): PlaylistEntity =
	PlaylistEntity(
		name        = name.trim(),
		description = description?.trim(),
		isSystem    = false,
		createdAt   = now,
		updatedAt   = now,
	)

fun createSystemPlaylistEntity(
	name: String,
	type: SystemPlaylistType,
	hexColor: String? = null,
	now: Long,
): PlaylistEntity = PlaylistEntity(
	name        = name,
	isSystem    = true,
	systemType  = type,
	isPinned    = true,
	hexColor    = hexColor,
	createdAt   = now,
	updatedAt   = now,
)