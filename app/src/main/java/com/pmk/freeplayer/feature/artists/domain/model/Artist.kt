package com.pmk.freeplayer.feature.artists.domain.model

import com.pmk.freeplayer.core.domain.model.enums.SocialPlatform
import com.pmk.freeplayer.feature.genres.domain.model.Genre

data class Artist(
	val id: Long,
	val name: String,
	
	// ═══════════════════════════════════════════════════════════════
	// IDENTITY
	// ═══════════════════════════════════════════════════════════════
	val realName: String?,
	val isVerified: Boolean,
	
	// Location
	val country: String?,
	val city: String?,
	
	// ═══════════════════════════════════════════════════════════════
	// MULTIMEDIA
	// ═══════════════════════════════════════════════════════════════
	val coverUri: String?,
	val headerUri: String?,
	
	// ═══════════════════════════════════════════════════════════════
	// BIOGRAPHY & DESCRIPTION
	// ═══════════════════════════════════════════════════════════════
	val biography: String?,
	val description: String?,
	
	// ═══════════════════════════════════════════════════════════════
	// TECHNICAL DATA
	// ═══════════════════════════════════════════════════════════════
	val type: ArtistType,
	val genre: Genre?,
	
	// ═══════════════════════════════════════════════════════════════
	// STATISTICS
	// ═══════════════════════════════════════════════════════════════
	val songCount: Int,
	val albumCount: Int,
	val playCount: Int,
	val isFavorite: Boolean,
	
	// ═══════════════════════════════════════════════════════════════
	// DATES
	// ═══════════════════════════════════════════════════════════════
	val careerStartYear: Int?,
	val birthDate: Long?,
	
	// ═══════════════════════════════════════════════════════════════
	// SOCIAL LINKS (Integrated - replaces SocialLink.kt)
	// ═══════════════════════════════════════════════════════════════
	val socialLinks: SocialLinks = SocialLinks()
) {
	
	// ═══════════════════════════════════════════════════════════════
	// COMPUTED PROPERTIES
	// ═══════════════════════════════════════════════════════════════
	
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

/**
 * Artist type - previously was String
 */
enum class ArtistType {
	SOLO,
	BAND,
	DUO,
	ORCHESTRA,
	CHOIR,
	DJ,
	COLLECTIVE,
	UNKNOWN;
	
	companion object {
		fun fromString(value: String?): ArtistType {
			if (value.isNullOrBlank()) return UNKNOWN
			return entries.find { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
		}
	}
}

/**
 * Embedded social links - All platforms in one place
 * Replaces the separate SocialLink.kt entity
 */
data class SocialLinks(
	// Primary platforms
	val websiteUrl: String? = null,
	val spotifyUrl: String? = null,
	val appleMusicUrl: String? = null,
	val youtubeMusicUrl: String? = null,
	
	// Social media
	val instagramUsername: String? = null,
	val twitterUsername: String? = null,
	val tiktokUsername: String? = null,
	val facebookUrl: String? = null,
	
	// Music platforms
	val soundcloudUrl: String? = null,
	val bandcampUrl: String? = null,
	
	// Metadata sources
	val geniusUrl: String? = null,
	val discogsUrl: String? = null,
	val musicBrainzId: String? = null
) {
	val hasAny: Boolean
		get() = listOf(
			websiteUrl, spotifyUrl, appleMusicUrl, youtubeMusicUrl,
			instagramUsername, twitterUsername, tiktokUsername, facebookUrl,
			soundcloudUrl, bandcampUrl, geniusUrl, discogsUrl, musicBrainzId
		).any { !it.isNullOrBlank() }
	
	// Helpers for UI - Generate full URLs from usernames
	val instagramUrl: String?
		get() = instagramUsername?.let { "https://instagram.com/$it" }
	
	val twitterUrl: String?
		get() = twitterUsername?.let { "https://x.com/$it" }
	
	val tiktokUrl: String?
		get() = tiktokUsername?.let { "https://tiktok.com/@$it" }
	
	/**
	 * Returns all available links as a list for easy iteration in UI
	 */
	fun toList(): List<SocialLinkItem> = buildList {
		websiteUrl?.let { add(SocialLinkItem(SocialPlatform.WEBSITE, it)) }
		spotifyUrl?.let { add(SocialLinkItem(SocialPlatform.SPOTIFY, it)) }
		appleMusicUrl?.let { add(SocialLinkItem(SocialPlatform.APPLE_MUSIC, it)) }
		youtubeMusicUrl?.let { add(SocialLinkItem(SocialPlatform.YOUTUBE_MUSIC, it)) }
		instagramUrl?.let { add(SocialLinkItem(SocialPlatform.INSTAGRAM, it)) }
		twitterUrl?.let { add(SocialLinkItem(SocialPlatform.TWITTER, it)) }
		tiktokUrl?.let { add(SocialLinkItem(SocialPlatform.TIKTOK, it)) }
		facebookUrl?.let { add(SocialLinkItem(SocialPlatform.FACEBOOK, it)) }
		soundcloudUrl?.let { add(SocialLinkItem(SocialPlatform.SOUNDCLOUD, it)) }
		bandcampUrl?.let { add(SocialLinkItem(SocialPlatform.BANDCAMP, it)) }
		geniusUrl?.let { add(SocialLinkItem(SocialPlatform.GENIUS, it)) }
	}
}

/**
 * Simple item for UI iteration
 */
data class SocialLinkItem(
	val platform: SocialPlatform,
	val url: String
)

/**
 * Supported social platforms
 */
