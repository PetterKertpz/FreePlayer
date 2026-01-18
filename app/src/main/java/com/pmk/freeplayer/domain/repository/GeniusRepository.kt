package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Genius
import com.pmk.freeplayer.domain.model.ResultadoBusquedaGenius

interface GeniusRepository {

  // ─────────────────────────────────────────────────────────────
  // Búsqueda en API
  // ─────────────────────────────────────────────────────────────
  suspend fun buscarCancion(titulo: String, artista: String): ResultadoBusquedaGenius?

  suspend fun obtenerDetallesCancion(geniusId: Long): Genius?

  // ─────────────────────────────────────────────────────────────
  // Scraping de letras
  // ─────────────────────────────────────────────────────────────
  suspend fun obtenerLetra(geniusUrl: String): String?

  // ─────────────────────────────────────────────────────────────
  // Caché de búsquedas fallidas (evitar re-buscar)
  // ─────────────────────────────────────────────────────────────
  suspend fun marcarBusquedaFallida(cancionId: Long)

  suspend fun fueBusquedaFallida(cancionId: Long): Boolean

  suspend fun limpiarCacheFallidas(antiguedadDias: Int = 30)
}
