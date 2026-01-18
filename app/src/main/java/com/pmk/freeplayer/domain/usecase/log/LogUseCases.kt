package com.pmk.freeplayer.domain.usecase.log

import com.pmk.freeplayer.domain.model.LogEntry
import com.pmk.freeplayer.domain.model.enums.FaseProceso
import com.pmk.freeplayer.domain.model.enums.NivelLog
import com.pmk.freeplayer.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ════════════════════════════════════════════════════════════
// ESCRITURA DE LOGS
// ════════════════════════════════════════════════════════════

class RegistrarLogUseCase @Inject constructor(
	private val repository: LogRepository
) {
	suspend operator fun invoke(
		nivel: NivelLog,
		fase: FaseProceso,
		mensaje: String,
		cancionId: Long? = null,
		detalles: Map<String, String>? = null
	): Result<Unit> {
		return try {
			repository.registrar(nivel, fase, mensaje, cancionId, detalles)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class RegistrarDebugUseCase @Inject constructor(
	private val repository: LogRepository
) {
	suspend operator fun invoke(
		fase: FaseProceso,
		mensaje: String,
		cancionId: Long? = null
	): Result<Unit> {
		return try {
			repository.debug(fase, mensaje, cancionId)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class RegistrarInfoUseCase @Inject constructor(
	private val repository: LogRepository
) {
	suspend operator fun invoke(
		fase: FaseProceso,
		mensaje: String,
		cancionId: Long? = null
	): Result<Unit> {
		return try {
			repository.info(fase, mensaje, cancionId)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class RegistrarWarningUseCase @Inject constructor(
	private val repository: LogRepository
) {
	suspend operator fun invoke(
		fase: FaseProceso,
		mensaje: String,
		cancionId: Long? = null
	): Result<Unit> {
		return try {
			repository.warning(fase, mensaje, cancionId)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class RegistrarErrorUseCase @Inject constructor(
	private val repository: LogRepository
) {
	suspend operator fun invoke(
		fase: FaseProceso,
		mensaje: String,
		cancionId: Long? = null,
		excepcion: Throwable? = null
	): Result<Unit> {
		return try {
			repository.error(fase, mensaje, cancionId, excepcion)
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
	private val repository: LogRepository
) {
	operator fun invoke(
		limite: Int = 100,
		nivelMinimo: NivelLog = NivelLog.INFO
	): Flow<List<LogEntry>> = repository.obtenerLogs(limite, nivelMinimo)
}

class ObtenerLogsPorFaseUseCase @Inject constructor(
	private val repository: LogRepository
) {
	operator fun invoke(
		fase: FaseProceso,
		limite: Int = 50
	): Flow<List<LogEntry>> = repository.obtenerLogsPorFase(fase, limite)
}

class ObtenerLogsPorCancionUseCase @Inject constructor(
	private val repository: LogRepository
) {
	operator fun invoke(cancionId: Long): Flow<List<LogEntry>> =
		repository.obtenerLogsPorCancion(cancionId)
}

class ObtenerErroresRecientesUseCase @Inject constructor(
	private val repository: LogRepository
) {
	operator fun invoke(limite: Int = 20): Flow<List<LogEntry>> =
		repository.obtenerErroresRecientes(limite)
}

// ════════════════════════════════════════════════════════════
// LIMPIEZA
// ════════════════════════════════════════════════════════════

class LimpiarLogsAntiguosUseCase @Inject constructor(
	private val repository: LogRepository
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
	private val repository: LogRepository
) {
	suspend operator fun invoke(): Result<Unit> {
		return try {
			repository.limpiarTodos()
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}

class ContarLogsUseCase @Inject constructor(
	private val repository: LogRepository
) {
	suspend operator fun invoke(): Result<Int> {
		return try {
			Result.success(repository.contarLogs())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}