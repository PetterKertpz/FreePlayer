package com.pmk.freeplayer.data.repository

import com.pmk.freeplayer.data.local.datastore.SettingsPreferences
import com.pmk.freeplayer.domain.model.enums.AccentColor
import com.pmk.freeplayer.domain.model.enums.EqualizerPreset
import com.pmk.freeplayer.domain.model.enums.FontSize
import com.pmk.freeplayer.domain.model.enums.Language
import com.pmk.freeplayer.domain.model.enums.RepeatMode
import com.pmk.freeplayer.domain.model.enums.SortConfig
import com.pmk.freeplayer.domain.model.enums.ThemeMode
import com.pmk.freeplayer.domain.model.preferences.UserPreferences
import com.pmk.freeplayer.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
	private val prefs: SettingsPreferences
) : SettingsRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// COMPLETE PREFERENCES
	// ═══════════════════════════════════════════════════════════════
	
	override fun getPreferences(): Flow<UserPreferences> = prefs.preferences
	
	// ═══════════════════════════════════════════════════════════════
	// APPEARANCE
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun setThemeMode(mode: ThemeMode) = prefs.setThemeMode(mode)
	override suspend fun setAccentColor(color: AccentColor) = prefs.setAccentColor(color)
	override suspend fun setUseDynamicCoverColors(enabled: Boolean) = prefs.setUseDynamicCoverColors(enabled)
	override suspend fun setLanguage(language: Language) = prefs.setLanguage(language)
	
	// ═══════════════════════════════════════════════════════════════
	// PLAYBACK DEFAULTS
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun setDefaultRepeatMode(mode: RepeatMode) = prefs.setDefaultRepeatMode(mode)
	override suspend fun setDefaultShuffleEnabled(enabled: Boolean) = prefs.setDefaultShuffleEnabled(enabled)
	override suspend fun setGaplessPlaybackEnabled(enabled: Boolean) = prefs.setGaplessPlaybackEnabled(enabled)
	override suspend fun setCrossfadeDuration(seconds: Int) = prefs.setCrossfadeDuration(seconds)
	override suspend fun setResumeOnStart(enabled: Boolean) = prefs.setResumeOnStart(enabled)
	
	// ═══════════════════════════════════════════════════════════════
	// AUDIO EFFECTS
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun setEqualizerEnabled(enabled: Boolean) = prefs.setEqualizerEnabled(enabled)
	override suspend fun setEqualizerPreset(preset: EqualizerPreset) = prefs.setEqualizerPreset(preset)
	override suspend fun setBassBoostLevel(level: Int) = prefs.setBassBoostLevel(level)
	override suspend fun setVirtualizerLevel(level: Int) = prefs.setVirtualizerLevel(level)
	override suspend fun setPlaybackSpeed(speed: Float) = prefs.setPlaybackSpeed(speed)
	override suspend fun setNormalizationEnabled(enabled: Boolean) = prefs.setNormalizationEnabled(enabled)
	
	// ═══════════════════════════════════════════════════════════════
	// LIBRARY
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun setSortConfig(sortConfig: SortConfig) = prefs.setSortConfig(sortConfig)
	override suspend fun setGridColumns(columns: Int) = prefs.setGridColumns(columns)
	override suspend fun setMinDurationSeconds(seconds: Int) = prefs.setMinDurationSeconds(seconds)
	
	// ═══════════════════════════════════════════════════════════════
	// LYRICS
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun setAutoSearchLyrics(enabled: Boolean) = prefs.setAutoSearchLyrics(enabled)
	override suspend fun setShowTranslation(enabled: Boolean) = prefs.setShowTranslation(enabled)
	override suspend fun setLyricsFontSize(size: FontSize) = prefs.setLyricsFontSize(size)
	
	// ═══════════════════════════════════════════════════════════════
	// NOTIFICATIONS
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun setShowNotification(enabled: Boolean) = prefs.setShowNotification(enabled)
	override suspend fun setShowLockScreenControls(enabled: Boolean) = prefs.setShowLockScreenControls(enabled)
	
	// ═══════════════════════════════════════════════════════════════
	// SLEEP TIMER DEFAULT
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun setDefaultSleepTimerMinutes(minutes: Int) = prefs.setDefaultSleepTimerMinutes(minutes)
	
	// ═══════════════════════════════════════════════════════════════
	// RESET
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun restoreDefaults() { prefs.clear() }
}