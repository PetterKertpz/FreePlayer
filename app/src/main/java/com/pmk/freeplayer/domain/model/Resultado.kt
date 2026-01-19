package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.audio.Genero

data class DatosLimpieza(
	val titulo: String,
	val artista: String,
	val album: String,
	val albumArtista: String? = null,
	val genero: Genero? = null,
	val anio: Int? = null,
	val numeroPista: Int? = null
)
data class EstadisticasEscaneo(
	val totalEscaneos: Int,
	val totalCancionesEscaneadas: Int,
	val totalCancionesLimpiadas: Int,
	val totalCancionesEnriquecidas: Int,
	val totalLetrasObtenidas: Int,
	val tiempoTotalProcesamiento: Duracion,
)
data class ResultadoEscaneo(
	val id: Long = 0,
	val fecha: Long,
	val archivosDetectados: Int,
	val cancionesNuevas: Int,
	val cancionesActualizadas: Int,
	val duplicadosIgnorados: Int,
	val archivosEliminados: Int,
	val errores: Int,
	val tiempoMs: Duracion
) {
	val exitoso: Boolean get() = errores == 0
}

data class ResultadoLimpieza(
	val id: Long = 0,
	val fecha: Long,
	val cancionesProcesadas: Int,
	val cancionesLimpiadas: Int,
	val errores: Int,
	val tiempoMs: Duracion
)

data class ResultadoEnriquecimiento(
	val id: Long = 0,
	val cancionId: Long,
	val fecha: Long,
	val exitoso: Boolean,
	val datosActualizados: Boolean,
	val letraEncontrada: Boolean,
	val nivelCoincidencia: Float?,
	val error: String?
)

data class ResultadoComparacion(
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
