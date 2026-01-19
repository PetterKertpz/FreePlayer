package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.config.ModoRepeticion

data class ColaReproduccion(val canciones: List<Cancion>, val indiceActual: Int) {
   val cancionActual: Cancion?
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
   val cancionActual: Cancion?,
   val estaReproduciendo: Boolean,
   val posicionActual: Duracion, // ms
   val duracionTotal: Duracion,
   val modoRepeticion: ModoRepeticion,
   val aleatorioActivado: Boolean,
   val velocidad: Float,
) {
   companion object {
      val VACIO =
         EstadoReproduccion(
            null,
            false,
            Duracion.CERO,
            Duracion.CERO,
            ModoRepeticion.DESACTIVADO,
            false,
            1f,
         )
   }
}
