package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.enums.AudioOutput
import com.pmk.freeplayer.domain.model.enums.PlaybackSource

data class PlaybackHistory(
	val id: Long,
	val songId: Long,
	val timestamp: Long,
	
	// Tiempos (Usamos tus value classes si quieres, o Long simple para reportes)
	val playedDurationMs: TrackDuration,
	val totalDurationMs: Long,
	val isCompleted: Boolean,
	
	// Contexto
	val source: PlaybackSource,
	val contextName: String?,
	
	// Técnico
	val outputType: AudioOutput,
	val volume: Float?,
	
	// Analytics
	val pauseCount: Int,
	val seekCount: Int
)