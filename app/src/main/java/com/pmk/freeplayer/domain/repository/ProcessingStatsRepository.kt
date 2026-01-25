package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.TrackDuration

interface ProcessingStatsRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Processing time statistics
	// ─────────────────────────────────────────────────────────────
	suspend fun getTotalProcessingTime(): TrackDuration
	
	suspend fun getScanProcessingTime(): TrackDuration
	
	suspend fun getCleaningProcessingTime(): TrackDuration
	
	suspend fun getEnrichmentProcessingTime(): TrackDuration
}