package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.ListaReproduccion
import kotlinx.coroutines.flow.Flow

interface ListaReproduccionRepository {

    // ─────────────────────────────────────────────────────────────
    // CRUD de playlists
    // ─────────────────────────────────────────────────────────────
    fun obtenerTodas(): Flow<List<ListaReproduccion>>

    fun obtenerPorId(id: Long): Flow<ListaReproduccion?>

    suspend fun crear(nombre: String, descripcion: String? = null): Long

    suspend fun actualizar(id: Long, nombre: String, descripcion: String?)

    suspend fun eliminar(id: Long)

    suspend fun duplicar(id: Long, nuevoNombre: String): Long

    // ─────────────────────────────────────────────────────────────
    // Gestión de canciones en playlist
    // ─────────────────────────────────────────────────────────────
    suspend fun agregarCancion(playlistId: Long, cancionId: Long)

    suspend fun agregarCanciones(playlistId: Long, cancionIds: List<Long>)

    suspend fun eliminarCancion(playlistId: Long, cancionId: Long)

    suspend fun moverCancion(playlistId: Long, desdePosicion: Int, haciaPosicion: Int)

    suspend fun existeCancionEnPlaylist(playlistId: Long, cancionId: Long): Boolean

    // ─────────────────────────────────────────────────────────────
    // Portada
    // ─────────────────────────────────────────────────────────────
    suspend fun actualizarPortada(id: Long, uri: String?)

    // ─────────────────────────────────────────────────────────────
    // Búsqueda
    // ─────────────────────────────────────────────────────────────
    fun buscar(consulta: String): Flow<List<ListaReproduccion>>
}