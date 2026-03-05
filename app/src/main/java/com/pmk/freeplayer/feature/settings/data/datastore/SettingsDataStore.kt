package com.pmk.freeplayer.feature.settings.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.pmk.freeplayer.core.domain.model.enums.AccentColor
import com.pmk.freeplayer.core.domain.model.enums.EqualizerPreset
import com.pmk.freeplayer.core.domain.model.enums.FontSize
import com.pmk.freeplayer.core.domain.model.enums.Language
import com.pmk.freeplayer.core.domain.model.enums.RepeatMode
import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.core.domain.model.enums.SortDirection
import com.pmk.freeplayer.core.domain.model.enums.SortField
import com.pmk.freeplayer.core.domain.model.enums.ThemeMode
import com.pmk.freeplayer.core.domain.session.SessionProvider
import com.pmk.freeplayer.feature.settings.domain.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// FIX: removed top-level preferencesDataStore delegate — replaced by
// PreferenceDataStoreFactory to support dynamic names per userId.

@Singleton
class SettingsDataStore @Inject constructor(
	@ApplicationContext private val context: Context,
	private val sessionProvider: SessionProvider,  // FIX: injected from core
) {
	// ── Per-user DataStore ────────────────────────────────────────
	
	private val datastoreCache = mutableMapOf<String, DataStore<Preferences>>()
	
	private val activeDataStore: DataStore<Preferences>
		get() {
			val userId = sessionProvider.currentUserId?.toString() ?: "guest"
			return datastoreCache.getOrPut(userId) {
				PreferenceDataStoreFactory.create(
					produceFile = {
						context.preferencesDataStoreFile("settings_prefs_$userId")
					}
				)
			}
		}
	
	// ── Keys ──────────────────────────────────────────────────────
	
	private object Keys {
		val THEME_MODE                  = stringPreferencesKey("theme_mode")
		val ACCENT_COLOR                = stringPreferencesKey("accent_color")
		val USE_DYNAMIC_COVER_COLORS    = booleanPreferencesKey("use_dynamic_cover_colors")
		val LANGUAGE                    = stringPreferencesKey("language")
		val DEFAULT_REPEAT_MODE         = stringPreferencesKey("default_repeat_mode")
		val DEFAULT_SHUFFLE_ENABLED     = booleanPreferencesKey("default_shuffle_enabled")
		val GAPLESS_PLAYBACK_ENABLED    = booleanPreferencesKey("gapless_playback_enabled")
		val CROSSFADE_DURATION_SECONDS  = intPreferencesKey("crossfade_duration_seconds")
		val RESUME_ON_START             = booleanPreferencesKey("resume_on_start")
		val EQUALIZER_ENABLED           = booleanPreferencesKey("equalizer_enabled")
		val EQUALIZER_PRESET            = stringPreferencesKey("equalizer_preset")
		val BASS_BOOST_LEVEL            = intPreferencesKey("bass_boost_level")
		val VIRTUALIZER_LEVEL           = intPreferencesKey("virtualizer_level")
		val PLAYBACK_SPEED              = floatPreferencesKey("playback_speed")
		val NORMALIZATION_ENABLED       = booleanPreferencesKey("normalization_enabled")
		val SORT_FIELD                  = stringPreferencesKey("sort_field")
		val SORT_DIRECTION              = stringPreferencesKey("sort_direction")
		val GRID_COLUMNS                = intPreferencesKey("grid_columns")
		val MIN_DURATION_SECONDS        = intPreferencesKey("min_duration_seconds")
		val AUTO_SEARCH_LYRICS          = booleanPreferencesKey("auto_search_lyrics")
		val SHOW_TRANSLATION            = booleanPreferencesKey("show_translation")
		val LYRICS_FONT_SIZE            = stringPreferencesKey("lyrics_font_size")
		val SHOW_NOTIFICATION           = booleanPreferencesKey("show_notification")
		val SHOW_LOCK_SCREEN_CONTROLS   = booleanPreferencesKey("show_lock_screen_controls")
		val DEFAULT_SLEEP_TIMER_MINUTES = intPreferencesKey("default_sleep_timer_minutes")
	}
	
	// ── Read ──────────────────────────────────────────────────────
	
	// FIX: val → get() so each access resolves the current user's DataStore.
	// If it were a plain val, it would capture the DataStore of whoever was
	// active at construction time and never switch on session change.
	val preferences: Flow<UserPreferences>
		get() = activeDataStore.data.map { p ->
			val d = UserPreferences.DEFAULT
			UserPreferences(
				themeMode                = p[Keys.THEME_MODE]?.toEnum<ThemeMode>()             ?: d.themeMode,
				accentColor              = p[Keys.ACCENT_COLOR]?.toEnum<AccentColor>()         ?: d.accentColor,
				useDynamicCoverColors    = p[Keys.USE_DYNAMIC_COVER_COLORS]                    ?: d.useDynamicCoverColors,
				language                 = p[Keys.LANGUAGE]?.toEnum<Language>()               ?: d.language,
				defaultRepeatMode        = p[Keys.DEFAULT_REPEAT_MODE]?.toEnum<RepeatMode>()   ?: d.defaultRepeatMode,
				defaultShuffleEnabled    = p[Keys.DEFAULT_SHUFFLE_ENABLED]                     ?: d.defaultShuffleEnabled,
				gaplessPlaybackEnabled   = p[Keys.GAPLESS_PLAYBACK_ENABLED]                    ?: d.gaplessPlaybackEnabled,
				crossfadeDurationSeconds = p[Keys.CROSSFADE_DURATION_SECONDS]                  ?: d.crossfadeDurationSeconds,
				resumeOnStart            = p[Keys.RESUME_ON_START]                             ?: d.resumeOnStart,
				equalizerEnabled         = p[Keys.EQUALIZER_ENABLED]                           ?: d.equalizerEnabled,
				equalizerPreset          = p[Keys.EQUALIZER_PRESET]?.toEnum<EqualizerPreset>() ?: d.equalizerPreset,
				bassBoostLevel           = p[Keys.BASS_BOOST_LEVEL]                            ?: d.bassBoostLevel,
				virtualizerLevel         = p[Keys.VIRTUALIZER_LEVEL]                           ?: d.virtualizerLevel,
				playbackSpeed            = p[Keys.PLAYBACK_SPEED]                              ?: d.playbackSpeed,
				normalizationEnabled     = p[Keys.NORMALIZATION_ENABLED]                       ?: d.normalizationEnabled,
				sortConfig = SortConfig(
					field     = p[Keys.SORT_FIELD]?.toEnum<SortField>()         ?: d.sortConfig.field,
					direction = p[Keys.SORT_DIRECTION]?.toEnum<SortDirection>() ?: d.sortConfig.direction,
				),
				gridColumns              = p[Keys.GRID_COLUMNS]                                ?: d.gridColumns,
				minDurationSeconds       = p[Keys.MIN_DURATION_SECONDS]                        ?: d.minDurationSeconds,
				autoSearchLyrics         = p[Keys.AUTO_SEARCH_LYRICS]                          ?: d.autoSearchLyrics,
				showTranslation          = p[Keys.SHOW_TRANSLATION]                            ?: d.showTranslation,
				lyricsFontSize           = p[Keys.LYRICS_FONT_SIZE]?.toEnum<FontSize>()        ?: d.lyricsFontSize,
				showNotification         = p[Keys.SHOW_NOTIFICATION]                           ?: d.showNotification,
				showLockScreenControls   = p[Keys.SHOW_LOCK_SCREEN_CONTROLS]                   ?: d.showLockScreenControls,
				defaultSleepTimerMinutes = p[Keys.DEFAULT_SLEEP_TIMER_MINUTES]                 ?: d.defaultSleepTimerMinutes,
			)
		}
	
	// ── Appearance ────────────────────────────────────────────────
	
	suspend fun setThemeMode(mode: ThemeMode)        = edit { it[Keys.THEME_MODE] = mode.name }
	suspend fun setAccentColor(color: AccentColor)   = edit { it[Keys.ACCENT_COLOR] = color.name }
	suspend fun setUseDynamicCoverColors(v: Boolean) = edit { it[Keys.USE_DYNAMIC_COVER_COLORS] = v }
	suspend fun setLanguage(language: Language)      = edit { it[Keys.LANGUAGE] = language.name }
	
	// ── Playback ──────────────────────────────────────────────────
	
	suspend fun setDefaultRepeatMode(mode: RepeatMode) = edit { it[Keys.DEFAULT_REPEAT_MODE] = mode.name }
	suspend fun setDefaultShuffleEnabled(v: Boolean)   = edit { it[Keys.DEFAULT_SHUFFLE_ENABLED] = v }
	suspend fun setGaplessPlaybackEnabled(v: Boolean)  = edit { it[Keys.GAPLESS_PLAYBACK_ENABLED] = v }
	suspend fun setCrossfadeDuration(seconds: Int)     = edit { it[Keys.CROSSFADE_DURATION_SECONDS] = seconds }
	suspend fun setResumeOnStart(v: Boolean)           = edit { it[Keys.RESUME_ON_START] = v }
	
	// ── Audio effects ─────────────────────────────────────────────
	
	suspend fun setEqualizerEnabled(v: Boolean)        = edit { it[Keys.EQUALIZER_ENABLED] = v }
	suspend fun setEqualizerPreset(p: EqualizerPreset) = edit { it[Keys.EQUALIZER_PRESET] = p.name }
	suspend fun setBassBoostLevel(level: Int)          = edit { it[Keys.BASS_BOOST_LEVEL] = level.coerceIn(0, 100) }
	suspend fun setVirtualizerLevel(level: Int)        = edit { it[Keys.VIRTUALIZER_LEVEL] = level.coerceIn(0, 100) }
	suspend fun setPlaybackSpeed(speed: Float)         = edit { it[Keys.PLAYBACK_SPEED] = speed.coerceIn(0.5f, 2.0f) }
	suspend fun setNormalizationEnabled(v: Boolean)    = edit { it[Keys.NORMALIZATION_ENABLED] = v }
	
	// ── Library ───────────────────────────────────────────────────
	
	suspend fun setSortConfig(config: SortConfig) = edit {
		it[Keys.SORT_FIELD]     = config.field.name
		it[Keys.SORT_DIRECTION] = config.direction.name
	}
	suspend fun setGridColumns(columns: Int)          = edit { it[Keys.GRID_COLUMNS] = columns.coerceIn(1, 4) }
	suspend fun setMinDurationSeconds(seconds: Int)   = edit { it[Keys.MIN_DURATION_SECONDS] = seconds.coerceAtLeast(0) }
	
	// ── Lyrics ────────────────────────────────────────────────────
	
	suspend fun setAutoSearchLyrics(v: Boolean)   = edit { it[Keys.AUTO_SEARCH_LYRICS] = v }
	suspend fun setShowTranslation(v: Boolean)    = edit { it[Keys.SHOW_TRANSLATION] = v }
	suspend fun setLyricsFontSize(size: FontSize) = edit { it[Keys.LYRICS_FONT_SIZE] = size.name }
	
	// ── Notifications ─────────────────────────────────────────────
	
	suspend fun setShowNotification(v: Boolean)       = edit { it[Keys.SHOW_NOTIFICATION] = v }
	suspend fun setShowLockScreenControls(v: Boolean) = edit { it[Keys.SHOW_LOCK_SCREEN_CONTROLS] = v }
	
	// ── Sleep timer ───────────────────────────────────────────────
	
	suspend fun setDefaultSleepTimerMinutes(minutes: Int) =
		edit { it[Keys.DEFAULT_SLEEP_TIMER_MINUTES] = minutes.coerceAtLeast(1) }
	
	// ── Reset ─────────────────────────────────────────────────────
	
	suspend fun clear(): Unit {
		activeDataStore.edit { it.clear() }
	}
	
	// ── Internal ──────────────────────────────────────────────────
	
	private suspend fun edit(block: (MutablePreferences) -> Unit): Unit {
		activeDataStore.edit(block)
	}
	
	private inline fun <reified T : Enum<T>> String.toEnum(): T? =
		runCatching { enumValueOf<T>(this) }.getOrNull()
}