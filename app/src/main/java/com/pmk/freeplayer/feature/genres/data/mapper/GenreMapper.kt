package com.pmk.freeplayer.feature.genres.data.mapper

import com.pmk.freeplayer.feature.genres.data.local.entity.GenreEntity
import com.pmk.freeplayer.feature.genres.domain.model.Genre
import java.util.Locale

// ── Entity → Domain ───────────────────────────────────────────────────────────

fun GenreEntity.toDomain(): Genre = Genre(
	id            = genreId,
	name          = name,
	description   = description,
	hexColor      = hexColor,
	iconUri       = localIconPath ?: remoteIconUrl,  // local takes priority
	songCount     = songCount,
	artistCount   = artistCount,
	albumCount    = albumCount,
	originDecade  = originDecade,
	originCountry = originCountry,
)

fun List<GenreEntity>.toDomain(): List<Genre> = map { it.toDomain() }

// ── Domain → Entity (read-modify-write) ──────────────────────────────────────

/**
 * Preserves system fields (dateAdded, normalizedName, structural counts)
 * that have no domain representation.
 * Structural counts (songCount, artistCount, albumCount) are updated by
 * the Scanner via refreshStats(), never by domain writes.
 */
fun Genre.toEntity(original: GenreEntity, now: Long): GenreEntity = original.copy(
	name          = name.trim(),
	normalizedName = name.trim().uppercase(),
	description   = description?.trim(),
	hexColor      = hexColor,
	originDecade  = originDecade,
	originCountry = originCountry,
	
	// Image: preserve local; update remote only if iconUri is http
	localIconPath  = original.localIconPath
		?: iconUri?.takeIf { !it.startsWith("http", ignoreCase = true) },
	remoteIconUrl  = iconUri?.takeIf { it.startsWith("http", ignoreCase = true) }
		?: original.remoteIconUrl,
	
	// Structural counts are owned by the Scanner — never overwritten by domain edits
	songCount    = original.songCount,
	artistCount  = original.artistCount,
	albumCount   = original.albumCount,
	
	lastUpdated  = now,
	// dateAdded preserved from original via copy()
)

// ── Scanner factory ───────────────────────────────────────────────────────────

fun createGenreEntity(name: String, now: Long): GenreEntity {
	val clean = name.trim()
	return GenreEntity(
		genreId        = 0,
		name           = clean.replaceFirstChar { it.titlecase(Locale.ROOT) },
		normalizedName = clean.uppercase(),
		songCount      = 1,  // at least one song triggered creation
		dateAdded      = now,
		lastUpdated    = now,
	)
}