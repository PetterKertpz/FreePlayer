package com.pmk.freeplayer.feature.settings.domain.model

import com.pmk.freeplayer.core.domain.model.enums.AccentColor
import com.pmk.freeplayer.core.domain.model.enums.EqualizerPreset
import com.pmk.freeplayer.core.domain.model.enums.FontSize
import com.pmk.freeplayer.core.domain.model.enums.Language
import com.pmk.freeplayer.core.domain.model.enums.RepeatMode
import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.core.domain.model.enums.SortDirection
import com.pmk.freeplayer.core.domain.model.enums.SortField
import com.pmk.freeplayer.core.domain.model.enums.ThemeMode

data class UserPreferences(
	// ── Appearance ────────────────────────────────────────────────
	val themeMode: ThemeMode            = ThemeMode.SYSTEM,
	val accentColor: AccentColor        = AccentColor.DEFAULT,
	val useDynamicCoverColors: Boolean  = true,
	val language: Language              = Language.SYSTEM,
	
	// ── Playback defaults ─────────────────────────────────────────
	val defaultRepeatMode: RepeatMode   = RepeatMode.OFF,
	val defaultShuffleEnabled: Boolean  = false,
	val gaplessPlaybackEnabled: Boolean = true,
	val crossfadeDurationSeconds: Int   = 0,
	val resumeOnStart: Boolean          = true,
	
	// ── Audio effects ─────────────────────────────────────────────
	val equalizerEnabled: Boolean         = false,
	val equalizerPreset: EqualizerPreset  = EqualizerPreset.FLAT,
	val bassBoostLevel: Int               = 0,
	val virtualizerLevel: Int             = 0,
	val playbackSpeed: Float              = 1.0f,
	val normalizationEnabled: Boolean     = false,
	
	// ── Library ───────────────────────────────────────────────────
	val sortConfig: SortConfig          = SortConfig(SortField.NAME, SortDirection.ASCENDING),
	val gridColumns: Int                = 2,
	val minDurationSeconds: Int         = 30,
	
	// ── Lyrics ────────────────────────────────────────────────────
	val autoSearchLyrics: Boolean       = true,
	val showTranslation: Boolean        = false,
	val lyricsFontSize: FontSize        = FontSize.MEDIUM,
	
	// ── Notifications ─────────────────────────────────────────────
	val showNotification: Boolean       = true,
	val showLockScreenControls: Boolean = true,
	
	// ── Sleep timer ───────────────────────────────────────────────
	val defaultSleepTimerMinutes: Int   = 30,
) {
	companion object {
		val DEFAULT = UserPreferences()
	}
}