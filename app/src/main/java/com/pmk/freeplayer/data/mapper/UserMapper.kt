package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.UserEntity
import com.pmk.freeplayer.domain.model.User
import com.pmk.freeplayer.domain.model.UserStats
import com.pmk.freeplayer.domain.model.enums.AuthType

/**
 * 🔄 USER MAPPER
 * Convierte entre la base de datos (Entity) y la UI (Domain Model).
 */

// ==================== ENTITY -> DOMAIN ====================

fun UserEntity.toDomain(): User {
	return User(
		id = this.userId,
		username = this.username,
		email = this.email,
		fullName = this.fullName,
		avatarUri = this.avatarUri,
		
		// Conversión String -> Enum
		authType = mapStringToAuthType(this.authType),
		
		joinDate = this.createdAt,
		
		// ✅ AHORA MAPEAMOS LOS 3 VALORES DE TU NUEVO USERSTATS
		stats = UserStats(
			playCount = this.totalPlays,      // Entity: totalPlays -> Domain: playCount
			favoriteCount = this.favoriteCount,
			
			// Si agregaste el campo a la Entity úsalo aquí.
			// Si NO lo agregaste, pon '0' temporalmente.
			playlistCount = this.playlistCount
		)
	)
}

// ==================== DOMAIN -> ENTITY ====================

fun User.toEntity(): UserEntity {
	return UserEntity(
		userId = this.id,
		username = this.username,
		email = this.email,
		
		// Seguridad: Se dejan nulos
		passwordHash = null,
		salt = null,
		
		fullName = this.fullName,
		avatarUri = this.avatarUri,
		birthDate = null,
		
		authType = this.authType.name,
		externalId = null,
		
		isActive = true,
		createdAt = this.joinDate,
		lastLogin = System.currentTimeMillis(),
		
		// ✅ DESEMPAQUETADO DE ESTADÍSTICAS
		totalPlays = this.stats?.playCount ?: 0,
		favoriteCount = this.stats?.favoriteCount ?: 0,
		playlistCount = this.stats?.playlistCount ?: 0 // Nuevo campo
	)
}

// ==================== LISTAS ====================

fun List<UserEntity>.toDomain(): List<User> = map { it.toDomain() }

// ==================== HELPERS ====================

private fun mapStringToAuthType(typeStr: String): AuthType {
	return try {
		AuthType.valueOf(typeStr.uppercase())
	} catch (e: Exception) {
		AuthType.LOCAL
	}
}