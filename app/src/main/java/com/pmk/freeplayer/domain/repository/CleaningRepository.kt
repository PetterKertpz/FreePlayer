package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.CleaningResult
import kotlinx.coroutines.flow.Flow

interface CleaningRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Cleaning results
	// ─────────────────────────────────────────────────────────────
	suspend fun saveCleaningResult(result: CleaningResult): Long
	
	fun getCleaningHistory(limit: Int = 20): Flow<List<CleaningResult>>
	
	suspend fun getLastCleaning(): CleaningResult?
	
	// ─────────────────────────────────────────────────────────────
	// Accumulated statistics
	// ─────────────────────────────────────────────────────────────
	suspend fun getTotalCleanedSongs(): Int
	
	// ─────────────────────────────────────────────────────────────
	// Cleanup
	// ─────────────────────────────────────────────────────────────
	suspend fun clearCleaningHistory(keepLast: Int = 10)
}