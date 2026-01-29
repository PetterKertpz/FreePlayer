package com.pmk.freeplayer.domain.useCase.user

import com.pmk.freeplayer.domain.model.User
import com.pmk.freeplayer.domain.model.UserStats
import com.pmk.freeplayer.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
	private val repository: UserRepository
) {
	operator fun invoke(): Flow<User?> = repository.getCurrentUser()
}

// domain/useCase/user/UpdateUserProfileUseCase.kt
data class UserProfileUpdate(
	val username: String? = null,
	val fullName: String? = null,
	val avatarUri: String? = null
)

class UpdateUserProfileUseCase @Inject constructor(
	private val repository: UserRepository
) {
	suspend operator fun invoke(update: UserProfileUpdate) {
		update.username?.let {
			require(it.isNotBlank()) { "Username cannot be blank" }
			repository.updateUsername(it.trim())
		}
		update.fullName?.let { repository.updateFullName(it.trim().ifBlank { null }) }
		update.avatarUri?.let { repository.updateAvatar(it) }
	}
}

// domain/useCase/user/GetUserStatsUseCase.kt
class GetUserStatsUseCase @Inject constructor(
	private val repository: UserRepository
) {
	operator fun invoke(): Flow<UserStats> = repository.getUserStats()
}

// domain/useCase/user/RefreshUserStatsUseCase.kt
class RefreshUserStatsUseCase @Inject constructor(
	private val repository: UserRepository
) {
	suspend operator fun invoke() = repository.refreshStats()
}

