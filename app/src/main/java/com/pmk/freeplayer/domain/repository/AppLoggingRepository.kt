package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.LogApp
import com.pmk.freeplayer.domain.model.MediaProcessingState
import com.pmk.freeplayer.domain.model.enums.LogLevel
import kotlinx.coroutines.flow.Flow

interface AppLoggingRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Write logs
	// ─────────────────────────────────────────────────────────────
	suspend fun recordLog(
		level: LogLevel,
		phase: MediaProcessingState,
		message: String,
		songId: Long? = null,
		details: Map<String, String>? = null,
		exception: Throwable? = null,
	)
	
	suspend fun logDebug(phase: MediaProcessingState, message: String, songId: Long? = null)
	
	suspend fun logInfo(phase: MediaProcessingState, message: String, songId: Long? = null, details: Map<String, String>? = null)
	
	suspend fun logWarning(phase: MediaProcessingState, message: String, songId: Long? = null)
	
	suspend fun logError(
		phase: MediaProcessingState,
		message: String,
		songId: Long? = null,
		exception: Throwable? = null,
	)
	
	// ─────────────────────────────────────────────────────────────
	// Query logs
	// ─────────────────────────────────────────────────────────────
	fun getLogs(limit: Int = 100, minLevel: LogLevel = LogLevel.INFO): Flow<LogApp>
	
	fun getLogsByPhase(phase: MediaProcessingState, limit: Int = 50): Flow<List<LogApp>>
	
	fun getLogsBySong(songId: Long): Flow<List<LogApp>>
	
	fun getRecentErrors(limit: Int = 20): Flow<List<LogApp>>
	
	// ─────────────────────────────────────────────────────────────
	// Clear logs
	// ─────────────────────────────────────────────────────────────
	suspend fun clearOldLogs(ageDays: Int = 7)
	
	suspend fun clearAllLogs()
	
	suspend fun countLogs(): Int
}