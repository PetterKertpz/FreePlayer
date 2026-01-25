package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.PlaylistEntity
import com.pmk.freeplayer.domain.model.Playlist
import com.pmk.freeplayer.domain.model.TrackDuration

/**
 * 🔄 PLAYLIST MAPPER
 * Convierte entre la base de datos (Entity) y la UI (Domain Model).
 */

// ==================== ENTITY -> DOMAIN ====================

fun PlaylistEntity.toDomain(): Playlist {
	return Playlist(
		id = this.playlistId,
		name = this.name,
		description = this.description,
		
		// Mapeo directo de ruta a URI
		coverUri = this.coverPath,
		hexColor = this.hexColor,
		
		// Configuración
		isSystem = this.isSystem,
		isPinned = this.isPinned,
		
		// Stats: Convertimos Long -> TrackDuration
		songCount = this.songCount,
		totalDuration = TrackDuration(this.totalDurationMs),
		
		// Timestamps
		createdAt = this.createdAt,
		updatedAt = this.updatedAt
	)
}

// ==================== DOMAIN -> ENTITY ====================

/**
 * Mapper inverso.
 * Nota: 'systemType' se deja en null por defecto porque el Dominio no suele conocer
 * los strings internos de la BD ("FAVORITES", "HISTORY").
 * Si necesitas preservar el systemType al editar, usa .copy() en el Repository.
 */
fun Playlist.toEntity(): PlaylistEntity {
	return PlaylistEntity(
		playlistId = this.id,
		name = this.name,
		description = this.description,
		
		coverPath = this.coverUri,
		hexColor = this.hexColor,
		
		isSystem = this.isSystem,
		systemType = null, // Se asume null o se maneja en el Repositorio si es una System List
		isPinned = this.isPinned,
		
		// Stats: Convertimos TrackDuration -> Long
		songCount = this.songCount,
		totalDurationMs = this.totalDuration.millis,
		
		createdAt = this.createdAt,
		updatedAt = this.updatedAt
	)
}

// ==================== LISTAS ====================

fun List<PlaylistEntity>.toDomain(): List<Playlist> = map { it.toDomain() }