package com.pmk.freeplayer.feature.statistics.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.pmk.freeplayer.feature.statistics.data.local.entity.PlayEventEntity
import com.pmk.freeplayer.feature.statistics.data.local.entity.StatsAggregateEntity
import com.pmk.freeplayer.feature.statistics.domain.model.EntityRank
import com.pmk.freeplayer.feature.statistics.domain.model.EntityType
import kotlinx.coroutines.flow.Flow

@Dao
interface StatisticsDao {

   // ── Write path (always call recordPlay, never insert directly) ─

   @Insert suspend fun insertEvent(event: PlayEventEntity): Long

   /**
    * Upserts the aggregate for ONE entity. Called once per affected entity inside the recordPlay
    * transaction.
    */
   @Query(
      """
        INSERT INTO stats_aggregates
            (entity_type, entity_id, play_count, total_listened_ms, skip_count, last_played_at, first_played_at)
        VALUES
            (:type, :id, 1, :listenedMs, :skipDelta, :playedAt, :playedAt)
        ON CONFLICT(entity_type, entity_id) DO UPDATE SET
            play_count        = play_count + 1,
            total_listened_ms = total_listened_ms + :listenedMs,
            skip_count        = skip_count + :skipDelta,
            last_played_at    = :playedAt,
            first_played_at   = COALESCE(first_played_at, :playedAt)
    """
   )
   suspend fun upsertAggregate(
      type: EntityType,
      id: Long,
      listenedMs: Long,
      skipDelta: Long, // 1 if skipped, 0 otherwise
      playedAt: Long,
   )

   /**
    * Core write transaction: inserts the event log entry and atomically updates all affected entity
    * aggregates in one shot.
    */
   @Transaction
   suspend fun recordPlay(event: PlayEventEntity) {
      insertEvent(event)
      val skip = if (event.wasSkipped) 1L else 0L

      upsertAggregate(EntityType.SONG, event.songId, event.listenedMs, skip, event.playedAt)
      event.artistId?.let {
         upsertAggregate(EntityType.ARTIST, it, event.listenedMs, skip, event.playedAt)
      }
      event.albumId?.let {
         upsertAggregate(EntityType.ALBUM, it, event.listenedMs, skip, event.playedAt)
      }
      event.genreId?.let {
         upsertAggregate(EntityType.GENRE, it, event.listenedMs, skip, event.playedAt)
      }
      event.playlistId?.let {
         upsertAggregate(EntityType.PLAYLIST, it, event.listenedMs, skip, event.playedAt)
      }
   }

   // ── Aggregate reads (O(1) PK lookup) ──────────────────────────

   @Query("SELECT * FROM stats_aggregates WHERE entity_type = :type AND entity_id = :id")
   fun getStats(type: EntityType, id: Long): Flow<StatsAggregateEntity?>

   @Query("SELECT * FROM stats_aggregates WHERE entity_type = :type AND entity_id = :id")
   suspend fun getStatsOnce(type: EntityType, id: Long): StatsAggregateEntity?

   // ── Ranking reads (index-backed ORDER BY) ─────────────────────

   @Query(
      """
        SELECT entity_id, entity_type, play_count, last_played_at
        FROM stats_aggregates
        WHERE entity_type = :type
        ORDER BY play_count DESC
        LIMIT :limit
    """
   )
   fun getTopByPlayCount(type: EntityType, limit: Int = 20): Flow<List<EntityRank>>

   @Query(
      """
        SELECT entity_id, entity_type, play_count, last_played_at
        FROM stats_aggregates
        WHERE entity_type = :type
        ORDER BY last_played_at DESC
        LIMIT :limit
    """
   )
   fun getRecentlyPlayed(type: EntityType, limit: Int = 20): Flow<List<EntityRank>>

   // ── Time-range event queries (analytics) ──────────────────────

   @Query(
      """
        SELECT * FROM play_events
        WHERE song_id = :songId
        ORDER BY played_at DESC
        LIMIT :limit
    """
   )
   fun getEventsForSong(songId: Long, limit: Int = 100): Flow<List<PlayEventEntity>>

   @Query(
      """
        SELECT * FROM play_events
        WHERE played_at BETWEEN :from AND :to
        ORDER BY played_at DESC
    """
   )
   fun getEventsBetween(from: Long, to: Long): Flow<List<PlayEventEntity>>

   @Query("SELECT COALESCE(SUM(listened_ms), 0) FROM play_events WHERE played_at >= :since")
   fun getTotalListenedMsSince(since: Long): Flow<Long>

   @Query("SELECT COUNT(*) FROM play_events WHERE played_at >= :since")
   fun getPlayCountSince(since: Long): Flow<Int>

   // ── Maintenance ───────────────────────────────────────────────

   /** Prunes events older than [beforeTimestamp]. Aggregates are preserved. */
   @Query("DELETE FROM play_events WHERE played_at < :beforeTimestamp")
   suspend fun pruneOldEvents(beforeTimestamp: Long)

   /**
    * Full recompute of ALL aggregates from raw events. Only for data recovery / migration. Very
    * expensive — run in background.
    */
   @Query(
      """
        INSERT OR REPLACE INTO stats_aggregates
            (entity_type, entity_id, play_count, total_listened_ms, skip_count, last_played_at, first_played_at)
        SELECT
            'SONG'            AS entity_type,
            song_id           AS entity_id,
            COUNT(*)          AS play_count,
            SUM(listened_ms)  AS total_listened_ms,
            SUM(was_skipped)  AS skip_count,
            MAX(played_at)    AS last_played_at,
            MIN(played_at)    AS first_played_at
        FROM play_events
        GROUP BY song_id
    """
   )
   suspend fun recomputeSongAggregates()
}
