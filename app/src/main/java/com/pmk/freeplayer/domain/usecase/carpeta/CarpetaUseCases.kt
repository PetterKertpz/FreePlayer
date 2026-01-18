package com.pmk.freeplayer.domain.usecase.carpeta

import com.pmk.freeplayer.domain.model.Carpeta
import com.pmk.freeplayer.domain.repository.CarpetaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// CONSULTAS
// ════════════════════════════════════════════════════════════

class ObtenerTodasCarpetasUseCase @Inject constructor(
	private val repository: CarpetaRepository
) {
	operator fun invoke(): Flow<List<Carpeta>> = repository.obtenerTodas()
}

class ObtenerCarpetasVisiblesUseCase @Inject constructor(
	private val repository: CarpetaRepository
) {
	operator fun invoke(): Flow<List<Carpeta>> = repository.obtenerVisibles()
}

class ObtenerCarpetasOcultasUseCase @Inject constructor(
	private val repository: CarpetaRepository
) {
	operator fun invoke(): Flow<List<Carpeta>> = repository.obtenerOcultas()
}

// ════════════════════════════════════════════════════════════
// VISIBILIDAD
// ════════════════════════════════════════════════════════════

class OcultarCarpetaUseCase @Inject constructor(
	private val repository: CarpetaRepository
) {
	suspend operator fun invoke(ruta: String): Result<Unit> {
		return try {
			require(ruta.isNotBlank()) { "La ruta no puede estar vacía" }
			repository.ocultarCarpeta(ruta)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class MostrarCarpetaUseCase @Inject constructor(
	private val repository: CarpetaRepository
) {
	suspend operator fun invoke(ruta: String): Result<Unit> {
		return try {
			require(ruta.isNotBlank()) { "La ruta no puede estar vacía" }
			repository.mostrarCarpeta(ruta)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}