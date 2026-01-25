package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.SongEntity
import com.pmk.freeplayer.data.local.entity.relation.SongArtistEntity
import com.pmk.freeplayer.domain.model.FileSize
import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.TrackDuration

/**
 * 🔄 SONG MAPPER
 * Convierte entre la base de datos (Entity) y la UI (Domain Model).
 */

// ==================== ENTITY -> DOMAIN ====================

/**
 * Opción A: Cuando vienes de una Query con JOINs (SongArtistEntity).
 */
fun SongArtistEntity.toDomain(): Song {
	return this.song.toDomain(
		artistName = this.artistaNombre ?: "Unknown Artist",
		albumName = this.albumNombre, // Pasamos el nombre del álbum
		coverUri = this.portadaPath   // Pasamos la portada del álbum
	)
}

/**
 * Opción B: Cuando solo tienes la Entidad suelta.
 * Mapea TODOS los campos técnicos y envuelve los Value Classes.
 */
fun SongEntity.toDomain(
	artistName: String = "Unknown Artist",
	albumName: String? = null,
	coverUri: String? = null
): Song {
	return Song(
		// --- Identidad Básica ---
		id = this.songId,
		title = this.title,
		artistName = artistName, // Nombre real (del JOIN o default)
		
		// --- Ubicación y Relaciones ---
		filePath = this.filePath,
		trackNumber = this.trackNumber,
		discNumber = this.discNumber,
		year = this.year,
		
		artistId = this.artistId,
		albumId = this.albumId,
		genreId = this.genreId,
		
		// --- Objetos de Valor (Value Classes) ---
		duration = TrackDuration(this.duration), // Long -> TrackDuration
		size = FileSize(this.size),              // Long -> FileSize ✅ CORREGIDO
		
		// --- 1. Detalles Técnicos ---
		mimeType = this.mimeType,
		bitrate = this.bitrate,
		sampleRate = this.sampleRate,
		audioQuality = this.audioQuality,
		
		// --- 2. Auditoría ---
		originalTitle = this.originalTitle,
		originalArtist = this.originalArtist,
		
		// --- 3. Metadatos Ricos ---
		versionType = this.versionType,
		dateAdded = this.dateAdded,
		dateModified = this.dateModified,
		
		// --- 4. Info Externa ---
		geniusUrl = this.geniusUrl,
		isHot = this.isHot,
		externalIds = this.externalIdsJson,
		
		// --- 5. Estado y Stats ---
		isFavorite = this.isFavorite,
		playCount = this.playCount,
		rating = this.rating,
		hasLyrics = this.hasLyrics,
		metadataStatus = this.metadataStatus
	)
	// Nota: 'featuringArtists' se inicializa vacío por defecto en el data class,
	// no hace falta mapearlo aquí a menos que tengas esa lógica lista.
}

// ==================== DOMAIN -> ENTITY ====================

/**
 * Mapeo inverso: De Dominio a Base de Datos.
 */
fun Song.toEntity(): SongEntity {
	return SongEntity(
		// Identidad
		songId = this.id,
		title = this.title,
		
		// Desempaquetamos los Value Classes
		duration = this.duration.millis,
		size = this.size.bytes, // FileSize -> Long ✅ CORREGIDO
		
		// Relaciones
		artistId = this.artistId,
		albumId = this.albumId,
		genreId = this.genreId,
		
		// Información Técnica
		filePath = this.filePath,
		trackNumber = this.trackNumber,
		discNumber = this.discNumber,
		year = this.year,
		mimeType = this.mimeType,
		
		// Calidad
		bitrate = this.bitrate,
		sampleRate = this.sampleRate,
		audioQuality = this.audioQuality,
		
		// Auditoría
		originalTitle = this.originalTitle,
		originalArtist = this.originalArtist,
		versionType = this.versionType,
		
		// Extras
		geniusUrl = this.geniusUrl,
		isHot = this.isHot,
		externalIdsJson = this.externalIds,
		
		// Estadísticas y Flags
		playCount = this.playCount,
		rating = this.rating,
		isFavorite = this.isFavorite,
		hasLyrics = this.hasLyrics,
		
		// Sistema
		metadataStatus = this.metadataStatus,
		dateAdded = this.dateAdded,
		dateModified = this.dateModified,
		
		// Campos que Song no tiene pero Entity sí:
		hasCover = false, // Se calculará al escanear
		fileHash = null,
		sourceType = "MANUAL",
		geniusTitle = null,
		pageviews = null,
		lastPlayed = null,
		confidenceScore = 0f
	)
}

// ==================== LISTAS ====================

fun List<SongArtistEntity>.toDomain(): List<Song> = map { it.toDomain() }