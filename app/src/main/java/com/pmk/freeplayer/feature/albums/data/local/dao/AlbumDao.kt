package com.pmk.freeplayer.feature.albums.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pmk.freeplayer.feature.albums.data.local.entity.AlbumEntity
import com.pmk.freeplayer.core.data.local.relation.AlbumArtistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

   // ==================== BASE JOIN QUERY ====================
   companion object {
      private const val SELECT_WITH_ARTIST =
         """
            SELECT
                a.album_id, a.title, a.artist_id,
                ar.name AS artist_name,
                a.cover_path, a.cover_url, a.album_type, a.genres,
                a.year, a.date_added, a.description, a.producer,
                a.record_label, a.total_songs, a.total_duration_ms,
                a.play_count, a.rating_average, a.is_favorite,
                a.genius_id, a.spotify_id
            FROM albums a
            INNER JOIN artists ar ON a.artist_id = ar.artist_id
        """
   }

   // ==================== INSERTS ====================

   @Insert(onConflict = OnConflictStrategy.Companion.REPLACE) suspend fun insert(album: AlbumEntity): Long

   @Insert(onConflict = OnConflictStrategy.Companion.REPLACE) suspend fun insertAll(albums: List<AlbumEntity>)

   // ==================== UPDATES & DELETES ====================

   @Update
   suspend fun update(album: AlbumEntity)

   @Delete
   suspend fun delete(album: AlbumEntity)

   @Query("DELETE FROM albums WHERE album_id = :albumId") suspend fun deleteById(albumId: Long)

   @Query("DELETE FROM albums WHERE total_songs = 0") suspend fun deleteEmptyAlbums()

   // ==================== QUERIES CON JOIN ====================

   @Query("$SELECT_WITH_ARTIST WHERE a.album_id = :albumId")
   fun getAlbumById(albumId: Long): Flow<AlbumArtistEntity?>

   @Query("$SELECT_WITH_ARTIST ORDER BY a.title COLLATE NOCASE ASC")
   fun getAllAlbums(): Flow<List<AlbumArtistEntity>>

   @Query("$SELECT_WITH_ARTIST WHERE a.artist_id = :artistId ORDER BY a.year DESC")
   fun getAlbumsByArtistId(artistId: Long): Flow<List<AlbumArtistEntity>>

   @Query("$SELECT_WITH_ARTIST WHERE ar.name LIKE '%' || :artistName || '%' ORDER BY a.year DESC")
   fun getAlbumsByArtistName(artistName: String): Flow<List<AlbumArtistEntity>>

   @Query(
      """
        $SELECT_WITH_ARTIST
        WHERE a.title LIKE '%' || :query || '%' OR ar.name LIKE '%' || :query || '%'
        ORDER BY
            CASE WHEN a.title LIKE :query || '%' THEN 0 ELSE 1 END,
            a.title COLLATE NOCASE ASC
    """
   )
   fun searchAlbums(query: String): Flow<List<AlbumArtistEntity>>

   // ==================== SORTED QUERIES ====================

   @Query("$SELECT_WITH_ARTIST ORDER BY a.title COLLATE NOCASE ASC")
   fun getAllSortedByTitleAsc(): Flow<List<AlbumArtistEntity>>

   @Query("$SELECT_WITH_ARTIST ORDER BY a.title COLLATE NOCASE DESC")
   fun getAllSortedByTitleDesc(): Flow<List<AlbumArtistEntity>>

   @Query("$SELECT_WITH_ARTIST ORDER BY ar.name COLLATE NOCASE ASC, a.year DESC")
   fun getAllSortedByArtistAsc(): Flow<List<AlbumArtistEntity>>

   @Query("$SELECT_WITH_ARTIST ORDER BY ar.name COLLATE NOCASE DESC, a.year DESC")
   fun getAllSortedByArtistDesc(): Flow<List<AlbumArtistEntity>>

   // 1. Nulos al final en orden Descendente (2025, 2024... null)
   @Query("$SELECT_WITH_ARTIST ORDER BY CASE WHEN a.year IS NULL THEN 1 ELSE 0 END, a.year DESC")
   fun getAllSortedByYearDesc(): Flow<List<AlbumArtistEntity>>

   // 2. Nulos al final en orden Ascendente (1990, 1991... null)
   @Query("$SELECT_WITH_ARTIST ORDER BY CASE WHEN a.year IS NULL THEN 1 ELSE 0 END, a.year ASC")
   fun getAllSortedByYearAsc(): Flow<List<AlbumArtistEntity>>

   @Query("$SELECT_WITH_ARTIST ORDER BY a.date_added DESC")
   fun getAllSortedByDateAddedDesc(): Flow<List<AlbumArtistEntity>>

   @Query("$SELECT_WITH_ARTIST ORDER BY a.date_added ASC")
   fun getAllSortedByDateAddedAsc(): Flow<List<AlbumArtistEntity>>

   @Query("$SELECT_WITH_ARTIST ORDER BY a.play_count DESC")
   fun getAllSortedByPlayCountDesc(): Flow<List<AlbumArtistEntity>>

   @Query("$SELECT_WITH_ARTIST ORDER BY a.play_count ASC")
   fun getAllSortedByPlayCountAsc(): Flow<List<AlbumArtistEntity>>

   @Query("$SELECT_WITH_ARTIST ORDER BY a.total_duration_ms DESC")
   fun getAllSortedByDurationDesc(): Flow<List<AlbumArtistEntity>>

   @Query("$SELECT_WITH_ARTIST ORDER BY a.total_duration_ms ASC")
   fun getAllSortedByDurationAsc(): Flow<List<AlbumArtistEntity>>

   // ==================== STATS ====================

   @Query("SELECT COUNT(*) FROM albums") suspend fun getTotalCount(): Int

   @Query("UPDATE albums SET play_count = play_count + 1 WHERE album_id = :albumId")
   suspend fun incrementPlayCount(albumId: Long)

   @Query(
      """
        UPDATE albums SET
            total_songs = (SELECT COUNT(*) FROM songs WHERE songs.album_id = albums.album_id),
            total_duration_ms = (SELECT COALESCE(SUM(duration), 0) FROM songs WHERE songs.album_id = albums.album_id)
        WHERE album_id = :albumId
    """
   )
   suspend fun refreshAlbumStats(albumId: Long)

   // ==================== SCANNER HELPERS ====================

   @Query("SELECT * FROM albums WHERE title = :title AND artist_id = :artistId LIMIT 1")
   suspend fun findByTitleAndArtist(title: String, artistId: Long): AlbumEntity?

   @Transaction
   suspend fun getOrCreateAlbumId(title: String, artistId: Long, year: Int?): Long {
      return findByTitleAndArtist(title, artistId)?.albumId
         ?: insert(AlbumEntity(title = title, artistId = artistId, year = year))
   }
}