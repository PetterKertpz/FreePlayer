package com.pmk.freeplayer.domain.repository

interface GeniusCacheRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Cache of failed searches (avoid re-searching)
	// ─────────────────────────────────────────────────────────────
	suspend fun markGeniusSearchFailed(songId: Long)
	
	suspend fun wasGeniusSearchFailed(songId: Long): Boolean
	
	suspend fun clearFailedSearchCache(ageDays: Int = 30)
}