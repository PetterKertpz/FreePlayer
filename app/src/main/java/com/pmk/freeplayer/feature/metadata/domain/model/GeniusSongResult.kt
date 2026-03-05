package com.pmk.freeplayer.feature.metadata.domain.model

data class GeniusSongResult(
	// ── Identifiers ───────────────────────────────────────────────
	val geniusId: Int,
	val geniusUrl: String,
	val apiPath: String,
	
	// ── Song core ─────────────────────────────────────────────────
	val title: String,
	val fullTitle: String,              // "Star Shopping by Lil Peep"
	val titleWithFeatured: String?,
	val language: String?,
	val releaseDate: String?,           // "2015-08-17"
	val recordingLocation: String?,
	val lyricsState: String?,           // "complete" | "incomplete"
	
	// ── Images ────────────────────────────────────────────────────
	val songArtImageUrl: String?,
	val songArtThumbnailUrl: String?,
	val headerImageUrl: String?,
	
	// ── Colors (from song art) ────────────────────────────────────
	val primaryColor: String?,
	val secondaryColor: String?,
	val textColor: String?,
	
	// ── External IDs ─────────────────────────────────────────────
	val appleMusicId: String?,
	
	// ── Artist ────────────────────────────────────────────────────
	val primaryArtistId: Int?,
	val primaryArtistName: String?,
	val primaryArtistImageUrl: String?,
	val primaryArtistBio: String?,      // scraped/extracted from description
	
	// ── Credits ──────────────────────────────────────────────────
	val featuredArtists: List<String>,
	val producerArtists: List<String>,
	val writerArtists: List<String>,
	
	// ── Album ─────────────────────────────────────────────────────
	val albumId: Int?,
	val albumTitle: String?,
	val albumCoverUrl: String?,
	val albumReleaseDate: String?,
	
	// ── Match quality ─────────────────────────────────────────────
	val confidenceScore: Float,         // Levenshtein similarity vs cleanTitle+artist
)