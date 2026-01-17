package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Cancion
import com.pmk.freeplayer.domain.model.enums.TipoOrdenamiento
import kotlinx.coroutines.flow.Flow

interface CancionRepository {

    // ─────────────────────────────────────────────────────────────
    // Obtener canciones
    // ─────────────────────────────────────────────────────────────
    fun obtenerTodas(): Flow<List<Cancion>>

    fun obtenerPorId(id: Long): Flow<Cancion?>

    fun obtenerPorAlbum(albumId: Long): Flow<List<Cancion>>

    fun obtenerPorArtista(artista: String): Flow<List<Cancion>>

    fun obtenerPorGenero(genero: String): Flow<List<Cancion>>

    fun obtenerPorCarpeta(ruta: String): Flow<List<Cancion>>

    fun obtenerOrdenadas(ordenamiento: TipoOrdenamiento): Flow<List<Cancion>>

    // ─────────────────────────────────────────────────────────────
    // Búsqueda
    // ─────────────────────────────────────────────────────────────
    fun buscar(consulta: String): Flow<List<Cancion>>

    // ─────────────────────────────────────────────────────────────
    // Favoritos
    // ─────────────────────────────────────────────────────────────
    fun obtenerFavoritas(): Flow<List<Cancion>>

    suspend fun marcarComoFavorita(id: Long, esFavorita: Boolean)

    suspend fun alternarFavorita(id: Long)

    // ─────────────────────────────────────────────────────────────
    // Estadísticas de reproducción
    // ─────────────────────────────────────────────────────────────
    fun obtenerMasReproducidas(limite: Int = 50): Flow<List<Cancion>>

    fun obtenerReproducidasRecientemente(limite: Int = 50): Flow<List<Cancion>>

    fun obtenerAgregadasRecientemente(limite: Int = 50): Flow<List<Cancion>>

    suspend fun incrementarReproduccion(id: Long)

    suspend fun actualizarUltimaReproduccion(id: Long, timestamp: Long)

    // ─────────────────────────────────────────────────────────────
    // Sincronización con dispositivo
    // ─────────────────────────────────────────────────────────────
    suspend fun escanearDispositivo()

    suspend fun obtenerCantidadTotal(): Int
}