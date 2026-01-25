package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.EnrichmentResult
import kotlinx.coroutines.flow.Flow

interface EnrichmentRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Enrichment results
	// ─────────────────────────────────────────────────────────────
	suspend fun saveEnrichmentResult(result: EnrichmentResult): Long
	
	fun getEnrichmentHistory(limit: Int = 20): Flow<List<EnrichmentResult>>
	
	suspend fun getLastEnrichment(): EnrichmentResult?
	
	// ─────────────────────────────────────────────────────────────
	// Accumulated statistics
	// ─────────────────────────────────────────────────────────────
	suspend fun getTotalEnrichedSongs(): Int
	
	suspend fun getTotalLyricsObtained(): Int
	
	// ─────────────────────────────────────────────────────────────
	// Cleanup
	// ─────────────────────────────────────────────────────────────
	suspend fun clearEnrichmentHistory(keepLast: Int = 10)
}