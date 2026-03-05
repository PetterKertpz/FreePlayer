package com.pmk.freeplayer.feature.artists.domain.model

import kotlinx.serialization.Serializable

data class Artist(
	val id: Long,
	val name: String,
	
	// ── Identity ──────────────────────────────────────────────────
	val realName: String?,
	val isVerified: Boolean,
	val type: ArtistType,
	
	// ── Location ──────────────────────────────────────────────────
	val country: String?,
	val city: String?,
	
	// ── Multimedia ────────────────────────────────────────────────
	val coverUri: String?,
	val headerUri: String?,
	
	// ── Biography ─────────────────────────────────────────────────
	val biography: String?,
	val description: String?,
	
	// ── Genre (denormalized — avoids cross-feature entity dependency) ──
	val genreId: Long?,
	val genreName: String?,
	
	// ── Cached structural counts (not statistics/play behavior) ───
	val songCount: Int,
	val albumCount: Int,
	
	// ── Dates ─────────────────────────────────────────────────────
	val careerStartYear: Int?,
	val birthDate: Long?,
	
	// ── User preferences ──────────────────────────────────────────
	val isFavorite: Boolean,
	
	// ── Social links ──────────────────────────────────────────────
	val socialLinks: SocialLinks = SocialLinks(),
) {
	val hasDetails: Boolean
		get() = !biography.isNullOrBlank() || !description.isNullOrBlank()
	
	val locationText: String?
		get() = when {
			!city.isNullOrBlank() && !country.isNullOrBlank() -> "$city, $country"
			!country.isNullOrBlank() -> country
			else -> null
		}
	
	val hasSocialLinks: Boolean
		get() = socialLinks.hasAny
}

// ── Embedded value object ─────────────────────────────────────────────────────

@Serializable
data class SocialLinks(
	val websiteUrl: String?         = null,
	val spotifyUrl: String?         = null,
	val appleMusicUrl: String?      = null,
	val youtubeMusicUrl: String?    = null,
	val instagramUsername: String?  = null,
	val twitterUsername: String?    = null,
	val tiktokUsername: String?     = null,
	val facebookUrl: String?        = null,
	val soundcloudUrl: String?      = null,
	val bandcampUrl: String?        = null,
	val geniusUrl: String?          = null,
	val discogsUrl: String?         = null,
	val musicBrainzId: String?      = null,
) {
	val hasAny: Boolean
		get() = listOf(
			websiteUrl, spotifyUrl, appleMusicUrl, youtubeMusicUrl,
			instagramUsername, twitterUsername, tiktokUsername, facebookUrl,
			soundcloudUrl, bandcampUrl, geniusUrl, discogsUrl, musicBrainzId,
		).any { !it.isNullOrBlank() }
	
	val instagramUrl: String? get() = instagramUsername?.let { "https://instagram.com/$it" }
	val twitterUrl: String?   get() = twitterUsername?.let { "https://x.com/$it" }
	val tiktokUrl: String?    get() = tiktokUsername?.let { "https://tiktok.com/@$it" }
	
	fun toList(): List<SocialLinkItem> = buildList {
		websiteUrl?.let       { add(SocialLinkItem(SocialPlatform.WEBSITE, it)) }
		spotifyUrl?.let       { add(SocialLinkItem(SocialPlatform.SPOTIFY, it)) }
		appleMusicUrl?.let    { add(SocialLinkItem(SocialPlatform.APPLE_MUSIC, it)) }
		youtubeMusicUrl?.let  { add(SocialLinkItem(SocialPlatform.YOUTUBE_MUSIC, it)) }
		instagramUrl?.let     { add(SocialLinkItem(SocialPlatform.INSTAGRAM, it)) }
		twitterUrl?.let       { add(SocialLinkItem(SocialPlatform.TWITTER, it)) }
		tiktokUrl?.let        { add(SocialLinkItem(SocialPlatform.TIKTOK, it)) }
		soundcloudUrl?.let    { add(SocialLinkItem(SocialPlatform.SOUNDCLOUD, it)) }
		bandcampUrl?.let      { add(SocialLinkItem(SocialPlatform.BANDCAMP, it)) }
		geniusUrl?.let        { add(SocialLinkItem(SocialPlatform.GENIUS, it)) }
		discogsUrl?.let       { add(SocialLinkItem(SocialPlatform.DISCOGS, it)) }
	}
}

data class SocialLinkItem(val platform: SocialPlatform, val url: String)