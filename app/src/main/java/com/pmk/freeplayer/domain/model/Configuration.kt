package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.config.ColorAcento
import com.pmk.freeplayer.domain.model.config.CriterioOrdenamiento
import com.pmk.freeplayer.domain.model.config.DireccionOrdenamiento
import com.pmk.freeplayer.domain.model.config.IdiomaApp
import com.pmk.freeplayer.domain.model.config.ModoRepeticion
import com.pmk.freeplayer.domain.model.config.ModoTema
import com.pmk.freeplayer.domain.model.config.Ordenamiento
import com.pmk.freeplayer.domain.model.config.PresetEcualizador
import com.pmk.freeplayer.domain.model.config.TamanioFuente

data class TemporizadorSuenio(
   val estaActivo: Boolean,
   val minutosRestantes: Int,
   val finalizarAlTerminarCancion: Boolean,
) {
   companion object {
      val DESACTIVADO = TemporizadorSuenio(false, 0, false)
   }
}

data class UserPreferences(
	// === Visual ===
	val themeMode: ModoTema = ModoTema.SISTEMA,
	val accentColor: ColorAcento = ColorAcento.PREDETERMINADO,
	val useDynamicCoverColors: Boolean = true,
	
	// === Player ===
	val repeatMode: ModoRepeticion = ModoRepeticion.DESACTIVADO,
	val isShuffleEnabled: Boolean = false,
	val isGaplessPlaybackEnabled: Boolean = true,
	val crossfadeDurationSeconds: Int = 0,
	val resumeOnStart: Boolean = true,
	
	// === Audio ===
	val isEqEnabled: Boolean = false,
	val eqPreset: PresetEcualizador = PresetEcualizador.PLANO,
	val bassBoostLevel: Int = 0,        // 0-100
	val virtualizerLevel: Int = 0,      // 0-100
	val playbackSpeed: Float = 1.0f,
	val isNormalizationEnabled: Boolean = false,
	
	// === Library ===
	val sortOrder: Ordenamiento = Ordenamiento(direccion = DireccionOrdenamiento.ASCENDENTE, criterio = CriterioOrdenamiento.TITULO), // Tu clase envolvente
	val gridColumns: Int = 2,
	val showFolders: Boolean = true,
	val ignoredFolders: List<String> = emptyList(),
	val minDurationSeconds: Int = 30, // Filtro anti-audios de WhatsApp
	
	// === Lyrics ===
	val autoSearchLyrics: Boolean = true,
	val showTranslation: Boolean = false,
	val lyricsFontSize: TamanioFuente = TamanioFuente.MEDIANO,
	
	// === Notifications ===
	val showNotification: Boolean = true,
	val showLockScreenControls: Boolean = true,
	
	// === General ===
	val idiomaApp: IdiomaApp = IdiomaApp.SISTEMA,
	val sleepTimerMinutes: Int = 30
)