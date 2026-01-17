package com.pmk.freeplayer.domain.model

data class EstadisticasBiblioteca(
	val totalCanciones: Int,
	val cancionesCrudas: Int,
	val cancionesLimpias: Int,
	val cancionesEnriquecidas: Int,
	val letrasEncontradas: Int,
	val letrasSinBuscar: Int,
	val ultimoEscaneo: Long?,
	val duracionTotalMs: Long,
	val tamanioTotalBytes: Long
)