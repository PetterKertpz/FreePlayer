package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.LetraCancion
import kotlinx.coroutines.flow.Flow

interface LetraRepository {

    fun obtenerLetra(cancionId: Long): Flow<LetraCancion?>

    suspend fun buscarLetraEnLinea(
        titulo: String,
        artista: String
    ): LetraCancion?

    suspend fun buscarArchivoLrcLocal(rutaCancion: String): LetraCancion?

    suspend fun guardarLetra(letra: LetraCancion)

    suspend fun eliminarLetra(cancionId: Long)

    suspend fun tieneLetra(cancionId: Long): Boolean
}