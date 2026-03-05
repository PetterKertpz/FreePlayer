package com.pmk.freeplayer.feature.settings.data.repository

import com.pmk.freeplayer.core.domain.model.enums.AccentColor
import com.pmk.freeplayer.core.domain.model.enums.EqualizerPreset
import com.pmk.freeplayer.core.domain.model.enums.FontSize
import com.pmk.freeplayer.core.domain.model.enums.Language
import com.pmk.freeplayer.core.domain.model.enums.RepeatMode
import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.core.domain.model.enums.ThemeMode
import com.pmk.freeplayer.feature.settings.data.datastore.SettingsDataStore
import com.pmk.freeplayer.feature.settings.domain.model.UserPreferences
import com.pmk.freeplayer.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(private val dataStore: SettingsDataStore) :
   SettingsRepository {

   override fun getPreferences(): Flow<UserPreferences> = dataStore.preferences

   // ── Appearance ────────────────────────────────────────────────
   override suspend fun setThemeMode(mode: ThemeMode) = dataStore.setThemeMode(mode)

   override suspend fun setAccentColor(color: AccentColor) = dataStore.setAccentColor(color)

   override suspend fun setUseDynamicCoverColors(v: Boolean) = dataStore.setUseDynamicCoverColors(v)

   override suspend fun setLanguage(language: Language) = dataStore.setLanguage(language)

   // ── Playback defaults ─────────────────────────────────────────
   override suspend fun setDefaultRepeatMode(mode: RepeatMode) =
      dataStore.setDefaultRepeatMode(mode)

   override suspend fun setDefaultShuffleEnabled(v: Boolean) = dataStore.setDefaultShuffleEnabled(v)

   override suspend fun setGaplessPlaybackEnabled(v: Boolean) =
      dataStore.setGaplessPlaybackEnabled(v)

   override suspend fun setCrossfadeDuration(seconds: Int) = dataStore.setCrossfadeDuration(seconds)

   override suspend fun setResumeOnStart(v: Boolean) = dataStore.setResumeOnStart(v)

   // ── Audio effects ─────────────────────────────────────────────
   override suspend fun setEqualizerEnabled(v: Boolean) = dataStore.setEqualizerEnabled(v)

   override suspend fun setEqualizerPreset(p: EqualizerPreset) = dataStore.setEqualizerPreset(p)

   override suspend fun setBassBoostLevel(level: Int) = dataStore.setBassBoostLevel(level)

   override suspend fun setVirtualizerLevel(level: Int) = dataStore.setVirtualizerLevel(level)

   override suspend fun setPlaybackSpeed(speed: Float) = dataStore.setPlaybackSpeed(speed)

   override suspend fun setNormalizationEnabled(v: Boolean) = dataStore.setNormalizationEnabled(v)

   // ── Library ───────────────────────────────────────────────────
   override suspend fun setSortConfig(config: SortConfig) = dataStore.setSortConfig(config)

   override suspend fun setGridColumns(columns: Int) = dataStore.setGridColumns(columns)

   override suspend fun setMinDurationSeconds(seconds: Int) =
      dataStore.setMinDurationSeconds(seconds)

   // ── Lyrics ────────────────────────────────────────────────────
   override suspend fun setAutoSearchLyrics(v: Boolean) = dataStore.setAutoSearchLyrics(v)

   override suspend fun setShowTranslation(v: Boolean) = dataStore.setShowTranslation(v)

   override suspend fun setLyricsFontSize(size: FontSize) = dataStore.setLyricsFontSize(size)

   // ── Notifications ─────────────────────────────────────────────
   override suspend fun setShowNotification(v: Boolean) = dataStore.setShowNotification(v)

   override suspend fun setShowLockScreenControls(v: Boolean) =
      dataStore.setShowLockScreenControls(v)

   // ── Sleep timer ───────────────────────────────────────────────
   override suspend fun setDefaultSleepTimerMinutes(minutes: Int) =
      dataStore.setDefaultSleepTimerMinutes(minutes)

   // ── Reset ─────────────────────────────────────────────────────
   override suspend fun restoreDefaults(): Unit {
      dataStore.clear()
   }
}
