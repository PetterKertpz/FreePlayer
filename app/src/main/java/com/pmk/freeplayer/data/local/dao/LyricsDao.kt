package com.pmk.freeplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pmk.freeplayer.data.local.dto.TopItemDto // Asegúrate de usar el DTO correcto
import com.pmk.freeplayer.data.local.entity.LyricsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LyricsDao {
	
	// ==================== INSERTS & UPDATES ====================
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(lyrics: LyricsEntity): Long
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(lyricsList: List<LyricsEntity>)
	
	@Update
	suspend fun update(lyrics: LyricsEntity)
	
	// Upsert Manual (Lógica de negocio en Transacción)
	@Transaction
	suspend fun upsertLyrics(
		songId: Long,
		plainText: String,
		isSynced: Boolean = false,
		source: String = "MANUAL"
	) {
		val existing = getLyricsBySongIdSync(songId)
		if (existing != null) {
			val updated = existing.copy(
				plainLyrics = plainText,
				isSynced = isSynced,
				source = source,
				dateAdded = System.currentTimeMillis()
			)
			update(updated)
		} else {
			val newLyrics = LyricsEntity(
				songId = songId,
				plainLyrics = plainText,
				isSynced = isSynced,
				source = source
			)
			insert(newLyrics)
		}
	}
	
	// ==================== DELETES ====================
	
	@Delete
	suspend fun delete(lyrics: LyricsEntity)
	
	@Query("DELETE FROM lyrics WHERE song_id = :songId")
	suspend fun deleteBySongId(songId: Long)
	
	@Query("DELETE FROM lyrics WHERE plain_lyrics IS NULL OR LENGTH(plain_lyrics) < 10")
	suspend fun deleteInvalidLyrics()
	
	// ==================== QUERIES ====================
	
	@Query("SELECT * FROM lyrics WHERE song_id = :songId LIMIT 1")
	fun getLyricsBySongId(songId: Long): Flow<LyricsEntity?>
	
	@Query("SELECT * FROM lyrics WHERE song_id = :songId LIMIT 1")
	suspend fun getLyricsBySongIdSync(songId: Long): LyricsEntity?
	
	@Query("SELECT * FROM lyrics ORDER BY date_added DESC")
	fun getAllLyrics(): Flow<List<LyricsEntity>>
	
	// ==================== BÚSQUEDA ====================
	
	/**
	 * Busca dentro del contenido de la letra.
	 * Útil para "Sé que la canción dice... pero no sé el título".
	 */
	@Query("""
        SELECT * FROM lyrics 
        WHERE plain_lyrics LIKE '%' || :query || '%' 
        ORDER BY song_id ASC 
        LIMIT :limit
    """)
	suspend fun searchLyricsContent(query: String, limit: Int = 50): List<LyricsEntity>
	
	// ==================== ESTADÍSTICAS & MANTENIMIENTO ====================
	
	@Query("SELECT COUNT(*) FROM lyrics")
	suspend fun getLyricsCount(): Int
	
	@Query("SELECT EXISTS(SELECT 1 FROM lyrics WHERE song_id = :songId)")
	suspend fun hasLyrics(songId: Long): Boolean
	
	@Query("SELECT COUNT(*) FROM lyrics WHERE is_synced = 1")
	suspend fun getSyncedLyricsCount(): Int
	
	/**
	 * Encuentra canciones que NO tienen letra.
	 * Útil para el worker de "Descargar letras faltantes".
	 */
	@Query("""
        SELECT s.song_id 
        FROM songs s 
        LEFT JOIN lyrics l ON s.song_id = l.song_id 
        WHERE l.lyrics_id IS NULL
        LIMIT :limit
    """)
	suspend fun getMissingLyricsSongIds(limit: Int = 100): List<Long>
	
	/**
	 * Estadísticas por fuente (Genius, Manual, etc.)
	 * Mapeado al DTO genérico 'TopItemDto' para reutilizar lógica de UI.
	 */
	@Query("""
        SELECT 
            0 as id,
            source as name, 
            NULL as subtitle,
            COUNT(*) as count,
            NULL as image_uri
        FROM lyrics 
        GROUP BY source 
        ORDER BY count DESC
    """)
	suspend fun getLyricsStatsBySource(): List<TopItemDto>
}