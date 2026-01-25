package com.pmk.freeplayer.domain.model


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
