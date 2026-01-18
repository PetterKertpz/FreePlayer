package com.pmk.freeplayer.domain.usecase.letra

import com.pmk.freeplayer.domain.model.Cancion
import com.pmk.freeplayer.domain.model.LetraCancion
import com.pmk.freeplayer.domain.model.ResultadoBusquedaLetra
import com.pmk.freeplayer.domain.model.enums.EstadoLetra
import com.pmk.freeplayer.domain.repository.LetraRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// CONSULTAS
// ════════════════════════════════════════════════════════════

class ObtenerLetraUseCase @Inject constructor(
	private val repository: LetraRepository
) {
	operator fun invoke(cancionId: Long): Flow<LetraCancion?> =
		repository.obtenerLetra(cancionId)
}

class VerificarTieneLetraUseCase @Inject constructor(
	private val repository: LetraRepository
) {
	suspend operator fun invoke(cancionId: Long): Result<Boolean> {
		return try {
			Result.success(repository.tieneLetra(cancionId))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class ObtenerCancionesSinLetraUseCase @Inject constructor(
	private val repository: LetraRepository
) {
	operator fun invoke(limite: Int = 50): Flow<List<Cancion>> =
		repository.obtenerCancionesSinLetraBuscada(limite)
}

class ContarPorEstadoLetraUseCase @Inject constructor(
	private val repository: LetraRepository
) {
	suspend operator fun invoke(estado: EstadoLetra): Result<Int> {
		return try {
			Result.success(repository.contarPorEstadoLetra(estado))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// BÚSQUEDA DE LETRAS
// ════════════════════════════════════════════════════════════

class BuscarLetraEnLineaUseCase @Inject constructor(
	private val repository: LetraRepository
) {
	suspend operator fun invoke(titulo: String, artista: String): Result<LetraCancion?> {
		return try {
			require(titulo.isNotBlank()) { "El título no puede estar vacío" }
			require(artista.isNotBlank()) { "El artista no puede estar vacío" }
			val letra = repository.buscarLetraEnLinea(titulo, artista)
			Result.success(letra)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class BuscarArchivoLrcLocalUseCase @Inject constructor(
	private val repository: LetraRepository
) {
	suspend operator fun invoke(rutaCancion: String): Result<LetraCancion?> {
		return try {
			require(rutaCancion.isNotBlank()) { "La ruta no puede estar vacía" }
			val letra = repository.buscarArchivoLrcLocal(rutaCancion)
			Result.success(letra)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// GESTIÓN DE LETRAS
// ════════════════════════════════════════════════════════════

class GuardarLetraUseCase @Inject constructor(
	private val repository: LetraRepository
) {
	suspend operator fun invoke(letra: LetraCancion): Result<Unit> {
		return try {
			repository.guardarLetra(letra)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class EliminarLetraUseCase @Inject constructor(
	private val repository: LetraRepository
) {
	suspend operator fun invoke(cancionId: Long): Result<Unit> {
		return try {
			repository.eliminarLetra(cancionId)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class ActualizarEstadoLetraUseCase @Inject constructor(
	private val repository: LetraRepository
) {
	suspend operator fun invoke(cancionId: Long, estado: EstadoLetra): Result<Unit> {
		return try {
			repository.actualizarEstadoLetra(cancionId, estado)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class GuardarLetraConEstadoUseCase @Inject constructor(
	private val repository: LetraRepository
) {
	suspend operator fun invoke(
		cancionId: Long,
		letra: String,
		geniusUrl: String? = null
	): Result<Unit> {
		return try {
			require(letra.isNotBlank()) { "La letra no puede estar vacía" }
			repository.guardarLetraConEstado(cancionId, letra, geniusUrl)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class MarcarLetraNoEncontradaUseCase @Inject constructor(
	private val repository: LetraRepository
) {
	suspend operator fun invoke(cancionId: Long): Result<Unit> {
		return try {
			repository.marcarLetraNoEncontrada(cancionId)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// CASO DE USO COMPUESTO: Búsqueda completa de letra
// ════════════════════════════════════════════════════════════

class ObtenerLetraCompletaUseCase @Inject constructor(
	private val repository: LetraRepository
) {
	suspend operator fun invoke(cancion: Cancion): ResultadoBusquedaLetra {
		return try {
			// 1. Buscar archivo .lrc local primero (sincronizada)
			val letraLocal = repository.buscarArchivoLrcLocal(cancion.ruta)
			if (letraLocal != null && letraLocal.tieneLetra) {
				repository.guardarLetra(letraLocal)
				return ResultadoBusquedaLetra.Encontrada(letraLocal)
			}
			
			// 2. Buscar en línea
			val letraOnline = repository.buscarLetraEnLinea(cancion.titulo, cancion.artista)
			if (letraOnline != null && letraOnline.tieneLetra) {
				repository.guardarLetra(letraOnline)
				return ResultadoBusquedaLetra.Encontrada(letraOnline)
			}
			
			// 3. No encontrada
			repository.marcarLetraNoEncontrada(cancion.id)
			ResultadoBusquedaLetra.NoEncontrada
			
		} catch (e: Exception) {
			ResultadoBusquedaLetra.Error(
				mensaje = e.message ?: "Error al buscar letra",
				causa = e
			)
		}
	}
}