package com.pmk.freeplayer.domain.model

// Asumiendo que AuthType ya lo tienes en /audio o /user
import com.pmk.freeplayer.domain.model.audio.AuthType

data class User(
	val id: Long,
	val username: String,
	val email: String,
	val fullName: String?,
	val avatarUri: String?,
	
	val authType: AuthType,
	val joinDate: Long,
	
	val stats: UserStats?
)

data class UserStats(
	val playCount: Int,
	val favoriteCount: Int,
	val playlistCount: Int
)