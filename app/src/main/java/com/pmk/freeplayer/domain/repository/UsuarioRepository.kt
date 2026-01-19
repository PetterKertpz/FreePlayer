package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.HistorialReproduccion
import com.pmk.freeplayer.domain.model.LogApp
import com.pmk.freeplayer.domain.model.NivelLog
import com.pmk.freeplayer.domain.model.PerfilUsuario
import com.pmk.freeplayer.domain.model.PreferenciasUsuario
import com.pmk.freeplayer.domain.model.audio.Genero
import com.pmk.freeplayer.domain.model.config.ColorAcento
import com.pmk.freeplayer.domain.model.config.IdiomaApp
import com.pmk.freeplayer.domain.model.config.ModoRepeticion
import com.pmk.freeplayer.domain.model.config.ModoTema
import com.pmk.freeplayer.domain.model.config.Ordenamiento
import com.pmk.freeplayer.domain.model.config.PresetEcualizador
import com.pmk.freeplayer.domain.model.config.TamanioFuente
import com.pmk.freeplayer.domain.model.state.EstadoProcesoMedia
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {

   // ═════════════════════════════════════════════════════════════
   // PERFIL DE USUARIO
   // ═════════════════════════════════════════════════════════════

   fun obtenerPerfil(): Flow<PerfilUsuario>

   suspend fun actualizarNombrePerfil(nombre: String)

   suspend fun actualizarAvatarPerfil(uri: String?)

   suspend fun incrementarTiempoEscuchado(milisegundos: Long)

   suspend fun incrementarCancionesReproducidas()

   suspend fun actualizarGeneroFavorito(genero: Genero)

   suspend fun actualizarArtistaFavorito(artista: String)

   suspend fun reiniciarEstadisticasPerfil()

   // ═════════════════════════════════════════════════════════════
   // PREFERENCIAS
   // ═════════════════════════════════════════════════════════════

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
   suspend fun setOrdenamiento(ordenamiento: Ordenamiento)

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
   // Reset de preferencias
   // ─────────────────────────────────────────────────────────────
   suspend fun restaurarValoresPredeterminados()

   // ═════════════════════════════════════════════════════════════
   // HISTORIAL DE REPRODUCCIÓN
   // ═════════════════════════════════════════════════════════════

   fun obtenerHistorial(limite: Int = 100): Flow<List<HistorialReproduccion>>

   fun obtenerHistorialPorFecha(
      fechaInicio: Long,
      fechaFin: Long,
   ): Flow<List<HistorialReproduccion>>

   suspend fun registrarReproduccion(cancionId: Long, duracionEscuchada: Long, completada: Boolean)

   suspend fun limpiarHistorial()

   suspend fun eliminarEntradaHistorial(id: Long)

   // ─────────────────────────────────────────────────────────────
   // Estadísticas de historial
   // ─────────────────────────────────────────────────────────────
   suspend fun obtenerTiempoTotalEscuchado(): Long

   suspend fun obtenerCancionesReproducidasHoy(): Int

   // ═════════════════════════════════════════════════════════════
   // LOGS Y DIAGNÓSTICO
   // ═════════════════════════════════════════════════════════════

   // ─────────────────────────────────────────────────────────────
   // Escritura de logs
   // ─────────────────────────────────────────────────────────────
   suspend fun registrarLog(
      nivel: NivelLog,
      fase: EstadoProcesoMedia,
      mensaje: String,
      cancionId: Long? = null,
      detalles: Map<String, String>? = null,
      excepcion: Throwable? = null,
   )

   suspend fun logDebug(fase: EstadoProcesoMedia, mensaje: String, cancionId: Long? = null)

   suspend fun logInfo(fase: EstadoProcesoMedia, mensaje: String, cancionId: Long? = null)

   suspend fun logWarning(fase: EstadoProcesoMedia, mensaje: String, cancionId: Long? = null)

   suspend fun logError(
      fase: EstadoProcesoMedia,
      mensaje: String,
      cancionId: Long? = null,
      excepcion: Throwable? = null,
   )

   // ─────────────────────────────────────────────────────────────
   // Consulta de logs
   // ─────────────────────────────────────────────────────────────
   fun obtenerLogs(limite: Int = 100, nivelMinimo: NivelLog = NivelLog.INFO): Flow<List<LogApp>>

   fun obtenerLogsPorFase(fase: EstadoProcesoMedia, limite: Int = 50): Flow<List<LogApp>>

   fun obtenerLogsPorCancion(cancionId: Long): Flow<List<LogApp>>

   fun obtenerErroresRecientes(limite: Int = 20): Flow<List<LogApp>>

   // ─────────────────────────────────────────────────────────────
   // Limpieza de logs
   // ─────────────────────────────────────────────────────────────
   suspend fun limpiarLogsAntiguos(diasAntiguedad: Int = 7)

   suspend fun limpiarTodosLosLogs()

   suspend fun contarLogs(): Int
}
