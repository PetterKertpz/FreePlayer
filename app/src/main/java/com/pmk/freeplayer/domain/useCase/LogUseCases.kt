package com.pmk.freeplayer.domain.useCase

import com.pmk.freeplayer.domain.model.LogApp
import com.pmk.freeplayer.domain.model.NivelLog
import com.pmk.freeplayer.domain.model.state.MediaProcessingState
import com.pmk.freeplayer.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// ESCRITURA DE LOGS
// ════════════════════════════════════════════════════════════

class RegistrarLogUseCase @Inject constructor(
	private val repository: UsuarioRepository
) {
	suspend operator fun invoke(
		nivel: NivelLog,
		fase: MediaProcessingState,
		mensaje: String,
		cancionId: Long? = null,
		detalles: Map<String, String>? = null
	): Result<Unit> {
		return try {
			repository.registrarLog(nivel, fase, mensaje, cancionId, detalles)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class RegistrarDebugUseCase @Inject constructor(
	private val repository: UsuarioRepository
) {
	suspend operator fun invoke(
		fase: MediaProcessingState,
		mensaje: String,
		cancionId: Long? = null
	): Result<Unit> {
		return try {
			repository.logDebug(fase, mensaje, cancionId)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class RegistrarInfoUseCase @Inject constructor(
	private val repository: UsuarioRepository
) {
	suspend operator fun invoke(
		fase: MediaProcessingState,
		mensaje: String,
		cancionId: Long? = null
	): Result<Unit> {
		return try {
			repository.logInfo(fase, mensaje, cancionId)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class RegistrarWarningUseCase @Inject constructor(
	private val repository: UsuarioRepository
) {
	suspend operator fun invoke(
		fase: MediaProcessingState,
		mensaje: String,
		cancionId: Long? = null
	): Result<Unit> {
		return try {
			repository.logWarning(fase, mensaje, cancionId)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class RegistrarErrorUseCase @Inject constructor(
	private val repository: UsuarioRepository
) {
	suspend operator fun invoke(
		fase: MediaProcessingState,
		mensaje: String,
		cancionId: Long? = null,
		excepcion: Throwable? = null
	): Result<Unit> {
		return try {
			repository.logError(fase, mensaje, cancionId, excepcion)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

// ════════════════════════════════════════════════════════════
// CONSULTA DE LOGS
// ════════════════════════════════════════════════════════════

class ObtenerLogsUseCase @Inject constructor(
	private val repository: UsuarioRepository
) {
	operator fun invoke(
		limite: Int = 100,
		nivelMinimo: NivelLog = NivelLog.INFO
	): Flow<List<LogApp>> = repository.obtenerLogs(limite, nivelMinimo)
}

class ObtenerLogsPorFaseUseCase @Inject constructor(
	private val repository: UsuarioRepository
) {
	operator fun invoke(
		fase: MediaProcessingState,
		limite: Int = 50
	): Flow<List<LogApp>> = repository.obtenerLogsPorFase(fase, limite)
}

class ObtenerLogsPorCancionUseCase @Inject constructor(
	private val repository: UsuarioRepository
) {
	operator fun invoke(cancionId: Long): Flow<List<LogApp>> =
		repository.obtenerLogsPorCancion(cancionId)
}

class ObtenerErroresRecientesUseCase @Inject constructor(
	private val repository: UsuarioRepository
) {
	operator fun invoke(limite: Int = 20): Flow<List<LogApp>> =
		repository.obtenerErroresRecientes(limite)
}

// ════════════════════════════════════════════════════════════
// LIMPIEZA
// ════════════════════════════════════════════════════════════

class LimpiarLogsAntiguosUseCase @Inject constructor(
	private val repository: UsuarioRepository
) {
	suspend operator fun invoke(diasAntiguedad: Int = 7): Result<Unit> {
		return try {
			require(diasAntiguedad > 0) { "diasAntiguedad debe ser > 0" }
			repository.limpiarLogsAntiguos(diasAntiguedad)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class LimpiarTodosLogsUseCase @Inject constructor(
	private val repository: UsuarioRepository
) {
	suspend operator fun invoke(): Result<Unit> {
		return try {
			repository.limpiarTodosLosLogs()
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class ContarLogsUseCase @Inject constructor(
	private val repository: UsuarioRepository
) {
	suspend operator fun invoke(): Result<Int> {
		return try {
			Result.success(repository.contarLogs())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}