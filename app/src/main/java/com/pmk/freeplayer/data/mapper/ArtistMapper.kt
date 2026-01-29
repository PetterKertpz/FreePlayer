package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.ArtistEntity
import com.pmk.freeplayer.domain.model.Artist
import com.pmk.freeplayer.domain.model.ArtistType
import com.pmk.freeplayer.domain.model.Genre
import com.pmk.freeplayer.domain.model.SocialLinks

/**
 * 🔄 ARTIST MAPPER
 *
 * Convierte entre la base de datos (Entity) y la UI (Domain Model).
 * Responsabilidad única: Transformación de datos entre capas.
 *
 * IMPORTANTE: Nunca exponer ArtistEntity fuera de la capa de datos.
 */

// ═══════════════════════════════════════════════════════════════
// ENTITY -> DOMAIN
// ═══════════════════════════════════════════════════════════════

fun ArtistEntity.toArtistDomain(): Artist {
	return Artist(
		// --- Identidad ---
		id = this.artistId,
		name = this.name,
		realName = this.realName,
		isVerified = this.isVerified,
		
		// --- Ubicación ---
		country = this.country,
		city = this.city,
		
		// --- Multimedia ---
		// Prioridad: Imagen local > Imagen remota
		coverUri = this.localImagePath ?: this.remoteImageUrl,
		headerUri = this.remoteHeaderUrl,
		
		// --- Textos ---
		biography = this.biography,
		description = this.description,
		
		// --- Datos Técnicos ---
		type = ArtistType.fromString(this.type),
		genre = mapStringToGenre(this.genres),
		
		// --- Estadísticas ---
		songCount = this.totalSongs,
		albumCount = this.totalAlbums,
		playCount = this.playCount,
		isFavorite = this.isFavorite,
		
		// --- Fechas ---
		careerStartYear = this.careerStartYear,
		birthDate = this.birthDate,
		
		// --- Enlaces Sociales (Objeto embebido) ---
		socialLinks = mapEntityToSocialLinks(this)
	)
}

// ═══════════════════════════════════════════════════════════════
// DOMAIN -> ENTITY
// ═══════════════════════════════════════════════════════════════

/**
 * Mapper inverso para guardar cambios desde la UI.
 *
 * @param originalEntity Entity original para preservar campos del sistema
 *                       (dateAdded, lastUpdated, localImagePath).
 */
fun Artist.toEntity(originalEntity: ArtistEntity? = null): ArtistEntity {
	val now = System.currentTimeMillis()
	
	return ArtistEntity(
		artistId = this.id,
		name = this.name,
		realName = this.realName,
		
		// --- Ubicación ---
		country = this.country,
		city = this.city,
		
		// --- Textos ---
		description = this.description,
		biography = this.biography,
		
		// --- Fechas ---
		birthDate = this.birthDate,
		careerStartYear = this.careerStartYear,
		
		// --- Multimedia ---
		// Lógica de imágenes: Preservar local si existe
		localImagePath = resolveLocalImagePath(
			originalLocalPath = originalEntity?.localImagePath,
			coverUri = this.coverUri
		),
		remoteImageUrl = resolveRemoteImageUrl(this.coverUri),
		remoteHeaderUrl = this.headerUri,
		
		// --- IDs externos desde SocialLinks ---
		geniusId = extractIdFromGeniusUrl(this.socialLinks.geniusUrl),
		spotifyId = extractIdFromSpotifyUrl(this.socialLinks.spotifyUrl),
		websiteUrl = this.socialLinks.websiteUrl,
		
		// --- Clasificación ---
		genres = this.genre?.name,
		type = this.type.name,
		
		// --- Flags ---
		isVerified = this.isVerified,
		isFavorite = this.isFavorite,
		
		// --- Estadísticas ---
		totalSongs = this.songCount,
		totalAlbums = this.albumCount,
		playCount = this.playCount,
		
		// --- Metadatos del Sistema ---
		// Preservar dateAdded original o usar timestamp actual si es nuevo
		dateAdded = originalEntity?.dateAdded ?: now,
		// Siempre actualizar lastUpdated
		lastUpdated = now
	)
}

// ═══════════════════════════════════════════════════════════════
// PRIVATE HELPERS - Social Links Mapping
// ═══════════════════════════════════════════════════════════════

/**
 * Construye el objeto SocialLinks desde los campos dispersos en ArtistEntity.
 *
 * ⚠️ LIMITACIÓN ACTUAL: ArtistEntity solo almacena websiteUrl, spotifyId y geniusId.
 * Los demás campos de SocialLinks (Instagram, Twitter, TikTok, etc.) devolverán null
 * hasta que se realice una migración de base de datos para soportarlos.
 *
 * TODO: Migración de BD para agregar columnas:
 *  - instagram_username, twitter_username, tiktok_username
 *  - apple_music_id, youtube_music_url, facebook_url
 *  - soundcloud_url, bandcamp_url, discogs_url, musicbrainz_id
 */
private fun mapEntityToSocialLinks(entity: ArtistEntity): SocialLinks {
	return SocialLinks(
		// ✅ Campos soportados actualmente
		websiteUrl = entity.websiteUrl,
		spotifyUrl = entity.spotifyId?.let { buildSpotifyArtistUrl(it) },
		geniusUrl = entity.geniusId?.let { buildGeniusArtistUrl(it) },
		
		// ⚠️ Campos no soportados - requieren migración
		appleMusicUrl = null,
		youtubeMusicUrl = null,
		instagramUsername = null,
		twitterUsername = null,
		tiktokUsername = null,
		facebookUrl = null,
		soundcloudUrl = null,
		bandcampUrl = null,
		discogsUrl = null,
		musicBrainzId = null
	)
}

// ═══════════════════════════════════════════════════════════════
// PRIVATE HELPERS - URL Builders & Extractors
// ═══════════════════════════════════════════════════════════════

private fun buildSpotifyArtistUrl(spotifyId: String): String {
	return "https://open.spotify.com/artist/$spotifyId"
}

private fun buildGeniusArtistUrl(geniusId: String): String {
	return "https://genius.com/artists/$geniusId"
}

private fun extractIdFromSpotifyUrl(url: String?): String? {
	if (url.isNullOrBlank()) return null
	// URL format: https://open.spotify.com/artist/{id}
	val regex = Regex("spotify\\.com/artist/([a-zA-Z0-9]+)")
	return regex.find(url)?.groupValues?.getOrNull(1)
}

private fun extractIdFromGeniusUrl(url: String?): String? {
	if (url.isNullOrBlank()) return null
	// URL format: https://genius.com/artists/{slug} or /{id}
	// Extraer el último segmento de la URL
	return url.trimEnd('/')
		.split("/")
		.lastOrNull()
		?.takeIf { it.isNotBlank() }
}

// ═══════════════════════════════════════════════════════════════
// PRIVATE HELPERS - Image Path Resolution
// ═══════════════════════════════════════════════════════════════

/**
 * Resuelve la ruta local de la imagen.
 *
 * Lógica:
 * 1. Si existe una imagen local guardada anteriormente, preservarla
 * 2. Si no, verificar si coverUri es una ruta local (no HTTP)
 * 3. Si no, devolver null
 */
private fun resolveLocalImagePath(originalLocalPath: String?, coverUri: String?): String? {
	return when {
		// Preservar imagen local existente
		!originalLocalPath.isNullOrBlank() -> originalLocalPath
		// Si coverUri es ruta local, usarla
		!coverUri.isNullOrBlank() && !coverUri.startsWith("http") -> coverUri
		// No hay imagen local
		else -> null
	}
}

/**
 * Resuelve la URL remota de la imagen.
 *
 * Lógica:
 * - Solo devolver coverUri si es una URL (empieza con http)
 */
private fun resolveRemoteImageUrl(coverUri: String?): String? {
	return coverUri?.takeIf { it.startsWith("http", ignoreCase = true) }
}

// ═══════════════════════════════════════════════════════════════
// PRIVATE HELPERS - Genre Mapping
// ═══════════════════════════════════════════════════════════════

/**
 * Convierte el String CSV ("Pop, Rock, Jazz") a un objeto Genre.
 * Toma solo el género principal (primer elemento).
 *
 * NOTA: ArtistEntity almacena géneros como String CSV simple.
 * Si se necesita soporte multi-género completo, considerar tabla
 * de relación many-to-many.
 */
private fun mapStringToGenre(genresStr: String?): Genre? {
	if (genresStr.isNullOrBlank()) return null
	
	val mainGenreName = genresStr
		.split(",")
		.firstOrNull()
		?.trim()
		?.takeIf { it.isNotBlank() }
		?: return null
	
	return Genre(
		id = UNMAPPED_GENRE_ID,
		name = mainGenreName,
		description = null,
		hexColor = null,
		iconUri = null,
		songCount = 0,
		playCount = 0,
		originDecade = null,
		originCountry = null
	)
}

// ═══════════════════════════════════════════════════════════════
// LIST EXTENSIONS
// ═══════════════════════════════════════════════════════════════

fun List<ArtistEntity>.toArtistDomainList(): List<Artist> = map { it.toArtistDomain() }

fun List<Artist>.toEntityList(originalEntities: List<ArtistEntity> = emptyList()): List<ArtistEntity> {
	val entityMap = originalEntities.associateBy { it.artistId }
	return map { artist ->
		artist.toEntity(originalEntity = entityMap[artist.id])
	}
}

// ═══════════════════════════════════════════════════════════════
// CONSTANTS
// ═══════════════════════════════════════════════════════════════

/**
 * ID temporal para géneros no mapeados a la tabla genres.
 * Permite identificar géneros extraídos del campo CSV.
 */
private const val UNMAPPED_GENRE_ID = -1L