package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.audio.AudioOutput
import com.pmk.freeplayer.domain.model.audio.PlaybackSource

data class EstadisticasEscaneo(
	val totalEscaneos: Int,
	val totalCancionesEscaneadas: Int,
	val totalCancionesLimpiadas: Int,
	val totalCancionesEnriquecidas: Int,
	val totalLetrasObtenidas: Int,
	val tiempoTotalProcesamiento: TrackDuration,
)

data class EstadisticasBiblioteca(
	val totalCanciones: Int,
	val cancionesCrudas: Int,
	val cancionesLimpias: Int,
	val cancionesEnriquecidas: Int,
	val letrasEncontradas: Int,
	val letrasSinBuscar: Int,
	val ultimoEscaneo: Long?,
	val duracionTotalMs: TrackDuration,
	val tamanioTotalBytes: FileSize,
)

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