package com.pmk.freeplayer.domain.useCase

import com.pmk.freeplayer.domain.model.enums.AccentColor
import com.pmk.freeplayer.domain.model.enums.EqualizerPreset
import com.pmk.freeplayer.domain.model.enums.Language
import com.pmk.freeplayer.domain.model.enums.RepeatMode
import com.pmk.freeplayer.domain.model.enums.ThemeMode
import com.pmk.freeplayer.domain.model.preferences.UserPreferences
import com.pmk.freeplayer.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserPreferencesUseCase @Inject constructor(
	private val repository: SettingsRepository
) {
	operator fun invoke(): Flow<UserPreferences> = repository.getPreferences()
}



class SetThemeModeUseCase @Inject constructor(
	private val repository: SettingsRepository
) {
	suspend operator fun invoke(mode: ThemeMode) = repository.setThemeMode(mode)
}

// domain/useCase/settings/appearance/SetAccentColorUseCase.kt
class SetAccentColorUseCase @Inject constructor(
	private val repository: SettingsRepository
) {
	suspend operator fun invoke(color: AccentColor) = repository.setAccentColor(color)
}

// domain/useCase/settings/appearance/SetLanguageUseCase.kt
class SetLanguageUseCase @Inject constructor(
	private val repository: SettingsRepository
) {
	suspend operator fun invoke(language: Language) = repository.setLanguage(language)
}



data class PlaybackSettings(
	val repeatMode: RepeatMode? = null,
	val shuffleEnabled: Boolean? = null,
	val gaplessEnabled: Boolean? = null,
	val crossfadeDuration: Int? = null,
	val resumeOnStart: Boolean? = null
)

class UpdatePlaybackSettingsUseCase @Inject constructor(
	private val repository: SettingsRepository
) {
	suspend operator fun invoke(settings: PlaybackSettings) {
		settings.repeatMode?.let { repository.setDefaultRepeatMode(it) }
		settings.shuffleEnabled?.let { repository.setDefaultShuffleEnabled(it) }
		settings.gaplessEnabled?.let { repository.setGaplessPlaybackEnabled(it) }
		settings.crossfadeDuration?.let { repository.setCrossfadeDuration(it) }
		settings.resumeOnStart?.let { repository.setResumeOnStart(it) }
	}
}


data class AudioEffects(
	val equalizerEnabled: Boolean? = null,
	val equalizerPreset: EqualizerPreset? = null,
	val bassBoostLevel: Int? = null,
	val virtualizerLevel: Int? = null,
	val playbackSpeed: Float? = null,
	val normalizationEnabled: Boolean? = null
)

class UpdateAudioEffectsUseCase @Inject constructor(
	private val repository: SettingsRepository
) {
	suspend operator fun invoke(effects: AudioEffects) {
		effects.equalizerEnabled?.let { repository.setEqualizerEnabled(it) }
		effects.equalizerPreset?.let { repository.setEqualizerPreset(it) }
		effects.bassBoostLevel?.let { repository.setBassBoostLevel(it) }
		effects.virtualizerLevel?.let { repository.setVirtualizerLevel(it) }
		effects.playbackSpeed?.let { repository.setPlaybackSpeed(it) }
		effects.normalizationEnabled?.let { repository.setNormalizationEnabled(it) }
	}
}

class RestoreDefaultSettingsUseCase @Inject constructor(
	private val repository: SettingsRepository
) {
	suspend operator fun invoke() = repository.restoreDefaults()
}