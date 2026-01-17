package com.pmk.freeplayer.domain.model

data class HistorialReproduccion(
    val id: Long,
    val cancion: Cancion,
    val fechaReproduccion: Long,            // timestamp
    val duracionEscuchada: Long,            // cuánto tiempo se escuchó
    val completada: Boolean                 // si se escuchó completa
)