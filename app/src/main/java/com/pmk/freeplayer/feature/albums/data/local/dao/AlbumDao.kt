package com.pmk.freeplayer.feature.albums.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pmk.freeplayer.feature.albums.data.local.entity.AlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
	
	// ── Writes ────────────────────────────────────────────────────
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(album: AlbumEntity): Long
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(albums: List<AlbumEntity>)
	
	@Update
	suspend fun update(album: AlbumEntity)
	
	// ── Deletes ───────────────────────────────────────────────────
	
	@Query("DELETE FROM albums WHERE album_id = :id")
	suspend fun deleteById(id: Long)
	
	@Query("DELETE FROM albums WHERE total_songs = 0")
	suspend fun deleteEmpty()
	
	// ── Point reads ───────────────────────────────────────────────
	
	@Query("SELECT * FROM albums WHERE album_id = :id LIMIT 1")
	fun getByIdFlow(id: Long): Flow<AlbumEntity?>
	
	@Query("SELECT * FROM albums WHERE album_id = :id LIMIT 1")
	suspend fun getById(id: Long): AlbumEntity?
	
	@Query("SELECT * FROM albums WHERE title = :title AND artist_id = :artistId LIMIT 1")
	suspend fun getByTitleAndArtist(title: String, artistId: Long): AlbumEntity?
	
	// ── Collection reads ──────────────────────────────────────────
	
	// FIX: Single getAll() replaces 8 sort-specific queries.
	// Sorting is applied in-memory in the repository (same rationale as songs/artists:
	// dynamic ORDER BY requires @RawQuery which breaks type safety).
	@Query("SELECT * FROM albums ORDER BY title COLLATE NOCASE ASC")
	fun getAll(): Flow<List<AlbumEntity>>
	
	@Query("SELECT * FROM albums WHERE artist_id = :artistId ORDER BY year DESC")
	fun getByArtist(artistId: Long): Flow<List<AlbumEntity>>
	
	@Query("SELECT * FROM albums ORDER BY date_added DESC LIMIT :limit")
	fun getRecentlyAdded(limit: Int = 10): Flow<List<AlbumEntity>>
	
	@Query("SELECT COUNT(*) FROM albums")
	suspend fun count(): Int
	
	// ── Search ────────────────────────────────────────────────────
	
	@Query("""
        SELECT * FROM albums
        WHERE title LIKE '%' || :query || '%'
           OR artist_name LIKE '%' || :query || '%'
        ORDER BY CASE WHEN title LIKE :query || '%' THEN 0 ELSE 1 END,
                 title COLLATE NOCASE ASC
    """)
	fun search(query: String): Flow<List<AlbumEntity>>
	
	// ── User preferences ──────────────────────────────────────────
	
	@Query("UPDATE albums SET is_favorite = NOT is_favorite WHERE album_id = :id")
	suspend fun toggleFavorite(id: Long)
	
	@Query("UPDATE albums SET rating = :rating WHERE album_id = :id")
	suspend fun setRating(id: Long, rating: Float)
	
	// ── Structural stats (cache refresh — called by Scanner) ──────
	
	@Query("""
        UPDATE albums
        SET total_songs       = (SELECT COUNT(*) FROM songs WHERE songs.album_id = albums.album_id),
            total_duration_ms = (SELECT COALESCE(SUM(duration), 0) FROM songs WHERE songs.album_id = albums.album_id),
            last_updated      = :timestamp
        WHERE album_id = :id
    """)
	suspend fun refreshStats(id: Long, timestamp: Long)
	
	@Query("""
        UPDATE albums
        SET total_songs       = (SELECT COUNT(*) FROM songs WHERE songs.album_id = albums.album_id),
            total_duration_ms = (SELECT COALESCE(SUM(duration), 0) FROM songs WHERE songs.album_id = albums.album_id),
            last_updated      = :timestamp
    """)
	suspend fun refreshAllStats(timestamp: Long)
	
	// ── Scanner helper ────────────────────────────────────────────
	
	@Transaction
	suspend fun getOrCreate(title: String, artistId: Long, artistName: String, year: Int?, now: Long): Long =
		getByTitleAndArtist(title, artistId)?.albumId
			?: insert(AlbumEntity(
				title       = title,
				artistId    = artistId,
				artistName  = artistName,
				year        = year,
				dateAdded   = now,
				lastUpdated = now,
			))
}