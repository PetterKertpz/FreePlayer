package com.pmk.freeplayer.domain.model

data class ResultadoEscaneo(
	val id: Long = 0,
	val fecha: Long,
	val archivosDetectados: Int,
	val cancionesNuevas: Int,
	val cancionesActualizadas: Int,
	val duplicadosIgnorados: Int,
	val archivosEliminados: Int,
	val errores: Int,
	val tiempoMs: Long
) {
	val exitoso: Boolean get() = errores == 0
	val tiempoFormateado: String get() = "${tiempoMs / 1000}.${tiempoMs % 1000}s"
}