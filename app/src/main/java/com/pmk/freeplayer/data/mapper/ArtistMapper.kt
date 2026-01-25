package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.ArtistEntity
import com.pmk.freeplayer.domain.model.Artist
import com.pmk.freeplayer.domain.model.Genre

/**
 * 🔄 ARTIST MAPPER
 * Convierte entre la base de datos (Entity) y la UI (Domain Model).
 */

// ==================== ENTITY -> DOMAIN ====================

fun ArtistEntity.toDomain(): Artist {
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
		// Si el tipo es nulo en la BD, asumimos "UNKNOWN" o "SOLO"
		type = this.type ?: "UNKNOWN",
		genre = mapStringToGenre(this.genres), // Reutilizamos lógica de conversión
		
		// --- Estadísticas ---
		songCount = this.totalSongs,
		albumCount = this.totalAlbums,
		playCount = this.playCount,
		isFavorite = this.isFavorite,
		
		// --- Fechas ---
		careerStartYear = this.careerStartYear,
		birthDate = this.birthDate,
		
		// --- Enlaces Externos ---
		websiteUrl = this.websiteUrl,
		spotifyId = this.spotifyId,
		geniusId = this.geniusId
	)
}

// ==================== DOMAIN -> ENTITY ====================

/**
 * Mapper inverso para guardar cambios desde la UI (ej: si editas el perfil del artista).
 */
fun Artist.toEntity(originalLocalPath: String? = null): ArtistEntity {
	return ArtistEntity(
		artistId = this.id,
		name = this.name,
		realName = this.realName,
		
		country = this.country,
		city = this.city,
		
		description = this.description,
		biography = this.biography,
		
		birthDate = this.birthDate,
		careerStartYear = this.careerStartYear,
		
		// Lógica de imágenes: Mantenemos la local si se pasa, o intentamos deducirla
		localImagePath = originalLocalPath ?: if (this.coverUri?.startsWith("http") == false) this.coverUri else null,
		remoteImageUrl = if (this.coverUri?.startsWith("http") == true) this.coverUri else null,
		remoteHeaderUrl = this.headerUri,
		
		geniusId = this.geniusId,
		spotifyId = this.spotifyId,
		websiteUrl = this.websiteUrl,
		
		genres = this.genre?.name, // Guardamos solo el nombre del género principal
		type = this.type,
		
		isVerified = this.isVerified,
		isFavorite = this.isFavorite,
		
		totalSongs = this.songCount,
		totalAlbums = this.albumCount,
		playCount = this.playCount
	)
}

// ==================== HELPERS PRIVADOS ====================

/**
 * Convierte el String CSV ("Pop, Rock") a un objeto Genre simple.
 * (Es el mismo helper que usamos en AlbumMapper).
 */
private fun mapStringToGenre(genresStr: String?): Genre? {
	if (genresStr.isNullOrBlank()) return null
	
	val mainGenreName = genresStr.split(",").first().trim()
	
	return Genre(
		id = -1,
		name = mainGenreName,
		
		// Campos visuales vacíos (ArtistEntity no los tiene)
		description = null,
		hexColor = null,
		iconUri = null,
		
		// Stats vacíos
		songCount = 0,
		playCount = 0,
		
		originDecade = null,
		originCountry = null
	)
}

// Extensión para listas
fun List<ArtistEntity>.toDomain(): List<Artist> = map { it.toDomain() }