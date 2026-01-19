package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.audio.Genero
import com.pmk.freeplayer.domain.model.config.ModoRepeticion

data class PerfilUsuario(
   val id: String,
   val nombre: String,
   val avatarUri: String?,
   val creadoEn: Long,
   val tiempoTotalEscuchado: Duracion, // milisegundos totales
   val cancionesReproducidas: Int,
   val generoFavorito: Genero?,
   val artistaFavorito: String?,
)

data class HistorialReproduccion(
   val id: Long,
   val cancion: Cancion,
   val fechaReproduccion: Long, // timestamp
   val duracionEscuchada: Duracion, // cuánto tiempo se escuchó
   val completada: Boolean, // si se escuchó completa
)

data class EstadoReproductorGuardado(
   val cancionId: Long?,
   val posicion: Duracion,
   val cola: ColaReproduccion,
   val modoRepeticion: ModoRepeticion,
   val aleatorioActivado: Boolean,
)

data class TemporizadorSuenio(
   val estaActivo: Boolean,
   val minutosRestantes: Int,
   val finalizarAlTerminarCancion: Boolean,
) {
   companion object {
      val DESACTIVADO = TemporizadorSuenio(false, 0, false)
   }
}
