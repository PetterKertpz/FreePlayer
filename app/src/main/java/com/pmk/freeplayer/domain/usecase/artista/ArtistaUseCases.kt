package com.pmk.freeplayer.domain.usecase.artista

import com.pmk.freeplayer.domain.model.Artista
import com.pmk.freeplayer.domain.model.enums.TipoOrdenamiento
import com.pmk.freeplayer.domain.repository.ArtistaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// CONSULTAS
// ════════════════════════════════════════════════════════════

class ObtenerTodosArtistasUseCase @Inject constructor(
	private val repository: ArtistaRepository
) {
	operator fun invoke(): Flow<List<Artista>> = repository.obtenerTodos()
}

class ObtenerArtistaPorIdUseCase @Inject constructor(
	private val repository: ArtistaRepository
) {
	operator fun invoke(id: Long): Flow<Artista?> = repository.obtenerPorId(id)
}

class ObtenerArtistasOrdenadosUseCase @Inject constructor(
	private val repository: ArtistaRepository
) {
	operator fun invoke(
		ordenamiento: TipoOrdenamiento = TipoOrdenamiento.TITULO_ASC
	): Flow<List<Artista>> = repository.obtenerOrdenados(ordenamiento)
}

class BuscarArtistasUseCase @Inject constructor(
	private val repository: ArtistaRepository
) {
	operator fun invoke(consulta: String): Flow<List<Artista>> {
		if (consulta.isBlank()) return emptyFlow()
		return repository.buscar(consulta.trim())
	}
}

// ════════════════════════════════════════════════════════════
// ESTADÍSTICAS
// ════════════════════════════════════════════════════════════

class ObtenerCantidadArtistasUseCase @Inject constructor(
	private val repository: ArtistaRepository
) {
	suspend operator fun invoke(): Result<Int> {
		return try {
			Result.success(repository.obtenerCantidadTotal())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}