package com.pmk.freeplayer.domain.model

data class ColaReproduccion(
    val canciones: List<Cancion>,
    val indiceActual: Int
) {
    val cancionActual: Cancion?
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
        val VACIA = ColaReproduccion(
            canciones = emptyList(),
            indiceActual = 0
        )
    }
}