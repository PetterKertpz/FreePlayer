package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.enums.Genero

data class Album(
    val id: Long,
    val nombre: String,
    val artista: String,
    val cantidadCanciones: Int,
    val duracionTotal: Long,                // milisegundos
    val anio: Int?,
    val portadaUri: String?,
    val fechaAgregado: Long,
    val genero: Genero?
) {
    val duracionFormateada: String
        get() {
            val horas = (duracionTotal / 1000) / 3600
            val minutos = ((duracionTotal / 1000) % 3600) / 60
            return if (horas > 0) "${horas}h ${minutos}min" else "$minutos min"
        }
}