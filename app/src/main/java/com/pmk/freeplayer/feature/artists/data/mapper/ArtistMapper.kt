package com.pmk.freeplayer.feature.artists.data.mapper

import com.pmk.freeplayer.feature.artists.data.local.entity.ArtistEntity
import com.pmk.freeplayer.feature.artists.domain.model.Artist
import com.pmk.freeplayer.feature.artists.domain.model.SocialLinks

// ── Entity → Domain ───────────────────────────────────────────────────────────

fun ArtistEntity.toDomain(): Artist = Artist(
	id             = artistId,
	name           = name,
	realName       = realName,
	isVerified     = isVerified,
	type           = type,                         // FIX: direct enum, no fromString() needed
	country        = country,
	city           = city,
	coverUri       = localImagePath ?: remoteImageUrl,
	headerUri      = remoteHeaderUrl,
	biography      = biography,
	description    = description,
	genreId        = genreId,
	genreName      = genreName,
	songCount      = totalSongs,
	albumCount     = totalAlbums,
	careerStartYear = careerStartYear,
	birthDate      = birthDate,
	isFavorite     = isFavorite,
	socialLinks    = buildSocialLinks(),
)

fun List<ArtistEntity>.toDomain(): List<Artist> = map { it.toDomain() }

// ── Domain → Entity ───────────────────────────────────────────────────────────

/**
 * Read-modify-write: preserves system fields (dateAdded, localImagePath, cached counts)
 * that have no domain representation.
 */
fun Artist.toEntity(original: ArtistEntity, now: Long): ArtistEntity = original.copy(
	name            = name,
	realName        = realName,
	isVerified      = isVerified,
	type            = type,
	country         = country,
	city            = city,
	biography       = biography,
	description     = description,
	genreId         = genreId,
	genreName       = genreName,
	birthDate       = birthDate,
	careerStartYear = careerStartYear,
	isFavorite      = isFavorite,
	
	// Image: preserve local path; remote updated only if coverUri is http
	localImagePath  = original.localImagePath
		?: coverUri?.takeIf { !it.startsWith("http", ignoreCase = true) },
	remoteImageUrl  = coverUri?.takeIf { it.startsWith("http", ignoreCase = true) },
	remoteHeaderUrl = headerUri,
	
	// Social links
	websiteUrl       = socialLinks.websiteUrl,
	spotifyId        = extractSpotifyId(socialLinks.spotifyUrl),
	geniusId         = extractPathSlug(socialLinks.geniusUrl),
	instagramUsername = socialLinks.instagramUsername,
	twitterUsername  = socialLinks.twitterUsername,
	tiktokUsername   = socialLinks.tiktokUsername,
	appleMusicUrl    = socialLinks.appleMusicUrl,
	youtubeMusicUrl  = socialLinks.youtubeMusicUrl,
	facebookUrl      = socialLinks.facebookUrl,
	soundcloudUrl    = socialLinks.soundcloudUrl,
	bandcampUrl      = socialLinks.bandcampUrl,
	discogsUrl       = socialLinks.discogsUrl,
	musicBrainzId    = socialLinks.musicBrainzId,
	
	lastUpdated = now,
)

// ── Private helpers ───────────────────────────────────────────────────────────

private fun ArtistEntity.buildSocialLinks(): SocialLinks = SocialLinks(
	websiteUrl       = websiteUrl,
	spotifyUrl       = spotifyId?.let { "https://open.spotify.com/artist/$it" },
	geniusUrl        = geniusId?.let { "https://genius.com/artists/$it" },
	appleMusicUrl    = appleMusicUrl,
	youtubeMusicUrl  = youtubeMusicUrl,
	instagramUsername = instagramUsername,
	twitterUsername  = twitterUsername,
	tiktokUsername   = tiktokUsername,
	facebookUrl      = facebookUrl,
	soundcloudUrl    = soundcloudUrl,
	bandcampUrl      = bandcampUrl,
	discogsUrl       = discogsUrl,
	musicBrainzId    = musicBrainzId,
)

private fun extractSpotifyId(url: String?): String? =
	url?.let { Regex("spotify\\.com/artist/([a-zA-Z0-9]+)").find(it)?.groupValues?.getOrNull(1) }

private fun extractPathSlug(url: String?): String? =
	url?.trimEnd('/')?.split("/")?.lastOrNull()?.takeIf { it.isNotBlank() }