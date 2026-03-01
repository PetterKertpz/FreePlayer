package com.pmk.freeplayer.core.domain.usecase

// ─────────────────────────────────────────────────────────────────────────────
// ABSORBE (eliminados):
//   GetUserPreferencesUseCase, SetThemeModeUseCase, SetAccentColorUseCase,
//   SetLanguageUseCase, UpdatePlaybackSettingsUseCase,
//   UpdateAudioEffectsUseCase, RestoreDefaultSettingsUseCase
//
// CONSERVA: data classes PlaybackSettings y AudioEffects — el patrón de
//           actualización parcial en batch es bueno y se mantiene.
//           Se añade AppearanceSettings para simetría.
// ─────────────────────────────────────────────────────────────────────────────

import com.pmk.freeplayer.core.datastore.UserPreferences
import com.pmk.freeplayer.core.domain.model.enums.AccentColor
import com.pmk.freeplayer.core.domain.model.enums.EqualizerPreset
import com.pmk.freeplayer.core.domain.model.enums.FontSize
import com.pmk.freeplayer.core.domain.model.enums.Language
import com.pmk.freeplayer.core.domain.model.enums.RepeatMode
import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.core.domain.model.enums.ThemeMode
import com.pmk.freeplayer.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ═════════════════════════════════════════════════════════════════════════════
// PARÁMETROS — Batch update value objects (actualizaciones parciales)
// ═════════════════════════════════════════════════════════════════════════════

/** Actualización parcial de ajustes de apariencia. */
data class AppearanceSettings(
	val themeMode: ThemeMode? = null,
	val accentColor: AccentColor? = null,
	val dynamicCoverColors: Boolean? = null,
	val language: Language? = null,
)

/** Actualización parcial de ajustes de reproducción. */
data class PlaybackSettings(
	val repeatMode: RepeatMode? = null,
	val shuffleEnabled: Boolean? = null,
	val gaplessEnabled: Boolean? = null,
	val crossfadeDuration: Int? = null,
	val resumeOnStart: Boolean? = null,
)

/** Actualización parcial de efectos de audio. */
data class AudioEffects(
	val equalizerEnabled: Boolean? = null,
	val equalizerPreset: EqualizerPreset? = null,
	val bassBoostLevel: Int? = null,
	val virtualizerLevel: Int? = null,
	val playbackSpeed: Float? = null,
	val normalizationEnabled: Boolean? = null,
)

/** Actualización parcial de ajustes de biblioteca. */
data class LibrarySettings(
	val sortConfig: SortConfig? = null,
	val gridColumns: Int? = null,
	val minDurationSeconds: Int? = null,
)

/** Actualización parcial de ajustes de letras. */
data class LyricsSettings(
	val autoSearch: Boolean? = null,
	val showTranslation: Boolean? = null,
	val fontSize: FontSize? = null,
)

// ═════════════════════════════════════════════════════════════════════════════
// CONTENEDOR ÚNICO
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Acceso unificado a preferencias de usuario.
 *
 * Uso en ViewModel:
 * ```kotlin
 * // Observar preferencias completas
 * settingsUseCase()
 *
 * // Actualizaciones en batch (solo los campos no-null se aplican)
 * settingsUseCase.updateAppearance(AppearanceSettings(themeMode = DARK))
 * settingsUseCase.updatePlayback(PlaybackSettings(shuffleEnabled = true))
 * settingsUseCase.updateAudioEffects(AudioEffects(playbackSpeed = 1.25f))
 * settingsUseCase.updateLibrary(LibrarySettings(gridColumns = 3))
 * settingsUseCase.updateLyrics(LyricsSettings(autoSearch = false))
 *
 * // Notificaciones y Sleep Timer (simples, sin batch)
 * settingsUseCase.setShowNotification(true)
 * settingsUseCase.setDefaultSleepTimer(30)
 *
 * settingsUseCase.restoreDefaults()
 * ```
 */
class SettingsUseCase @Inject constructor(
	private val repository: SettingsRepository,
) {
	
	/** Emite las [UserPreferences] completas. Se reconstruye ante cualquier cambio. */
	operator fun invoke(): Flow<UserPreferences> = repository.getPreferences()
	
	// ─── Apariencia ───────────────────────────────────────────────
	
	suspend fun updateAppearance(settings: AppearanceSettings) {
		settings.themeMode?.let { repository.setThemeMode(it) }
		settings.accentColor?.let { repository.setAccentColor(it) }
		settings.dynamicCoverColors?.let { repository.setUseDynamicCoverColors(it) }
		settings.language?.let { repository.setLanguage(it) }
	}
	
	// ─── Reproducción ────────────────────────────────────────────
	
	suspend fun updatePlayback(settings: PlaybackSettings) {
		settings.repeatMode?.let { repository.setDefaultRepeatMode(it) }
		settings.shuffleEnabled?.let { repository.setDefaultShuffleEnabled(it) }
		settings.gaplessEnabled?.let { repository.setGaplessPlaybackEnabled(it) }
		settings.crossfadeDuration?.let { repository.setCrossfadeDuration(it) }
		settings.resumeOnStart?.let { repository.setResumeOnStart(it) }
	}
	
	// ─── Efectos de audio ─────────────────────────────────────────
	
	suspend fun updateAudioEffects(effects: AudioEffects) {
		effects.equalizerEnabled?.let { repository.setEqualizerEnabled(it) }
		effects.equalizerPreset?.let { repository.setEqualizerPreset(it) }
		effects.bassBoostLevel?.let { repository.setBassBoostLevel(it) }
		effects.virtualizerLevel?.let { repository.setVirtualizerLevel(it) }
		effects.playbackSpeed?.let { repository.setPlaybackSpeed(it) }
		effects.normalizationEnabled?.let { repository.setNormalizationEnabled(it) }
	}
	
	// ─── Biblioteca ───────────────────────────────────────────────
	
	suspend fun updateLibrary(settings: LibrarySettings) {
		settings.sortConfig?.let { repository.setSortConfig(it) }
		settings.gridColumns?.let { repository.setGridColumns(it) }
		settings.minDurationSeconds?.let { repository.setMinDurationSeconds(it) }
	}
	
	// ─── Letras ───────────────────────────────────────────────────
	
	suspend fun updateLyrics(settings: LyricsSettings) {
		settings.autoSearch?.let { repository.setAutoSearchLyrics(it) }
		settings.showTranslation?.let { repository.setShowTranslation(it) }
		settings.fontSize?.let { repository.setLyricsFontSize(it) }
	}
	
	// ─── Notificaciones ───────────────────────────────────────────
	
	suspend fun setShowNotification(enabled: Boolean) =
		repository.setShowNotification(enabled)
	
	suspend fun setShowLockScreenControls(enabled: Boolean) =
		repository.setShowLockScreenControls(enabled)
	
	// ─── Sleep Timer ──────────────────────────────────────────────
	
	suspend fun setDefaultSleepTimer(minutes: Int) =
		repository.setDefaultSleepTimerMinutes(minutes)
	
	// ─── Reset ────────────────────────────────────────────────────
	
	suspend fun restoreDefaults() = repository.restoreDefaults()
}