package com.pmk.freeplayer.domain.model

data class EstadisticasEscaneo(
    val totalEscaneos: Int,
    val totalCancionesEscaneadas: Int,
    val totalCancionesLimpiadas: Int,
    val totalCancionesEnriquecidas: Int,
    val totalLetrasObtenidas: Int,
    val tiempoTotalProcesamiento: Long,
) {
  val tiempoFormateado: String
    get() {
      val segundos = tiempoTotalProcesamiento / 1000
      val minutos = segundos / 60
      val horas = minutos / 60
      return when {
        horas > 0 -> "${horas}h ${minutos % 60}m"
        minutos > 0 -> "${minutos}m ${segundos % 60}s"
        else -> "${segundos}s"
      }
    }

  val porcentajeEnriquecidas: Float
    get() =
        if (totalCancionesLimpiadas > 0) {
          (totalCancionesEnriquecidas.toFloat() / totalCancionesLimpiadas) * 100
        } else 0f
}
