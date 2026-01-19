package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Cancion
import com.pmk.freeplayer.domain.model.GeniusMetadata
import com.pmk.freeplayer.domain.model.LetraCancion
import com.pmk.freeplayer.domain.model.audio.EstadoLetra
import kotlinx.coroutines.flow.Flow

interface GeniusRepository {

   // ═════════════════════════════════════════════════════════════
   // LETRAS
   // ═════════════════════════════════════════════════════════════

   // ─────────────────────────────────────────────────────────────
   // Obtención y gestión de letras
   // ─────────────────────────────────────────────────────────────
   fun obtenerLetra(cancionId: Long): Flow<LetraCancion?>

   suspend fun guardarLetra(letra: LetraCancion)

   suspend fun eliminarLetra(cancionId: Long)

   suspend fun tieneLetra(cancionId: Long): Boolean

   // ─────────────────────────────────────────────────────────────
   // Búsqueda de letras
   // ─────────────────────────────────────────────────────────────
   suspend fun buscarLetraEnLinea(titulo: String, artista: String): LetraCancion?

   suspend fun buscarArchivoLrcLocal(rutaCancion: String): LetraCancion?

   // ─────────────────────────────────────────────────────────────
   // Estados de letras
   // ─────────────────────────────────────────────────────────────
   suspend fun actualizarEstadoLetra(cancionId: Long, estado: EstadoLetra)

   suspend fun guardarLetraConEstado(cancionId: Long, letra: String, geniusUrl: String?)

   suspend fun marcarLetraNoEncontrada(cancionId: Long)

   fun obtenerCancionesSinLetraBuscada(limite: Int = 50): Flow<List<Cancion>>

   suspend fun contarPorEstadoLetra(estado: EstadoLetra): Int

   // ═════════════════════════════════════════════════════════════
   // GENIUS API
   // ═════════════════════════════════════════════════════════════

   // ─────────────────────────────────────────────────────────────
   // Búsqueda en API
   // ─────────────────────────────────────────────────────────────
   suspend fun buscarCancionEnGenius(titulo: String, artista: String): GeniusMetadata?

   suspend fun obtenerDetallesCancionGenius(geniusId: Long): GeniusMetadata?

   // ─────────────────────────────────────────────────────────────
   // Scraping de letras desde Genius
   // ─────────────────────────────────────────────────────────────
   suspend fun obtenerLetraDesdeGenius(geniusUrl: String): String?

   // ─────────────────────────────────────────────────────────────
   // Caché de búsquedas fallidas (evitar re-buscar)
   // ─────────────────────────────────────────────────────────────
   suspend fun marcarBusquedaGeniusFallida(cancionId: Long)

   suspend fun fueBusquedaGeniusFallida(cancionId: Long): Boolean

   suspend fun limpiarCacheBusquedasFallidas(antiguedadDias: Int = 30)
}
