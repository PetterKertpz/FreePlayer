package com.pmk.freeplayer.feature.settings.domain.usecase

import com.pmk.freeplayer.core.domain.model.enums.AccentColor
import com.pmk.freeplayer.core.domain.model.enums.EqualizerPreset
import com.pmk.freeplayer.core.domain.model.enums.FontSize
import com.pmk.freeplayer.core.domain.model.enums.Language
import com.pmk.freeplayer.core.domain.model.enums.RepeatMode
import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.core.domain.model.enums.ThemeMode
import com.pmk.freeplayer.feature.settings.domain.model.UserPreferences
import com.pmk.freeplayer.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ── Batch update params ───────────────────────────────────────────────────────

data class AppearanceSettings(
	val themeMode: ThemeMode?        = null,
	val accentColor: AccentColor?    = null,
	val dynamicCoverColors: Boolean? = null,
	val language: Language?          = null,
)

data class PlaybackSettings(
	val repeatMode: RepeatMode?    = null,
	val shuffleEnabled: Boolean?   = null,
	val gaplessEnabled: Boolean?   = null,
	val crossfadeDuration: Int?    = null,
	val resumeOnStart: Boolean?    = null,
)

data class AudioEffectsSettings(
	val equalizerEnabled: Boolean?       = null,
	val equalizerPreset: EqualizerPreset? = null,
	val bassBoostLevel: Int?             = null,
	val virtualizerLevel: Int?           = null,
	val playbackSpeed: Float?            = null,
	val normalizationEnabled: Boolean?   = null,
)

data class LibrarySettings(
	val sortConfig: SortConfig?      = null,
	val gridColumns: Int?            = null,
	val minDurationSeconds: Int?     = null,
)

data class LyricsSettings(
	val autoSearch: Boolean?       = null,
	val showTranslation: Boolean?  = null,
	val fontSize: FontSize?        = null,
)

// ── Query use case ────────────────────────────────────────────────────────────

class GetPreferencesUseCase @Inject constructor(
	private val repository: SettingsRepository,
) {
	operator fun invoke(): Flow<UserPreferences> = repository.getPreferences()
}

// ── Mutation use cases ────────────────────────────────────────────────────────

class UpdateAppearanceUseCase @Inject constructor(
	private val repository: SettingsRepository,
) {
	suspend operator fun invoke(settings: AppearanceSettings) {
		settings.themeMode?.let         { repository.setThemeMode(it) }
		settings.accentColor?.let       { repository.setAccentColor(it) }
		settings.dynamicCoverColors?.let { repository.setUseDynamicCoverColors(it) }
		settings.language?.let          { repository.setLanguage(it) }
	}
}

class UpdatePlaybackUseCase @Inject constructor(
	private val repository: SettingsRepository,
) {
	suspend operator fun invoke(settings: PlaybackSettings) {
		settings.repeatMode?.let      { repository.setDefaultRepeatMode(it) }
		settings.shuffleEnabled?.let  { repository.setDefaultShuffleEnabled(it) }
		settings.gaplessEnabled?.let  { repository.setGaplessPlaybackEnabled(it) }
		settings.crossfadeDuration?.let { repository.setCrossfadeDuration(it) }
		settings.resumeOnStart?.let   { repository.setResumeOnStart(it) }
	}
}

class UpdateAudioEffectsUseCase @Inject constructor(
	private val repository: SettingsRepository,
) {
	suspend operator fun invoke(settings: AudioEffectsSettings) {
		settings.equalizerEnabled?.let   { repository.setEqualizerEnabled(it) }
		settings.equalizerPreset?.let    { repository.setEqualizerPreset(it) }
		settings.bassBoostLevel?.let     { repository.setBassBoostLevel(it) }
		settings.virtualizerLevel?.let   { repository.setVirtualizerLevel(it) }
		settings.playbackSpeed?.let      { repository.setPlaybackSpeed(it) }
		settings.normalizationEnabled?.let { repository.setNormalizationEnabled(it) }
	}
}

class UpdateLibrarySettingsUseCase @Inject constructor(
	private val repository: SettingsRepository,
) {
	suspend operator fun invoke(settings: LibrarySettings) {
		settings.sortConfig?.let         { repository.setSortConfig(it) }
		settings.gridColumns?.let        { repository.setGridColumns(it) }
		settings.minDurationSeconds?.let { repository.setMinDurationSeconds(it) }
	}
}

class UpdateLyricsSettingsUseCase @Inject constructor(
	private val repository: SettingsRepository,
) {
	suspend operator fun invoke(settings: LyricsSettings) {
		settings.autoSearch?.let      { repository.setAutoSearchLyrics(it) }
		settings.showTranslation?.let { repository.setShowTranslation(it) }
		settings.fontSize?.let        { repository.setLyricsFontSize(it) }
	}
}

class SetNotificationSettingsUseCase @Inject constructor(
	private val repository: SettingsRepository,
) {
	suspend fun setShowNotification(enabled: Boolean)       = repository.setShowNotification(enabled)
	suspend fun setShowLockScreenControls(enabled: Boolean) = repository.setShowLockScreenControls(enabled)
}

class SetSleepTimerDefaultUseCase @Inject constructor(
	private val repository: SettingsRepository,
) {
	suspend operator fun invoke(minutes: Int) = repository.setDefaultSleepTimerMinutes(minutes)
}

class RestoreDefaultSettingsUseCase @Inject constructor(
	private val repository: SettingsRepository,
) {
	suspend operator fun invoke() = repository.restoreDefaults()
}