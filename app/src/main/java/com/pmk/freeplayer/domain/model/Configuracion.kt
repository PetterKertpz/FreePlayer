package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.config.ColorAcento
import com.pmk.freeplayer.domain.model.config.CriterioOrdenamiento
import com.pmk.freeplayer.domain.model.config.IdiomaApp
import com.pmk.freeplayer.domain.model.config.ModoRepeticion
import com.pmk.freeplayer.domain.model.config.ModoTema
import com.pmk.freeplayer.domain.model.config.Ordenamiento
import com.pmk.freeplayer.domain.model.config.PresetEcualizador
import com.pmk.freeplayer.domain.model.config.TamanioFuente
import com.pmk.freeplayer.domain.model.config.TipoListaSistema
import com.pmk.freeplayer.domain.model.state.EstadoProcesoMedia

data class PreferenciasUsuario(
	// Visual
	val modoTema: ModoTema = ModoTema.SISTEMA,
	val colorAcento: ColorAcento = ColorAcento.PREDETERMINADO,
	val usarColoresPortada: Boolean = true,     // colores dinámicos de portada
	
	// === Reproductor ===
	val modoRepeticion: ModoRepeticion = ModoRepeticion.DESACTIVADO,
	val aleatorioActivado: Boolean = false,
	val reproduccionSinPausas: Boolean = true,  // gapless playback
	val duracionFundido: Int = 0,               // segundos de crossfade
	val reanudarAlIniciar: Boolean = true,
	
	// === Audio ===
	val ecualizadorActivado: Boolean = false,
	val presetEcualizador: PresetEcualizador = PresetEcualizador.PLANO,
	val nivelGraves: Int = 0,                   // 0-100 (bass boost)
	val nivelVirtualizador: Int = 0,            // 0-100
	val velocidadReproduccion: Float = 1.0f,    // 0.5x - 2.0x
	val normalizacionAudio: Boolean = false,
	
	// === Biblioteca ===
	val ordenamiento: Ordenamiento = Ordenamiento(CriterioOrdenamiento.TITULO),
	val columnasGrid: Int = 2,
	val mostrarCarpetas: Boolean = true,
	val carpetasIgnoradas: List<String> = emptyList(),
	val duracionMinimaSegundos: Int = 30,       // filtrar audios cortos
	
	// === Letras ===
	val buscarLetrasAuto: Boolean = true,
	val mostrarTraduccion: Boolean = false,
	val tamanioFuenteLetras: TamanioFuente = TamanioFuente.MEDIANO,
	
	// === Notificaciones ===
	val mostrarNotificacion: Boolean = true,
	val controlesEnPantallaBloqueada: Boolean = true,
	
	// === General ===
	val idioma: IdiomaApp = IdiomaApp.SISTEMA,
	val temporizadorSuenioMinutos: Int = 30
)

data class ListaReproduccion(
	val id: Long,
	val nombre: String,
	val descripcion: String?,
	val canciones: List<Cancion>,
	val portadaUri: String?,                // portada personalizada
	val tipoSistema: TipoListaSistema?,     // null si es creada por usuario
	val creadoEn: Long,
	val actualizadoEn: Long
)

data class EstadisticasBiblioteca(
	val totalCanciones: Int,
	val cancionesCrudas: Int,
	val cancionesLimpias: Int,
	val cancionesEnriquecidas: Int,
	val letrasEncontradas: Int,
	val letrasSinBuscar: Int,
	val ultimoEscaneo: Long?,
	val duracionTotalMs: Duracion,
	val tamanioTotalBytes: Tamanio
)

// Logs internos para depuración en UI
data class LogApp(
	val id: Long = 0,
	val timestamp: Long,
	val nivel: NivelLog,
	val fase: EstadoProcesoMedia,
	val mensaje: String,
	val cancionId: Long?,
	val detalles: Map<String, String>?,
	val stackTrace: String?,
)
enum class NivelLog {
	DEBUG,
	INFO,
	WARNING,
	ERROR
}