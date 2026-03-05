package com.pmk.freeplayer.feature.songs.data.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pmk.freeplayer.feature.songs.data.local.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

   // ── Writes ────────────────────────────────────────────────────

   @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(song: SongEntity): Long

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertAll(songs: List<SongEntity>): List<Long>

   @Update suspend fun update(song: SongEntity)

   /** Preserves user-owned fields (dateAdded, isFavorite, rating) on re-scan. */
   @Transaction
   suspend fun safeUpsert(song: SongEntity): Long {
      val scan = getScanInfoByPath(song.filePath)
      return if (scan != null) {
         update(
            song.copy(
               songId = scan.songId,
               dateAdded = scan.dateAdded,
               isFavorite = scan.isFavorite,
               rating = scan.rating,
            )
         )
         scan.songId
      } else {
         insert(song)
      }
   }

   // ── Deletes ───────────────────────────────────────────────────

   @Query("DELETE FROM songs WHERE song_id = :id") suspend fun deleteById(id: Long)

   @Query("DELETE FROM songs WHERE song_id IN (:ids)") suspend fun deleteByIds(ids: List<Long>)

   @Query("DELETE FROM songs WHERE file_path = :path") suspend fun deleteByPath(path: String)

   @Query("DELETE FROM songs WHERE file_path IN (:paths)")
   suspend fun deleteByPaths(paths: List<String>)

   @Query("DELETE FROM songs WHERE file_path IS NULL OR length(trim(file_path)) = 0")
   suspend fun deleteInvalid()

   // ── Point reads ───────────────────────────────────────────────

   @Query("SELECT * FROM songs WHERE song_id = :id LIMIT 1")
   suspend fun getById(id: Long): SongEntity?

   @Query("SELECT * FROM songs WHERE song_id = :id LIMIT 1")
   fun getByIdFlow(id: Long): Flow<SongEntity?>

   // ── Collection reads ──────────────────────────────────────────

   @Query("SELECT * FROM songs WHERE is_hidden = 0 ORDER BY title ASC")
   fun getAll(): Flow<List<SongEntity>>

   @Query("SELECT * FROM songs WHERE song_id IN (:ids) AND is_hidden = 0")
   fun getByIds(ids: List<Long>): Flow<List<SongEntity>>

   @Query("SELECT * FROM songs WHERE is_hidden = 1 ORDER BY title ASC")
   fun getHidden(): Flow<List<SongEntity>>

   @Query("SELECT COUNT(*) FROM songs WHERE is_hidden = 0") suspend fun count(): Int

   // ── Relation filters ──────────────────────────────────────────

   @Query("SELECT * FROM songs WHERE artist_id = :artistId AND is_hidden = 0 ORDER BY title ASC")
   fun getByArtist(artistId: Long): Flow<List<SongEntity>>

   @Query(
      "SELECT * FROM songs WHERE album_id = :albumId AND is_hidden = 0 ORDER BY disc_number ASC, track_number ASC"
   )
   fun getByAlbum(albumId: Long): Flow<List<SongEntity>>

   @Query("SELECT * FROM songs WHERE genre_id = :genreId AND is_hidden = 0 ORDER BY title ASC")
   fun getByGenre(genreId: Long): Flow<List<SongEntity>>

   @Query("SELECT * FROM songs WHERE is_favorite = 1 AND is_hidden = 0 ORDER BY title ASC")
   fun getFavorites(): Flow<List<SongEntity>>

   @Query("SELECT * FROM songs WHERE is_hidden = 0 ORDER BY date_added DESC LIMIT :limit")
   fun getRecentlyAdded(limit: Int = 20): Flow<List<SongEntity>>

   // ── Search ────────────────────────────────────────────────────

   @Query(
      """
        SELECT * FROM songs
        WHERE (title LIKE '%' || :query || '%' OR artist_name LIKE '%' || :query || '%')
          AND is_hidden = 0
        ORDER BY CASE WHEN title LIKE :query || '%' THEN 0 ELSE 1 END, title ASC
        LIMIT :limit
    """
   )
   fun search(query: String, limit: Int = 50): Flow<List<SongEntity>>

   // ── User preferences ──────────────────────────────────────────

   @Query("UPDATE songs SET is_favorite = :isFavorite WHERE song_id = :id")
   suspend fun setFavorite(id: Long, isFavorite: Boolean)

   @Query("UPDATE songs SET is_favorite = NOT is_favorite WHERE song_id = :id")
   suspend fun toggleFavorite(id: Long)

   @Query("UPDATE songs SET is_hidden = :hidden WHERE song_id = :id")
   suspend fun setHidden(id: Long, hidden: Boolean)

   @Query("UPDATE songs SET rating = :rating WHERE song_id = :id")
   suspend fun setRating(id: Long, rating: Float)

   // ── Scanner helpers ───────────────────────────────────────────

   @Query(
      """
        SELECT song_id, file_path, date_modified, file_size,
               date_added, is_favorite, rating
        FROM songs WHERE file_path = :path LIMIT 1
    """
   )
   suspend fun getScanInfoByPath(path: String): SongScanInfo?

   @Query("SELECT file_path FROM songs") suspend fun getAllFilePaths(): List<String>

   @Query(
      """
        SELECT song_id, file_path, date_modified, file_size,
               date_added, is_favorite, rating
        FROM songs
    """
   )
   suspend fun getAllScanInfo(): List<SongScanInfo>

   // ── Metadata pipeline ─────────────────────────────────────────

   @Query("SELECT * FROM songs WHERE metadata_status = 'RAW' ORDER BY date_added ASC LIMIT :limit")
   suspend fun getPendingCleaning(limit: Int = 100): List<SongEntity>

   @Query(
      "SELECT * FROM songs WHERE metadata_status = 'CLEAN' ORDER BY date_added ASC LIMIT :limit"
   )
   suspend fun getPendingEnrichment(limit: Int = 100): List<SongEntity>

   @Query(
      "UPDATE songs SET metadata_status = :status, confidence_score = :confidence WHERE song_id = :id"
   )
   suspend fun updateMetadataStatus(id: Long, status: String, confidence: Float = 1f)

   @Query(
      """
        UPDATE songs
        SET metadata_status = 'CLEAN', cleaned_at = :timestamp,
            title = :cleanTitle, artist_name = :cleanArtist, confidence_score = :confidence
        WHERE song_id = :id
    """
   )
   suspend fun markAsClean(
      id: Long,
      cleanTitle: String,
      cleanArtist: String,
      confidence: Float,
      timestamp: Long,
   )

   @Query(
      """
        UPDATE songs
        SET metadata_status = 'ENRICHED', enriched_at = :timestamp,
            has_lyrics = :hasLyrics, genius_id = :geniusId,
            genius_url = :geniusUrl, confidence_score = :confidence
        WHERE song_id = :id
    """
   )
   suspend fun markAsEnriched(
      id: Long,
      hasLyrics: Boolean,
      geniusId: String?,
      geniusUrl: String?,
      confidence: Float,
      timestamp: Long,
   )

   @Query("SELECT COUNT(*) FROM songs WHERE metadata_status = :status")
   suspend fun countByStatus(status: String): Int

   // ── Utilities ─────────────────────────────────────────────────

   @Query("SELECT COALESCE(SUM(duration), 0) FROM songs WHERE song_id IN (:ids)")
   suspend fun getTotalDuration(ids: List<Long>): Long

   // ── Internal DTOs ─────────────────────────────────────────────

   data class SongScanInfo(
      @ColumnInfo(name = "song_id") val songId: Long,
      @ColumnInfo(name = "file_path") val filePath: String,
      @ColumnInfo(name = "date_modified") val dateModified: Long?,
      @ColumnInfo(name = "file_size") val fileSize: Long,
      @ColumnInfo(name = "date_added") val dateAdded: Long,
      @ColumnInfo(name = "is_favorite") val isFavorite: Boolean,
      @ColumnInfo(name = "rating") val rating: Float,
   )
}
