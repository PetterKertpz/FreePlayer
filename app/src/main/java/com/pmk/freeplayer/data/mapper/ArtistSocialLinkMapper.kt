package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.ArtistSocialLinkEntity
import com.pmk.freeplayer.domain.model.SocialLink
import com.pmk.freeplayer.domain.model.enums.SocialPlatform

/**
 * 🔄 ARTIST SOCIAL LINK MAPPER
 * Convierte los enlaces sociales crudos de la BD en objetos UI con Enums.
 */

// ==================== ENTITY -> DOMAIN ====================

fun ArtistSocialLinkEntity.toDomain(): SocialLink {
	return SocialLink(
		id = this.linkId,
		
		// Convertimos String -> Enum para pintar iconos
		platform = mapStringToPlatform(this.platform),
		
		username = this.username,
		url = this.url,
		isVerified = this.isVerified,
		followerCount = this.followerCount
	)
}

// ==================== DOMAIN -> ENTITY ====================

/**
 * Nota: El modelo de dominio 'SocialLink' NO tiene artistId (es agnóstico).
 * Por eso, debemos pasar el 'artistId' como parámetro al guardar en la BD.
 */
fun SocialLink.toEntity(artistId: Long): ArtistSocialLinkEntity {
	return ArtistSocialLinkEntity(
		linkId = this.id,
		artistId = artistId, // 🔗 Vinculamos con el FK
		
		// Convertimos Enum -> String
		platform = this.platform.name,
		
		username = this.username,
		url = this.url,
		isVerified = this.isVerified,
		followerCount = this.followerCount,
		
		// Fecha de creación (si es edición, idealmente mantendrías la original,
		// pero para simplificar reiniciamos o ignoramos en updates parciales).
		createdAt = System.currentTimeMillis()
	)
}

// ==================== LISTAS ====================

fun List<ArtistSocialLinkEntity>.toDomain(): List<SocialLink> = map { it.toDomain() }

fun List<SocialLink>.toEntity(artistId: Long): List<ArtistSocialLinkEntity> = map { it.toEntity(artistId) }

// ==================== HELPERS PRIVADOS ====================

private fun mapStringToPlatform(platformStr: String): SocialPlatform {
	return try {
		SocialPlatform.valueOf(platformStr.uppercase())
	} catch (e: Exception) {
		// Si la base de datos tiene algo raro o nuevo ("MYSPACE"),
		// devolvemos WEBSITE o UNKNOWN para evitar crash.
		if (platformStr.startsWith("http")) SocialPlatform.WEBSITE else SocialPlatform.UNKNOWN
	}
}