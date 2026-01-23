package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Duracion
import com.pmk.freeplayer.domain.pipeline.ResultadoEnriquecimiento
import com.pmk.freeplayer.domain.pipeline.ResultadoEscaneo
import com.pmk.freeplayer.domain.pipeline.ResultadoLimpieza
import kotlinx.coroutines.flow.Flow

interface EscaneoRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Resultados de escaneo
	// ─────────────────────────────────────────────────────────────
	suspend fun guardarResultadoEscaneo(resultado: ResultadoEscaneo): Long
	
	suspend fun guardarResultadoLimpieza(resultado: ResultadoLimpieza): Long
	
	suspend fun guardarResultadoEnriquecimiento(resultado: ResultadoEnriquecimiento): Long
	
	fun obtenerHistorialEscaneos(limite: Int = 20): Flow<List<ResultadoEscaneo>>
	
	suspend fun obtenerUltimoEscaneo(): ResultadoEscaneo?
	
	// ─────────────────────────────────────────────────────────────
	// Estadísticas acumuladas
	// ─────────────────────────────────────────────────────────────
	suspend fun obtenerTotalEscaneos(): Int
	
	suspend fun obtenerTotalCancionesEscaneadas(): Int
	
	suspend fun obtenerTotalCancionesLimpiadas(): Int
	
	suspend fun obtenerTotalCancionesEnriquecidas(): Int
	
	suspend fun obtenerTotalLetrasObtenidas(): Int
	
	suspend fun obtenerTiempoTotalProcesamiento(): Duracion
	
	// ─────────────────────────────────────────────────────────────
	// Limpieza
	// ─────────────────────────────────────────────────────────────
	suspend fun limpiarHistorialEscaneos(mantenerUltimos: Int = 10)
}