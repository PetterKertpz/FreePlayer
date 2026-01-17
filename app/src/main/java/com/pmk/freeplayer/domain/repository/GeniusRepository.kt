package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Cancion
import com.pmk.freeplayer.domain.model.DatosGenius
import com.pmk.freeplayer.domain.model.ResultadoBusquedaGenius
import com.pmk.freeplayer.domain.model.ResultadoComparacion

interface GeniusRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Búsqueda en API
	// ─────────────────────────────────────────────────────────────
	suspend fun buscarCancion(
		titulo: String,
		artista: String
	): ResultadoBusquedaGenius?
	
	suspend fun obtenerDetallesCancion(geniusId: Long): DatosGenius?
	
	// ─────────────────────────────────────────────────────────────
	// Scraping de letras
	// ─────────────────────────────────────────────────────────────
	suspend fun obtenerLetra(geniusUrl: String): String?
	
	// ─────────────────────────────────────────────────────────────
	// Comparación
	// ─────────────────────────────────────────────────────────────
	fun calcularCoincidencia(
		cancion: Cancion,
		resultado: ResultadoBusquedaGenius
	): ResultadoComparacion
	
	// ─────────────────────────────────────────────────────────────
	// Caché de búsquedas fallidas (evitar re-buscar)
	// ─────────────────────────────────────────────────────────────
	suspend fun marcarBusquedaFallida(cancionId: Long)
	
	suspend fun fueBusquedaFallida(cancionId: Long): Boolean
	
	suspend fun limpiarCacheFallidas(antiguedadDias: Int = 30)
}