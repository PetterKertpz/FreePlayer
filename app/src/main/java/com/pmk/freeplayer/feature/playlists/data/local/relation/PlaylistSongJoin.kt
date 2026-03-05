package com.pmk.freeplayer.feature.playlists.data.local.relation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.pmk.freeplayer.feature.playlists.data.local.entity.PlaylistEntity

/**
 * Join table for playlist↔song membership.
 *
 * FIX: FK to SongEntity removed — SongEntity lives in a separate feature module.
 * Cross-module physical FKs break feature isolation (same rationale as ArtistEntity/AlbumEntity).
 * Referential integrity is enforced logically: orphaned rows are pruned by
 * PlaylistDao.pruneOrphanedJoins() after song deletions.
 *
 * FK to PlaylistEntity is kept — both entities live in the same feature module.
 */
@Entity(
	tableName = "playlist_song_join",
	primaryKeys = ["playlist_id", "song_id"],
	foreignKeys = [
		ForeignKey(
			entity     = PlaylistEntity::class,
			parentColumns = ["playlist_id"],
			childColumns  = ["playlist_id"],
			onDelete   = ForeignKey.CASCADE,
		),
	],
	indices = [
		Index(value = ["playlist_id"]),
		Index(value = ["song_id"]),
		Index(value = ["playlist_id", "sort_order"]),
	]
)
data class PlaylistSongJoin(
	@ColumnInfo(name = "playlist_id") val playlistId: Long,
	@ColumnInfo(name = "song_id")     val songId: Long,
	@ColumnInfo(name = "sort_order")  val sortOrder: Int,
	@ColumnInfo(name = "added_at")    val addedAt: Long,
)