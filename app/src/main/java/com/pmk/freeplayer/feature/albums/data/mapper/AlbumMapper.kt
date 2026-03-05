package com.pmk.freeplayer.feature.albums.data.mapper

import com.pmk.freeplayer.core.common.utils.TrackDuration
import com.pmk.freeplayer.feature.albums.data.local.entity.AlbumEntity
import com.pmk.freeplayer.feature.albums.domain.model.Album

// ── Entity → Domain ───────────────────────────────────────────────────────────

fun AlbumEntity.toDomain(): Album = Album(
	id           = albumId,
	title        = title,
	artistId     = artistId,
	artistName   = artistName,
	coverUri     = localCoverPath ?: remoteCoverUrl,
	type         = type,                         // FIX: direct enum, no fromString() needed
	genreId      = genreId,
	genreName    = genreName,
	year         = year,
	dateAdded    = dateAdded,
	description  = description,
	producer     = producer,
	recordLabel  = recordLabel,
	songCount    = totalSongs,
	totalDuration = TrackDuration(totalDurationMs),
	rating       = rating,
	isFavorite   = isFavorite,
	geniusId     = geniusId,
	spotifyId    = spotifyId,
)

fun List<AlbumEntity>.toDomain(): List<Album> = map { it.toDomain() }

// ── Domain → Entity (read-modify-write) ──────────────────────────────────────

/**
 * Preserves system fields (dateAdded, releaseDate, localCoverPath, structural counts)
 * that have no domain representation.
 */
fun Album.toEntity(original: AlbumEntity, now: Long): AlbumEntity = original.copy(
	title        = title,
	artistId     = artistId,
	artistName   = artistName,
	type         = type,
	genreId      = genreId,
	genreName    = genreName,
	year         = year,
	description  = description,
	producer     = producer,
	recordLabel  = recordLabel,
	isFavorite   = isFavorite,
	rating       = rating,
	geniusId     = geniusId,
	spotifyId    = spotifyId,
	
	// Image: preserve existing local cover; update remote only if coverUri is http
	localCoverPath  = original.localCoverPath
		?: coverUri?.takeIf { !it.startsWith("http", ignoreCase = true) },
	remoteCoverUrl  = coverUri?.takeIf { it.startsWith("http", ignoreCase = true) },
	
	// structural counts updated by Scanner via refreshStats(), not by domain writes
	totalSongs      = original.totalSongs,
	totalDurationMs = original.totalDurationMs,
	
	lastUpdated     = now,
	// dateAdded and releaseDate preserved from original via copy()
)