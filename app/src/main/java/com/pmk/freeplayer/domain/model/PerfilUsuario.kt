package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.enums.Genero

data class PerfilUsuario(
    val id: String,
    val nombre: String,
    val avatarUri: String?,
    val creadoEn: Long,
    val tiempoTotalEscuchado: Long,         // milisegundos totales
    val cancionesReproducidas: Int,
    val generoFavorito: Genero?,
    val artistaFavorito: String?
) {
    // Tiempo formateado (ej: "45h 30min")
    val tiempoEscuchadoFormateado: String
        get() {
            val horas = (tiempoTotalEscuchado / 1000) / 3600
            val minutos = ((tiempoTotalEscuchado / 1000) % 3600) / 60
            return "${horas}h ${minutos}min"
        }
}