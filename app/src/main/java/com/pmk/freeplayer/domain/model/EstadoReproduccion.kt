package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.enums.ModoRepeticion

data class EstadoReproduccion(
    val cancionActual: Cancion?,
    val estaReproduciendo: Boolean,
    val posicionActual: Long,               // milisegundos
    val duracionTotal: Long,
    val modoRepeticion: ModoRepeticion,
    val aleatorioActivado: Boolean,
    val velocidad: Float
) {
    val progresoPorcentaje: Float
        get() = if (duracionTotal > 0) {
            (posicionActual.toFloat() / duracionTotal) * 100
        } else 0f

    val posicionFormateada: String
        get() {
            val minutos = (posicionActual / 1000) / 60
            val segundos = (posicionActual / 1000) % 60
            return "$minutos:${segundos.toString().padStart(2, '0')}"
        }

    companion object {
        val VACIO = EstadoReproduccion(
            cancionActual = null,
            estaReproduciendo = false,
            posicionActual = 0,
            duracionTotal = 0,
            modoRepeticion = ModoRepeticion.DESACTIVADO,
            aleatorioActivado = false,
            velocidad = 1.0f
        )
    }
}