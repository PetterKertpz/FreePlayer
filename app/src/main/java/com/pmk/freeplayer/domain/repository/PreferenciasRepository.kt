package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.*
import com.pmk.freeplayer.domain.model.enums.ColorAcento
import com.pmk.freeplayer.domain.model.enums.IdiomaApp
import com.pmk.freeplayer.domain.model.enums.ModoRepeticion
import com.pmk.freeplayer.domain.model.enums.ModoTema
import com.pmk.freeplayer.domain.model.enums.PresetEcualizador
import com.pmk.freeplayer.domain.model.enums.TamanioFuente
import com.pmk.freeplayer.domain.model.enums.TipoOrdenamiento
import kotlinx.coroutines.flow.Flow

interface PreferenciasRepository {

    // ─────────────────────────────────────────────────────────────
    // Obtener preferencias completas
    // ─────────────────────────────────────────────────────────────
    fun obtenerPreferencias(): Flow<PreferenciasUsuario>

    // ─────────────────────────────────────────────────────────────
    // Apariencia
    // ─────────────────────────────────────────────────────────────
    suspend fun setModoTema(modo: ModoTema)

    suspend fun setColorAcento(color: ColorAcento)

    suspend fun setUsarColoresPortada(usar: Boolean)

    // ─────────────────────────────────────────────────────────────
    // Reproductor
    // ─────────────────────────────────────────────────────────────
    suspend fun setModoRepeticion(modo: ModoRepeticion)

    suspend fun setAleatorio(activado: Boolean)

    suspend fun setReproduccionSinPausas(activado: Boolean)

    suspend fun setDuracionFundido(segundos: Int)

    suspend fun setReanudarAlIniciar(activado: Boolean)

    // ─────────────────────────────────────────────────────────────
    // Audio
    // ─────────────────────────────────────────────────────────────
    suspend fun setEcualizadorActivado(activado: Boolean)

    suspend fun setPresetEcualizador(preset: PresetEcualizador)

    suspend fun setNivelGraves(nivel: Int)

    suspend fun setNivelVirtualizador(nivel: Int)

    suspend fun setVelocidadReproduccion(velocidad: Float)

    suspend fun setNormalizacionAudio(activado: Boolean)

    // ─────────────────────────────────────────────────────────────
    // Biblioteca
    // ─────────────────────────────────────────────────────────────
    suspend fun setOrdenamiento(ordenamiento: TipoOrdenamiento)

    suspend fun setColumnasGrid(columnas: Int)

    suspend fun setMostrarCarpetas(mostrar: Boolean)

    suspend fun setCarpetasIgnoradas(carpetas: List<String>)

    suspend fun setDuracionMinima(segundos: Int)

    // ─────────────────────────────────────────────────────────────
    // Letras
    // ─────────────────────────────────────────────────────────────
    suspend fun setBuscarLetrasAuto(activado: Boolean)

    suspend fun setMostrarTraduccion(mostrar: Boolean)

    suspend fun setTamanioFuenteLetras(tamanio: TamanioFuente)

    // ─────────────────────────────────────────────────────────────
    // Notificaciones
    // ─────────────────────────────────────────────────────────────
    suspend fun setMostrarNotificacion(mostrar: Boolean)

    suspend fun setControlesPantallaBloqueada(mostrar: Boolean)

    // ─────────────────────────────────────────────────────────────
    // General
    // ─────────────────────────────────────────────────────────────
    suspend fun setIdioma(idioma: IdiomaApp)

    suspend fun setTemporizadorSuenioPredeterminado(minutos: Int)

    // ─────────────────────────────────────────────────────────────
    // Reset
    // ─────────────────────────────────────────────────────────────
    suspend fun restaurarValoresPredeterminados()
}