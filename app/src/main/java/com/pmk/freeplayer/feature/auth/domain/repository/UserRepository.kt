package com.pmk.freeplayer.feature.auth.domain.repository

import com.pmk.freeplayer.feature.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de autenticación y perfil.
 *
 * Responsabilidad exclusiva: ciclo de vida de la sesión y datos de perfil.
 * No gestiona estadísticas de uso — eso pertenece a [feature.statistics].
 */
interface UserRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// SESIÓN
	// ═══════════════════════════════════════════════════════════════
	
	/** Emite el [User] activo o `null` si no hay sesión iniciada. */
	fun getCurrentUser(): Flow<User?>
	
	/** Retorna el usuario activo una sola vez (para validaciones). */
	suspend fun getCurrentUserOnce(): User?
	
	suspend fun signOut()
	
	suspend fun deleteAccount()
	
	// ═══════════════════════════════════════════════════════════════
	// REGISTRO Y AUTENTICACIÓN LOCAL
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Registra un nuevo usuario local.
	 * La implementación es responsable del hash bcrypt + salt.
	 */
	suspend fun registerLocal(
		username: String,
		email: String,
		password: String,
	): Result<User>
	
	/**
	 * Autentica con email/username + contraseña.
	 * La implementación verifica el hash internamente.
	 */
	suspend fun loginLocal(identifier: String, password: String): Result<User>
	
	suspend fun recoverPassword(email: String): Result<Unit>
	
	// ═══════════════════════════════════════════════════════════════
	// AUTENTICACIÓN EXTERNA
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Lanza el selector de cuentas Google y autentica al usuario.
	 * @param activityContext Contexto de la Activity activa.
	 */
	suspend fun loginWithGoogle(activityContext: android.content.Context): Result<User>
	
	// ═══════════════════════════════════════════════════════════════
	// VALIDACIONES
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun isEmailTaken(email: String): Boolean
	
	suspend fun isUsernameTaken(username: String): Boolean
	
	// ═══════════════════════════════════════════════════════════════
	// PERFIL
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun updateUsername(username: String)
	
	suspend fun updateFullName(fullName: String?)
	
	suspend fun updateAvatar(uri: String?)
	
	suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit>
}