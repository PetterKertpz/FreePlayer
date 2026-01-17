package com.pmk.freeplayer.domain.model

data class Artista(
    val id: Long,
    val nombre: String,
    val cantidadCanciones: Int,
    val cantidadAlbumes: Int,
    val duracionTotal: Long,
    val imagenUri: String?
) {
    val duracionFormateada: String
        get() {
            val horas = (duracionTotal / 1000) / 3600
            val minutos = ((duracionTotal / 1000) % 3600) / 60
            return if (horas > 0) "${horas}h ${minutos}min" else "$minutos min"
        }
}