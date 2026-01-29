package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.AlbumEntity
import com.pmk.freeplayer.data.local.entity.relation.AlbumArtistEntity
import com.pmk.freeplayer.domain.model.Album
import com.pmk.freeplayer.domain.model.Genre
import com.pmk.freeplayer.domain.model.TrackDuration
import com.pmk.freeplayer.domain.model.enums.AlbumType

/**
 * 🔄 ALBUM MAPPER
 *
 * Convierte entre la base de datos (Entity) y la UI (Domain Model).
 * Responsabilidad única: Transformación de datos entre capas.
 *
 * IMPORTANTE: Nunca exponer AlbumEntity fuera de la capa de datos.
 */

// ═══════════════════════════════════════════════════════════════
// ENTITY -> DOMAIN
// ═══════════════════════════════════════════════════════════════

/**
 * Mapeo principal cuando usamos proyección JOIN de álbum + artista.
 * Usado en queries que traen ambas tablas con una sola consulta.
 *
 * ✅ RECOMENDADO: Usar este método siempre que sea posible para evitar queries adicionales.
 */
fun AlbumArtistEntity.toDomain(): Album {
	return Album(
		// --- 1. Identidad ---
		id = this.albumId,
		title = this.title,
		
		// --- 2. Artista ---
		artistId = this.artistId,
		artistName = this.artistName,
		
		// --- 3. Multimedia ---
		// Prioridad: Imagen local > Imagen remota
		coverUri = this.localCoverPath ?: this.remoteCoverUrl,
		
		// --- 4. Metadatos Básicos ---
		type = mapStringToAlbumType(this.type),
		genre = mapStringToGenre(this.genres),
		year = this.year,
		dateAdded = this.dateAdded,
		
		// --- 5. Metadatos Ricos ---
		description = this.description,
		producer = this.producer,
		recordLabel = this.recordLabel,
		
		// --- 6. Estadísticas ---
		songCount = this.totalSongs,
		totalDuration = TrackDuration(this.totalDurationMs),
		playCount = this.playCount,
		rating = this.rating,
		isFavorite = this.isFavorite,
		
		// --- 7. Referencias Externas ---
		geniusId = this.geniusId,
		spotifyId = this.spotifyId
	)
}

/**
 * Mapeo manual cuando ya conocemos el nombre del artista externamente.
 * Útil cuando trabajas solo con AlbumEntity sin JOIN.
 *
 * ⚠️ ADVERTENCIA: Requiere artistName externo. Preferir AlbumArtistEntity.toDomain().
 */
fun AlbumEntity.toDomain(artistName: String): Album {
	return Album(
		// --- 1. Identidad ---
		id = this.albumId,
		title = this.title,
		
		// --- 2. Artista ---
		artistId = this.artistId,
		artistName = artistName,
		
		// --- 3. Multimedia ---
		// Prioridad: Imagen local > Imagen remota
		coverUri = this.localCoverPath ?: this.remoteCoverUrl,
		
		// --- 4. Metadatos Básicos ---
		type = mapStringToAlbumType(this.type),
		genre = mapStringToGenre(this.genres),
		year = this.year,
		dateAdded = this.dateAdded,
		
		// --- 5. Metadatos Ricos ---
		description = this.description,
		producer = this.producer,
		recordLabel = this.label, // ⚠️ Nota: Entity usa "label", Domain usa "recordLabel"
		
		// --- 6. Estadísticas ---
		songCount = this.totalSongs,
		totalDuration = TrackDuration(this.totalDurationMs),
		playCount = this.playCount,
		rating = this.rating,
		isFavorite = this.isFavorite,
		
		// --- 7. Referencias Externas ---
		geniusId = this.geniusId,
		spotifyId = this.spotifyId
	)
}

// ═══════════════════════════════════════════════════════════════
// DOMAIN -> ENTITY
// ═══════════════════════════════════════════════════════════════

/**
 * Mapper inverso para guardar cambios desde la UI.
 *
 * @param originalEntity Entity original para preservar campos del sistema
 *                       (dateAdded, releaseDate, localCoverPath).
 */
fun Album.toEntity(originalEntity: AlbumEntity? = null): AlbumEntity {
	return AlbumEntity(
		albumId = this.id,
		artistId = this.artistId,
		title = this.title,
		
		// --- Multimedia ---
		// Lógica de imágenes: Preservar local si existe
		localCoverPath = resolveLocalCoverPath(
			originalLocalPath = originalEntity?.localCoverPath,
			coverUri = this.coverUri
		),
		remoteCoverUrl = resolveRemoteCoverUrl(this.coverUri),
		
		// --- Metadatos Temporales ---
		year = this.year,
		// Preservar releaseDate original si existe
		releaseDate = originalEntity?.releaseDate,
		// Preservar dateAdded original o usar timestamp del domain
		dateAdded = originalEntity?.dateAdded ?: this.dateAdded,
		
		// --- Clasificación ---
		type = this.type.name,
		genres = this.genre?.name,
		totalDurationMs = this.totalDuration.millis,
		
		// --- Metadatos Ricos ---
		label = this.recordLabel, // ⚠️ Entity usa "label", Domain usa "recordLabel"
		producer = this.producer,
		description = this.description,
		
		// --- Estadísticas ---
		totalSongs = this.songCount,
		playCount = this.playCount,
		isFavorite = this.isFavorite,
		rating = this.rating,
		
		// --- Referencias Externas ---
		geniusId = this.geniusId,
		spotifyId = this.spotifyId
	)
}

// ═══════════════════════════════════════════════════════════════
// PRIVATE HELPERS - Cover Path Resolution
// ═══════════════════════════════════════════════════════════════

/**
 * Resuelve la ruta local de la portada del álbum.
 *
 * Lógica:
 * 1. Si existe una portada local guardada anteriormente, preservarla
 * 2. Si no, verificar si coverUri es una ruta local (no HTTP)
 * 3. Si no, devolver null
 */
private fun resolveLocalCoverPath(originalLocalPath: String?, coverUri: String?): String? {
	return when {
		// Preservar portada local existente
		!originalLocalPath.isNullOrBlank() -> originalLocalPath
		// Si coverUri es ruta local, usarla
		!coverUri.isNullOrBlank() && !coverUri.startsWith("http") -> coverUri
		// No hay portada local
		else -> null
	}
}

/**
 * Resuelve la URL remota de la portada del álbum.
 *
 * Lógica:
 * - Solo devolver coverUri si es una URL (empieza con http)
 */
private fun resolveRemoteCoverUrl(coverUri: String?): String? {
	return coverUri?.takeIf { it.startsWith("http", ignoreCase = true) }
}

// ═══════════════════════════════════════════════════════════════
// PRIVATE HELPERS - Type & Genre Mapping
// ═══════════════════════════════════════════════════════════════

/**
 * Convierte el String de la BD al Enum de Dominio.
 * Maneja casos edge y valores inválidos.
 */
private fun mapStringToAlbumType(typeStr: String?): AlbumType {
	if (typeStr.isNullOrBlank()) return AlbumType.ALBUM
	
	return try {
		AlbumType.valueOf(typeStr.uppercase())
	} catch (e: IllegalArgumentException) {
		// Fallback seguro para valores desconocidos
		AlbumType.ALBUM
	}
}

/**
 * Crea un objeto Genre desde el String CSV de la BD.
 * Solo toma el primer género como principal.
 *
 * NOTA: AlbumEntity almacena géneros como String CSV simple.
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

fun List<AlbumArtistEntity>.toAlbumDomainList(): List<Album> = map { it.toDomain() }

fun List<AlbumEntity>.toAlbumDomainList(artistName: String): List<Album> =
	map { it.toDomain(artistName) }

fun List<Album>.toAlbumEntityList(originalEntities: List<AlbumEntity> = emptyList()): List<AlbumEntity> {
	val entityMap = originalEntities.associateBy { it.albumId }
	return map { album ->
		album.toEntity(originalEntity = entityMap[album.id])
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