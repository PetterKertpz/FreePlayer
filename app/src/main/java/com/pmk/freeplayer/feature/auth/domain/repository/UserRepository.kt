package com.pmk.freeplayer.feature.auth.domain.repository

import com.pmk.freeplayer.feature.auth.domain.model.User
import com.pmk.freeplayer.feature.auth.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface UserRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// USER PROFILE
	// ═══════════════════════════════════════════════════════════════
	
	fun getCurrentUser(): Flow<User?>
	
	suspend fun updateUsername(username: String)
	suspend fun updateFullName(fullName: String?)
	suspend fun updateAvatar(uri: String?)
	
	/** Estadísticas calculadas desde [com.pmk.freeplayer.feature.player.domain.repository.PlayerRepository.getHistory] y cacheadas aquí. */
	fun getUserStats(): Flow<UserStats>
	
	/** Recalcula las estadísticas desde el historial de reproducción. */
	suspend fun refreshStats()
	
	suspend fun signOut()
	suspend fun deleteAccount()
}