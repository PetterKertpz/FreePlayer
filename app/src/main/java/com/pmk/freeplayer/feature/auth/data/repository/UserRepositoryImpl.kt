package com.pmk.freeplayer.feature.auth.data.repository

import com.pmk.freeplayer.core.domain.model.enums.AuthType
import com.pmk.freeplayer.feature.auth.data.local.dao.UserDao
import com.pmk.freeplayer.feature.auth.data.mapper.buildExternalUserEntity
import com.pmk.freeplayer.feature.auth.data.mapper.buildLocalUserEntity
import com.pmk.freeplayer.feature.auth.data.mapper.toDomain
import com.pmk.freeplayer.feature.auth.data.mapper.withNewPassword
import com.pmk.freeplayer.feature.auth.data.mapper.withUpdatedLogin
import com.pmk.freeplayer.feature.auth.data.remote.OAuthManager
import com.pmk.freeplayer.feature.auth.data.remote.model.OAuthUser
import com.pmk.freeplayer.feature.auth.data.security.PasswordHasher
import com.pmk.freeplayer.feature.auth.data.session.SessionManager
import com.pmk.freeplayer.feature.auth.domain.model.User
import com.pmk.freeplayer.feature.auth.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de [UserRepository].
 *
 * Coordina:
 * - [UserDao]         → persistencia local (Room)
 * - [PasswordHasher]  → BCrypt para auth local
 * - [OAuthManager]    → verificación de tokens Google/Facebook vía Firebase
 * - [SessionManager]  → persistencia del userId activo entre reinicios
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
	private val userDao: UserDao,
	private val passwordHasher: PasswordHasher,
	private val oAuthManager: OAuthManager,
	private val sessionManager: SessionManager,
) : UserRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// SESIÓN
	// ═══════════════════════════════════════════════════════════════
	
	override fun getCurrentUser(): Flow<User?> =
		sessionManager.getActiveUserId().flatMapLatest { userId ->
			if (userId == null) flowOf(null)
			else userDao.getByIdFlow(userId).map { it?.toDomain() }
		}
	
	override suspend fun getCurrentUserOnce(): User? =
		sessionManager.getActiveUserId()
			.firstOrNull()
			?.let { userDao.getById(it)?.toDomain() }
	
	override suspend fun signOut() {
		currentUserId()?.let { userDao.updateLastLogin(it) }
		sessionManager.clearSession()
	}
	
	override suspend fun deleteAccount() {
		currentUserId()?.let { userDao.deleteById(it) }
		sessionManager.clearSession()
	}
	
	// ═══════════════════════════════════════════════════════════════
	// REGISTRO Y AUTENTICACIÓN LOCAL
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun registerLocal(
		username: String,
		email: String,
		password: String,
	): Result<User> = runCatching {
		if (userDao.existsEmail(email)) error("Email already registered")
		if (userDao.existsUsername(username)) error("Username already taken")
		
		val salt = passwordHasher.generateSalt()
		val hash = passwordHasher.hash(password, salt)
		
		val id = userDao.insert(buildLocalUserEntity(username, email, hash, salt))
		sessionManager.saveSession(id)
		userDao.getById(id)?.toDomain() ?: error("Failed to retrieve created user")
	}
	
	override suspend fun loginLocal(
		identifier: String,
		password: String,
	): Result<User> = runCatching {
		val entity = userDao.findForLogin(identifier) ?: error("User not found")
		val hash = entity.passwordHash ?: error("Account uses external authentication")
		val salt = entity.salt ?: error("Corrupted credentials")
		
		if (!passwordHasher.verify(password, hash, salt)) error("Invalid password")
		
		userDao.updateLastLogin(entity.userId)
		sessionManager.saveSession(entity.userId)
		entity.withUpdatedLogin().toDomain()
	}
	
	override suspend fun recoverPassword(email: String): Result<Unit> = runCatching {
		val entity = userDao.findByEmail(email) ?: error("Email not found")
		require(entity.authType == AuthType.LOCAL.name) {
			"Account uses external authentication — password recovery not available"
		}
		// TODO: generar token temporal → persistir → mostrar pantalla de reset
	}
	
	override suspend fun updatePassword(
		currentPassword: String,
		newPassword: String,
	): Result<Unit> = runCatching {
		val entity = userDao.getById(currentUserId() ?: error("No active session"))
			?: error("User not found")
		val hash = entity.passwordHash ?: error("Account uses external authentication")
		val salt = entity.salt ?: error("Corrupted credentials")
		
		if (!passwordHasher.verify(currentPassword, hash, salt)) error("Current password is incorrect")
		
		val newSalt = passwordHasher.generateSalt()
		val newHash = passwordHasher.hash(newPassword, newSalt)
		userDao.update(entity.withNewPassword(newHash, newSalt))
	}
	
	// ═══════════════════════════════════════════════════════════════
	// AUTENTICACIÓN EXTERNA
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun loginWithGoogle(activityContext: android.content.Context): Result<User> = runCatching {
		findOrCreateExternalUser(oAuthManager.launchGoogleSignIn(activityContext), AuthType.GOOGLE)
	}
	
	// ═══════════════════════════════════════════════════════════════
	// VALIDACIONES
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun isEmailTaken(email: String): Boolean =
		userDao.existsEmail(email)
	
	override suspend fun isUsernameTaken(username: String): Boolean =
		userDao.existsUsername(username)
	
	// ═══════════════════════════════════════════════════════════════
	// PERFIL
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun updateUsername(username: String) {
		currentUserId()?.let { userDao.updateUsername(it, username) }
	}
	
	override suspend fun updateFullName(fullName: String?) {
		val id = currentUserId() ?: return
		val entity = userDao.getById(id) ?: return
		userDao.updateProfile(id, fullName, entity.avatarUri)
	}
	
	override suspend fun updateAvatar(uri: String?) {
		val id = currentUserId() ?: return
		val entity = userDao.getById(id) ?: return
		userDao.updateProfile(id, entity.fullName, uri)
	}
	
	// ═══════════════════════════════════════════════════════════════
	// HELPERS PRIVADOS
	// ═══════════════════════════════════════════════════════════════
	
	private suspend fun currentUserId(): Long? =
		sessionManager.getActiveUserId().firstOrNull()
	
	private suspend fun findOrCreateExternalUser(
		oAuthUser: OAuthUser,
		authType: AuthType,
	): User {
		val existing = userDao.findByExternalId(oAuthUser.externalId, authType.name)
		if (existing != null) {
			userDao.updateLastLogin(existing.userId)
			sessionManager.saveSession(existing.userId)
			return existing.withUpdatedLogin().toDomain()
		}
		val id = userDao.insert(
			buildExternalUserEntity(
				username = resolveUniqueUsername(oAuthUser.email.substringBefore("@")),
				email = oAuthUser.email,
				authType = authType,
				externalId = oAuthUser.externalId,
				fullName = oAuthUser.fullName,
				avatarUri = oAuthUser.avatarUri,
			)
		)
		sessionManager.saveSession(id)
		return userDao.getById(id)?.toDomain() ?: error("Failed to retrieve created user")
	}
	
	private suspend fun resolveUniqueUsername(base: String): String {
		if (!userDao.existsUsername(base)) return base
		var counter = 2
		while (userDao.existsUsername("${base}_$counter")) counter++
		return "${base}_$counter"
	}
}