package com.pmk.freeplayer.domain.model

data class ResultadoLimpieza(
	val id: Long = 0,
	val fecha: Long,
	val cancionesProcesadas: Int,
	val cancionesLimpiadas: Int,
	val errores: Int,
	val tiempoMs: Long
)