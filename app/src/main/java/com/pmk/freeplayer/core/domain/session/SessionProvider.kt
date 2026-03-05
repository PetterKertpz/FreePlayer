package com.pmk.freeplayer.core.domain.session

/**
 * Exposes the active user identity to any feature
 * without creating a direct dependency on feature/auth.
 *
 * feature/auth     → implements SessionProvider
 * feature/settings → consumes SessionProvider
 */
interface SessionProvider {
	/** Active userId, or null if no session exists. */
	val currentUserId: Long?
}