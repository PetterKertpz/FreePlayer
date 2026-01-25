package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.enums.LogLevel

// Logs internos para depuración en UI
data class LogApp(
	val id: Long = 0,
	val timestamp: Long,
	val nivel: LogLevel,
	val fase: MediaProcessingState,
	val mensaje: String,
	val cancionId: Long?,
	val detalles: Map<String, String>?,
	val stackTrace: String?,
)

