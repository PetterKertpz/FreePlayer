package com.pmk.freeplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pmk.freeplayer.data.local.entity.PlaylistEntity
import com.pmk.freeplayer.data.local.entity.relation.PlaylistSongJoin
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
		timestamp: Long // ⚠️ Quitado valor por defecto - mejor pasarlo explícitamente
	)
	
	// ==================== DELETES ====================
	
	@Delete
	suspend fun delete(playlist: PlaylistEntity)
	
	@Query("DELETE FROM playlists WHERE playlist_id = :id")
	suspend fun deleteById(id: Long)
	
	// Limpieza de playlists vacías (Excepto las de sistema)
	@Query("DELETE FROM playlists WHERE song_count = 0 AND is_system = 0")
	suspend fun deleteEmptyPlaylists(): Int // ⚠️ Retorna cantidad eliminada
	
	// ==================== QUERIES BÁSICAS ====================
	
	@Query("SELECT * FROM playlists WHERE playlist_id = :id")
	fun getPlaylistById(id: Long): Flow<PlaylistEntity?>
	
	@Query("SELECT * FROM playlists WHERE playlist_id = :id LIMIT 1")
	suspend fun getPlaylistByIdSync(id: Long): PlaylistEntity?
	
	@Query("SELECT * FROM playlists ORDER BY name ASC")
	fun getAllPlaylists(): Flow<List<PlaylistEntity>>
	
	// Playlists del usuario (no sistema) - útil para mostrar "Mis Playlists"
	@Query("SELECT * FROM playlists WHERE is_system = 0 ORDER BY is_pinned DESC, name ASC")
	fun getUserPlaylists(): Flow<List<PlaylistEntity>>
	
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
	
	// Buscar por tipo de sistema (FAVORITES, HISTORY, etc.)
	@Query("SELECT * FROM playlists WHERE system_type = :type LIMIT 1")
	suspend fun getSystemPlaylistByType(type: String): PlaylistEntity?
	
	// ==================== TOGGLE STATES ====================
	
	@Query("UPDATE playlists SET is_pinned = NOT is_pinned, updated_at = :timestamp WHERE playlist_id = :id")
	suspend fun togglePinned(id: Long, timestamp: Long)
	
	// ==================== LÓGICA DE NEGOCIO (Estadísticas) ====================
	
	@Query("UPDATE playlists SET song_count = song_count + :delta, updated_at = :timestamp WHERE playlist_id = :id")
	suspend fun updateSongCount(id: Long, delta: Int, timestamp: Long)
	
	
	// ==================== UTILIDADES ====================
	
	@Query("SELECT * FROM playlists WHERE name = :name COLLATE NOCASE LIMIT 1")
	suspend fun findPlaylistByName(name: String): PlaylistEntity?
	
	@Query("SELECT EXISTS(SELECT 1 FROM playlists WHERE name = :name COLLATE NOCASE)")
	suspend fun existsByName(name: String): Boolean
	
	@Query("SELECT COUNT(*) FROM playlists WHERE is_system = 0")
	suspend fun getUserPlaylistCount(): Int
	
	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insert(join: PlaylistSongJoin): Long
	
	@Query("DELETE FROM playlist_song_join WHERE playlist_id = :playlistId AND song_id = :songId")
	suspend fun delete(playlistId: Long, songId: Long)
	
	@Query("DELETE FROM playlist_song_join WHERE playlist_id = :playlistId")
	suspend fun deleteAllInPlaylist(playlistId: Long)
	
	@Query("SELECT MAX(sort_order) FROM playlist_song_join WHERE playlist_id = :playlistId")
	suspend fun getMaxSortOrder(playlistId: Long): Int?
	
	@Query("SELECT COUNT(*) FROM playlist_song_join WHERE playlist_id = :playlistId AND song_id = :songId")
	suspend fun exists(playlistId: Long, songId: Long): Boolean
	
	// Para reordenamiento: Actualiza el orden de una entrada específica
	@Query("UPDATE playlist_song_join SET sort_order = :newOrder WHERE playlist_id = :playlistId AND song_id = :songId")
	suspend fun updateOrder(playlistId: Long, songId: Long, newOrder: Int)
	
	// Obtener todos los IDs de canciones en orden
	@Query("SELECT song_id FROM playlist_song_join WHERE playlist_id = :playlistId ORDER BY sort_order ASC")
	suspend fun getSongIds(playlistId: Long): List<Long>
	
	// Obtener pares para lógica de reordenamiento en memoria
	@Query("SELECT song_id, sort_order FROM playlist_song_join WHERE playlist_id = :playlistId ORDER BY sort_order ASC")
	suspend fun getJoinData(playlistId: Long): List<JoinSortData>
	
	// DTO simple para el DAO
	data class JoinSortData(val song_id: Long, val sort_order: Int)
	
	@Transaction
	suspend fun createPlaylistIfNotExists(name: String, description: String? = null): Long {
		val existing = findPlaylistByName(name)
		return existing?.playlistId ?: run {
			val now = System.currentTimeMillis()
			insert(
				PlaylistEntity(
					name = name.trim(),
					description = description,
					createdAt = now,
					updatedAt = now
				)
			)
		}
	}
	
	// Crear playlist de sistema (solo usar en inicialización de la app)
	@Transaction
	suspend fun createSystemPlaylist(name: String, systemType: String): Long {
		val existing = getSystemPlaylistByType(systemType)
		return existing?.playlistId ?: run {
			val now = System.currentTimeMillis()
			insert(
				PlaylistEntity(
					name = name,
					isSystem = true,
					systemType = systemType,
					createdAt = now,
					updatedAt = now
				)
			)
		}
	}
}