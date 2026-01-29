package com.pmk.freeplayer.domain.model.preferences

import com.pmk.freeplayer.domain.model.enums.AccentColor
import com.pmk.freeplayer.domain.model.enums.EqualizerPreset
import com.pmk.freeplayer.domain.model.enums.FontSize
import com.pmk.freeplayer.domain.model.enums.Language
import com.pmk.freeplayer.domain.model.enums.RepeatMode
import com.pmk.freeplayer.domain.model.enums.SortConfig
import com.pmk.freeplayer.domain.model.enums.SortDirection
import com.pmk.freeplayer.domain.model.enums.SortField
import com.pmk.freeplayer.domain.model.enums.ThemeMode

data class UserPreferences(
   // ═══════════════════════════════════════════════════════════════
   // APPEARANCE
   // ═══════════════════════════════════════════════════════════════
	val themeMode: ThemeMode = ThemeMode.SISTEMA,
	val accentColor: AccentColor = AccentColor.PREDETERMINADO,
	val useDynamicCoverColors: Boolean = true,
	val language: Language = Language.SISTEMA,

   // ═══════════════════════════════════════════════════════════════
   // PLAYBACK (Default values - runtime state lives in PlaybackState)
   // ═══════════════════════════════════════════════════════════════
	val defaultRepeatMode: RepeatMode = RepeatMode.OFF,
	val defaultShuffleEnabled: Boolean = false,
	val gaplessPlaybackEnabled: Boolean = true,
	val crossfadeDurationSeconds: Int = 0,
	val resumeOnStart: Boolean = true,

   // ═══════════════════════════════════════════════════════════════
   // AUDIO EFFECTS
   // ═══════════════════════════════════════════════════════════════
	val equalizerEnabled: Boolean = false,
	val equalizerPreset: EqualizerPreset = EqualizerPreset.PLANO,
	val bassBoostLevel: Int = 0, // 0-100
	val virtualizerLevel: Int = 0, // 0-100
	val playbackSpeed: Float = 1.0f,
	val normalizationEnabled: Boolean = false,

   // ═══════════════════════════════════════════════════════════════
   // LIBRARY
   // ═══════════════════════════════════════════════════════════════
	val sortConfig: SortConfig =
      SortConfig(field = SortField.NAME, direction = SortDirection.ASCENDING),
	val gridColumns: Int = 2,
	val minDurationSeconds: Int = 30, // Filter short audio clips

   // ═══════════════════════════════════════════════════════════════
   // LYRICS
   // ═══════════════════════════════════════════════════════════════
	val autoSearchLyrics: Boolean = true,
	val showTranslation: Boolean = false,
	val lyricsFontSize: FontSize = FontSize.MEDIANO,

   // ═══════════════════════════════════════════════════════════════
   // NOTIFICATIONS
   // ═══════════════════════════════════════════════════════════════
	val showNotification: Boolean = true,
	val showLockScreenControls: Boolean = true,

   // ═══════════════════════════════════════════════════════════════
   // SLEEP TIMER
   // ═══════════════════════════════════════════════════════════════
	val defaultSleepTimerMinutes: Int = 30,
) {
   companion object {
      val DEFAULT = UserPreferences()
   }
}
