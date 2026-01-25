package com.pmk.freeplayer.domain.useCase

import com.pmk.freeplayer.domain.model.Album
import com.pmk.freeplayer.domain.model.config.CriterioOrdenamiento
import com.pmk.freeplayer.domain.model.config.DireccionOrdenamiento
import com.pmk.freeplayer.domain.model.config.Ordenamiento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// CONSULTAS
// ════════════════════════════════════════════════════════════

class ObtenerTodosAlbumesUseCase @Inject constructor(private val repository: BibliotecaRepository) {
   operator fun invoke(): Flow<List<Album>> = repository.obtenerTodosLosAlbumes()
}

class ObtenerAlbumPorIdUseCase @Inject constructor(private val repository: BibliotecaRepository) {
   operator fun invoke(id: Long): Flow<Album?> = repository.obtenerAlbumPorId(id)
}

class ObtenerAlbumesPorArtistaUseCase
@Inject
constructor(private val repository: BibliotecaRepository) {
   operator fun invoke(artista: String): Flow<List<Album>> =
      repository.obtenerAlbumesPorArtista(artista)
}

class ObtenerAlbumesOrdenadosUseCase
@Inject
constructor(private val repository: BibliotecaRepository) {
   operator fun invoke(
      ordenamiento: Ordenamiento = Ordenamiento(CriterioOrdenamiento.ALBUM, DireccionOrdenamiento.ASCENDENTE)
   ): Flow<List<Album>> = repository.obtenerAlbumesOrdenados(ordenamiento)
}

class BuscarAlbumesUseCase @Inject constructor(private val repository: BibliotecaRepository) {
   operator fun invoke(consulta: String): Flow<List<Album>> {
      if (consulta.isBlank()) return emptyFlow()
      return repository.buscarAlbumes(consulta.trim())
   }
}

// ════════════════════════════════════════════════════════════
// ESTADÍSTICAS
// ════════════════════════════════════════════════════════════

class ObtenerCantidadAlbumesUseCase
@Inject
constructor(private val repository: BibliotecaRepository) {
   suspend operator fun invoke(): Result<Int> {
      return try {
         Result.success(repository.obtenerCantidadTotalAlbumes())
      } catch (e: Exception) {
         Result.failure(e)
      }
   }
}
