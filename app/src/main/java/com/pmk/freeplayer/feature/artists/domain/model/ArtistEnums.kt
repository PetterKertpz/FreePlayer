package com.pmk.freeplayer.feature.artists.domain.model

enum class SocialPlatform {
	SPOTIFY, APPLE_MUSIC, YOUTUBE_MUSIC, SOUNDCLOUD, BANDCAMP,
	INSTAGRAM, TWITTER, TIKTOK,
	GENIUS, DISCOGS, MUSICBRAINZ,
	WEBSITE, UNKNOWN;
	
	companion object {
		fun from(value: String): SocialPlatform =
			entries.find { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
	}
}

enum class ArtistType {
	SOLO, BAND, DUO, ORCHESTRA, CHOIR, DJ, COLLECTIVE, UNKNOWN;
	
	companion object {
		fun from(value: String?): ArtistType =
			if (value.isNullOrBlank()) UNKNOWN
			else entries.find { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
	}
}