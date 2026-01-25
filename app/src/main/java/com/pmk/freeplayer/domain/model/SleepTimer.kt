package com.pmk.freeplayer.domain.model

data class SleepTimer(
   val estaActivo: Boolean,
   val minutosRestantes: Int,
   val finalizarAlTerminarCancion: Boolean,
) {
   companion object {
      val DESACTIVADO = SleepTimer(false, 0, false)
   }
}
