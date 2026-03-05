package com.pmk.freeplayer.feature.playlists.data.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pmk.freeplayer.feature.playlists.data.local.entity.PlaylistEntity
import com.pmk.freeplayer.feature.playlists.data.local.relation.PlaylistSongJoin
import com.pmk.freeplayer.feature.playlists.domain.model.SystemPlaylistType
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

   // ── Playlist writes ───────────────────────────────────────────

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(playlist: PlaylistEntity): Long

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertAll(playlists: List<PlaylistEntity>)

   @Update suspend fun update(playlist: PlaylistEntity)

   // ── Playlist deletes ──────────────────────────────────────────

   @Query("DELETE FROM playlists WHERE playlist_id = :id") suspend fun deleteById(id: Long)

   @Query("DELETE FROM playlists WHERE song_count = 0 AND is_system = 0")
   suspend fun deleteEmpty(): Int

   // ── Playlist reads ────────────────────────────────────────────

   @Query("SELECT * FROM playlists WHERE playlist_id = :id LIMIT 1")
   fun getByIdFlow(id: Long): Flow<PlaylistEntity?>

   @Query(
      """
    SELECT song_id FROM playlist_song_join
    WHERE playlist_id = :playlistId ORDER BY sort_order ASC
"""
   )
   fun getSongIdsFlow(playlistId: Long): Flow<List<Long>>

   @Query("SELECT * FROM playlists WHERE playlist_id = :id LIMIT 1")
   suspend fun getById(id: Long): PlaylistEntity?

   @Query("SELECT * FROM playlists WHERE name = :name COLLATE NOCASE LIMIT 1")
   suspend fun getByName(name: String): PlaylistEntity?

   @Query("SELECT * FROM playlists WHERE system_type = :type LIMIT 1")
   suspend fun getBySystemType(type: SystemPlaylistType): PlaylistEntity?

   @Query("SELECT * FROM playlists ORDER BY is_pinned DESC, name ASC")
   fun getAll(): Flow<List<PlaylistEntity>>

   @Query("SELECT * FROM playlists WHERE is_system = 0 ORDER BY is_pinned DESC, name ASC")
   fun getUserPlaylists(): Flow<List<PlaylistEntity>>

   @Query("SELECT * FROM playlists WHERE is_system = 1 ORDER BY name ASC")
   fun getSystemPlaylists(): Flow<List<PlaylistEntity>>

   @Query("SELECT * FROM playlists WHERE is_pinned = 1 ORDER BY name ASC")
   fun getPinned(): Flow<List<PlaylistEntity>>

   @Query("SELECT COUNT(*) FROM playlists WHERE is_system = 0")
   suspend fun countUserPlaylists(): Int

   // ── Search ────────────────────────────────────────────────────

   @Query(
      """
        SELECT * FROM playlists
        WHERE name LIKE '%' || :query || '%'
        ORDER BY CASE WHEN name LIKE :query || '%' THEN 0 ELSE 1 END, name ASC
    """
   )
   fun search(query: String): Flow<List<PlaylistEntity>>

   // ── Preference toggles ────────────────────────────────────────

   @Query(
      "UPDATE playlists SET is_pinned = NOT is_pinned, updated_at = :timestamp WHERE playlist_id = :id"
   )
   suspend fun togglePinned(id: Long, timestamp: Long)

   // ── Structural cache update ───────────────────────────────────

   /**
    * Recomputes song_count and total_duration_ms from the join table. Called atomically after every
    * add/remove operation. total_duration_ms is sourced from the songs table directly to avoid
    * stale caches from incremental deltas.
    */
   @Query(
      """
        UPDATE playlists
        SET song_count       = (SELECT COUNT(*) FROM playlist_song_join WHERE playlist_id = :id),
            total_duration_ms = (
                SELECT COALESCE(SUM(s.duration), 0)
                FROM playlist_song_join j
                INNER JOIN songs s ON s.song_id = j.song_id
                WHERE j.playlist_id = :id
            ),
            updated_at = :timestamp
        WHERE playlist_id = :id
    """
   )
   suspend fun refreshStats(id: Long, timestamp: Long)

   // ── Scanner / initializer helpers ─────────────────────────────

   @Transaction
   suspend fun getOrCreate(name: String, now: Long): Long =
      getByName(name)?.playlistId
         ?: insert(PlaylistEntity(name = name.trim(), createdAt = now, updatedAt = now))

   @Transaction
   suspend fun getOrCreateSystem(name: String, type: SystemPlaylistType, now: Long): Long =
      getBySystemType(type)?.playlistId
         ?: insert(
            PlaylistEntity(
               name = name,
               isSystem = true,
               systemType = type,
               isPinned = true,
               createdAt = now,
               updatedAt = now,
            )
         )

   // ── Join writes ───────────────────────────────────────────────

   @Insert(onConflict = OnConflictStrategy.IGNORE)
   suspend fun insertJoin(join: PlaylistSongJoin): Long

   @Insert(onConflict = OnConflictStrategy.IGNORE)
   suspend fun insertJoins(joins: List<PlaylistSongJoin>): List<Long>

   @Query("DELETE FROM playlist_song_join WHERE playlist_id = :playlistId AND song_id = :songId")
   suspend fun deleteJoin(playlistId: Long, songId: Long)

   @Query("DELETE FROM playlist_song_join WHERE playlist_id = :playlistId")
   suspend fun deleteAllJoins(playlistId: Long)

   /** Removes join rows whose song_id no longer exists in songs table. */
   @Query(
      """
        DELETE FROM playlist_song_join
        WHERE song_id NOT IN (SELECT song_id FROM songs)
    """
   )
   suspend fun pruneOrphanedJoins()

   // ── Join reads ────────────────────────────────────────────────

   @Query("SELECT MAX(sort_order) FROM playlist_song_join WHERE playlist_id = :playlistId")
   suspend fun getMaxSortOrder(playlistId: Long): Int?

   @Query(
      """
        SELECT COUNT(*) > 0 FROM playlist_song_join
        WHERE playlist_id = :playlistId AND song_id = :songId
    """
   )
   suspend fun joinExists(playlistId: Long, songId: Long): Boolean

   @Query(
      """
        SELECT song_id FROM playlist_song_join
        WHERE playlist_id = :playlistId ORDER BY sort_order ASC
    """
   )
   suspend fun getSongIds(playlistId: Long): List<Long>

   @Query(
      """
        SELECT song_id, sort_order FROM playlist_song_join
        WHERE playlist_id = :playlistId ORDER BY sort_order ASC
    """
   )
   suspend fun getJoinOrder(playlistId: Long): List<JoinOrderRow>

   @Query(
      """
        UPDATE playlist_song_join SET sort_order = :newOrder
        WHERE playlist_id = :playlistId AND song_id = :songId
    """
   )
   suspend fun updateJoinOrder(playlistId: Long, songId: Long, newOrder: Int)

   // ── Internal DTO ──────────────────────────────────────────────

   data class JoinOrderRow(
      @ColumnInfo(name = "song_id") val songId: Long,
      @ColumnInfo(name = "sort_order") val sortOrder: Int,
   )
}
