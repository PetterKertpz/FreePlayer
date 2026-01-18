package com.pmk.freeplayer.domain.usecase.genius

import com.pmk.freeplayer.domain.model.Cancion
import com.pmk.freeplayer.domain.model.Genius
import com.pmk.freeplayer.domain.model.ResultadoBusquedaGenius
import com.pmk.freeplayer.domain.model.ResultadoComparacion
import com.pmk.freeplayer.domain.repository.GeniusRepository
import com.pmk.freeplayer.domain.service.ComparadorCanciones
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
	): Result<ResultadoBusquedaGenius?> {
		return try {
			require(titulo.isNotBlank()) { "El título no puede estar vacío" }
			require(artista.isNotBlank()) { "El artista no puede estar vacío" }
			val resultado = repository.buscarCancion(titulo, artista)
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
			val detalles = repository.obtenerDetallesCancion(geniusId)
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
			val letra = repository.obtenerLetra(geniusUrl)
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
	private val comparador: ComparadorCanciones
) {
	operator fun invoke(
		cancion: Cancion,
		resultado: ResultadoBusquedaGenius
	): ResultadoComparacion = comparador.calcularCoincidencia(cancion, resultado)
}

// ════════════════════════════════════════════════════════════
// CACHÉ DE BÚSQUEDAS FALLIDAS
// ════════════════════════════════════════════════════════════

class MarcarBusquedaFallidaUseCase @Inject constructor(
	private val repository: GeniusRepository
) {
	suspend operator fun invoke(cancionId: Long): Result<Unit> {
		return try {
			repository.marcarBusquedaFallida(cancionId)
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
			Result.success(repository.fueBusquedaFallida(cancionId))
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
			repository.limpiarCacheFallidas(antiguedadDias)
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
	private val comparador: ComparadorCanciones
) {
	suspend operator fun invoke(
		cancion: Cancion,
		umbralCoincidencia: Float = 0.85f
	): ResultadoEnriquecerCancion {  // Tipo actualizado
		return try {
			if (repository.fueBusquedaFallida(cancion.id)) {
				return ResultadoEnriquecerCancion.BusquedaFallidaPrevia
			}
			
			val busqueda = repository.buscarCancion(cancion.titulo, cancion.artista)
				?: run {
					repository.marcarBusquedaFallida(cancion.id)
					return ResultadoEnriquecerCancion.SinCoincidencia
				}
			
			// Usar el servicio inyectado
			val coincidencia = comparador.calcularCoincidencia(cancion, busqueda)
			
			if (!coincidencia.esConfiable ||
				coincidencia.puntuacionTotal < umbralCoincidencia) {
				repository.marcarBusquedaFallida(cancion.id)
				return ResultadoEnriquecerCancion.SinCoincidencia
			}
			
			val detalles = repository.obtenerDetallesCancion(busqueda.geniusId)
				?: return ResultadoEnriquecerCancion.Error("No se pudieron obtener detalles")
			
			val letra = try {
				repository.obtenerLetra(busqueda.url)
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