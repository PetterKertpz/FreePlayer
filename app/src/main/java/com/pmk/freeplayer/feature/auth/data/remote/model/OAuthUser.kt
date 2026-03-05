package com.pmk.freeplayer.feature.auth.data.remote.model

/**
 * DTO interno que representa el resultado de una autenticación OAuth exitosa.
 *
 * No es un modelo de dominio — solo viaja entre [OAuthManagerImpl]
 * y [UserRepositoryImpl]. No debe exponerse fuera de la capa data.
 */
data class OAuthUser(
	val externalId: String,
	val email: String,
	val fullName: String?,
	val avatarUri: String?,
)