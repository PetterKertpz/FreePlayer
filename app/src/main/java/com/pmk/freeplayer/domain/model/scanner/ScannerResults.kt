package com.pmk.freeplayer.domain.model.scanner

import com.pmk.freeplayer.domain.model.TrackDuration

// ═══════════════════════════════════════════════════════════════
// EXISTING MODELS (keep as-is)
// ═══════════════════════════════════════════════════════════════

data class ScanResult(
	val id: Long = 0,
	val fecha: Long,
	val archivosDetectados: Int,
	val cancionesNuevas: Int,
	val cancionesActualizadas: Int,
	val duplicadosIgnorados: Int,
	val archivosEliminados: Int,
	val errores: Int,
	val tiempoMs: TrackDuration
) {
	val exitoso: Boolean get() = errores == 0
	
	// Helpers for UI
	val totalProcesadas: Int get() = cancionesNuevas + cancionesActualizadas
	val tieneNuevas: Boolean get() = cancionesNuevas > 0
}

data class CleaningResult(
	val id: Long = 0,
	val fecha: Long,
	val cancionesProcesadas: Int,
	val cancionesLimpiadas: Int,
	val errores: Int,
	val tiempoMs: TrackDuration
)

data class EnrichmentResult(
	val id: Long = 0,
	val cancionId: Long,
	val fecha: Long,
	val exitoso: Boolean,
	val datosActualizados: Boolean,
	val letraEncontrada: Boolean,
	val nivelCoincidencia: Float?,
	val error: String?
)

data class ComparisonResult(
	val puntuacionTitulo: Float,
	val puntuacionArtista: Float,
	val puntuacionAlbum: Float,
	val puntuacionTotal: Float,
	val esConfiable: Boolean
) {
	companion object {
		const val UMBRAL_CONFIABLE = 0.85f
	}
}

// ═══════════════════════════════════════════════════════════════
// NEW: SCAN PROGRESS (for real-time UI updates)
// ═══════════════════════════════════════════════════════════════

/**
 * Phases of the scan operation
 */
enum class ScanPhase {
	IDLE,       // No scan running
	SCANNING,   // Reading files from device
	ANALYZING,  // Comparing with database
	INSERTING,  // Adding new songs
	UPDATING,   // Updating existing songs
	CLEANING,   // Removing orphaned entries
	FINALIZING, // Saving results
	COMPLETED,  // Scan finished successfully
	ERROR       // Scan failed
}

/**
 * Real-time progress information for UI
 */
data class ScanProgress(
	val phase: ScanPhase,
	val progress: Float, // 0.0 - 1.0
	val message: String
) {
	/** Is scan currently active? */
	val isActive: Boolean
		get() = phase !in listOf(ScanPhase.IDLE, ScanPhase.COMPLETED, ScanPhase.ERROR)
	
	/** Did scan complete successfully? */
	val isCompleted: Boolean
		get() = phase == ScanPhase.COMPLETED
	
	/** Did scan fail? */
	val isError: Boolean
		get() = phase == ScanPhase.ERROR
	
	/** Progress as percentage (0-100) */
	val progressPercent: Int
		get() = (progress * 100).toInt()
	
	/** Localized phase name for UI */
	val phaseName: String
		get() = when (phase) {
			ScanPhase.IDLE -> "Listo"
			ScanPhase.SCANNING -> "Escaneando"
			ScanPhase.ANALYZING -> "Analizando"
			ScanPhase.INSERTING -> "Agregando"
			ScanPhase.UPDATING -> "Actualizando"
			ScanPhase.CLEANING -> "Limpiando"
			ScanPhase.FINALIZING -> "Finalizando"
			ScanPhase.COMPLETED -> "Completado"
			ScanPhase.ERROR -> "Error"
		}
	
	companion object {
		fun idle() = ScanProgress(
			phase = ScanPhase.IDLE,
			progress = 0f,
			message = ""
		)
		
		fun completed(message: String = "Escaneo completado") = ScanProgress(
			phase = ScanPhase.COMPLETED,
			progress = 1f,
			message = message
		)
		
		fun error(message: String) = ScanProgress(
			phase = ScanPhase.ERROR,
			progress = 0f,
			message = message
		)
	}
}