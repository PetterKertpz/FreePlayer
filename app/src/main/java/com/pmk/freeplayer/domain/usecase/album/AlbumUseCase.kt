package com.pmk.freeplayer.domain.usecase.album

import com.pmk.freeplayer.domain.model.Album
import com.pmk.freeplayer.domain.model.enums.TipoOrdenamiento
import com.pmk.freeplayer.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// CONSULTAS
// ════════════════════════════════════════════════════════════

class ObtenerTodosAlbumesUseCase @Inject constructor(
	private val repository: AlbumRepository
) {
	operator fun invoke(): Flow<List<Album>> = repository.obtenerTodos()
}

class ObtenerAlbumPorIdUseCase @Inject constructor(
	private val repository: AlbumRepository
) {
	operator fun invoke(id: Long): Flow<Album?> = repository.obtenerPorId(id)
}

class ObtenerAlbumesPorArtistaUseCase @Inject constructor(
	private val repository: AlbumRepository
) {
	operator fun invoke(artista: String): Flow<List<Album>> =
		repository.obtenerPorArtista(artista)
}

class ObtenerAlbumesOrdenadosUseCase @Inject constructor(
	private val repository: AlbumRepository
) {
	operator fun invoke(
		ordenamiento: TipoOrdenamiento = TipoOrdenamiento.TITULO_ASC
	): Flow<List<Album>> = repository.obtenerOrdenados(ordenamiento)
}

class BuscarAlbumesUseCase @Inject constructor(
	private val repository: AlbumRepository
) {
	operator fun invoke(consulta: String): Flow<List<Album>> {
		if (consulta.isBlank()) return emptyFlow()
		return repository.buscar(consulta.trim())
	}
}

// ════════════════════════════════════════════════════════════
// ESTADÍSTICAS
// ════════════════════════════════════════════════════════════

class ObtenerCantidadAlbumesUseCase @Inject constructor(
	private val repository: AlbumRepository
) {
	suspend operator fun invoke(): Result<Int> {
		return try {
			Result.success(repository.obtenerCantidadTotal())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}