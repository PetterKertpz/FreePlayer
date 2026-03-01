package com.pmk.freeplayer.feature.playlists.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "playlists",
	indices = [
		Index(value = ["name"]),
		Index(value = ["is_system"]), // Para separar "Mis listas" de "Automáticas"
		Index(value = ["is_pinned"])  // Para mostrar las fijadas primero
	]
)
data class PlaylistEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "playlist_id") val playlistId: Long = 0,
	
	// ==================== INFO BÁSICA ====================
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "description") val description: String? = null,
	
	// Ruta de imagen personalizada (si el usuario sube una foto)
	// Si es null, la UI suele hacer un collage con las 4 primeras canciones
	@ColumnInfo(name = "cover_path") val coverPath: String? = null,
	
	// ==================== CONFIGURACIÓN ====================
	@ColumnInfo(name = "hex_color") val hexColor: String? = null, // "#FF0000"
	
	// Si es TRUE, es una lista inteligente (ej: "Agregadas Recientemente")
	// y no se puede borrar ni cambiar nombre.
	@ColumnInfo(name = "is_system") val isSystem: Boolean = false,
	@ColumnInfo(name = "system_type") val systemType: String? = null, // "FAVORITES", "HISTORY"
	
	@ColumnInfo(name = "is_pinned") val isPinned: Boolean = false,
	
	// ==================== CACHÉ DE ESTADÍSTICAS (Optimización) ====================
	// Actualiza estos campos cada vez que agregues/borres una canción.
	// Evita contar 500 canciones cada vez que muestras la lista de playlists.
	@ColumnInfo(name = "song_count") val songCount: Int = 0,
	@ColumnInfo(name = "total_duration_ms") val totalDurationMs: Long = 0,
	
	// ==================== TIMESTAMPS ====================
	@ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
	@ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)