package com.pmk.freeplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pmk.freeplayer.data.local.entity.ArtistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {
	
	// ==================== INSERTS & UPDATES ====================
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(artist: ArtistEntity): Long
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(artists: List<ArtistEntity>)
	
	@Update
	suspend fun update(artist: ArtistEntity)
	
	@Query("""
        UPDATE artists
        SET biography = :biography,
            image_path = :imagePath,
            last_updated = :timestamp
        WHERE artist_id = :id
    """)
	suspend fun updateBasicInfo(
		id: Long,
		biography: String?,
		imagePath: String?,
		timestamp: Long = System.currentTimeMillis()
	)
	
	// ==================== DELETES ====================
	
	@Delete
	suspend fun delete(artist: ArtistEntity)
	
	@Query("DELETE FROM artists WHERE artist_id = :id")
	suspend fun deleteById(id: Long)
	
	@Query("DELETE FROM artists WHERE total_songs = 0 AND total_albums = 0")
	suspend fun deleteEmptyArtists()
	
	// ==================== QUERIES BÁSICAS ====================
	
	@Query("SELECT * FROM artists WHERE artist_id = :id")
	fun getArtistById(id: Long): Flow<ArtistEntity?>
	
	@Query("SELECT * FROM artists ORDER BY name ASC")
	fun getAllArtists(): Flow<List<ArtistEntity>>
	
	@Query("SELECT COUNT(*) FROM artists")
	fun getArtistCount(): Flow<Int>
	
	// ==================== BÚSQUEDA ====================
	
	@Query("""
        SELECT * FROM artists
        WHERE name LIKE '%' || :query || '%'
        ORDER BY
            CASE WHEN name LIKE :query || '%' THEN 1 ELSE 2 END,
            name ASC
    """)
	fun searchArtists(query: String): Flow<List<ArtistEntity>>
	
	@Query("SELECT * FROM artists WHERE name = :name COLLATE NOCASE LIMIT 1")
	suspend fun findArtistByName(name: String): ArtistEntity?
	
	// ==================== FILTROS & RANKINGS ====================
	
	@Query("SELECT * FROM artists ORDER BY play_count DESC LIMIT :limit")
	fun getMostPlayedArtists(limit: Int = 20): Flow<List<ArtistEntity>>
	
	@Query("SELECT * FROM artists ORDER BY total_songs DESC LIMIT :limit")
	fun getArtistsWithMostSongs(limit: Int = 20): Flow<List<ArtistEntity>>
	
	@Query("SELECT * FROM artists ORDER BY date_added DESC LIMIT :limit")
	fun getRecentArtists(limit: Int = 20): Flow<List<ArtistEntity>>
	
	// ==================== LÓGICA DE NEGOCIO (Estadísticas) ====================
	
	@Query("UPDATE artists SET play_count = play_count + 1 WHERE artist_id = :id")
	suspend fun incrementPlayCount(id: Long)
	
	/**
	 * Recalcula las estadísticas de un artista específico basándose en las canciones.
	 * IMPORTANTE: 'songs' debe tener 'artist_id' y 'album_id'.
	 */
	@Query("""
        UPDATE artists
        SET total_songs = (SELECT COUNT(*) FROM songs WHERE songs.artist_id = artists.artist_id),
            total_albums = (SELECT COUNT(DISTINCT album_id) FROM songs WHERE songs.artist_id = artists.artist_id AND album_id IS NOT NULL),
            last_updated = :timestamp
        WHERE artist_id = :id
    """)
	suspend fun refreshArtistStats(id: Long, timestamp: Long = System.currentTimeMillis())
	
	/**
	 * Recálculo masivo para toda la biblioteca.
	 * Útil después de un escaneo completo.
	 */
	@Transaction
	suspend fun refreshAllArtistStats() {
		// 1. Resetear contadores de seguridad
		resetCounters()
		// 2. Calcular canciones
		updateSongCounts()
		// 3. Calcular álbumes
		updateAlbumCounts()
	}
	
	@Query("UPDATE artists SET total_songs = 0, total_albums = 0")
	suspend fun resetCounters()
	
	@Query("""
        UPDATE artists
        SET total_songs = (
            SELECT COUNT(*) FROM songs WHERE songs.artist_id = artists.artist_id
        )
        WHERE EXISTS (SELECT 1 FROM songs WHERE songs.artist_id = artists.artist_id)
    """)
	suspend fun updateSongCounts()
	
	@Query("""
        UPDATE artists
        SET total_albums = (
            SELECT COUNT(DISTINCT album_id) FROM songs
            WHERE songs.artist_id = artists.artist_id AND album_id IS NOT NULL
        )
        WHERE EXISTS (SELECT 1 FROM songs WHERE songs.artist_id = artists.artist_id)
    """)
	suspend fun updateAlbumCounts()
	
	// ==================== SCANNER HELPERS ====================
	
	/**
	 * Método transaccional para el Escáner:
	 * Busca el artista por nombre. Si no existe, lo crea.
	 * Devuelve siempre un ID válido.
	 */
	@Transaction
	suspend fun getOrCreateArtistId(name: String): Long {
		val existing = findArtistByName(name)
		return if (existing != null) {
			existing.artistId
		} else {
			val newArtist = ArtistEntity(
				name = name,
				dateAdded = System.currentTimeMillis()
			)
			insert(newArtist)
		}
	}
}