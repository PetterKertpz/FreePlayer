package com.pmk.freeplayer.feature.auth.data.security

/**
 * Contrato para hashing y verificación de contraseñas.
 * Abstrae el algoritmo concreto (BCrypt) del repositorio.
 */
interface PasswordHasher {
	/** Genera un salt aleatorio criptográficamente seguro. */
	fun generateSalt(): String
	
	/** Hashea [password] con el [salt] dado. */
	fun hash(password: String, salt: String): String
	
	/** Verifica que [password] coincide con [hash] usando [salt]. */
	fun verify(password: String, hash: String, salt: String): Boolean
}