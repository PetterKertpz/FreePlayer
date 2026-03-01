package com.pmk.freeplayer.feature.statistics.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pmk.freeplayer.feature.songs.data.local.entity.SongEntity

@Entity(
	tableName = "playback_history",
	foreignKeys = [
		ForeignKey(
			entity = SongEntity::class,
			parentColumns = ["song_id"],
			childColumns = ["song_id"],
			onDelete = ForeignKey.Companion.CASCADE // Si borras la canción, se borra su historial
		)
		// Descomenta si tienes UserEntity implementada
		/*, ForeignKey(
			 entity = UserEntity::class,
			 parentColumns = ["user_id"],
			 childColumns = ["user_id"],
			 onDelete = ForeignKey.CASCADE
		)*/
	],
	indices = [
		Index(value = ["song_id"]),
		Index(value = ["timestamp"]), // Para ordenar "Escuchado recientemente"
		Index(value = ["source"]),    // Para filtrar "Escuchado desde Playlist"
		Index(value = ["is_completed"]), // Para contar reproducciones reales
		
		// Índices compuestos para Analytics Rápido
		Index(value = ["song_id", "timestamp"]), // Historial de una canción específica
		Index(value = ["hour_of_day", "day_of_week"]) // Para "Patrones de Escucha"
	]
)
data class PlaybackHistoryEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "history_id") val historyId: Long = 0,
	
	@ColumnInfo(name = "user_id") val userId: Long = 1, // Default 1 para apps offline
	@ColumnInfo(name = "song_id") val songId: Long,
	
	// ==================== TIEMPO ====================
	@ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis(),
	
	@ColumnInfo(name = "played_duration_ms") val playedDurationMs: Long,
	@ColumnInfo(name = "total_duration_ms") val totalDurationMs: Long,
	
	// Calculado: played / total. Útil para queries: "SELECT AVG(completion_ratio)..."
	@ColumnInfo(name = "completion_ratio") val completionRatio: Float = 0f,
	@ColumnInfo(name = "is_completed") val isCompleted: Boolean = false, // > 80%
	
	// ==================== CONTEXTO (¿De dónde vino?) ====================
	// Enums guardados como String: "ALBUM", "PLAYLIST", "SEARCH"
	@ColumnInfo(name = "source") val source: String,
	
	@ColumnInfo(name = "context_id") val contextId: Long? = null, // ID del album/playlist
	@ColumnInfo(name = "context_name") val contextName: String? = null, // "Rock Clásico"
	@ColumnInfo(name = "context_position") val contextPosition: Int? = null,
	
	// ==================== TÉCNICO ====================
	@ColumnInfo(name = "quality") val quality: String? = null, // "MP3_320", "FLAC"
	@ColumnInfo(name = "playback_mode") val playbackMode: String, // "SHUFFLE", "NORMAL"
	@ColumnInfo(name = "volume") val volume: Float? = null, // 0.0 - 1.0
	@ColumnInfo(name = "eq_enabled") val eqEnabled: Boolean = false,
	
	// ==================== DISPOSITIVO ====================
	@ColumnInfo(name = "device_id") val deviceId: String? = null,
	@ColumnInfo(name = "output_type") val outputType: String? = null, // "HEADPHONES", "SPEAKER"
	
	// ==================== COMPORTAMIENTO ====================
	@ColumnInfo(name = "pause_count") val pauseCount: Int = 0,
	@ColumnInfo(name = "seek_count") val seekCount: Int = 0,
	@ColumnInfo(name = "liked_during_playback") val likedDuringPlayback: Boolean = false,
	
	// ==================== METADATA ANALÍTICA ====================
	// Guardar esto pre-calculado acelera mucho los gráficos
	@ColumnInfo(name = "hour_of_day") val hourOfDay: Int, // 0-23
	@ColumnInfo(name = "day_of_week") val dayOfWeek: Int, // 1-7
	@ColumnInfo(name = "location") val location: String? = null,
	
	@ColumnInfo(name = "sync_id") val syncId: String? = null
)