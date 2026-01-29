package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.SongEntity
import com.pmk.freeplayer.data.local.entity.relation.SongArtistEntity
import com.pmk.freeplayer.domain.model.FileSize
import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.TrackDuration
import com.pmk.freeplayer.domain.model.enums.IntegrityStatus

/**
 * 🔄 SONG MAPPER
 *
 * Convierte entre la capa de persistencia (Entity) y la capa de dominio (Model).
 * Maneja conversiones de tipos, value classes, y mapeo de relaciones.
 */

// ==================== ENTITY -> DOMAIN ====================

/**
 * Opción A: Convierte desde una query con JOINs (SongArtistEntity).
 * Incluye información del artista y álbum relacionados.
 */
fun SongArtistEntity.toDomain(): Song {
	return this.song.toDomain(
		artistName = this.artistaNombre ?: "Unknown Artist",
		albumName = this.albumNombre,
		coverUri = this.portadaPath
	)
}

/**
 * Opción B: Convierte desde una SongEntity individual.
 * Mapea todos los campos técnicos y envuelve los Value Classes.
 *
 * @param artistName Nombre del artista (de un JOIN o valor por defecto)
 * @param albumName Nombre del álbum (opcional)
 * @param coverUri URI de la portada del álbum (opcional)
 */
fun SongEntity.toDomain(
	artistName: String = "Unknown Artist",
	albumName: String? = null,
	coverUri: String? = null
): Song {
	return Song(
		// Identidad básica
		id = this.songId,
		title = this.title,
		artistName = artistName,
		
		// Ubicación y relaciones
		filePath = this.filePath,
		trackNumber = this.trackNumber,
		discNumber = this.discNumber,
		year = this.year,
		
		artistId = this.artistId,
		albumId = this.albumId,
		genreId = this.genreId,
		
		// Value Classes (Long -> Wrappers tipados)
		duration = TrackDuration(this.duration),
		size = FileSize(this.size),
		
		// Detalles técnicos
		mimeType = this.mimeType,
		bitrate = this.bitrate,
		sampleRate = this.sampleRate,
		audioQuality = this.audioQuality,
		
		// Auditoría (para edición de etiquetas)
		originalTitle = this.originalTitle,
		originalArtist = this.originalArtist,
		
		// Metadatos ricos
		versionType = this.versionType,
		dateAdded = this.dateAdded,
		dateModified = this.dateModified,
		
		// Integración externa (Genius)
		geniusUrl = this.geniusUrl,
		isHot = this.isHot,
		externalIds = this.externalIdsJson,
		
		// Estado y estadísticas
		isFavorite = this.isFavorite,
		playCount = this.playCount,
		rating = this.rating,
		hasLyrics = this.hasLyrics,
		metadataStatus = IntegrityStatus.valueOf(this.metadataStatus.uppercase())
	)
}

/**
 * Convierte lista de entities a lista de domain models.
 */
fun List<SongArtistEntity>.toDomain(): List<Song> = map { it.toDomain() }

/**
 * Convierte lista de SongEntity (sin JOINs) a lista de domain models.
 */
fun List<SongEntity>.toDomain(defaultArtistName: String = "Unknown Artist"): List<Song> =
	map { it.toDomain(artistName = defaultArtistName) }

// ==================== DOMAIN -> ENTITY ====================

/**
 * Convierte Song (Domain) a SongEntity (DB).
 * Incluye todos los campos necesarios para la persistencia.
 *
 * @param sourceType Origen de la canción ("SCANNER", "MANUAL", "DOWNLOAD")
 * @param fileHash Hash del archivo para detección de duplicados
 * @param hasCover Si tiene portada embebida
 * @param geniusId ID de Genius (si existe)
 * @param geniusTitle Título completo de Genius
 * @param pageviews Vistas en Genius
 * @param lastPlayed Timestamp de última reproducción
 * @param confidenceScore Confianza del match de metadata (0.0-1.0)
 */
fun Song.toEntity(
	sourceType: String = "SCANNER",
	fileHash: String? = null,
	hasCover: Boolean = false,
	geniusId: String? = null,
	geniusTitle: String? = null,
	pageviews: Int? = null,
	lastPlayed: Long? = null,
	confidenceScore: Float = 0f
): SongEntity {
	return SongEntity(
		// Identidad
		songId = this.id,
		title = this.title,
		
		// Value Classes (desempaquetado)
		duration = this.duration.millis,
		size = this.size.bytes,
		
		// Relaciones
		artistId = this.artistId,
		albumId = this.albumId,
		genreId = this.genreId,
		
		// Información del archivo
		filePath = this.filePath,
		trackNumber = this.trackNumber,
		discNumber = this.discNumber,
		year = this.year,
		mimeType = this.mimeType,
		
		// Calidad de audio
		bitrate = this.bitrate,
		sampleRate = this.sampleRate,
		audioQuality = this.audioQuality,
		
		// Auditoría
		originalTitle = this.originalTitle,
		originalArtist = this.originalArtist,
		versionType = this.versionType,
		
		// Integración Genius
		geniusId = geniusId,
		geniusUrl = this.geniusUrl,
		geniusTitle = geniusTitle,
		isHot = this.isHot,
		pageviews = pageviews,
		externalIdsJson = this.externalIds,
		
		// Estadísticas y estado
		playCount = this.playCount,
		lastPlayed = lastPlayed,
		isFavorite = this.isFavorite,
		rating = this.rating,
		hasLyrics = this.hasLyrics,
		
		// Metadatos del sistema
		metadataStatus = this.metadataStatus.name,
		confidenceScore = confidenceScore,
		dateAdded = this.dateAdded,
		dateModified = this.dateModified,
		
		// Archivo físico
		fileHash = fileHash,
		hasCover = hasCover,
		sourceType = sourceType
	)
}

/**
 * Sobrecarga simplificada sin parámetros adicionales.
 * Usa valores por defecto para campos opcionales.
 */
fun Song.toEntity(): SongEntity = toEntity(
	sourceType = "SCANNER",
	fileHash = null,
	hasCover = false,
	geniusId = null,
	geniusTitle = null,
	pageviews = null,
	lastPlayed = null,
	confidenceScore = 0f
)

// ==================== HELPERS DE CREACIÓN ====================

/**
 * Crea una SongEntity desde un archivo escaneado con valores calculados.
 * Útil durante el proceso de escaneo de medios.
 *
 * @param filePath Ruta del archivo de audio
 * @param title Título de la canción
 * @param duration Duración en milisegundos
 * @param size Tamaño del archivo en bytes
 * @param mimeType Tipo MIME (ej: "audio/mpeg")
 * @param artistId ID del artista (puede ser null)
 * @param albumId ID del álbum (puede ser null)
 * @param genreId ID del género (puede ser null)
 * @param trackNumber Número de pista
 * @param discNumber Número de disco
 * @param year Año de lanzamiento
 * @param bitrate Bitrate en kbps
 * @param sampleRate Sample rate en Hz
 * @param fileHash Hash del archivo para detección de duplicados
 * @param hasCover Si tiene portada embebida
 */
fun createScannedSongEntity(
	filePath: String,
	title: String,
	duration: Long,
	size: Long,
	mimeType: String,
	artistId: Long? = null,
	albumId: Long? = null,
	genreId: Long? = null,
	trackNumber: Int? = null,
	discNumber: Int? = 1,
	year: Int? = null,
	bitrate: Int? = null,
	sampleRate: Int? = null,
	fileHash: String? = null,
	hasCover: Boolean = false
): SongEntity {
	val now = System.currentTimeMillis()
	
	// Calcular calidad de audio basada en bitrate
	val audioQuality = when {
		bitrate == null -> null
		bitrate >= 1411 -> "HI-RES"
		bitrate >= 320 && mimeType.contains("flac", ignoreCase = true) -> "LOSSLESS"
		bitrate >= 320 -> "HIGH"
		bitrate >= 192 -> "MEDIUM"
		else -> "LOW"
	}
	
	return SongEntity(
		songId = 0, // Autogenerado
		artistId = artistId,
		albumId = albumId,
		genreId = genreId,
		title = title,
		duration = duration,
		trackNumber = trackNumber,
		discNumber = discNumber,
		year = year,
		originalTitle = title, // Guardar el original para auditoría
		originalArtist = null,
		versionType = detectVersionType(title),
		sourceType = "SCANNER",
		filePath = filePath,
		size = size,
		fileHash = fileHash,
		mimeType = mimeType,
		bitrate = bitrate,
		sampleRate = sampleRate,
		audioQuality = audioQuality,
		geniusId = null,
		geniusUrl = null,
		geniusTitle = null,
		isHot = false,
		pageviews = null,
		externalIdsJson = null,
		playCount = 0,
		lastPlayed = null,
		isFavorite = false,
		rating = 0f,
		dateAdded = now,
		dateModified = null,
		hasLyrics = false,
		hasCover = hasCover,
		metadataStatus = IntegrityStatus.CRUDO.name,
		confidenceScore = 0f
	)
}

// ==================== EXTENSION FUNCTIONS ====================

/**
 * Incrementa el contador de reproducciones y actualiza last_played.
 */
fun SongEntity.incrementPlayCount(): SongEntity {
	return this.copy(
		playCount = this.playCount + 1,
		lastPlayed = System.currentTimeMillis()
	)
}

/**
 * Alterna el estado de favorito.
 */
fun SongEntity.toggleFavorite(): SongEntity {
	return this.copy(isFavorite = !this.isFavorite)
}

/**
 * Actualiza la calificación de la canción.
 * @param newRating Nueva calificación (0.0 - 5.0)
 */
fun SongEntity.updateRating(newRating: Float): SongEntity {
	return this.copy(rating = newRating.coerceIn(0f, 5f))
}

/**
 * Actualiza el estado de metadata.
 * @param status Nuevo estado ("PENDING", "VERIFIED", "CORRECTED", "FAILED")
 * @param confidence Puntuación de confianza (0.0 - 1.0)
 */
fun SongEntity.updateMetadataStatus(status: String, confidence: Float = 1f): SongEntity {
	return this.copy(
		metadataStatus = status,
		confidenceScore = confidence.coerceIn(0f, 1f)
	)
}

/**
 * Actualiza la información de Genius.
 */
fun SongEntity.updateGeniusInfo(
	geniusId: String?,
	geniusUrl: String?,
	geniusTitle: String?,
	pageviews: Int?,
	isHot: Boolean
): SongEntity {
	return this.copy(
		geniusId = geniusId,
		geniusUrl = geniusUrl,
		geniusTitle = geniusTitle,
		pageviews = pageviews,
		isHot = isHot
	)
}

/**
 * Marca que la canción tiene letras disponibles.
 */
fun SongEntity.markHasLyrics(): SongEntity {
	return this.copy(hasLyrics = true)
}

// ==================== EXTENSION FUNCTIONS PARA SONG ====================

/**
 * Verifica si la canción es de alta calidad (lossless o hi-res).
 */
val Song.isHighQuality: Boolean
	get() = audioQuality in listOf("LOSSLESS", "HI-RES")

/**
 * Verifica si la canción tiene baja calidad.
 */
val Song.isLowQuality: Boolean
	get() = audioQuality == "LOW" || (bitrate != null && bitrate < 192)

/**
 * Obtiene una etiqueta descriptiva del tipo de versión.
 * Ej: "🎤 Live" o "🎛️ Remix"
 */
fun Song.getVersionLabel(): String? {
	return when (versionType?.uppercase()) {
		"LIVE" -> "🎤 Live"
		"REMIX" -> "🎛️ Remix"
		"ACOUSTIC" -> "🎸 Acoustic"
		"COVER" -> "🎵 Cover"
		"RADIO_EDIT" -> "📻 Radio Edit"
		else -> null
	}
}

/**
 * Verifica si la canción fue reproducida recientemente (últimas 24 horas).
 */
fun Song.wasPlayedRecently(): Boolean {
	// Nota: lastPlayed no está en Song, esta función asume que se puede extender
	// o usar desde SongEntity. Ajustar según necesidad.
	return false // Placeholder
}

/**
 * Obtiene el porcentaje de calificación (0-100).
 */
val Song.ratingPercentage: Int
	get() = ((rating / 5f) * 100).toInt().coerceIn(0, 100)

// ==================== HELPERS PRIVADOS ====================

/**
 * Detecta el tipo de versión basándose en el título.
 * Busca palabras clave comunes en títulos.
 */
private fun detectVersionType(title: String): String? {
	val lowerTitle = title.lowercase()
	return when {
		lowerTitle.contains("live") -> "LIVE"
		lowerTitle.contains("remix") -> "REMIX"
		lowerTitle.contains("acoustic") -> "ACOUSTIC"
		lowerTitle.contains("cover") -> "COVER"
		lowerTitle.contains("radio edit") -> "RADIO_EDIT"
		lowerTitle.contains("instrumental") -> "INSTRUMENTAL"
		lowerTitle.contains("acapella") || lowerTitle.contains("a capella") -> "ACAPELLA"
		else -> null
	}
}