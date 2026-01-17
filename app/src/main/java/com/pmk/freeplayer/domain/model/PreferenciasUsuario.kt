package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.enums.TipoOrdenamiento

data class PreferenciasUsuario(
    // === Apariencia ===
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
    val ordenamiento: TipoOrdenamiento = TipoOrdenamiento.TITULO_ASC,
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
