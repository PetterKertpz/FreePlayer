package com.pmk.freeplayer.domain.repository

import kotlinx.coroutines.flow.Flow

interface AlbumRepository {

    fun obtenerTodos(): Flow<List<Album>>

    fun obtenerPorId(id: Long): Flow<Album?>

    fun obtenerPorArtista(artista: String): Flow<List<Album>>

    fun obtenerOrdenados(ordenamiento: TipoOrdenamiento): Flow<List<Album>>

    fun buscar(consulta: String): Flow<List<Album>>

    suspend fun obtenerCantidadTotal(): Int
}