package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.state.MediaProcessingState

// Logs internos para depuración en UI
data class LogApp(
	val id: Long = 0,
	val timestamp: Long,
	val nivel: NivelLog,
	val fase: MediaProcessingState,
	val mensaje: String,
	val cancionId: Long?,
	val detalles: Map<String, String>?,
	val stackTrace: String?,
)

enum class NivelLog {
	DEBUG,
	INFO,
	WARNING,
	ERROR,
}
