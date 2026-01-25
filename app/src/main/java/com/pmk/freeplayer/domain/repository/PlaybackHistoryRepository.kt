package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.PlaybackHistory
import kotlinx.coroutines.flow.Flow

interface PlaybackHistoryRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Get history
	// ─────────────────────────────────────────────────────────────
	fun getHistory(limit: Int = 100): Flow<List<PlaybackHistory>>
	
	fun getHistoryByDateRange(
		startDate: Long,
		endDate: Long,
	): Flow<List<PlaybackHistory>>
	
	// ─────────────────────────────────────────────────────────────
	// Record playback
	// ─────────────────────────────────────────────────────────────
	suspend fun recordPlayback(songId: Long, listenedDuration: Long, completed: Boolean)
	
	// ─────────────────────────────────────────────────────────────
	// Clear history
	// ─────────────────────────────────────────────────────────────
	suspend fun clearHistory()
	
	suspend fun deleteHistoryEntry(id: Long)
	
	// ─────────────────────────────────────────────────────────────
	// History statistics
	// ─────────────────────────────────────────────────────────────
	suspend fun getTotalListeningTime(): Long
	
	suspend fun getSongsPlayedToday(): Int
}