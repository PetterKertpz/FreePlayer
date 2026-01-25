package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.data.remote.dto.GeniusDto

interface GeniusApiRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Search on API
	// ─────────────────────────────────────────────────────────────
	suspend fun searchSongOnGenius(title: String, artist: String): GeniusDto?
	
	suspend fun getSongDetailsFromGenius(geniusId: Long): GeniusDto?
	
	// ─────────────────────────────────────────────────────────────
	// Scrape lyrics from Genius
	// ─────────────────────────────────────────────────────────────
	suspend fun getLyricsFromGenius(geniusUrl: String): String?
}