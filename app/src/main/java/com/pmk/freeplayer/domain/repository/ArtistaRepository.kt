package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Artista
import com.pmk.freeplayer.domain.model.enums.TipoOrdenamiento
import kotlinx.coroutines.flow.Flow

interface ArtistaRepository {

    fun obtenerTodos(): Flow<List<Artista>>

    fun obtenerPorId(id: Long): Flow<Artista?>

    fun obtenerOrdenados(ordenamiento: TipoOrdenamiento): Flow<List<Artista>>

    fun buscar(consulta: String): Flow<List<Artista>>

    suspend fun obtenerCantidadTotal(): Int
}