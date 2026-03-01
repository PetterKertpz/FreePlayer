package com.pmk.freeplayer.feature.statistics.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pmk.freeplayer.feature.statistics.data.local.entity.PlaybackHistoryEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

@Dao
interface PlaybackHistoryDao {
	
	// ==================== INSERTS & UPDATES ====================
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(history: PlaybackHistoryEntity): Long
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(historyList: List<PlaybackHistoryEntity>)
	
	@Update
	suspend fun update(history: PlaybackHistoryEntity)
	
	@Query("""
        UPDATE playback_history
        SET played_duration_ms = :playedDuration,
            is_completed = :isCompleted
        WHERE history_id = :historyId
    """)
	suspend fun updatePlaybackProgress(historyId: Long, playedDuration: Long, isCompleted: Boolean)
	
	// ==================== DELETES ====================
	
	@Delete
	suspend fun delete(history: PlaybackHistoryEntity)
	
	@Query("DELETE FROM playback_history WHERE history_id = :historyId")
	suspend fun deleteById(historyId: Long)
	
	@Query("DELETE FROM playback_history WHERE user_id = :userId")
	suspend fun deleteByUserId(userId: Long)
	
	@Query("DELETE FROM playback_history WHERE timestamp < :timestampLimit")
	suspend fun deleteOldHistory(timestampLimit: Long)
	
	// ==================== QUERIES BÁSICAS ====================
	
	@Query("SELECT * FROM playback_history WHERE history_id = :id LIMIT 1")
	suspend fun getHistoryById(id: Long): PlaybackHistoryEntity?
	
	@Query("SELECT * FROM playback_history WHERE user_id = :userId ORDER BY timestamp DESC")
	fun getHistoryByUserId(userId: Long): Flow<List<PlaybackHistoryEntity>>
	
	@Query("SELECT * FROM playback_history WHERE user_id = :userId ORDER BY timestamp DESC LIMIT :limit")
	fun getRecentHistory(userId: Long, limit: Int = 50): Flow<List<PlaybackHistoryEntity>>
	
	@Query("SELECT COUNT(*) FROM playback_history WHERE user_id = :userId")
	fun getHistoryCount(userId: Long): Flow<Int>
	
	// ==================== FILTROS POR CANCIÓN ====================
	
	@Query("SELECT * FROM playback_history WHERE user_id = :userId AND song_id = :songId ORDER BY timestamp DESC")
	fun getHistoryForSong(userId: Long, songId: Long): Flow<List<PlaybackHistoryEntity>>
	
	@Query("SELECT COUNT(*) FROM playback_history WHERE user_id = :userId AND song_id = :songId")
	suspend fun getPlayCountForSong(userId: Long, songId: Long): Int
	
	@Query("SELECT timestamp FROM playback_history WHERE user_id = :userId AND song_id = :songId ORDER BY timestamp DESC LIMIT 1")
	suspend fun getLastPlayedTimestamp(userId: Long, songId: Long): Long?
	
	// ==================== ESTADÍSTICAS GENERALES ====================
	
	@Query("SELECT SUM(played_duration_ms) FROM playback_history WHERE user_id = :userId")
	suspend fun getTotalListeningTime(userId: Long): Long?
	
	@Query("SELECT COUNT(DISTINCT song_id) FROM playback_history WHERE user_id = :userId")
	suspend fun getUniqueSongsPlayedCount(userId: Long): Int
	
	@Query("SELECT COUNT(DISTINCT DATE(timestamp / 1000, 'unixepoch')) FROM playback_history WHERE user_id = :userId")
	suspend fun getActiveDaysCount(userId: Long): Int
	
	// ==================== TOP CANCIONES (Analytics) ====================
	
	@Query("""
        SELECT song_id as songId, COUNT(*) as count
        FROM playback_history
        WHERE user_id = :userId
        GROUP BY song_id
        ORDER BY count DESC
        LIMIT :limit
    """)
	suspend fun getTopPlayedSongs(userId: Long, limit: Int = 20): List<TopSongStats>
	
	@Query("""
        SELECT song_id as songId, COUNT(*) as count
        FROM playback_history
        WHERE user_id = :userId AND timestamp >= :fromTimestamp
        GROUP BY song_id
        ORDER BY count DESC
        LIMIT :limit
    """)
	suspend fun getTopPlayedSongsInPeriod(userId: Long, fromTimestamp: Long, limit: Int = 20): List<TopSongStats>
	
	// ==================== ANÁLISIS TEMPORAL (Gráficos) ====================
	
	@Query("""
        SELECT
            CAST(strftime('%H', timestamp / 1000, 'unixepoch') AS INTEGER) as hour,
            COUNT(*) as count
        FROM playback_history
        WHERE user_id = :userId
        GROUP BY hour
        ORDER BY hour ASC
    """)
	suspend fun getPlaysByHour(userId: Long): List<HourStats>
	
	@Query("""
        SELECT
            DATE(timestamp / 1000, 'unixepoch') as date,
            COUNT(*) as count
        FROM playback_history
        WHERE user_id = :userId AND timestamp >= :fromTimestamp
        GROUP BY date
        ORDER BY date DESC
    """)
	suspend fun getPlaysByDay(userId: Long, fromTimestamp: Long): List<DayStats>
	
	// ==================== LIMPIEZA ====================
	
	// Limpia historial de canciones que ya no existen en la biblioteca
	@Query("DELETE FROM playback_history WHERE song_id NOT IN (SELECT song_id FROM songs)")
	suspend fun cleanOrphanHistory()
	
	// Limpia reproducciones muy cortas (ruido)
	@Query("DELETE FROM playback_history WHERE user_id = :userId AND played_duration_ms < :minDurationMs")
	suspend fun deleteShortPlays(userId: Long, minDurationMs: Long = 5000)
	
	// ==================== UTILIDAD DE REGISTRO RÁPIDO ====================
	
	@Transaction
	suspend fun logPlay(
		userId: Long,
		songId: Long,
		totalDurationMs: Long,
		playedDurationMs: Long,
		source: String,       // "ALBUM", "PLAYLIST"
		playbackMode: String, // "NORMAL", "SHUFFLE"  <-- ¡AQUÍ ESTÁ LO QUE FALTABA!
		contextId: Long? = null
	): Long {
		
		// Calculamos si se escuchó más del 80%
		val isCompleted = if (totalDurationMs > 0) {
			(playedDurationMs.toFloat() / totalDurationMs.toFloat()) > 0.8f
		} else {
			false
		}
		
		val history = PlaybackHistoryEntity(
			userId = userId,
			songId = songId,
			timestamp = System.currentTimeMillis(),
			
			totalDurationMs = totalDurationMs,
			playedDurationMs = playedDurationMs,
			completionRatio = if (totalDurationMs > 0) playedDurationMs.toFloat() / totalDurationMs else 0f,
			isCompleted = isCompleted,
			
			source = source,
			contextId = contextId,
			
			// ✅ CORREGIDO: Ahora pasamos el modo de reproducción obligatorio
			playbackMode = playbackMode,
			
			// Datos calculados automáticamente para Analytics
			hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
			dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
		)
		
		return insert(history)
	}
}

// ==================== DTOs INTERNOS ====================

data class TopSongStats(
	val songId: Long,
	val count: Int
)

data class HourStats(
	val hour: Int,
	val count: Int
)

data class DayStats(
	val date: String,
	val count: Int
)