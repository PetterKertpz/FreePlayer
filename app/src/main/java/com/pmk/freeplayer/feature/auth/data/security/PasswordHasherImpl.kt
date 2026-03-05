package com.pmk.freeplayer.feature.auth.data.security

import at.favre.lib.crypto.bcrypt.BCrypt
import javax.inject.Inject

/**
 * Implementación de [PasswordHasher] usando BCrypt (favre-lib).
 *
 * Dependencia en build.gradle.kts:
 * ```kotlin
 * implementation("at.favre.lib:bcrypt:0.10.2")
 * ```
 *
 * Cost factor 12 es el estándar recomendado en 2024+.
 * En dispositivos lentos considera reducir a 10.
 *
 * IMPORTANTE: BCrypt incluye el salt en el hash resultante.
 * El campo [salt] en [UserEntity] es un salt auxiliar para
 * poder migrar algoritmos en el futuro si fuera necesario.
 * Para verificación, BCrypt usa el salt embebido en el hash.
 */
class PasswordHasherImpl @Inject constructor() : PasswordHasher {
	
	private val COST_FACTOR = 12
	
	override fun generateSalt(): String =
		BCrypt.withDefaults().hashToString(COST_FACTOR, "salt_placeholder".toCharArray())
			.let { java.util.UUID.randomUUID().toString().replace("-", "") }
	
	override fun hash(password: String, salt: String): String =
		BCrypt.withDefaults()
			.hashToString(COST_FACTOR, password.toCharArray())
	
	override fun verify(password: String, hash: String, salt: String): Boolean =
		BCrypt.verifyer()
			.verify(password.toCharArray(), hash)
			.verified
}