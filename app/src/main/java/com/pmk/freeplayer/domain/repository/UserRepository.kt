package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.User
import com.pmk.freeplayer.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface UserRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// USER PROFILE
	// ═══════════════════════════════════════════════════════════════
	
	fun getCurrentUser(): Flow<User?>
	
	suspend fun updateUsername(username: String)
	suspend fun updateFullName(fullName: String?)
	suspend fun updateAvatar(uri: String?)
	
	// ═══════════════════════════════════════════════════════════════
	// USER STATS (Computed from PlaybackHistory, cached here)
	// ═══════════════════════════════════════════════════════════════
	
	fun getUserStats(): Flow<UserStats>
	
	/**
	 * Recalculate stats from PlaybackHistory
	 * Called periodically or on demand
	 */
	suspend fun refreshStats()
	
	// ═══════════════════════════════════════════════════════════════
	// AUTHENTICATION (If needed in future)
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun signOut()
	suspend fun deleteAccount()
}