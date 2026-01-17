package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.LogEntry
import com.pmk.freeplayer.domain.model.enums.FaseProceso
import com.pmk.freeplayer.domain.model.enums.NivelLog
import kotlinx.coroutines.flow.Flow

interface LogRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Escritura de logs
	// ─────────────────────────────────────────────────────────────
	suspend fun registrar(
		nivel: NivelLog,
		fase: FaseProceso,
		mensaje: String,
		cancionId: Long? = null,
		detalles: Map<String, String>? = null
	)
	
	suspend fun debug(fase: FaseProceso, mensaje: String, cancionId: Long? = null)
	
	suspend fun info(fase: FaseProceso, mensaje: String, cancionId: Long? = null)
	
	suspend fun warning(fase: FaseProceso, mensaje: String, cancionId: Long? = null)
	
	suspend fun error(
		fase: FaseProceso,
		mensaje: String,
		cancionId: Long? = null,
		excepcion: Throwable? = null
	)
	
	// ─────────────────────────────────────────────────────────────
	// Consulta de logs
	// ─────────────────────────────────────────────────────────────
	fun obtenerLogs(
		limite: Int = 100,
		nivelMinimo: NivelLog = NivelLog.INFO
	): Flow<List<LogEntry>>
	
	fun obtenerLogsPorFase(fase: FaseProceso, limite: Int = 50): Flow<List<LogEntry>>
	
	fun obtenerLogsPorCancion(cancionId: Long): Flow<List<LogEntry>>
	
	fun obtenerErroresRecientes(limite: Int = 20): Flow<List<LogEntry>>
	
	// ─────────────────────────────────────────────────────────────
	// Limpieza
	// ─────────────────────────────────────────────────────────────
	suspend fun limpiarLogsAntiguos(diasAntiguedad: Int = 7)
	
	suspend fun limpiarTodos()
	
	suspend fun contarLogs(): Int
}