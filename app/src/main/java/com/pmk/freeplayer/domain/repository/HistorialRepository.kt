package com.pmk.freeplayer.domain.repository

import kotlinx.coroutines.flow.Flow

interface HistorialRepository {

    fun obtenerHistorial(limite: Int = 100): Flow<List<HistorialReproduccion>>

    fun obtenerHistorialPorFecha(
        fechaInicio: Long,
        fechaFin: Long
    ): Flow<List<HistorialReproduccion>>

    suspend fun registrarReproduccion(
        cancionId: Long,
        duracionEscuchada: Long,
        completada: Boolean
    )

    suspend fun limpiarHistorial()

    suspend fun eliminarEntrada(id: Long)

    // Estadísticas
    suspend fun obtenerTiempoTotalEscuchado(): Long

    suspend fun obtenerCancionesReproducidasHoy(): Int
}