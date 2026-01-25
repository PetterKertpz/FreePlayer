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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Instancia única del DataStore ligada al contexto de la aplicación
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "player_prefs")

@Singleton // ✅ Recomendado: Solo debe haber una instancia de esto
class PlayerPreferences @Inject constructor(
	@ApplicationContext private val context: Context // ✅ Seguridad: Usa AppContext para evitar memory leaks
) {
	
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
	
	/** Guarda la última canción y posición al pausar o cerrar */
	suspend fun saveLastSong(songId: Long, positionMs: Long) {
		context.dataStore.edit { prefs ->
			prefs[LAST_PLAYED_SONG_ID] = songId
			prefs[LAST_POSITION_MS] = positionMs
		}
	}
	
	suspend fun toggleShuffle(isEnabled: Boolean) {
		context.dataStore.edit { prefs -> prefs[SHUFFLE_MODE] = isEnabled }
	}
	
	suspend fun setRepeatMode(mode: Int) {
		context.dataStore.edit { prefs -> prefs[REPEAT_MODE] = mode }
	}
	
	suspend fun setVolume(volume: Float) {
		context.dataStore.edit { prefs -> prefs[VOLUME] = volume }
	}
	
	suspend fun setPlaybackSpeed(speed: Float) {
		context.dataStore.edit { prefs -> prefs[PLAYBACK_SPEED] = speed }
	}
	
	// Función útil para resetear todo (ej: cerrar sesión)
	suspend fun clearState() {
		context.dataStore.edit { it.clear() }
	}
}

// Modelo de datos simple (DTO)
data class PlayerState(
	val lastSongId: Long,
	val lastPositionMs: Long,
	val isShuffleEnabled: Boolean,
	val repeatMode: Int,
	val volume: Float,
	val speed: Float
)