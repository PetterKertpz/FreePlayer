package com.pmk.freeplayer.feature.genres.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pmk.freeplayer.feature.genres.data.local.entity.GenreEntity
import kotlinx.coroutines.flow.Flow
import java.util.Locale

@Dao
interface GenreDao {
	
	// ── Writes ────────────────────────────────────────────────────
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(genre: GenreEntity): Long
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(genres: List<GenreEntity>)
	
	@Update
	suspend fun update(genre: GenreEntity)
	
	// ── Deletes ───────────────────────────────────────────────────
	
	@Query("DELETE FROM genres WHERE genre_id = :id")
	suspend fun deleteById(id: Long)
	
	@Query("DELETE FROM genres WHERE song_count = 0")
	suspend fun deleteEmpty()
	
	// ── Point reads ───────────────────────────────────────────────
	
	@Query("SELECT * FROM genres WHERE genre_id = :id LIMIT 1")
	fun getByIdFlow(id: Long): Flow<GenreEntity?>
	
	@Query("SELECT * FROM genres WHERE genre_id = :id LIMIT 1")
	suspend fun getById(id: Long): GenreEntity?
	
	@Query("SELECT * FROM genres WHERE normalized_name = :normalizedName LIMIT 1")
	suspend fun getByNormalizedName(normalizedName: String): GenreEntity?
	
	// ── Collection reads ──────────────────────────────────────────
	
	@Query("SELECT * FROM genres ORDER BY name ASC")
	fun getAll(): Flow<List<GenreEntity>>
	
	@Query("SELECT * FROM genres ORDER BY song_count DESC LIMIT :limit")
	fun getByMostSongs(limit: Int = 20): Flow<List<GenreEntity>>
	
	@Query("SELECT COUNT(*) FROM genres")
	suspend fun count(): Int
	
	// ── Search ────────────────────────────────────────────────────
	
	@Query("""
        SELECT * FROM genres
        WHERE name LIKE '%' || :query || '%'
        ORDER BY CASE WHEN name LIKE :query || '%' THEN 0 ELSE 1 END, name ASC
    """)
	fun search(query: String): Flow<List<GenreEntity>>
	
	// ── Structural cache refresh (called by Scanner) ──────────────
	
	@Query("""
        UPDATE genres
        SET song_count   = (SELECT COUNT(*) FROM songs WHERE songs.genre_id = genres.genre_id),
            artist_count = (SELECT COUNT(DISTINCT artist_id) FROM songs WHERE songs.genre_id = genres.genre_id),
            album_count  = (SELECT COUNT(DISTINCT album_id)  FROM songs WHERE songs.genre_id = genres.genre_id),
            last_updated = :timestamp
        WHERE genre_id = :id
    """)
	suspend fun refreshStats(id: Long, timestamp: Long)
	
	@Transaction
	suspend fun refreshAllStats(timestamp: Long) {
		resetCounts()
		recalculateSongCounts()
		recalculateArtistCounts()
		recalculateAlbumCounts()
	}
	
	@Query("UPDATE genres SET song_count = 0, artist_count = 0, album_count = 0")
	suspend fun resetCounts()
	
	@Query("""
        UPDATE genres
        SET song_count = (SELECT COUNT(*) FROM songs WHERE songs.genre_id = genres.genre_id)
        WHERE EXISTS (SELECT 1 FROM songs WHERE songs.genre_id = genres.genre_id)
    """)
	suspend fun recalculateSongCounts()
	
	@Query("""
        UPDATE genres
        SET artist_count = (SELECT COUNT(DISTINCT artist_id) FROM songs WHERE songs.genre_id = genres.genre_id)
        WHERE EXISTS (SELECT 1 FROM songs WHERE songs.genre_id = genres.genre_id)
    """)
	suspend fun recalculateArtistCounts()
	
	@Query("""
        UPDATE genres
        SET album_count = (SELECT COUNT(DISTINCT album_id) FROM songs WHERE songs.genre_id = genres.genre_id)
        WHERE EXISTS (SELECT 1 FROM songs WHERE songs.genre_id = genres.genre_id)
    """)
	suspend fun recalculateAlbumCounts()
	
	// ── Scanner helper ────────────────────────────────────────────
	
	@Transaction
	suspend fun getOrCreate(name: String, now: Long): Long {
		val normalized = name.trim().uppercase()
		return getByNormalizedName(normalized)?.genreId
			?: insert(
				GenreEntity(
					name           = name.trim().replaceFirstChar { it.titlecase(Locale.ROOT) },
					normalizedName = normalized,
					dateAdded      = now,
					lastUpdated    = now,
				)
			)
	}
}