package com.pmk.freeplayer.domain.usecase.historial

import com.pmk.freeplayer.domain.model.Duracion
import com.pmk.freeplayer.domain.model.HistorialReproduccion
import com.pmk.freeplayer.domain.repository.HistorialRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// CONSULTAS
// ════════════════════════════════════════════════════════════

class ObtenerHistorialUseCase @Inject constructor(
	private val repository: HistorialRepository
) {
	operator fun invoke(limite: Int = 100): Flow<List<HistorialReproduccion>> =
		repository.obtenerHistorial(limite)
}

class ObtenerHistorialPorFechaUseCase @Inject constructor(
	private val repository: HistorialRepository
) {
	operator fun invoke(
		fechaInicio: Long,
		fechaFin: Long
	): Flow<List<HistorialReproduccion>> {
		require(fechaInicio <= fechaFin) { "fechaInicio debe ser <= fechaFin" }
		return repository.obtenerHistorialPorFecha(fechaInicio, fechaFin)
	}
}

// ════════════════════════════════════════════════════════════
// REGISTRO
// ════════════════════════════════════════════════════════════

class RegistrarReproduccionUseCase @Inject constructor(
	private val repository: HistorialRepository
) {
	suspend operator fun invoke(
		cancionId: Long,
		duracionEscuchada: Long,
		completada: Boolean
	): Result<Unit> {
		return try {
			require(duracionEscuchada >= 0) { "duracionEscuchada debe ser >= 0" }
			repository.registrarReproduccion(cancionId, duracionEscuchada, completada)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// LIMPIEZA
// ════════════════════════════════════════════════════════════

class LimpiarHistorialUseCase @Inject constructor(
	private val repository: HistorialRepository
) {
	suspend operator fun invoke(): Result<Unit> {
		return try {
			repository.limpiarHistorial()
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class EliminarEntradaHistorialUseCase @Inject constructor(
	private val repository: HistorialRepository
) {
	suspend operator fun invoke(id: Long): Result<Unit> {
		return try {
			repository.eliminarEntrada(id)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// ESTADÍSTICAS
// ════════════════════════════════════════════════════════════

class ObtenerTiempoTotalEscuchadoUseCase @Inject constructor(
	private val repository: HistorialRepository
) {
	suspend operator fun invoke(): Result<Duracion> {
		return try {
			val tiempoMs = repository.obtenerTiempoTotalEscuchado()
			Result.success(Duracion(tiempoMs))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class ObtenerCancionesReproducidasHoyUseCase @Inject constructor(
	private val repository: HistorialRepository
) {
	suspend operator fun invoke(): Result<Int> {
		return try {
			Result.success(repository.obtenerCancionesReproducidasHoy())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}