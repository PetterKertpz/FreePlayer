package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.enums.SocialPlatform

data class SocialLink(
	val id: Long,
	val platform: SocialPlatform, // Enum (útil para decidir qué icono mostrar)
	val username: String,         // "@usuario"
	val url: String,              // Acción al hacer click
	val isVerified: Boolean,
	val followerCount: Int?       // Opcional
)
