package com.pmk.freeplayer.feature.auth.data.session

import kotlinx.coroutines.flow.Flow

/**
 * Gestiona la persistencia de la sesión activa entre reinicios de la app.
 *
 * Responsabilidad: recordar qué userId está activo para que
 * [UserRepositoryImpl] sepa a qué registro apuntar sin pedir login
 * cada vez que la app abre.
 */
interface SessionManager {
	/** Emite el userId activo o null si no hay sesión. */
	fun getActiveUserId(): Flow<Long?>
	
	/** Persiste el userId tras un login exitoso. */
	suspend fun saveSession(userId: Long)
	
	/** Limpia la sesión activa (logout). */
	suspend fun clearSession()
	
	/** Retorna true si existe una sesión activa. */
	suspend fun hasActiveSession(): Boolean
}