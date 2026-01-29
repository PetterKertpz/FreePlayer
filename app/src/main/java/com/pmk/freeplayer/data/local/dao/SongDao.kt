package com.pmk.freeplayer.data.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pmk.freeplayer.data.local.dto.LibraryStatsDto
import com.pmk.freeplayer.data.local.dto.TopItemDto
import com.pmk.freeplayer.data.local.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
	
	// ==================== INSERTS & UPDATES ====================
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(song: SongEntity): Long
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(songs: List<SongEntity>): List<Long>
	
	@Update
	suspend fun update(song: SongEntity)
	
	// Upsert inteligente para el escáner (preserva fecha de agregado)
	@Transaction
	suspend fun safeUpsert(song: SongEntity): Long {
		val existingId = getSongIdByPath(song.filePath)
		return if (existingId != null) {
			val dateAdded = getDateAdded(existingId) ?: System.currentTimeMillis()
			val playCount = getPlayCount(existingId) ?: 0
			
			// Mantenemos los datos de usuario al actualizar el archivo
			val updatedSong = song.copy(
				songId = existingId,
				dateAdded = dateAdded,
				playCount = playCount,
				isFavorite = isFavorite(existingId)
			)
			update(updatedSong)
			existingId
		} else {
			insert(song)
		}
	}
	
	// ==================== DELETES ====================
	
	@Delete
	suspend fun delete(song: SongEntity)
	
	@Query("DELETE FROM songs WHERE song_id = :id")
	suspend fun deleteById(id: Long)
	
	@Query("DELETE FROM songs WHERE file_path = :path")
	suspend fun deleteByPath(path: String)
	
	@Query("DELETE FROM songs WHERE file_path IN (:paths)")
	suspend fun deleteByPaths(paths: List<String>)
	
	// Limpieza de archivos inexistentes (huérfanos)
	@Query("DELETE FROM songs WHERE file_path IS NULL OR length(file_path) = 0")
	suspend fun cleanInvalidSongs()
	
	// ==================== QUERIES BÁSICAS ====================
	
	@Query("SELECT * FROM songs WHERE song_id = :id LIMIT 1")
	suspend fun getSongById(id: Long): SongEntity?
	
	@Query("SELECT * FROM songs WHERE song_id = :id LIMIT 1")
	fun getSongByIdFlow(id: Long): Flow<SongEntity?>
	
	@Query("SELECT * FROM songs ORDER BY title ASC")
	fun getAllSongs(): Flow<List<SongEntity>>
	
	@Query("SELECT * FROM songs ORDER BY title ASC LIMIT :limit OFFSET :offset")
	suspend fun getSongsPaged(limit: Int, offset: Int): List<SongEntity>
	
	@Query("SELECT COUNT(*) FROM songs")
	suspend fun getSongCount(): Int
	
	// ==================== FILTROS POR RELACIÓN ====================
	
	@Query("SELECT * FROM songs WHERE artist_id = :artistId ORDER BY title ASC")
	fun getSongsByArtist(artistId: Long): Flow<List<SongEntity>>
	
	@Query("SELECT * FROM songs WHERE album_id = :albumId ORDER BY track_number ASC")
	fun getSongsByAlbum(albumId: Long): Flow<List<SongEntity>>
	
	@Query("SELECT * FROM songs WHERE genre_id = :genreId ORDER BY title ASC")
	fun getSongsByGenre(genreId: Long): Flow<List<SongEntity>>
	
	@Query("SELECT * FROM songs WHERE is_favorite = 1 ORDER BY title ASC")
	fun getFavoriteSongs(): Flow<List<SongEntity>>
	
	// ==================== BÚSQUEDA AVANZADA ====================
	
	/**
	 * Busca en título.
	 * Si necesitas buscar en artista/álbum, se recomienda usar una query con JOIN
	 * o FTS (Full Text Search), pero para empezar esto es rápido.
	 */
	@Query("""
        SELECT * FROM songs 
        WHERE title LIKE '%' || :query || '%' 
        ORDER BY 
            CASE WHEN title LIKE :query || '%' THEN 1 ELSE 2 END,
            title ASC
        LIMIT :limit
    """)
	fun searchSongs(query: String, limit: Int = 50): Flow<List<SongEntity>>
	
	// ==================== METADATA SCANNER HELPERS ====================
	
	@Query("SELECT song_id FROM songs WHERE file_path = :path LIMIT 1")
	suspend fun getSongIdByPath(path: String): Long?
	
	@Query("SELECT date_added FROM songs WHERE song_id = :id LIMIT 1")
	suspend fun getDateAdded(id: Long): Long?
	
	@Query("SELECT play_count FROM songs WHERE song_id = :id LIMIT 1")
	suspend fun getPlayCount(id: Long): Int?
	
	@Query("SELECT is_favorite FROM songs WHERE song_id = :id LIMIT 1")
	suspend fun isFavorite(id: Long): Boolean
	
	@Query("SELECT file_path FROM songs")
	suspend fun getAllFilePaths(): List<String>
	
	// Obtiene info mínima para comparar cambios en el escáner
	@Query("SELECT song_id, file_path, date_modified, file_size FROM songs")
	suspend fun getScanInfo(): List<SongScanInfo>
	
	// ==================== ESTADÍSTICAS & REPRODUCCIÓN ====================
	
	@Query("UPDATE songs SET play_count = play_count + 1, last_played = :timestamp WHERE song_id = :id")
	suspend fun incrementPlayCount(id: Long, timestamp: Long = System.currentTimeMillis())
	
	@Query("UPDATE songs SET is_favorite = :isFavorite WHERE song_id = :id")
	suspend fun setFavorite(id: Long, isFavorite: Boolean)
	
	@Query("SELECT * FROM songs ORDER BY play_count DESC LIMIT :limit")
	fun getMostPlayedSongs(limit: Int = 20): Flow<List<SongEntity>>
	
	@Query("SELECT * FROM songs ORDER BY date_added DESC LIMIT :limit")
	fun getRecentSongs(limit: Int = 20): Flow<List<SongEntity>>
	
	// ==================== CALIDAD DE DATOS (Smart Playlists) ====================
	
	@Query("SELECT * FROM songs WHERE metadata_status = :status LIMIT :limit")
	suspend fun getSongsByMetadataStatus(status: String, limit: Int = 50): List<SongEntity>
	
	@Query("SELECT COALESCE(SUM(duration), 0) FROM songs WHERE song_id IN (:ids)")
	suspend fun getTotalDurationForIds(ids: List<Long>): Long
	
	// ==================== DTOs INTERNOS ====================
	
	data class SongScanInfo(
		@ColumnInfo(name = "song_id") val songId: Long,
		@ColumnInfo(name = "file_path") val filePath: String,
		@ColumnInfo(name = "date_modified") val dateModified: Long?,
		@ColumnInfo(name = "file_size") val fileSize: Long
	)
	
	// ==================== DTOs Y ESTADÍSTICAS ====================
	
	/**
	 * 📊 RESUMEN DE LA BIBLIOTECA
	 * Una sola query que cuenta todo de golpe. Muy eficiente.
	 */
	@Query("""
    SELECT
        (SELECT COUNT(*) FROM songs) as total_songs,
        (SELECT COUNT(*) FROM artists) as total_artists,
        (SELECT COUNT(*) FROM albums) as total_albums,
        (SELECT COUNT(*) FROM genres) as total_genres,
        (SELECT COALESCE(SUM(duration), 0) FROM songs) as total_duration_ms,
        (SELECT COALESCE(SUM(file_size), 0) FROM songs) as total_size,
        (SELECT MAX(date_added) FROM songs) as last_added,
        (SELECT COUNT(*) FROM songs WHERE metadata_status = 'CRUDO') as count_crude,
        (SELECT COUNT(*) FROM songs WHERE metadata_status = 'LIMPIO') as count_clean,
        (SELECT COUNT(*) FROM songs WHERE metadata_status = 'ENRIQUECIDO') as count_enriched,
        (SELECT COUNT(*) FROM songs WHERE has_lyrics = 1) as count_lyrics
""")
	suspend fun getLibraryStats(): LibraryStatsDto
	
	/**
	 * 🏆 TOP ARTISTAS (Mapeado a TopItemDto)
	 * Fíjate en el uso de 'AS': Renombramos 'artist_id' a 'id' para que coincida con el DTO.
	 */
	@Query("""
    SELECT
        artist_id AS id,
        name AS name,
        NULL AS subtitle,
        play_count AS count,
        image_path AS image_uri
    FROM artists
    ORDER BY play_count DESC
    LIMIT :limit
""")
	suspend fun getTopArtistsDto(limit: Int = 10): List<TopItemDto>
	
	/**
	 * 🏆 TOP CANCIONES (Mapeado a TopItemDto)
	 * Aquí 'subtitle' es el nombre del artista.
	 */
	@Query("""
    SELECT
        s.song_id AS id,
        s.title AS name,
        a.name AS subtitle,
        s.play_count AS count,
        al.cover_path AS image_uri
    FROM songs s
    LEFT JOIN artists a ON s.artist_id = a.artist_id
    LEFT JOIN albums al ON s.album_id = al.album_id
    ORDER BY s.play_count DESC
    LIMIT :limit
""")
	suspend fun getTopSongsDto(limit: Int = 10): List<TopItemDto>
	
	@Query("SELECT * FROM songs WHERE metadata_status = 'CRUDO' LIMIT :limit")
	suspend fun getSongsPendingCleaning(limit: Int = 100): List<SongEntity>
	
	/** Canciones limpias, pendientes de enriquecimiento */
	@Query("SELECT * FROM songs WHERE metadata_status = 'LIMPIO' LIMIT :limit")
	suspend fun getSongsPendingEnrichment(limit: Int = 100): List<SongEntity>
	
	/** Canciones ya enriquecidas */
	@Query("SELECT * FROM songs WHERE metadata_status = 'ENRIQUECIDO'")
	fun getEnrichedSongs(): Flow<List<SongEntity>>
	
	/** Actualizar estado de metadatos */
	@Query("""
    UPDATE songs
    SET metadata_status = :status,
        confidence_score = :confidence
    WHERE song_id = :songId
""")
	suspend fun updateMetadataStatus(songId: Long, status: String, confidence: Float = 1f)
	
	/** Marcar como LIMPIO con timestamp */
	@Query("""
    UPDATE songs
    SET metadata_status = 'LIMPIO',
        fecha_limpieza = :timestamp,
        title = :cleanTitle,
        confidence_score = :confidence
    WHERE song_id = :songId
""")
	suspend fun markAsCleaned(
		songId: Long,
		cleanTitle: String,
		confidence: Float,
		timestamp: Long = System.currentTimeMillis()
	)
	
	/** Marcar como ENRIQUECIDO con timestamp */
	@Query("""
    UPDATE songs
    SET metadata_status = 'ENRIQUECIDO',
        fecha_enriquecimiento = :timestamp,
        has_lyrics = :hasLyrics,
        genius_id = :geniusId,
        genius_url = :geniusUrl,
        confidence_score = :confidence
    WHERE song_id = :songId
""")
	suspend fun markAsEnriched(
		songId: Long,
		hasLyrics: Boolean,
		geniusId: String?,
		geniusUrl: String?,
		confidence: Float,
		timestamp: Long = System.currentTimeMillis()
	)
	
	/** Conteo por estado */
	@Query("SELECT COUNT(*) FROM songs WHERE metadata_status = :status")
	suspend fun countByMetadataStatus(status: String): Int
}

