package com.pmk.freeplayer.feature.auth.domain.model

import com.pmk.freeplayer.core.domain.model.enums.AuthType

/**
 * Entidad de dominio del usuario autenticado.
 *
 * Responsabilidad exclusiva: identidad y perfil.
 * Las estadísticas de uso (plays, favoritos, playlists) pertenecen
 * a [feature.statistics] y no forman parte de este agregado.
 */
data class User(
	val id: Long,
	val username: String,
	val email: String,
	val fullName: String?,
	val avatarUri: String?,
	val authType: AuthType,
	val joinDate: Long,
	val isActive: Boolean,
	val lastLogin: Long,
)