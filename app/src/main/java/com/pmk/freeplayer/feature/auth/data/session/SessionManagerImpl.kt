package com.pmk.freeplayer.feature.auth.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pmk.freeplayer.core.domain.session.SessionProvider  // FIX: from core, not feature/settings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore: DataStore<Preferences>
		by preferencesDataStore(name = "auth_session")

@Singleton
class SessionManagerImpl @Inject constructor(
	@ApplicationContext private val context: Context,
) : SessionManager, SessionProvider {
	
	private companion object {
		val KEY_USER_ID = longPreferencesKey("active_user_id")
		const val NO_SESSION = -1L
	}
	
	// FIX: in-memory cache so SessionProvider.currentUserId can be synchronous.
	// Initialized by reading DataStore on first access via initSession().
	// Updated immediately on saveSession() / clearSession() so all consumers
	// always see the correct value without suspending.
	private var _currentUserId: Long? = null
	
	// SessionProvider contract — consumed by feature/settings without suspending
	override val currentUserId: Long?
		get() = _currentUserId
	
	// ── SessionManager ────────────────────────────────────────────
	
	override fun getActiveUserId(): Flow<Long?> =
		context.sessionDataStore.data.map { prefs ->
			val id = prefs[KEY_USER_ID] ?: NO_SESSION
			if (id == NO_SESSION) null else id
		}
	
	override suspend fun saveSession(userId: Long) {
		context.sessionDataStore.edit { prefs ->
			prefs[KEY_USER_ID] = userId
		}
		_currentUserId = userId  // keep cache in sync
	}
	
	override suspend fun clearSession() {
		context.sessionDataStore.edit { prefs ->
			prefs.remove(KEY_USER_ID)
		}
		_currentUserId = null  // keep cache in sync
	}
	
	override suspend fun hasActiveSession(): Boolean =
		getActiveUserId().firstOrNull() != null
	
	/**
	 * Must be called once at app startup (e.g. from AppInitializer or MainActivity)
	 * to warm the in-memory cache from persisted DataStore state.
	 * Without this, currentUserId would be null after a cold start
	 * until saveSession() is called.
	 */
	suspend fun initSession() {
		_currentUserId = getActiveUserId().firstOrNull()
	}
}