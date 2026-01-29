package com.pmk.freeplayer.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pmk.freeplayer.domain.model.enums.AccentColor
import com.pmk.freeplayer.domain.model.enums.EqualizerPreset
import com.pmk.freeplayer.domain.model.enums.FontSize
import com.pmk.freeplayer.domain.model.enums.Language
import com.pmk.freeplayer.domain.model.enums.RepeatMode
import com.pmk.freeplayer.domain.model.enums.SortConfig
import com.pmk.freeplayer.domain.model.enums.SortDirection
import com.pmk.freeplayer.domain.model.enums.SortField
import com.pmk.freeplayer.domain.model.enums.ThemeMode
import com.pmk.freeplayer.domain.model.preferences.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")

@Singleton
class SettingsPreferences @Inject constructor(
	@ApplicationContext private val context: Context
) {
	
	private object Keys {
		// Appearance
		val THEME_MODE = stringPreferencesKey("theme_mode")
		val ACCENT_COLOR = stringPreferencesKey("accent_color")
		val USE_DYNAMIC_COVER_COLORS = booleanPreferencesKey("use_dynamic_cover_colors")
		val LANGUAGE = stringPreferencesKey("language")
		
		// Playback
		val DEFAULT_REPEAT_MODE = stringPreferencesKey("default_repeat_mode")
		val DEFAULT_SHUFFLE_ENABLED = booleanPreferencesKey("default_shuffle_enabled")
		val GAPLESS_PLAYBACK_ENABLED = booleanPreferencesKey("gapless_playback_enabled")
		val CROSSFADE_DURATION_SECONDS = intPreferencesKey("crossfade_duration_seconds")
		val RESUME_ON_START = booleanPreferencesKey("resume_on_start")
		
		// Audio Effects
		val EQUALIZER_ENABLED = booleanPreferencesKey("equalizer_enabled")
		val EQUALIZER_PRESET = stringPreferencesKey("equalizer_preset")
		val BASS_BOOST_LEVEL = intPreferencesKey("bass_boost_level")
		val VIRTUALIZER_LEVEL = intPreferencesKey("virtualizer_level")
		val PLAYBACK_SPEED = floatPreferencesKey("playback_speed")
		val NORMALIZATION_ENABLED = booleanPreferencesKey("normalization_enabled")
		
		// Library
		val SORT_FIELD = stringPreferencesKey("sort_field")
		val SORT_DIRECTION = stringPreferencesKey("sort_direction")
		val GRID_COLUMNS = intPreferencesKey("grid_columns")
		val MIN_DURATION_SECONDS = intPreferencesKey("min_duration_seconds")
		
		// Lyrics
		val AUTO_SEARCH_LYRICS = booleanPreferencesKey("auto_search_lyrics")
		val SHOW_TRANSLATION = booleanPreferencesKey("show_translation")
		val LYRICS_FONT_SIZE = stringPreferencesKey("lyrics_font_size")
		
		// Notifications
		val SHOW_NOTIFICATION = booleanPreferencesKey("show_notification")
		val SHOW_LOCK_SCREEN_CONTROLS = booleanPreferencesKey("show_lock_screen_controls")
		
		// Sleep Timer
		val DEFAULT_SLEEP_TIMER_MINUTES = intPreferencesKey("default_sleep_timer_minutes")
	}
	
	val preferences: Flow<UserPreferences> = context.settingsDataStore.data.map { prefs ->
		val defaults = UserPreferences.DEFAULT
		UserPreferences(
			// Appearance
			themeMode = prefs[Keys.THEME_MODE]?.toEnum<ThemeMode>() ?: defaults.themeMode,
			accentColor = prefs[Keys.ACCENT_COLOR]?.toEnum<AccentColor>() ?: defaults.accentColor,
			useDynamicCoverColors = prefs[Keys.USE_DYNAMIC_COVER_COLORS] ?: defaults.useDynamicCoverColors,
			language = prefs[Keys.LANGUAGE]?.toEnum<Language>() ?: defaults.language,
			
			// Playback
			defaultRepeatMode = prefs[Keys.DEFAULT_REPEAT_MODE]?.toEnum<RepeatMode>() ?: defaults.defaultRepeatMode,
			defaultShuffleEnabled = prefs[Keys.DEFAULT_SHUFFLE_ENABLED] ?: defaults.defaultShuffleEnabled,
			gaplessPlaybackEnabled = prefs[Keys.GAPLESS_PLAYBACK_ENABLED] ?: defaults.gaplessPlaybackEnabled,
			crossfadeDurationSeconds = prefs[Keys.CROSSFADE_DURATION_SECONDS] ?: defaults.crossfadeDurationSeconds,
			resumeOnStart = prefs[Keys.RESUME_ON_START] ?: defaults.resumeOnStart,
			
			// Audio Effects
			equalizerEnabled = prefs[Keys.EQUALIZER_ENABLED] ?: defaults.equalizerEnabled,
			equalizerPreset = prefs[Keys.EQUALIZER_PRESET]?.toEnum<EqualizerPreset>() ?: defaults.equalizerPreset,
			bassBoostLevel = prefs[Keys.BASS_BOOST_LEVEL] ?: defaults.bassBoostLevel,
			virtualizerLevel = prefs[Keys.VIRTUALIZER_LEVEL] ?: defaults.virtualizerLevel,
			playbackSpeed = prefs[Keys.PLAYBACK_SPEED] ?: defaults.playbackSpeed,
			normalizationEnabled = prefs[Keys.NORMALIZATION_ENABLED] ?: defaults.normalizationEnabled,
			
			// Library
			sortConfig = SortConfig(
				field = prefs[Keys.SORT_FIELD]?.toEnum<SortField>() ?: defaults.sortConfig.field,
				direction = prefs[Keys.SORT_DIRECTION]?.toEnum<SortDirection>() ?: defaults.sortConfig.direction
			),
			gridColumns = prefs[Keys.GRID_COLUMNS] ?: defaults.gridColumns,
			minDurationSeconds = prefs[Keys.MIN_DURATION_SECONDS] ?: defaults.minDurationSeconds,
			
			// Lyrics
			autoSearchLyrics = prefs[Keys.AUTO_SEARCH_LYRICS] ?: defaults.autoSearchLyrics,
			showTranslation = prefs[Keys.SHOW_TRANSLATION] ?: defaults.showTranslation,
			lyricsFontSize = prefs[Keys.LYRICS_FONT_SIZE]?.toEnum<FontSize>() ?: defaults.lyricsFontSize,
			
			// Notifications
			showNotification = prefs[Keys.SHOW_NOTIFICATION] ?: defaults.showNotification,
			showLockScreenControls = prefs[Keys.SHOW_LOCK_SCREEN_CONTROLS] ?: defaults.showLockScreenControls,
			
			// Sleep Timer
			defaultSleepTimerMinutes = prefs[Keys.DEFAULT_SLEEP_TIMER_MINUTES] ?: defaults.defaultSleepTimerMinutes
		)
	}
	
	// ═══════════════════════════════════════════════════════════════
	// APPEARANCE
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun setThemeMode(mode: ThemeMode) = edit { it[Keys.THEME_MODE] = mode.name }
	suspend fun setAccentColor(color: AccentColor) = edit { it[Keys.ACCENT_COLOR] = color.name }
	suspend fun setUseDynamicCoverColors(enabled: Boolean) = edit { it[Keys.USE_DYNAMIC_COVER_COLORS] = enabled }
	suspend fun setLanguage(language: Language) = edit { it[Keys.LANGUAGE] = language.name }
	
	// ═══════════════════════════════════════════════════════════════
	// PLAYBACK
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun setDefaultRepeatMode(mode: RepeatMode) = edit { it[Keys.DEFAULT_REPEAT_MODE] = mode.name }
	suspend fun setDefaultShuffleEnabled(enabled: Boolean) = edit { it[Keys.DEFAULT_SHUFFLE_ENABLED] = enabled }
	suspend fun setGaplessPlaybackEnabled(enabled: Boolean) = edit { it[Keys.GAPLESS_PLAYBACK_ENABLED] = enabled }
	suspend fun setCrossfadeDuration(seconds: Int) = edit { it[Keys.CROSSFADE_DURATION_SECONDS] = seconds }
	suspend fun setResumeOnStart(enabled: Boolean) = edit { it[Keys.RESUME_ON_START] = enabled }
	
	// ═══════════════════════════════════════════════════════════════
	// AUDIO EFFECTS
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun setEqualizerEnabled(enabled: Boolean) = edit { it[Keys.EQUALIZER_ENABLED] = enabled }
	suspend fun setEqualizerPreset(preset: EqualizerPreset) = edit { it[Keys.EQUALIZER_PRESET] = preset.name }
	suspend fun setBassBoostLevel(level: Int) = edit { it[Keys.BASS_BOOST_LEVEL] = level.coerceIn(0, 100) }
	suspend fun setVirtualizerLevel(level: Int) = edit { it[Keys.VIRTUALIZER_LEVEL] = level.coerceIn(0, 100) }
	suspend fun setPlaybackSpeed(speed: Float) = edit { it[Keys.PLAYBACK_SPEED] = speed.coerceIn(0.5f, 2.0f) }
	suspend fun setNormalizationEnabled(enabled: Boolean) = edit { it[Keys.NORMALIZATION_ENABLED] = enabled }
	
	// ═══════════════════════════════════════════════════════════════
	// LIBRARY
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun setSortConfig(config: SortConfig) = edit {
		it[Keys.SORT_FIELD] = config.field.name
		it[Keys.SORT_DIRECTION] = config.direction.name
	}
	suspend fun setGridColumns(columns: Int) = edit { it[Keys.GRID_COLUMNS] = columns.coerceIn(1, 4) }
	suspend fun setMinDurationSeconds(seconds: Int) = edit { it[Keys.MIN_DURATION_SECONDS] = seconds.coerceAtLeast(0) }
	
	// ═══════════════════════════════════════════════════════════════
	// LYRICS
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun setAutoSearchLyrics(enabled: Boolean) = edit { it[Keys.AUTO_SEARCH_LYRICS] = enabled }
	suspend fun setShowTranslation(enabled: Boolean) = edit { it[Keys.SHOW_TRANSLATION] = enabled }
	suspend fun setLyricsFontSize(size: FontSize) = edit { it[Keys.LYRICS_FONT_SIZE] = size.name }
	
	// ═══════════════════════════════════════════════════════════════
	// NOTIFICATIONS
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun setShowNotification(enabled: Boolean) = edit { it[Keys.SHOW_NOTIFICATION] = enabled }
	suspend fun setShowLockScreenControls(enabled: Boolean) = edit { it[Keys.SHOW_LOCK_SCREEN_CONTROLS] = enabled }
	
	// ═══════════════════════════════════════════════════════════════
	// SLEEP TIMER
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun setDefaultSleepTimerMinutes(minutes: Int) = edit { it[Keys.DEFAULT_SLEEP_TIMER_MINUTES] = minutes.coerceAtLeast(1) }
	
	// ═══════════════════════════════════════════════════════════════
	// RESET
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun clear() = context.settingsDataStore.edit { it.clear() }
	
	private suspend fun edit(block: (MutablePreferences) -> Unit) {
		context.settingsDataStore.edit(block)
	}
	
	private inline fun <reified T : Enum<T>> String.toEnum(): T? =
		runCatching { enumValueOf<T>(this) }.getOrNull()
}