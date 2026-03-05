package com.pmk.freeplayer.feature.settings.domain.repository

import com.pmk.freeplayer.core.domain.model.enums.AccentColor
import com.pmk.freeplayer.core.domain.model.enums.EqualizerPreset
import com.pmk.freeplayer.core.domain.model.enums.FontSize
import com.pmk.freeplayer.core.domain.model.enums.Language
import com.pmk.freeplayer.core.domain.model.enums.RepeatMode
import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.core.domain.model.enums.ThemeMode
import com.pmk.freeplayer.feature.settings.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
	
	fun getPreferences(): Flow<UserPreferences>
	
	// ── Appearance ────────────────────────────────────────────────
	suspend fun setThemeMode(mode: ThemeMode)
	suspend fun setAccentColor(color: AccentColor)
	suspend fun setUseDynamicCoverColors(enabled: Boolean)
	suspend fun setLanguage(language: Language)
	
	// ── Playback defaults ─────────────────────────────────────────
	suspend fun setDefaultRepeatMode(mode: RepeatMode)
	suspend fun setDefaultShuffleEnabled(enabled: Boolean)
	suspend fun setGaplessPlaybackEnabled(enabled: Boolean)
	suspend fun setCrossfadeDuration(seconds: Int)
	suspend fun setResumeOnStart(enabled: Boolean)
	
	// ── Audio effects ─────────────────────────────────────────────
	suspend fun setEqualizerEnabled(enabled: Boolean)
	suspend fun setEqualizerPreset(preset: EqualizerPreset)
	suspend fun setBassBoostLevel(level: Int)
	suspend fun setVirtualizerLevel(level: Int)
	suspend fun setPlaybackSpeed(speed: Float)
	suspend fun setNormalizationEnabled(enabled: Boolean)
	
	// ── Scanner ───────────────────────────────────────────────────
	suspend fun addExcludedPath(path: String)
	suspend fun removeExcludedPath(path: String)
	
	// ── Library ───────────────────────────────────────────────────
	suspend fun setSortConfig(sortConfig: SortConfig)
	suspend fun setGridColumns(columns: Int)
	suspend fun setMinDurationSeconds(seconds: Int)
	
	// ── Lyrics ────────────────────────────────────────────────────
	suspend fun setAutoSearchLyrics(enabled: Boolean)
	suspend fun setShowTranslation(enabled: Boolean)
	suspend fun setLyricsFontSize(size: FontSize)
	
	// ── Notifications ─────────────────────────────────────────────
	suspend fun setShowNotification(enabled: Boolean)
	suspend fun setShowLockScreenControls(enabled: Boolean)
	
	// ── Sleep timer ───────────────────────────────────────────────
	suspend fun setDefaultSleepTimerMinutes(minutes: Int)
	
	// ── Reset ─────────────────────────────────────────────────────
	suspend fun restoreDefaults()
}