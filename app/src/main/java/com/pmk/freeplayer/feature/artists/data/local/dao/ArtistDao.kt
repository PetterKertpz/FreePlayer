package com.pmk.freeplayer.feature.artists.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pmk.freeplayer.feature.artists.data.local.entity.ArtistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {
	
	// ── Writes ────────────────────────────────────────────────────
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(artist: ArtistEntity): Long
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(artists: List<ArtistEntity>)
	
	@Update
	suspend fun update(artist: ArtistEntity)
	
	// ── Deletes ───────────────────────────────────────────────────
	
	@Query("DELETE FROM artists WHERE artist_id = :id")
	suspend fun deleteById(id: Long)
	
	@Query("DELETE FROM artists WHERE total_songs = 0 AND total_albums = 0")
	suspend fun deleteEmpty()
	
	// ── Point reads ───────────────────────────────────────────────
	
	@Query("SELECT * FROM artists WHERE artist_id = :id LIMIT 1")
	fun getByIdFlow(id: Long): Flow<ArtistEntity?>
	
	@Query("SELECT * FROM artists WHERE artist_id = :id LIMIT 1")
	suspend fun getById(id: Long): ArtistEntity?
	
	@Query("SELECT * FROM artists WHERE name = :name COLLATE NOCASE LIMIT 1")
	suspend fun getByName(name: String): ArtistEntity?
	
	// ── Collection reads ──────────────────────────────────────────
	
	@Query("SELECT * FROM artists ORDER BY name ASC")
	fun getAll(): Flow<List<ArtistEntity>>
	
	@Query("SELECT * FROM artists ORDER BY date_added DESC LIMIT :limit")
	fun getRecent(limit: Int = 20): Flow<List<ArtistEntity>>
	
	@Query("SELECT * FROM artists ORDER BY total_songs DESC LIMIT :limit")
	fun getByMostSongs(limit: Int = 20): Flow<List<ArtistEntity>>
	
	@Query("SELECT COUNT(*) FROM artists")
	suspend fun count(): Int
	
	// ── Search ────────────────────────────────────────────────────
	
	@Query("""
        SELECT * FROM artists
        WHERE name LIKE '%' || :query || '%'
        ORDER BY CASE WHEN name LIKE :query || '%' THEN 0 ELSE 1 END, name ASC
    """)
	fun search(query: String): Flow<List<ArtistEntity>>
	
	// ── User preferences ──────────────────────────────────────────
	
	@Query("UPDATE artists SET is_favorite = NOT is_favorite WHERE artist_id = :id")
	suspend fun toggleFavorite(id: Long)
	
	// ── Stats ─────────────────────────────────────────────────────
	
	@Query("""
        UPDATE artists
        SET total_songs  = (SELECT COUNT(*) FROM songs WHERE songs.artist_id = artists.artist_id),
            total_albums = (SELECT COUNT(DISTINCT album_id) FROM songs WHERE songs.artist_id = artists.artist_id AND album_id IS NOT NULL),
            last_updated = :timestamp
        WHERE artist_id = :id
    """)
	suspend fun refreshStats(id: Long, timestamp: Long)
	
	@Transaction
	suspend fun refreshAllStats(timestamp: Long) {
		resetCounts()
		recalculateSongCounts()
		recalculateAlbumCounts()
	}
	
	@Query("UPDATE artists SET total_songs = 0, total_albums = 0")
	suspend fun resetCounts()
	
	@Query("""
        UPDATE artists
        SET total_songs = (SELECT COUNT(*) FROM songs WHERE songs.artist_id = artists.artist_id)
        WHERE EXISTS (SELECT 1 FROM songs WHERE songs.artist_id = artists.artist_id)
    """)
	suspend fun recalculateSongCounts()
	
	@Query("""
        UPDATE artists
        SET total_albums = (SELECT COUNT(DISTINCT album_id) FROM songs
                            WHERE songs.artist_id = artists.artist_id AND album_id IS NOT NULL)
        WHERE EXISTS (SELECT 1 FROM songs WHERE songs.artist_id = artists.artist_id)
    """)
	suspend fun recalculateAlbumCounts()
	
	// ── Scanner helper ────────────────────────────────────────────
	
	/**
	 * Returns existing artist ID or creates a minimal stub. Used by the Scanner.
	 */
	@Transaction
	suspend fun getOrCreate(name: String, now: Long): Long {
		val existing = getByName(name)
		return existing?.artistId ?: insert(ArtistEntity(name = name, dateAdded = now, lastUpdated = now))
	}
}