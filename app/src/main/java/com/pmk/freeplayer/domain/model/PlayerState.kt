package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.config.ModoRepeticion

data class ColaReproduccion(val canciones: List<Song>, val indiceActual: Int) {
   val songActual: Song?
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
      val VACIA = ColaReproduccion(emptyList(), 0)
   }
}

data class EstadoReproduccion(
	val songActual: Song?,
	val estaReproduciendo: Boolean,
	val posicionActual: TrackDuration, // ms
	val duracionTotal: TrackDuration,
	val modoRepeticion: ModoRepeticion,
	val aleatorioActivado: Boolean,
	val velocidad: Float,
) {
   companion object {
      val VACIO =
         EstadoReproduccion(
            null,
            false,
            TrackDuration.ZERO,
            TrackDuration.ZERO,
            ModoRepeticion.DESACTIVADO,
            false,
            1f,
         )
   }
}
data class EstadoReproductorGuardado(
	val cancionId: Long?,
	val posicion: TrackDuration,
	val cola: ColaReproduccion,
	val modoRepeticion: ModoRepeticion,
	val aleatorioActivado: Boolean,
)