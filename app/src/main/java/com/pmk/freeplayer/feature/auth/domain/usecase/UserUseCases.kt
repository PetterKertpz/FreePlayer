package com.pmk.freeplayer.feature.auth.domain.usecase

import com.pmk.freeplayer.feature.auth.domain.model.User
import com.pmk.freeplayer.feature.auth.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ═════════════════════════════════════════════════════════════════════════════
// PARÁMETROS
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Actualización parcial del perfil.
 * Los campos `null` se ignoran — no sobreescriben el valor existente.
 * Para borrar un campo nullable, pasar una cadena vacía `""`.
 */
data class UserProfileUpdate(
	val username: String? = null,
	val fullName: String? = null,
	val avatarUri: String? = null,
)

// ═════════════════════════════════════════════════════════════════════════════
// GET — Consultas de sesión y perfil
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Consultas del usuario activo.
 *
 * Uso en ViewModel:
 * ```kotlin
 * getUserUseCase()                // Flow<User?> — para observar sesión
 * getUserUseCase.once()           // User? — para validaciones puntuales
 * ```
 */
class GetUserUseCase @Inject constructor(
	private val repository: UserRepository,
) {
	/** Observa el usuario activo de forma reactiva. */
	operator fun invoke(): Flow<User?> = repository.getCurrentUser()
	
	/** Obtiene el usuario activo una sola vez. */
	suspend fun once(): User? = repository.getCurrentUserOnce()
}

// ═════════════════════════════════════════════════════════════════════════════
// AUTH — Registro, login y recuperación
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Operaciones de autenticación: registro, login y recuperación de contraseña.
 *
 * Uso en ViewModel:
 * ```kotlin
 * authUseCase.registerLocal("user", "email@test.com", "pass")
 * authUseCase.loginLocal("email@test.com", "pass")
 * authUseCase.loginWithGoogle(idToken)
 * authUseCase.loginWithFacebook(accessToken)
 * authUseCase.recoverPassword("email@test.com")
 * authUseCase.signOut()
 * authUseCase.deleteAccount()
 * authUseCase.isEmailTaken("email@test.com")
 * authUseCase.isUsernameTaken("username")
 * ```
 */
class AuthUseCase @Inject constructor(
	private val repository: UserRepository,
) {
	suspend fun registerLocal(
		username: String,
		email: String,
		password: String,
	): Result<User> {
		require(username.isNotBlank()) { "Username cannot be blank" }
		require(email.isNotBlank()) { "Email cannot be blank" }
		require(password.length >= 8) { "Password must be at least 8 characters" }
		return repository.registerLocal(username.trim(), email.trim().lowercase(), password)
	}
	
	suspend fun loginLocal(identifier: String, password: String): Result<User> {
		require(identifier.isNotBlank()) { "Email or username cannot be blank" }
		require(password.isNotBlank()) { "Password cannot be blank" }
		return repository.loginLocal(identifier.trim(), password)
	}
	
	suspend fun loginWithGoogle(activityContext: android.content.Context): Result<User> =
		repository.loginWithGoogle(activityContext)
	
	suspend fun recoverPassword(email: String): Result<Unit> {
		require(email.isNotBlank()) { "Email cannot be blank" }
		return repository.recoverPassword(email.trim().lowercase())
	}
	
	suspend fun signOut() = repository.signOut()
	
	suspend fun deleteAccount() = repository.deleteAccount()
	
	suspend fun isEmailTaken(email: String): Boolean =
		repository.isEmailTaken(email.trim().lowercase())
	
	suspend fun isUsernameTaken(username: String): Boolean =
		repository.isUsernameTaken(username.trim())
}

// ═════════════════════════════════════════════════════════════════════════════
// PROFILE — Edición del perfil del usuario
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Mutaciones del perfil del usuario activo.
 *
 * Uso en ViewModel:
 * ```kotlin
 * manageProfileUseCase.update(UserProfileUpdate(username = "Alex"))
 * manageProfileUseCase.updatePassword(current, new)
 * ```
 */
class ManageProfileUseCase @Inject constructor(
	private val repository: UserRepository,
) {
	/**
	 * Aplica una actualización parcial del perfil.
	 * Solo los campos no-null de [update] se persisten.
	 */
	suspend fun update(update: UserProfileUpdate) {
		update.username?.let {
			require(it.isNotBlank()) { "Username cannot be blank" }
			repository.updateUsername(it.trim())
		}
		update.fullName?.let {
			repository.updateFullName(it.trim().ifBlank { null })
		}
		update.avatarUri?.let {
			repository.updateAvatar(it.ifBlank { null })
		}
	}
	
	suspend fun updatePassword(
		currentPassword: String,
		newPassword: String,
	): Result<Unit> {
		require(newPassword.length >= 8) { "New password must be at least 8 characters" }
		return repository.updatePassword(currentPassword, newPassword)
	}
}