package com.pmk.freeplayer.feature.auth.data.mapper

import com.pmk.freeplayer.core.domain.model.enums.AuthType
import com.pmk.freeplayer.feature.auth.data.local.entity.UserEntity
import com.pmk.freeplayer.feature.auth.domain.model.User

/**
 * Mapper entre [UserEntity] (Room) y [User] (dominio).
 *
 * Responsabilidad exclusiva: traducir identidad, credenciales y perfil.
 * Se eliminaron los mappings de UserStats — esos datos los calcula
 * [feature.statistics] desde sus propias tablas.
 */

// ═════════════════════════════════════════════════════════════════════════════
// ENTITY → DOMAIN
// ═════════════════════════════════════════════════════════════════════════════

fun UserEntity.toDomain(): User = User(
	id = userId,
	username = username,
	email = email,
	fullName = fullName,
	avatarUri = avatarUri,
	authType = authType.toAuthTypeSafe(),
	joinDate = createdAt,
	isActive = isActive,
	lastLogin = lastLogin,
)

fun List<UserEntity>.toDomain(): List<User> = map { it.toDomain() }

// ═════════════════════════════════════════════════════════════════════════════
// DOMAIN → ENTITY
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Conversión completa para actualizaciones de perfil.
 * Los campos de seguridad (passwordHash, salt, externalId) se pasan
 * explícitamente porque nunca viajan en el modelo de dominio.
 */
fun User.toEntity(
	passwordHash: String? = null,
	salt: String? = null,
	externalId: String? = null,
): UserEntity = UserEntity(
	userId = id,
	username = username,
	email = email,
	passwordHash = passwordHash,
	salt = salt,
	fullName = fullName,
	avatarUri = avatarUri,
	authType = authType.name,
	externalId = externalId,
	isActive = isActive,
	lastLogin = lastLogin,
	createdAt = joinDate,
)

// ═════════════════════════════════════════════════════════════════════════════
// FACTORY HELPERS — Creación de nuevas entidades
// ═════════════════════════════════════════════════════════════════════════════

/** Crea una [UserEntity] para registro local con BCrypt. */
fun buildLocalUserEntity(
	username: String,
	email: String,
	passwordHash: String,
	salt: String,
	fullName: String? = null,
): UserEntity {
	val now = System.currentTimeMillis()
	return UserEntity(
		userId = 0,
		username = username,
		email = email,
		passwordHash = passwordHash,
		salt = salt,
		fullName = fullName,
		authType = AuthType.LOCAL.name,
		createdAt = now,
		lastLogin = now,
	)
}

/** Crea una [UserEntity] para autenticación OAuth (Google, Facebook). */
fun buildExternalUserEntity(
	username: String,
	email: String,
	authType: AuthType,
	externalId: String,
	fullName: String? = null,
	avatarUri: String? = null,
): UserEntity {
	val now = System.currentTimeMillis()
	return UserEntity(
		userId = 0,
		username = username,
		email = email,
		passwordHash = null,
		salt = null,
		fullName = fullName,
		avatarUri = avatarUri,
		authType = authType.name,
		externalId = externalId,
		createdAt = now,
		lastLogin = now,
	)
}

// ═════════════════════════════════════════════════════════════════════════════
// EXTENSIONS — Utilidades de entidad
// ═════════════════════════════════════════════════════════════════════════════

fun UserEntity.withUpdatedLogin(): UserEntity =
	copy(lastLogin = System.currentTimeMillis())

fun UserEntity.withNewPassword(hash: String, salt: String): UserEntity {
	require(authType == AuthType.LOCAL.name) {
		"Password update is only allowed for LOCAL auth users"
	}
	return copy(passwordHash = hash, salt = salt)
}

// ═════════════════════════════════════════════════════════════════════════════
// EXTENSIONS — Utilidades de dominio
// ═════════════════════════════════════════════════════════════════════════════

val User.isLocalAuth: Boolean
	get() = authType == AuthType.LOCAL

val User.isExternalAuth: Boolean
	get() = authType != AuthType.LOCAL

val User.displayName: String
	get() = fullName?.takeIf { it.isNotBlank() } ?: username

val User.initials: String
	get() {
		val name = fullName ?: username
		return name.split(" ")
			.take(2)
			.mapNotNull { it.firstOrNull()?.uppercaseChar() }
			.joinToString("")
			.ifEmpty { username.take(2).uppercase() }
	}

// ═════════════════════════════════════════════════════════════════════════════
// HELPERS PRIVADOS
// ═════════════════════════════════════════════════════════════════════════════

private fun String.toAuthTypeSafe(): AuthType =
	try {
		AuthType.valueOf(uppercase())
	} catch (e: IllegalArgumentException) {
		AuthType.LOCAL
	}