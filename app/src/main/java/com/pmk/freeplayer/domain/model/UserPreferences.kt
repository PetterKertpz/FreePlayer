package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.enums.AccentColor
import com.pmk.freeplayer.domain.model.enums.SortOrder
import com.pmk.freeplayer.domain.model.enums.SortDirection
import com.pmk.freeplayer.domain.model.enums.Language
import com.pmk.freeplayer.domain.model.enums.RepeatMode
import com.pmk.freeplayer.domain.model.enums.ThemeMode
import com.pmk.freeplayer.domain.model.enums.SortConfiguration
import com.pmk.freeplayer.domain.model.enums.EqualizerPreset
import com.pmk.freeplayer.domain.model.enums.FontSize

data class UserPreferences(
	// === Visual ===
	val themeMode: ThemeMode = ThemeMode.SISTEMA,
	val accentColor: AccentColor = AccentColor.PREDETERMINADO,
	val useDynamicCoverColors: Boolean = true,
	
	// === Player ===
	val repeatMode: RepeatMode = RepeatMode.DESACTIVADO,
	val isShuffleEnabled: Boolean = false,
	val isGaplessPlaybackEnabled: Boolean = true,
	val crossfadeDurationSeconds: Int = 0,
	val resumeOnStart: Boolean = true,
	
	// === enums ===
	val isEqEnabled: Boolean = false,
	val eqPreset: EqualizerPreset = EqualizerPreset.PLANO,
	val bassBoostLevel: Int = 0,        // 0-100
	val virtualizerLevel: Int = 0,      // 0-100
	val playbackSpeed: Float = 1.0f,
	val isNormalizationEnabled: Boolean = false,
	
	// === Library ===
	val sortOrder: SortConfiguration = SortConfiguration(direccion = SortDirection.ASCENDENTE, criterio = SortOrder.TITULO), // Tu clase envolvente
	val gridColumns: Int = 2,
	val showFolders: Boolean = true,
	val ignoredFolders: List<String> = emptyList(),
	val minDurationSeconds: Int = 30, // Filtro anti-audios de WhatsApp
	
	// === Lyrics ===
	val autoSearchLyrics: Boolean = true,
	val showTranslation: Boolean = false,
	val lyricsFontSize: FontSize = FontSize.MEDIANO,
	
	// === Notifications ===
	val showNotification: Boolean = true,
	val showLockScreenControls: Boolean = true,
	
	// === General ===
	val language: Language = Language.SISTEMA,
	val sleepTimerMinutes: Int = 30
)