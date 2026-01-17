package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Carpeta
import kotlinx.coroutines.flow.Flow

interface CarpetaRepository {

    fun obtenerTodas(): Flow<List<Carpeta>>

    fun obtenerVisibles(): Flow<List<Carpeta>>

    suspend fun ocultarCarpeta(ruta: String)

    suspend fun mostrarCarpeta(ruta: String)

    fun obtenerOcultas(): Flow<List<Carpeta>>
}