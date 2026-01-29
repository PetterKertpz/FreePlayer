package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.UserEntity
import com.pmk.freeplayer.domain.model.User
import com.pmk.freeplayer.domain.model.UserStats
import com.pmk.freeplayer.domain.model.enums.AuthType

/**
 * 🔄 USER MAPPER
 *
 * Convierte entre la capa de persistencia (Entity) y la capa de dominio (Model).
 * Maneja conversión de estadísticas, tipos de autenticación y datos de perfil.
 */

// ==================== ENTITY -> DOMAIN ====================

/**
 * Convierte UserEntity (DB) a User (Domain).
 * Agrupa las estadísticas en el objeto UserStats.
 */
fun UserEntity.toDomain(): User {
	return User(
		id = this.userId,
		username = this.username,
		email = this.email,
		fullName = this.fullName,
		avatarUri = this.avatarUri,
		
		// Conversión String -> Enum con fallback seguro
		authType = this.authType.toAuthType(),
		joinDate = this.createdAt,
		
		// Empaquetado de estadísticas en objeto UserStats
		stats = UserStats(
			playCount = this.totalPlays,
			favoriteCount = this.favoriteCount,
			playlistCount = this.playlistCount
		)
	)
}

/**
 * Convierte lista de entities a lista de domain models.
 */
fun List<UserEntity>.toDomain(): List<User> = map { it.toDomain() }

// ==================== DOMAIN -> ENTITY ====================

/**
 * Convierte User (Domain) a UserEntity (DB).
 *
 * Nota: Los campos de seguridad (passwordHash, salt) no se mapean desde el dominio
 * por razones de seguridad. Deben manejarse por separado en el repositorio.
 *
 * @param passwordHash Hash de la contraseña (opcional, solo para autenticación local)
 * @param salt Salt de la contraseña (opcional)
 * @param birthDate Fecha de nacimiento en timestamp
 * @param externalId ID externo (Google, Facebook, etc.)
 * @param isActive Si la cuenta está activa
 * @param lastLogin Timestamp de último login (se actualiza automáticamente)
 */
fun User.toEntity(
	passwordHash: String? = null,
	salt: String? = null,
	birthDate: Long? = null,
	externalId: String? = null,
	isActive: Boolean = true,
	lastLogin: Long = System.currentTimeMillis()
): UserEntity {
	return UserEntity(
		userId = this.id,
		username = this.username,
		email = this.email,
		
		// Seguridad (manejado por separado)
		passwordHash = passwordHash,
		salt = salt,
		
		// Perfil
		fullName = this.fullName,
		avatarUri = this.avatarUri,
		birthDate = birthDate,
		
		// Autenticación
		authType = this.authType.name,
		externalId = externalId,
		
		// Estado
		isActive = isActive,
		lastLogin = lastLogin,
		createdAt = this.joinDate,
		
		// Desempaquetado de estadísticas desde UserStats
		totalPlays = this.stats?.playCount ?: 0,
		favoriteCount = this.stats?.favoriteCount ?: 0,
		playlistCount = this.stats?.playlistCount ?: 0
	)
}

/**
 * Sobrecarga simplificada sin parámetros adicionales.
 * Útil para actualizaciones de perfil básicas.
 */
fun User.toEntity(): UserEntity = toEntity(
	passwordHash = null,
	salt = null,
	birthDate = null,
	externalId = null,
	isActive = true,
	lastLogin = System.currentTimeMillis()
)

// ==================== HELPERS DE CREACIÓN ====================

/**
 * Crea una UserEntity nueva para registro local.
 *
 * @param username Nombre de usuario único
 * @param email Email único
 * @param passwordHash Hash de la contraseña (BCrypt)
 * @param salt Salt usado para el hash
 * @param fullName Nombre completo (opcional)
 */
fun createLocalUserEntity(
	username: String,
	email: String,
	passwordHash: String,
	salt: String,
	fullName: String? = null
): UserEntity {
	val now = System.currentTimeMillis()
	return UserEntity(
		userId = 0, // Autogenerado
		username = username,
		email = email,
		passwordHash = passwordHash,
		salt = salt,
		fullName = fullName,
		avatarUri = null,
		birthDate = null,
		authType = AuthType.LOCAL.name,
		externalId = null,
		isActive = true,
		lastLogin = now,
		createdAt = now,
		totalPlays = 0,
		favoriteCount = 0,
		playlistCount = 0
	)
}

/**
 * Crea una UserEntity nueva para autenticación externa (Google, etc.).
 *
 * @param username Nombre de usuario único
 * @param email Email del proveedor externo
 * @param authType Tipo de autenticación (GOOGLE, FACEBOOK, etc.)
 * @param externalId ID del usuario en el proveedor externo
 * @param fullName Nombre completo obtenido del proveedor
 * @param avatarUri URL del avatar del proveedor
 */
fun createExternalUserEntity(
	username: String,
	email: String,
	authType: AuthType,
	externalId: String,
	fullName: String? = null,
	avatarUri: String? = null
): UserEntity {
	val now = System.currentTimeMillis()
	return UserEntity(
		userId = 0, // Autogenerado
		username = username,
		email = email,
		passwordHash = null, // No hay contraseña local
		salt = null,
		fullName = fullName,
		avatarUri = avatarUri,
		birthDate = null,
		authType = authType.name,
		externalId = externalId,
		isActive = true,
		lastLogin = now,
		createdAt = now,
		totalPlays = 0,
		favoriteCount = 0,
		playlistCount = 0
	)
}

// ==================== EXTENSION FUNCTIONS PARA USERENTITY ====================

/**
 * Actualiza el timestamp de último login.
 */
fun UserEntity.updateLastLogin(): UserEntity {
	return this.copy(lastLogin = System.currentTimeMillis())
}

/**
 * Actualiza las estadísticas del usuario.
 *
 * @param playCount Nuevo contador de reproducciones
 * @param favoriteCount Nuevo contador de favoritos
 * @param playlistCount Nuevo contador de playlists
 */
fun UserEntity.updateStats(
	playCount: Int? = null,
	favoriteCount: Int? = null,
	playlistCount: Int? = null
): UserEntity {
	return this.copy(
		totalPlays = playCount ?: this.totalPlays,
		favoriteCount = favoriteCount ?: this.favoriteCount,
		playlistCount = playlistCount ?: this.playlistCount
	)
}

/**
 * Incrementa el contador de reproducciones.
 *
 * @param amount Cantidad a incrementar (por defecto 1)
 */
fun UserEntity.incrementPlayCount(amount: Int = 1): UserEntity {
	return this.copy(totalPlays = this.totalPlays + amount)
}

/**
 * Incrementa el contador de favoritos.
 *
 * @param amount Cantidad a incrementar (por defecto 1)
 */
fun UserEntity.incrementFavoriteCount(amount: Int = 1): UserEntity {
	return this.copy(favoriteCount = this.favoriteCount + amount)
}

/**
 * Decrementa el contador de favoritos.
 *
 * @param amount Cantidad a decrementar (por defecto 1)
 */
fun UserEntity.decrementFavoriteCount(amount: Int = 1): UserEntity {
	return this.copy(favoriteCount = (this.favoriteCount - amount).coerceAtLeast(0))
}

/**
 * Incrementa el contador de playlists.
 *
 * @param amount Cantidad a incrementar (por defecto 1)
 */
fun UserEntity.incrementPlaylistCount(amount: Int = 1): UserEntity {
	return this.copy(playlistCount = this.playlistCount + amount)
}

/**
 * Decrementa el contador de playlists.
 *
 * @param amount Cantidad a decrementar (por defecto 1)
 */
fun UserEntity.decrementPlaylistCount(amount: Int = 1): UserEntity {
	return this.copy(playlistCount = (this.playlistCount - amount).coerceAtLeast(0))
}

/**
 * Actualiza el perfil del usuario.
 *
 * @param fullName Nuevo nombre completo
 * @param avatarUri Nueva URI del avatar
 * @param birthDate Nueva fecha de nacimiento
 */
fun UserEntity.updateProfile(
	fullName: String? = this.fullName,
	avatarUri: String? = this.avatarUri,
	birthDate: Long? = this.birthDate
): UserEntity {
	return this.copy(
		fullName = fullName,
		avatarUri = avatarUri,
		birthDate = birthDate
	)
}

/**
 * Actualiza la contraseña del usuario.
 * Solo aplicable para usuarios con autenticación LOCAL.
 *
 * @param newPasswordHash Nuevo hash de contraseña
 * @param newSalt Nuevo salt
 */
fun UserEntity.updatePassword(newPasswordHash: String, newSalt: String): UserEntity {
	require(this.authType == AuthType.LOCAL.name) {
		"Password can only be updated for LOCAL auth users"
	}
	return this.copy(
		passwordHash = newPasswordHash,
		salt = newSalt
	)
}

/**
 * Activa o desactiva la cuenta del usuario.
 */
fun UserEntity.setActive(active: Boolean): UserEntity {
	return this.copy(isActive = active)
}

/**
 * Vincula una cuenta externa a un usuario local.
 *
 * @param authType Tipo de autenticación externa
 * @param externalId ID del proveedor externo
 */
fun UserEntity.linkExternalAccount(authType: AuthType, externalId: String): UserEntity {
	return this.copy(
		authType = authType.name,
		externalId = externalId
	)
}

// ==================== EXTENSION FUNCTIONS PARA USER ====================

/**
 * Verifica si el usuario usa autenticación local.
 */
val User.isLocalAuth: Boolean
	get() = authType == AuthType.LOCAL

/**
 * Verifica si el usuario usa autenticación externa.
 */
val User.isExternalAuth: Boolean
	get() = authType != AuthType.LOCAL

/**
 * Verifica si el usuario tiene avatar personalizado.
 */
val User.hasAvatar: Boolean
	get() = !avatarUri.isNullOrBlank()

/**
 * Verifica si el usuario tiene estadísticas.
 */
val User.hasStats: Boolean
	get() = stats != null

/**
 * Obtiene el total de items del usuario (favoritos + playlists).
 */
val User.totalItems: Int
	get() = (stats?.favoriteCount ?: 0) + (stats?.playlistCount ?: 0)

/**
 * Verifica si es un usuario nuevo (sin actividad).
 */
val User.isNewUser: Boolean
	get() = stats?.playCount == 0 && totalItems == 0

/**
 * Obtiene las iniciales del nombre completo o username.
 * Útil para avatares con iniciales.
 */
val User.initials: String
	get() {
		val name = fullName ?: username
		return name.split(" ")
			.take(2)
			.mapNotNull { it.firstOrNull()?.uppercaseChar() }
			.joinToString("")
			.ifEmpty { username.take(2).uppercase() }
	}

/**
 * Obtiene el nombre para mostrar (fullName o username).
 */
val User.displayName: String
	get() = fullName?.takeIf { it.isNotBlank() } ?: username

// ==================== EXTENSION FUNCTIONS PARA USERSTATS ====================

/**
 * Verifica si las estadísticas están vacías.
 */
val UserStats.isEmpty: Boolean
	get() = playCount == 0 && favoriteCount == 0 && playlistCount == 0

/**
 * Obtiene un resumen textual de las estadísticas.
 * Ej: "150 reproducciones • 25 favoritos • 5 playlists"
 */
fun UserStats.getSummary(): String {
	val parts = mutableListOf<String>()
	
	if (playCount > 0) {
		val text = if (playCount == 1) "reproducción" else "reproducciones"
		parts.add("$playCount $text")
	}
	
	if (favoriteCount > 0) {
		val text = if (favoriteCount == 1) "favorito" else "favoritos"
		parts.add("$favoriteCount $text")
	}
	
	if (playlistCount > 0) {
		val text = if (playlistCount == 1) "playlist" else "playlists"
		parts.add("$playlistCount $text")
	}
	
	return parts.joinToString(" • ").ifEmpty { "Sin actividad" }
}

// ==================== HELPERS PRIVADOS ====================

/**
 * Convierte String a AuthType de forma segura.
 * Retorna LOCAL como fallback si el string no es válido.
 */
private fun String.toAuthType(): AuthType {
	return try {
		AuthType.valueOf(this.uppercase())
	} catch (e: IllegalArgumentException) {
		AuthType.LOCAL // Fallback seguro
	}
}