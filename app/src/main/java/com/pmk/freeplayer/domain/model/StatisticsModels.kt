package com.pmk.freeplayer.domain.model

data class ScanStats(
	val totalEscaneos: Int,
	val totalCancionesEscaneadas: Int,
	val totalCancionesLimpiadas: Int,
	val totalCancionesEnriquecidas: Int,
	val totalLetrasObtenidas: Int,
	val tiempoTotalProcesamiento: TrackDuration,
)

data class LibraryStats(
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



