package com.pmk.freeplayer.feature.metadata.data.remote.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pmk.freeplayer.feature.metadata.domain.model.MetadataConfig
import com.pmk.freeplayer.feature.settings.data.datastore.SettingsDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

// feature/metadata/data/datastore/MetadataConfigStore.kt

@Singleton
class MetadataConfigStore @Inject constructor(
	@ApplicationContext private val context: Context,
	private val settingsDataStore: SettingsDataStore,   // reutiliza el DataStore existente
) {
	// DataStore propio solo para claves sensibles/específicas de Metadata
	private val Context.metadataPrefs: DataStore<Preferences>
			by preferencesDataStore(name = "metadata_prefs")
	
	private object Keys {
		val GENIUS_ACCESS_TOKEN    = stringPreferencesKey("genius_access_token")
		val WRITE_METADATA_TO_FILE = booleanPreferencesKey("write_metadata_to_file")
	}
	
	// ── Flow reactivo (consumido por MetadataRepository) ─────────
	
	val config: Flow<MetadataConfig>
		get() = combine(
			context.metadataPrefs.data,
			settingsDataStore.preferences,   // ya contiene autoSearchLyrics
		) { metaPrefs, userPrefs ->
			MetadataConfig(
				writeMetadataToFile  = metaPrefs[Keys.WRITE_METADATA_TO_FILE] ?: false,
				geniusAccessToken    = metaPrefs[Keys.GENIUS_ACCESS_TOKEN]?.takeIf { it.isNotBlank() },
				similarityThreshold  = 0.75f,
				autoSearchEnabled    = userPrefs.autoSearchLyrics,  // delegado a Settings
			)
		}
	
	// ── Snapshot síncrono para el pipeline (sin suspender el Flow) ─
	
	suspend fun getCurrent(): MetadataConfig =
		config.first()
	
	// ── Escritura — llamadas desde SettingsViewModel ──────────────
	
	suspend fun setGeniusAccessToken(token: String) {
		context.metadataPrefs.edit { prefs ->
			if (token.isBlank()) prefs.remove(Keys.GENIUS_ACCESS_TOKEN)
			else prefs[Keys.GENIUS_ACCESS_TOKEN] = token.trim()
		}
	}
	
	suspend fun setWriteMetadataToFile(enabled: Boolean) {
		context.metadataPrefs.edit { it[Keys.WRITE_METADATA_TO_FILE] = enabled }
	}
	
	suspend fun clearGeniusToken() {
		context.metadataPrefs.edit { it.remove(Keys.GENIUS_ACCESS_TOKEN) }
	}
}