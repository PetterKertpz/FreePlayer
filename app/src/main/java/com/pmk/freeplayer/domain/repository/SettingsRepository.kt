package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.enums.AccentColor
import com.pmk.freeplayer.domain.model.enums.EqualizerPreset
import com.pmk.freeplayer.domain.model.enums.FontSize
import com.pmk.freeplayer.domain.model.enums.Language
import com.pmk.freeplayer.domain.model.enums.RepeatMode
import com.pmk.freeplayer.domain.model.enums.SortConfig
import com.pmk.freeplayer.domain.model.enums.ThemeMode
import com.pmk.freeplayer.domain.model.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// COMPLETE PREFERENCES
	// ═══════════════════════════════════════════════════════════════
	
	fun getPreferences(): Flow<UserPreferences>
	
	// ═══════════════════════════════════════════════════════════════
	// APPEARANCE
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun setThemeMode(mode: ThemeMode)
	suspend fun setAccentColor(color: AccentColor)
	suspend fun setUseDynamicCoverColors(enabled: Boolean)
	suspend fun setLanguage(language: Language)
	
	// ═══════════════════════════════════════════════════════════════
	// PLAYBACK DEFAULTS
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun setDefaultRepeatMode(mode: RepeatMode)
	suspend fun setDefaultShuffleEnabled(enabled: Boolean)
	suspend fun setGaplessPlaybackEnabled(enabled: Boolean)
	suspend fun setCrossfadeDuration(seconds: Int)
	suspend fun setResumeOnStart(enabled: Boolean)
	
	// ═══════════════════════════════════════════════════════════════
	// AUDIO EFFECTS
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun setEqualizerEnabled(enabled: Boolean)
	suspend fun setEqualizerPreset(preset: EqualizerPreset)
	suspend fun setBassBoostLevel(level: Int)
	suspend fun setVirtualizerLevel(level: Int)
	suspend fun setPlaybackSpeed(speed: Float)
	suspend fun setNormalizationEnabled(enabled: Boolean)
	
	// ═══════════════════════════════════════════════════════════════
	// LIBRARY
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun setSortConfig(sortConfig: SortConfig)
	suspend fun setGridColumns(columns: Int)
	suspend fun setMinDurationSeconds(seconds: Int)
	
	// ═══════════════════════════════════════════════════════════════
	// LYRICS
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun setAutoSearchLyrics(enabled: Boolean)
	suspend fun setShowTranslation(enabled: Boolean)
	suspend fun setLyricsFontSize(size: FontSize)
	
	// ═══════════════════════════════════════════════════════════════
	// NOTIFICATIONS
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun setShowNotification(enabled: Boolean)
	suspend fun setShowLockScreenControls(enabled: Boolean)
	
	// ═══════════════════════════════════════════════════════════════
	// SLEEP TIMER DEFAULT
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun setDefaultSleepTimerMinutes(minutes: Int)
	
	// ═══════════════════════════════════════════════════════════════
	// RESET
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun restoreDefaults()
}