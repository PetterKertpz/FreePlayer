package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Cancion
import com.pmk.freeplayer.domain.model.LetraCancion
import com.pmk.freeplayer.domain.model.enums.EstadoLetra
import kotlinx.coroutines.flow.Flow

interface LetraRepository {

  fun obtenerLetra(cancionId: Long): Flow<LetraCancion?>

  suspend fun buscarLetraEnLinea(titulo: String, artista: String): LetraCancion?

  suspend fun buscarArchivoLrcLocal(rutaCancion: String): LetraCancion?

  suspend fun guardarLetra(letra: LetraCancion)

  suspend fun eliminarLetra(cancionId: Long)

  suspend fun tieneLetra(cancionId: Long): Boolean

  suspend fun actualizarEstadoLetra(cancionId: Long, estado: EstadoLetra)

  suspend fun guardarLetraConEstado(cancionId: Long, letra: String, geniusUrl: String?)

  suspend fun marcarLetraNoEncontrada(cancionId: Long)

  fun obtenerCancionesSinLetraBuscada(limite: Int = 50): Flow<List<Cancion>>

  suspend fun contarPorEstadoLetra(estado: EstadoLetra): Int
}
