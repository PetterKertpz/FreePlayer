package com.pmk.freeplayer.data.repository

import com.pmk.freeplayer.data.local.dao.UserDao
import com.pmk.freeplayer.data.mapper.toDomain
import com.pmk.freeplayer.domain.model.User
import com.pmk.freeplayer.domain.model.UserStats
import com.pmk.freeplayer.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
	private val userDao: UserDao
) : UserRepository {
	
	// ID del usuario actual (single-user app)
	private val currentUserId: Long = 1L
	
	// ═══════════════════════════════════════════════════════════════
	// USER PROFILE
	// ═══════════════════════════════════════════════════════════════
	
	override fun getCurrentUser(): Flow<User?> =
		userDao.getUserByIdFlow(currentUserId).map { it?.toDomain() }
	
	override suspend fun updateUsername(username: String) {
		userDao.getUserById(currentUserId)?.let { entity ->
			userDao.update(entity.copy(username = username))
		}
	}
	
	override suspend fun updateFullName(fullName: String?) {
		userDao.getUserById(currentUserId)?.let { entity ->
			userDao.updateProfile(entity.userId, fullName, entity.avatarUri)
		}
	}
	
	override suspend fun updateAvatar(uri: String?) {
		userDao.getUserById(currentUserId)?.let { entity ->
			userDao.updateProfile(entity.userId, entity.fullName, uri)
		}
	}
	
	// ═══════════════════════════════════════════════════════════════
	// USER STATS
	// ═══════════════════════════════════════════════════════════════
	
	override fun getUserStats(): Flow<UserStats> =
		userDao.getUserByIdFlow(currentUserId).map { entity ->
			entity?.let {
				UserStats(
					playCount = it.totalPlays,
					favoriteCount = it.favoriteCount,
					playlistCount = it.playlistCount
				)
			} ?: UserStats(0, 0, 0)
		}
	
	override suspend fun refreshStats() {
		userDao.refreshUserStats(currentUserId)
	}
	
	// ═══════════════════════════════════════════════════════════════
	// AUTHENTICATION
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun signOut() {
		// Para single-user local: solo actualizar lastLogin o limpiar sesión
		userDao.updateLastLogin(currentUserId)
	}
	
	override suspend fun deleteAccount() {
		userDao.deleteById(currentUserId)
	}
}