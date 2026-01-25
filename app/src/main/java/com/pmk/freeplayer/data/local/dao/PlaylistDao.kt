package com.pmk.freeplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pmk.freeplayer.data.local.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
	
	// ==================== INSERTS & UPDATES ====================
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(playlist: PlaylistEntity): Long
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(playlists: List<PlaylistEntity>)
	
	@Update
	suspend fun update(playlist: PlaylistEntity)
	
	// Actualización rápida de UI (Nombre/Descripción/Portada)
	@Query("""
        UPDATE playlists
        SET name = :name,
            description = :description,
            cover_path = :coverPath,
            updated_at = :timestamp
        WHERE playlist_id = :id
    """)
	suspend fun updateBasicInfo(
		id: Long,
		name: String,
		description: String?,
		coverPath: String?,
		timestamp: Long = System.currentTimeMillis()
	)
	
	// ==================== DELETES ====================
	
	@Delete
	suspend fun delete(playlist: PlaylistEntity)
	
	@Query("DELETE FROM playlists WHERE playlist_id = :id")
	suspend fun deleteById(id: Long)
	
	// Limpieza de playlists vacías (Excepto las de sistema)
	@Query("DELETE FROM playlists WHERE song_count = 0 AND is_system = 0")
	suspend fun deleteEmptyPlaylists()
	
	// ==================== QUERIES BÁSICAS ====================
	
	@Query("SELECT * FROM playlists WHERE playlist_id = :id")
	fun getPlaylistById(id: Long): Flow<PlaylistEntity?>
	
	@Query("SELECT * FROM playlists WHERE playlist_id = :id LIMIT 1")
	suspend fun getPlaylistByIdSync(id: Long): PlaylistEntity?
	
	@Query("SELECT * FROM playlists ORDER BY name ASC")
	fun getAllPlaylists(): Flow<List<PlaylistEntity>>
	
	// ==================== BÚSQUEDA & FILTROS ====================
	
	@Query("""
        SELECT * FROM playlists
        WHERE name LIKE '%' || :query || '%'
        ORDER BY
            CASE WHEN name LIKE :query || '%' THEN 1 ELSE 2 END,
            name ASC
    """)
	fun searchPlaylists(query: String): Flow<List<PlaylistEntity>>
	
	@Query("SELECT * FROM playlists WHERE is_pinned = 1 ORDER BY name ASC")
	fun getPinnedPlaylists(): Flow<List<PlaylistEntity>>
	
	@Query("SELECT * FROM playlists WHERE is_system = 1 ORDER BY name ASC")
	fun getSystemPlaylists(): Flow<List<PlaylistEntity>>
	
	// ==================== LÓGICA DE NEGOCIO (Estadísticas) ====================
	
	@Query("UPDATE playlists SET song_count = song_count + :delta WHERE playlist_id = :id")
	suspend fun updateSongCount(id: Long, delta: Int)
	
	/**
	 * Recalcula contadores basándose en la tabla intermedia 'playlist_songs'.
	 * CRÍTICO: Usa las tablas correctas (playlist_songs, songs).
	 */
	@Query("""
        UPDATE playlists
        SET song_count = (SELECT COUNT(*) FROM playlist_songs WHERE playlist_songs.playlist_id = playlists.playlist_id),
            total_duration_ms = (
                SELECT COALESCE(SUM(s.duration), 0)
                FROM playlist_songs ps
                INNER JOIN songs s ON ps.song_id = s.song_id
                WHERE ps.playlist_id = playlists.playlist_id
            ),
            updated_at = :timestamp
        WHERE playlist_id = :id
    """)
	suspend fun refreshPlaylistStats(id: Long, timestamp: Long = System.currentTimeMillis())
	
	@Transaction
	suspend fun refreshAllPlaylistStats() {
		// Ejecuta la misma lógica para todas las playlists
		rawRefreshAllStats(System.currentTimeMillis())
	}
	
	@Query("""
        UPDATE playlists
        SET song_count = (SELECT COUNT(*) FROM playlist_songs WHERE playlist_songs.playlist_id = playlists.playlist_id),
            total_duration_ms = (
                SELECT COALESCE(SUM(s.duration), 0)
                FROM playlist_songs ps
                INNER JOIN songs s ON ps.song_id = s.song_id
                WHERE ps.playlist_id = playlists.playlist_id
            ),
            updated_at = :timestamp
    """)
	suspend fun rawRefreshAllStats(timestamp: Long)
	
	// ==================== UTILIDADES ====================
	
	@Query("SELECT * FROM playlists WHERE name = :name COLLATE NOCASE LIMIT 1")
	suspend fun findPlaylistByName(name: String): PlaylistEntity?
	
	@Transaction
	suspend fun createPlaylist(name: String): Long {
		val existing = findPlaylistByName(name)
		return if (existing != null) {
			existing.playlistId
		} else {
			val newPlaylist = PlaylistEntity(
				name = name,
				createdAt = System.currentTimeMillis(),
				updatedAt = System.currentTimeMillis()
			)
			insert(newPlaylist)
		}
	}
}