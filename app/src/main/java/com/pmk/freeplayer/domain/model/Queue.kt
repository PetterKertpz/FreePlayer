package com.pmk.freeplayer.domain.model

data class Queue(val canciones: List<Song>, val indiceActual: Int) {
	val songActual: Song?
		get() = canciones.getOrNull(indiceActual)
	
	val tieneSiguiente: Boolean
		get() = indiceActual < canciones.size - 1
	
	val tieneAnterior: Boolean
		get() = indiceActual > 0
	
	val estaVacia: Boolean
		get() = canciones.isEmpty()
	
	val cantidadTotal: Int
		get() = canciones.size
	
	val cancionesRestantes: Int
		get() = (canciones.size - indiceActual - 1).coerceAtLeast(0)
	
	companion object {
		val VACIA = Queue(emptyList(), 0)
	}
}