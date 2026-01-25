package com.pmk.freeplayer.domain.useCase

import com.pmk.freeplayer.domain.model.Artist
import com.pmk.freeplayer.domain.model.config.CriterioOrdenamiento
import com.pmk.freeplayer.domain.model.config.DireccionOrdenamiento
import com.pmk.freeplayer.domain.model.config.Ordenamiento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// CONSULTAS
// ════════════════════════════════════════════════════════════

class ObtenerTodosArtistasUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(): Flow<List<Artist>> = repository.obtenerTodosLosArtistas()
}

class ObtenerArtistaPorIdUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(id: Long): Flow<Artist?> = repository.obtenerArtistaPorId(id)
}

class ObtenerArtistasOrdenadosUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(
		ordenamiento: Ordenamiento = Ordenamiento(CriterioOrdenamiento.ARTISTA, DireccionOrdenamiento.ASCENDENTE)
	): Flow<List<Artist>> = repository.obtenerArtistasOrdenados(ordenamiento)
}

class BuscarArtistasUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(consulta: String): Flow<List<Artist>> {
		if (consulta.isBlank()) return emptyFlow()
		return repository.buscarArtistas(consulta.trim())
	}
}

// ════════════════════════════════════════════════════════════
// ESTADÍSTICAS
// ════════════════════════════════════════════════════════════

class ObtenerCantidadArtistasUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(): Result<Int> {
		return try {
			Result.success(repository.obtenerCantidadTotalArtistas())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}