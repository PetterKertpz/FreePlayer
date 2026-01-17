package com.pmk.freeplayer.domain.model

data class TemporizadorSuenio(
    val estaActivo: Boolean,
    val minutosRestantes: Int,
    val finalizarAlTerminarCancion: Boolean
) {
    val tiempoRestanteFormateado: String
        get() {
            val horas = minutosRestantes / 60
            val minutos = minutosRestantes % 60
            return if (horas > 0) {
                "${horas}h ${minutos}min"
            } else {
                "$minutos min"
            }
        }

    companion object {
        val DESACTIVADO = TemporizadorSuenio(
            estaActivo = false,
            minutosRestantes = 0,
            finalizarAlTerminarCancion = false
        )
    }
}