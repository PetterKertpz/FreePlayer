package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.UserPreferences
import com.pmk.freeplayer.domain.model.enums.AccentColor
import com.pmk.freeplayer.domain.model.enums.EqualizerPreset
import com.pmk.freeplayer.domain.model.enums.FontSize
import com.pmk.freeplayer.domain.model.enums.Language
import com.pmk.freeplayer.domain.model.enums.RepeatMode
import com.pmk.freeplayer.domain.model.enums.SortConfiguration
import com.pmk.freeplayer.domain.model.enums.ThemeMode
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Get complete preferences
	// ─────────────────────────────────────────────────────────────
	fun getPreferences(): Flow<UserPreferences>
	
	// ─────────────────────────────────────────────────────────────
	// Appearance
	// ─────────────────────────────────────────────────────────────
	suspend fun setThemeMode(mode: ThemeMode)
	
	suspend fun setAccentColor(color: AccentColor)
	
	suspend fun setUseCoverColors(use: Boolean)
	
	// ─────────────────────────────────────────────────────────────
	// Player
	// ─────────────────────────────────────────────────────────────
	suspend fun setRepeatMode(mode: RepeatMode)
	
	suspend fun setShuffle(enabled: Boolean)
	
	suspend fun setGaplessPlayback(enabled: Boolean)
	
	suspend fun setCrossfadeDuration(seconds: Int)
	
	suspend fun setResumeOnStart(enabled: Boolean)
	
	// ─────────────────────────────────────────────────────────────
	// Audio
	// ─────────────────────────────────────────────────────────────
	suspend fun setEqualizerEnabled(enabled: Boolean)
	
	suspend fun setEqualizerPreset(preset: EqualizerPreset)
	
	suspend fun setBassLevel(level: Int)
	
	suspend fun setVirtualizerLevel(level: Int)
	
	suspend fun setPlaybackSpeed(speed: Float)
	
	suspend fun setAudioNormalization(enabled: Boolean)
	
	// ─────────────────────────────────────────────────────────────
	// Library
	// ─────────────────────────────────────────────────────────────
	suspend fun setSortOrder(sortOrder: SortConfiguration)
	
	suspend fun setGridColumns(columns: Int)
	
	suspend fun setShowFolders(show: Boolean)
	
	suspend fun setIgnoredFolders(folders: List<String>)
	
	suspend fun setMinimumDuration(seconds: Int)
	
	// ─────────────────────────────────────────────────────────────
	// Lyrics
	// ─────────────────────────────────────────────────────────────
	suspend fun setAutoSearchLyrics(enabled: Boolean)
	
	suspend fun setShowTranslation(show: Boolean)
	
	suspend fun setLyricsFontSize(size: FontSize)
	
	// ─────────────────────────────────────────────────────────────
	// Notifications
	// ─────────────────────────────────────────────────────────────
	suspend fun setShowNotification(show: Boolean)
	
	suspend fun setLockScreenControls(show: Boolean)
	
	// ─────────────────────────────────────────────────────────────
	// General
	// ─────────────────────────────────────────────────────────────
	suspend fun setLanguage(language: Language)
	
	suspend fun setDefaultSleepTimer(minutes: Int)
	
	// ─────────────────────────────────────────────────────────────
	// Reset preferences
	// ─────────────────────────────────────────────────────────────
	suspend fun restoreDefaults()
}