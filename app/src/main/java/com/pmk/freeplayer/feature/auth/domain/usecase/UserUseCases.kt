package com.pmk.freeplayer.feature.auth.domain.usecase

// ─────────────────────────────────────────────────────────────────────────────
// ABSORBE (eliminados):
//   GetCurrentUserUseCase, UpdateUserProfileUseCase,
//   GetUserStatsUseCase, RefreshUserStatsUseCase
//
// CONSERVA: data class UserProfileUpdate y su lógica de validación.
// ─────────────────────────────────────────────────────────────────────────────

import com.pmk.freeplayer.feature.auth.domain.model.User
import com.pmk.freeplayer.feature.auth.domain.model.UserStats
import com.pmk.freeplayer.feature.auth.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ═════════════════════════════════════════════════════════════════════════════
// PARÁMETROS — Actualización parcial del perfil
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Permite actualizar solo los campos del perfil que cambian.
 * Los campos `null` se ignoran — no sobreescriben el valor existente.
 */
data class UserProfileUpdate(
	val username: String? = null,
	val fullName: String? = null,
	/** URI local o remota del avatar. `null` = sin cambio. Pasar `""` para borrarlo. */
	val avatarUri: String? = null,
)

// ═════════════════════════════════════════════════════════════════════════════
// CONTENEDOR ÚNICO
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Acceso al perfil y estadísticas del usuario.
 *
 * Uso en ViewModel:
 * ```kotlin
 * userUseCase()                                           // Flow<User?>
 * userUseCase.stats()                                     // Flow<UserStats>
 * userUseCase.update(UserProfileUpdate(username = "Alex"))
 * userUseCase.refreshStats()
 * userUseCase.signOut()
 * userUseCase.deleteAccount()
 * ```
 */
class UserUseCase @Inject constructor(
	private val repository: UserRepository,
) {
	
	/** Emite el [User] actual o `null` si no hay sesión. */
	operator fun invoke(): Flow<User?> = repository.getCurrentUser()
	
	/** Emite las [UserStats] calculadas desde el historial de reproducción. */
	fun stats(): Flow<UserStats> = repository.getUserStats()
	
	/**
	 * Aplica una actualización parcial del perfil.
	 * Solo los campos no-null de [update] se persisten.
	 *
	 * @throws IllegalArgumentException si [UserProfileUpdate.username] está en blanco.
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
			repository.updateAvatar(it)
		}
	}
	
	/** Recalcula [UserStats] desde el historial. Llamar tras sesiones largas. */
	suspend fun refreshStats() = repository.refreshStats()
	
	suspend fun signOut() = repository.signOut()
	
	suspend fun deleteAccount() = repository.deleteAccount()
}