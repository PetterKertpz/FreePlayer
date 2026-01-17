package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.enums.Genero
import com.pmk.freeplayer.domain.model.PerfilUsuario
import kotlinx.coroutines.flow.Flow

interface PerfilUsuarioRepository {

    fun obtenerPerfil(): Flow<PerfilUsuario>

    suspend fun actualizarNombre(nombre: String)

    suspend fun actualizarAvatar(uri: String?)

    suspend fun incrementarTiempoEscuchado(milisegundos: Long)

    suspend fun incrementarCancionesReproducidas()

    suspend fun actualizarGeneroFavorito(genero: Genero)

    suspend fun actualizarArtistaFavorito(artista: String)

    suspend fun reiniciarEstadisticas()
}