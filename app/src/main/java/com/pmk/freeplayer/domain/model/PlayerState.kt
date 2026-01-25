package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.enums.RepeatMode


data class PlaybackState(
	val songActual: Song?,
	val estaReproduciendo: Boolean,
	val posicionActual: TrackDuration, // ms
	val duracionTotal: TrackDuration,
	val repeatMode: RepeatMode,
	val aleatorioActivado: Boolean,
	val velocidad: Float,
) {
   companion object {
      val VACIO =
         PlaybackState(
            null,
            false,
            TrackDuration.ZERO,
            TrackDuration.ZERO,
            RepeatMode.DESACTIVADO,
            false,
            1f,
         )
   }
}
data class SavedPlayerState(
	val cancionId: Long?,
	val posicion: TrackDuration,
	val cola: Queue,
	val repeatMode: RepeatMode,
	val aleatorioActivado: Boolean,
)