package com.pmk.freeplayer.domain.useCase

import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.Genius
import com.pmk.freeplayer.domain.pipeline.ResultadoComparacion
import com.pmk.freeplayer.domain.repository.GeniusRepository
import com.pmk.freeplayer.domain.strategy.SongMatchingStrategy
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// BÚSQUEDA EN API
// ════════════════════════════════════════════════════════════

class BuscarCancionEnGeniusUseCase @Inject constructor(
	private val repository: GeniusRepository
) {
	suspend operator fun invoke(
		titulo: String,
		artista: String
	): Result<Genius?> {
		return try {
			require(titulo.isNotBlank()) { "El título no puede estar vacío" }
			require(artista.isNotBlank()) { "El artista no puede estar vacío" }
			val resultado = repository.buscarCancionEnGenius(titulo, artista)
			Result.success(resultado)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class ObtenerDetallesGeniusUseCase @Inject constructor(
	private val repository: GeniusRepository
) {
	suspend operator fun invoke(geniusId: Long): Result<Genius?> {
		return try {
			require(geniusId > 0) { "geniusId debe ser positivo" }
			val detalles = repository.obtenerDetallesCancionGenius(geniusId)
			Result.success(detalles)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// SCRAPING DE LETRAS
// ════════════════════════════════════════════════════════════

class ObtenerLetraGeniusUseCase @Inject constructor(
	private val repository: GeniusRepository
) {
	suspend operator fun invoke(geniusUrl: String): Result<String?> {
		return try {
			require(geniusUrl.isNotBlank()) { "La URL no puede estar vacía" }
			require(geniusUrl.contains("genius.com")) { "URL inválida de Genius" }
			val letra = repository.obtenerLetraDesdeGenius(geniusUrl)
			Result.success(letra)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// COMPARACIÓN
// ════════════════════════════════════════════════════════════

class CalcularCoincidenciaUseCase @Inject constructor(
	private val comparador: SongMatchingStrategy
) {
	operator fun invoke(
		song: Song,
		resultado: Genius
	): ResultadoComparacion = comparador.calcularCoincidencia(song, resultado)
}

// ════════════════════════════════════════════════════════════
// CACHÉ DE BÚSQUEDAS FALLIDAS
// ════════════════════════════════════════════════════════════

class MarcarBusquedaFallidaUseCase @Inject constructor(
	private val repository: GeniusRepository
) {
	suspend operator fun invoke(cancionId: Long): Result<Unit> {
		return try {
			repository.marcarBusquedaGeniusFallida(cancionId)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class VerificarBusquedaFallidaUseCase @Inject constructor(
	private val repository: GeniusRepository
) {
	suspend operator fun invoke(cancionId: Long): Result<Boolean> {
		return try {
			Result.success(repository.fueBusquedaGeniusFallida(cancionId))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class LimpiarCacheFallidasUseCase @Inject constructor(
	private val repository: GeniusRepository
) {
	suspend operator fun invoke(antiguedadDias: Int = 30): Result<Unit> {
		return try {
			require(antiguedadDias > 0) { "antiguedadDias debe ser > 0" }
			repository.limpiarCacheBusquedasFallidas(antiguedadDias)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// CASO DE USO COMPUESTO: Enriquecimiento completo
// ════════════════════════════════════════════════════════════

sealed class ResultadoEnriquecerCancion {
	data class Exitoso(
		val genius: Genius,
		val letra: String?,
		val coincidencia: ResultadoComparacion
	) : ResultadoEnriquecerCancion()
	
	data object SinCoincidencia : ResultadoEnriquecerCancion()
	data object BusquedaFallidaPrevia : ResultadoEnriquecerCancion()
	data class Error(val mensaje: String, val causa: Throwable? = null) : ResultadoEnriquecerCancion()
}

class EnriquecerCancionUseCase @Inject constructor(
	private val repository: GeniusRepository,
	private val comparador: SongMatchingStrategy
) {
	suspend operator fun invoke(
		song: Song,
		umbralCoincidencia: Float = 0.85f
	): ResultadoEnriquecerCancion {  // Tipo actualizado
		return try {
			if (repository.fueBusquedaGeniusFallida(song.id)) {
				return ResultadoEnriquecerCancion.BusquedaFallidaPrevia
			}
			
			val busqueda = repository.buscarCancionEnGenius(song.titulo, song.artista)
				?: run {
					repository.marcarBusquedaGeniusFallida(song.id)
					return ResultadoEnriquecerCancion.SinCoincidencia
				}
			
			// Usar el servicio inyectado
			val coincidencia = comparador.calcularCoincidencia(song, busqueda)
			
			if (!coincidencia.esConfiable ||
				coincidencia.puntuacionTotal < umbralCoincidencia) {
				repository.marcarBusquedaGeniusFallida(song.id)
				return ResultadoEnriquecerCancion.SinCoincidencia
			}
			
			val detalles = repository.obtenerDetallesCancionGenius(busqueda.geniusId)
				?: return ResultadoEnriquecerCancion.Error("No se pudieron obtener detalles")
			
			val letra = try {
				repository.obtenerLetraDesdeGenius(busqueda.url)
			} catch (e: Exception) {
				null
			}
			
			ResultadoEnriquecerCancion.Exitoso(
				genius = detalles,
				letra = letra,
				coincidencia = coincidencia
			)
		} catch (e: Exception) {
			ResultadoEnriquecerCancion.Error(
				mensaje = e.message ?: "Error desconocido",
				causa = e
			)
		}
	}
}