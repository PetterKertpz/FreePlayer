package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.PlaylistEntity
import com.pmk.freeplayer.domain.model.Playlist
import com.pmk.freeplayer.domain.model.TrackDuration

/**
 * 🔄 PLAYLIST MAPPER
 *
 * Convierte entre la capa de persistencia (Entity) y la capa de dominio (Model).
 * Maneja la conversión de duraciones, timestamps y configuraciones de playlist.
 */

// ==================== ENTITY -> DOMAIN ====================

/**
 * Convierte PlaylistEntity (DB) a Playlist (Domain).
 * Simplifica los datos para la capa de dominio/UI.
 */
fun PlaylistEntity.toDomain(): Playlist {
	return Playlist(
		id = this.playlistId,
		name = this.name,
		description = this.description,
		
		// Media
		coverUri = this.coverPath,
		hexColor = this.hexColor,
		
		// Configuración
		isSystem = this.isSystem,
		isPinned = this.isPinned,
		
		// Estadísticas (conversión Long -> TrackDuration)
		songCount = this.songCount,
		totalDuration = TrackDuration(this.totalDurationMs),
		
		// Timestamps
		createdAt = this.createdAt,
		updatedAt = this.updatedAt
	)
}

/**
 * Convierte lista de entities a lista de domain models.
 */
fun List<PlaylistEntity>.toDomain(): List<Playlist> = map { it.toDomain() }

// ==================== DOMAIN -> ENTITY ====================

/**
 * Convierte Playlist (Domain) a PlaylistEntity (DB).
 *
 * Nota: systemType se maneja por separado ya que el modelo de dominio
 * no necesita conocer los tipos internos del sistema ("FAVORITES", "HISTORY").
 *
 * @param systemType Tipo de playlist del sistema (ej: "FAVORITES", "HISTORY", "RECENT")
 */
fun Playlist.toEntity(
	systemType: String? = null
): PlaylistEntity {
	return PlaylistEntity(
		playlistId = this.id,
		name = this.name,
		description = this.description,
		
		// Media
		coverPath = this.coverUri,
		hexColor = this.hexColor,
		
		// Configuración
		isSystem = this.isSystem,
		systemType = systemType,
		isPinned = this.isPinned,
		
		// Estadísticas (conversión TrackDuration -> Long)
		songCount = this.songCount,
		totalDurationMs = this.totalDuration.millis,
		
		// Timestamps
		createdAt = this.createdAt,
		updatedAt = this.updatedAt
	)
}

/**
 * Sobrecarga simplificada sin systemType.
 * Útil para playlists de usuario (no del sistema).
 */
fun Playlist.toEntity(): PlaylistEntity = toEntity(systemType = null)

// ==================== HELPERS DE CREACIÓN ====================

/**
 * Crea una nueva PlaylistEntity vacía con valores por defecto.
 * Útil para crear playlists de usuario desde cero.
 *
 * @param name Nombre de la playlist
 * @param description Descripción opcional
 * @param coverPath Ruta de la portada personalizada
 * @param hexColor Color en formato hexadecimal (ej: "#FF0000")
 * @param isPinned Si la playlist está fijada arriba
 */
fun createUserPlaylistEntity(
	name: String,
	description: String? = null,
	coverPath: String? = null,
	hexColor: String? = null,
	isPinned: Boolean = false
): PlaylistEntity {
	val now = System.currentTimeMillis()
	return PlaylistEntity(
		playlistId = 0, // Autogenerado
		name = name,
		description = description,
		coverPath = coverPath,
		hexColor = hexColor,
		isSystem = false,
		systemType = null,
		isPinned = isPinned,
		songCount = 0,
		totalDurationMs = 0,
		createdAt = now,
		updatedAt = now
	)
}

/**
 * Crea una PlaylistEntity del sistema con valores por defecto.
 * Útil para crear playlists automáticas como "Favoritos", "Historial", etc.
 *
 * @param name Nombre de la playlist del sistema
 * @param systemType Tipo del sistema (ej: "FAVORITES", "HISTORY", "RECENT")
 * @param description Descripción opcional
 * @param hexColor Color en formato hexadecimal
 * @param isPinned Si la playlist está fijada arriba (las del sistema suelen estar fijadas)
 */
fun createSystemPlaylistEntity(
	name: String,
	systemType: String,
	description: String? = null,
	hexColor: String? = null,
	isPinned: Boolean = true
): PlaylistEntity {
	val now = System.currentTimeMillis()
	return PlaylistEntity(
		playlistId = 0, // Autogenerado
		name = name,
		description = description,
		coverPath = null, // Las playlists del sistema usan collages
		hexColor = hexColor,
		isSystem = true,
		systemType = systemType,
		isPinned = isPinned,
		songCount = 0,
		totalDurationMs = 0,
		createdAt = now,
		updatedAt = now
	)
}

/**
 * Actualiza las estadísticas de una PlaylistEntity.
 * Útil cuando se agregan o eliminan canciones.
 *
 * @param newSongCount Nuevo número de canciones
 * @param newTotalDurationMs Nueva duración total en milisegundos
 */
fun PlaylistEntity.updateStats(
	newSongCount: Int,
	newTotalDurationMs: Long
): PlaylistEntity {
	return this.copy(
		songCount = newSongCount,
		totalDurationMs = newTotalDurationMs,
		updatedAt = System.currentTimeMillis()
	)
}

/**
 * Actualiza el timestamp de modificación de la playlist.
 * Útil cuando se modifica cualquier aspecto de la playlist.
 */
fun PlaylistEntity.touch(): PlaylistEntity {
	return this.copy(updatedAt = System.currentTimeMillis())
}

/**
 * Actualiza la portada de la playlist.
 *
 * @param newCoverPath Nueva ruta de la portada (null para usar collage)
 */
fun PlaylistEntity.updateCover(newCoverPath: String?): PlaylistEntity {
	return this.copy(
		coverPath = newCoverPath,
		updatedAt = System.currentTimeMillis()
	)
}

/**
 * Alterna el estado de fijado de la playlist.
 */
fun PlaylistEntity.togglePin(): PlaylistEntity {
	return this.copy(
		isPinned = !isPinned,
		updatedAt = System.currentTimeMillis()
	)
}

// ==================== EXTENSION FUNCTIONS PARA DOMAIN ====================

/**
 * Verifica si la playlist está vacía.
 */
val Playlist.isEmpty: Boolean
	get() = songCount == 0

/**
 * Verifica si la playlist tiene contenido.
 */
val Playlist.isNotEmpty: Boolean
	get() = songCount > 0

/**
 * Obtiene una representación legible de la duración.
 * Ejemplo: "1h 23m" o "45m"
 */
fun Playlist.getFormattedDuration(): String {
	val hours = totalDuration.inHours
	val minutes = totalDuration.inMinutes % 60
	
	return when {
		hours > 0 -> "${hours}h ${minutes}m"
		minutes > 0 -> "${minutes}m"
		else -> "0m"
	}
}

/**
 * Obtiene un texto descriptivo de la playlist.
 * Ejemplo: "15 canciones • 1h 23m"
 */
fun Playlist.getInfoText(): String {
	val songText = if (songCount == 1) "1 canción" else "$songCount canciones"
	val durationText = getFormattedDuration()
	return "$songText • $durationText"
}