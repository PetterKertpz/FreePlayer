package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.PlaybackHistoryEntity
import com.pmk.freeplayer.domain.model.PlaybackHistory
import com.pmk.freeplayer.domain.model.TrackDuration
import com.pmk.freeplayer.domain.model.enums.AudioOutput
import com.pmk.freeplayer.domain.model.enums.PlaybackSource
import java.util.Calendar

/**
 * 🔄 PLAYBACK HISTORY MAPPER
 *
 * Convierte entre la capa de persistencia (Entity) y la capa de dominio (Model).
 * Maneja conversiones de tipos, enums, y cálculos necesarios para cada capa.
 */

// ==================== ENTITY -> DOMAIN ====================

/**
 * Convierte PlaybackHistoryEntity (DB) a PlaybackHistory (Domain).
 * Simplifica los datos para la capa de dominio/UI.
 */
fun PlaybackHistoryEntity.toDomain(): PlaybackHistory {
	return PlaybackHistory(
		id = this.historyId,
		songId = this.songId,
		timestamp = this.timestamp,
		
		// Value Classes
		playedDurationMs = TrackDuration(this.playedDurationMs),
		totalDurationMs = this.totalDurationMs,
		isCompleted = this.isCompleted,
		
		// Enums con mapeo seguro
		source = this.source.toPlaybackSource(),
		contextName = this.contextName,
		
		// Información técnica
		outputType = this.outputType.toAudioOutput(),
		volume = this.volume,
		
		// Métricas de comportamiento
		pauseCount = this.pauseCount,
		seekCount = this.seekCount
	)
}

/**
 * Convierte lista de entities a lista de domain models.
 */
fun List<PlaybackHistoryEntity>.toDomain(): List<PlaybackHistory> = map { it.toDomain() }

// ==================== DOMAIN -> ENTITY ====================

/**
 * Convierte PlaybackHistory (Domain) a PlaybackHistoryEntity (DB).
 * Calcula campos adicionales necesarios para la persistencia y analytics.
 *
 * @param userId ID del usuario (default 1 para apps offline)
 * @param contextId ID del contexto (álbum/playlist)
 * @param contextPosition Posición en el contexto
 * @param quality Calidad del audio (ej: "MP3_320", "FLAC")
 * @param playbackMode Modo de reproducción (ej: "SHUFFLE", "NORMAL", "REPEAT")
 * @param eqEnabled Si el ecualizador estaba activo
 * @param deviceId Identificador del dispositivo
 * @param likedDuringPlayback Si se dio like durante la reproducción
 * @param location Ubicación (opcional, para analytics)
 * @param syncId ID de sincronización (para sync multi-dispositivo)
 */
fun PlaybackHistory.toEntity(
	userId: Long = 1L,
	contextId: Long? = null,
	contextPosition: Int? = null,
	quality: String? = null,
	playbackMode: String = "NORMAL",
	eqEnabled: Boolean = false,
	deviceId: String? = null,
	likedDuringPlayback: Boolean = false,
	location: String? = null,
	syncId: String? = null
): PlaybackHistoryEntity {
	// Calcular metadatos temporales para analytics
	val calendar = Calendar.getInstance().apply {
		timeInMillis = this@toEntity.timestamp
	}
	
	// Calcular completion ratio
	val completionRatio = if (totalDurationMs > 0) {
		(playedDurationMs.millis.toFloat() / totalDurationMs).coerceIn(0f, 1f)
	} else {
		0f
	}
	
	return PlaybackHistoryEntity(
		historyId = this.id,
		userId = userId,
		songId = this.songId,
		
		// Tiempo
		timestamp = this.timestamp,
		playedDurationMs = this.playedDurationMs.millis,
		totalDurationMs = this.totalDurationMs,
		completionRatio = completionRatio,
		isCompleted = this.isCompleted,
		
		// Contexto
		source = this.source.name,
		contextId = contextId,
		contextName = this.contextName,
		contextPosition = contextPosition,
		
		// Técnico
		quality = quality,
		playbackMode = playbackMode,
		volume = this.volume,
		eqEnabled = eqEnabled,
		
		// Dispositivo
		deviceId = deviceId,
		outputType = this.outputType.name,
		
		// Comportamiento
		pauseCount = this.pauseCount,
		seekCount = this.seekCount,
		likedDuringPlayback = likedDuringPlayback,
		
		// Metadata analítica (pre-calculada para queries rápidas)
		hourOfDay = calendar.get(Calendar.HOUR_OF_DAY), // 0-23
		dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK), // 1-7
		location = location,
		
		// Sincronización
		syncId = syncId
	)
}

/**
 * Sobrecarga simplificada sin parámetros adicionales.
 * Usa valores por defecto para campos opcionales.
 */
fun PlaybackHistory.toEntity(): PlaybackHistoryEntity = toEntity(
	userId = 1L,
	contextId = null,
	contextPosition = null,
	quality = null,
	playbackMode = "NORMAL",
	eqEnabled = false,
	deviceId = null,
	likedDuringPlayback = false,
	location = null,
	syncId = null
)

// ==================== EXTENSION FUNCTIONS ====================

/**
 * Convierte String a PlaybackSource de forma segura.
 * Retorna LIBRARY como fallback si el string no es válido.
 */
private fun String.toPlaybackSource(): PlaybackSource {
	return try {
		PlaybackSource.valueOf(this.uppercase())
	} catch (e: IllegalArgumentException) {
		PlaybackSource.LIBRARY // Fallback seguro
	}
}

/**
 * Convierte String? a AudioOutput de forma segura.
 * Retorna UNKNOWN si el string es null o inválido.
 */
private fun String?.toAudioOutput(): AudioOutput {
	if (this == null) return AudioOutput.UNKNOWN
	return try {
		AudioOutput.valueOf(this.uppercase())
	} catch (e: IllegalArgumentException) {
		AudioOutput.UNKNOWN // Fallback seguro
	}
}

// ==================== HELPERS DE CREACIÓN ====================

/**
 * Crea un PlaybackHistoryEntity desde cero con valores calculados automáticamente.
 * Útil para registrar una nueva reproducción.
 */
fun createPlaybackHistoryEntity(
	songId: Long,
	playedDurationMs: Long,
	totalDurationMs: Long,
	source: PlaybackSource,
	outputType: AudioOutput,
	contextName: String? = null,
	contextId: Long? = null,
	contextPosition: Int? = null,
	volume: Float? = null,
	pauseCount: Int = 0,
	seekCount: Int = 0,
	playbackMode: String = "NORMAL",
	quality: String? = null,
	eqEnabled: Boolean = false,
	deviceId: String? = null,
	likedDuringPlayback: Boolean = false,
	location: String? = null
): PlaybackHistoryEntity {
	val now = System.currentTimeMillis()
	val calendar = Calendar.getInstance().apply { timeInMillis = now }
	
	val completionRatio = if (totalDurationMs > 0) {
		(playedDurationMs.toFloat() / totalDurationMs).coerceIn(0f, 1f)
	} else {
		0f
	}
	
	// Considera completado si se escuchó más del 80%
	val isCompleted = completionRatio >= 0.8f
	
	return PlaybackHistoryEntity(
		historyId = 0, // Autogenerado
		userId = 1L,
		songId = songId,
		timestamp = now,
		playedDurationMs = playedDurationMs,
		totalDurationMs = totalDurationMs,
		completionRatio = completionRatio,
		isCompleted = isCompleted,
		source = source.name,
		contextId = contextId,
		contextName = contextName,
		contextPosition = contextPosition,
		quality = quality,
		playbackMode = playbackMode,
		volume = volume,
		eqEnabled = eqEnabled,
		deviceId = deviceId,
		outputType = outputType.name,
		pauseCount = pauseCount,
		seekCount = seekCount,
		likedDuringPlayback = likedDuringPlayback,
		hourOfDay = calendar.get(Calendar.HOUR_OF_DAY),
		dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK),
		location = location,
		syncId = null
	)
}