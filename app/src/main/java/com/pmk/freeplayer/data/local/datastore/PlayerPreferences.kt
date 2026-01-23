package com.pmk.freeplayer.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Extensión para crear el archivo de preferencias (como un SharedPreferences moderno)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "player_prefs")

class PlayerPreferences @Inject constructor(private val context: Context) {
	
	// Definimos las claves (Keys)
	companion object {
		val LAST_PLAYED_SONG_ID = longPreferencesKey("last_song_id")
		val LAST_POSITION_MS = longPreferencesKey("last_position_ms")
		val SHUFFLE_MODE = booleanPreferencesKey("shuffle_mode")
		val REPEAT_MODE = intPreferencesKey("repeat_mode") // 0: Off, 1: One, 2: All
		val VOLUME = floatPreferencesKey("volume")
		val PLAYBACK_SPEED = floatPreferencesKey("playback_speed")
	}
	
	// --- LEER DATOS (Flow = Actualización en tiempo real) ---
	val playerState: Flow<PlayerState> = context.dataStore.data.map { prefs ->
		PlayerState(
			lastSongId = prefs[LAST_PLAYED_SONG_ID] ?: -1L,
			lastPositionMs = prefs[LAST_POSITION_MS] ?: 0L,
			isShuffleEnabled = prefs[SHUFFLE_MODE] ?: false,
			repeatMode = prefs[REPEAT_MODE] ?: 0,
			volume = prefs[VOLUME] ?: 1.0f,
			speed = prefs[PLAYBACK_SPEED] ?: 1.0f
		)
	}
	
	// --- GUARDAR DATOS (Funciones suspendidas) ---
	
	suspend fun saveLastSong(songId: Long, positionMs: Long) {
		context.dataStore.edit { prefs ->
			prefs[LAST_PLAYED_SONG_ID] = songId
			prefs[LAST_POSITION_MS] = positionMs
		}
	}
	
	suspend fun toggleShuffle(isEnabled: Boolean) {
		context.dataStore.edit { prefs -> prefs[SHUFFLE_MODE] = isEnabled }
	}
	
	// ... resto de funciones de guardado ...
}

// Un modelo simple para transportar los datos (No es una Entity)
data class PlayerState(
	val lastSongId: Long,
	val lastPositionMs: Long,
	val isShuffleEnabled: Boolean,
	val repeatMode: Int,
	val volume: Float,
	val speed: Float
)