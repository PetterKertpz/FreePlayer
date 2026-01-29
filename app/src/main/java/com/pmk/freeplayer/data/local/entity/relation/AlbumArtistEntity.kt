package com.pmk.freeplayer.data.local.entity.relation

import androidx.room.ColumnInfo

/**
 * Projection POJO for Album + Artist JOIN.
 * Only fetches required columns for Domain mapping.
 */
data class AlbumArtistEntity(
	@ColumnInfo(name = "album_id") val albumId: Long,
	@ColumnInfo(name = "title") val title: String,
	@ColumnInfo(name = "artist_id") val artistId: Long,
	@ColumnInfo(name = "artist_name") val artistName: String,
	@ColumnInfo(name = "cover_path") val localCoverPath: String?,
	@ColumnInfo(name = "cover_url") val remoteCoverUrl: String?,
	@ColumnInfo(name = "album_type") val type: String,
	@ColumnInfo(name = "genres") val genres: String?,
	@ColumnInfo(name = "year") val year: Int?,
	@ColumnInfo(name = "date_added") val dateAdded: Long,
	@ColumnInfo(name = "description") val description: String?,
	@ColumnInfo(name = "producer") val producer: String?,
	@ColumnInfo(name = "record_label") val recordLabel: String?,
	@ColumnInfo(name = "total_songs") val totalSongs: Int,
	@ColumnInfo(name = "total_duration_ms") val totalDurationMs: Long,
	@ColumnInfo(name = "play_count") val playCount: Int,
	@ColumnInfo(name = "rating_average") val rating: Float,
	@ColumnInfo(name = "is_favorite") val isFavorite: Boolean,
	@ColumnInfo(name = "genius_id") val geniusId: String?,
	@ColumnInfo(name = "spotify_id") val spotifyId: String?
)