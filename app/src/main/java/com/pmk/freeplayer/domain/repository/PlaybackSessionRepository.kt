package com.pmk.freeplayer.domain.repository

import kotlinx.coroutines.flow.Flow

interface PlaybackSessionRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Session persistence (save state between sessions)
	// ─────────────────────────────────────────────────────────────
	suspend fun saveSessionState(
		songId: Long?,
		positionMs: Long,
		queueIds: List<Long>,
		currentIndex: Int
	)
	
	// Retrieve last session when opening the app
	fun getLastSongId(): Flow<Long?>
	
	fun getLastPosition(): Flow<Long>
	
	fun getLastIndex(): Flow<Int>
	
	fun getLastQueueIds(): Flow<List<Long>>
}