package com.pmk.freeplayer.feature.statistics.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pmk.freeplayer.feature.statistics.domain.model.PlaySource

/**
 * Append-only event log. Never updated after insert.
 * Indexed for the most common time-range queries.
 */
@Entity(
	tableName = "play_events",
	indices = [
		Index(value = ["song_id"]),
		Index(value = ["artist_id"]),
		Index(value = ["album_id"]),
		Index(value = ["genre_id"]),
		Index(value = ["playlist_id"]),
		Index(value = ["played_at"]),   // for range queries (today, this week…)
	]
)
data class PlayEventEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "event_id")         val eventId: Long = 0,
	
	@ColumnInfo(name = "song_id")          val songId: Long,
	@ColumnInfo(name = "artist_id")        val artistId: Long?,
	@ColumnInfo(name = "album_id")         val albumId: Long?,
	@ColumnInfo(name = "genre_id")         val genreId: Long?,
	@ColumnInfo(name = "playlist_id")      val playlistId: Long?,
	
	@ColumnInfo(name = "played_at")        val playedAt: Long,
	@ColumnInfo(name = "listened_ms")      val listenedMs: Long,
	@ColumnInfo(name = "song_duration_ms") val songDurationMs: Long,
	@ColumnInfo(name = "completion_ratio") val completionRatio: Float,
	
	@ColumnInfo(name = "source")           val source: PlaySource,
	@ColumnInfo(name = "was_skipped")      val wasSkipped: Boolean,
)