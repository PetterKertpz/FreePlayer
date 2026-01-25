package com.pmk.freeplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pmk.freeplayer.data.local.entity.AlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
	
	// ==================== INSERTS ====================
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(album: AlbumEntity): Long
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(albums: List<AlbumEntity>)
	
	// ==================== UPDATES & DELETES ====================
	
	@Update
	suspend fun update(album: AlbumEntity)
	
	@Delete
	suspend fun delete(album: AlbumEntity)
	
	@Query("DELETE FROM albums WHERE album_id = :albumId")
	suspend fun deleteById(albumId: Long)
	
	// Limpieza de álbumes vacíos (fantasmas)
	@Query("DELETE FROM albums WHERE total_songs = 0")
	suspend fun deleteEmptyAlbums()
	
	// ==================== QUERIES BÁSICAS ====================
	
	@Query("SELECT * FROM albums WHERE album_id = :albumId")
	fun getAlbumById(albumId: Long): Flow<AlbumEntity?>
	
	// Versión suspendida para uso interno (Scanners, Workers)
	@Query("SELECT * FROM albums WHERE album_id = :albumId LIMIT 1")
	suspend fun getAlbumByIdSync(albumId: Long): AlbumEntity?
	
	@Query("SELECT * FROM albums ORDER BY title ASC")
	fun getAllAlbums(): Flow<List<AlbumEntity>>
	
	// ==================== FILTROS ====================
	
	@Query("SELECT * FROM albums WHERE artist_id = :artistId ORDER BY year DESC")
	fun getAlbumsByArtist(artistId: Long): Flow<List<AlbumEntity>>
	
	@Query("SELECT * FROM albums WHERE year = :year ORDER BY title ASC")
	fun getAlbumsByYear(year: Int): Flow<List<AlbumEntity>>
	
	// Búsqueda inteligente (Title Match > Partial Match)
	@Query("""
        SELECT * FROM albums
        WHERE title LIKE '%' || :query || '%'
        ORDER BY
            CASE WHEN title LIKE :query || '%' THEN 1 ELSE 2 END,
            title ASC
    """)
	fun searchAlbums(query: String): Flow<List<AlbumEntity>>
	
	// ==================== RANKINGS & HOME ====================
	
	@Query("SELECT * FROM albums ORDER BY date_added DESC LIMIT :limit")
	fun getRecentAlbums(limit: Int = 20): Flow<List<AlbumEntity>>
	
	@Query("SELECT * FROM albums ORDER BY play_count DESC LIMIT :limit")
	fun getMostPlayedAlbums(limit: Int = 20): Flow<List<AlbumEntity>>
	
	// ==================== LOGICA DE NEGOCIO (Estadísticas) ====================
	
	/**
	 * Recalcula los contadores basándose en la tabla 'songs'.
	 * CRÍTICO: Asume que la tabla se llama 'songs' y tiene 'album_id' y 'duration'.
	 */
	@Query("""
        UPDATE albums
        SET total_songs = (SELECT COUNT(*) FROM songs WHERE songs.album_id = albums.album_id),
            total_duration_ms = (SELECT COALESCE(SUM(duration), 0) FROM songs WHERE songs.album_id = albums.album_id)
        WHERE album_id = :albumId
    """)
	suspend fun refreshAlbumStats(albumId: Long)
	
	@Query("UPDATE albums SET play_count = play_count + 1 WHERE album_id = :albumId")
	suspend fun incrementPlayCount(albumId: Long)
	
	// ==================== HELPERS PARA SCANNER ====================
	// Estos métodos ayudan a evitar duplicados al escanear la biblioteca
	
	@Query("SELECT * FROM albums WHERE title = :title AND artist_id = :artistId LIMIT 1")
	suspend fun findAlbumByTitleAndArtist(title: String, artistId: Long): AlbumEntity?
	
	/**
	 * Método transaccional inteligente:
	 * Busca el álbum. Si existe, devuelve el ID existente.
	 * Si no existe, lo crea y devuelve el nuevo ID.
	 */
	@Transaction
	suspend fun getOrCreateAlbumId(title: String, artistId: Long, year: Int?): Long {
		val existing = findAlbumByTitleAndArtist(title, artistId)
		return if (existing != null) {
			existing.albumId
		} else {
			val newAlbum = AlbumEntity(
				title = title,
				artistId = artistId,
				year = year,
				dateAdded = System.currentTimeMillis()
			)
			insert(newAlbum)
		}
	}
	
	// ==================== DASHBOARD STATS ====================
	
	@Query("SELECT year, COUNT(*) as count FROM albums WHERE year IS NOT NULL GROUP BY year ORDER BY year DESC")
	suspend fun getAlbumsCountByYear(): List<AlbumYearStats>
}

// DTO Auxiliar para el resultado de la query getAlbumsCountByYear
data class AlbumYearStats(
	val year: Int,
	val count: Int
)