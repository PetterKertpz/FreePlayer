package com.pmk.freeplayer.feature.playlists.data.local.relation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.pmk.freeplayer.feature.playlists.data.local.entity.PlaylistEntity
import com.pmk.freeplayer.feature.songs.data.local.entity.SongEntity

@Entity(
	tableName = "playlist_song_join",
	primaryKeys = ["playlist_id", "song_id"],
	foreignKeys = [
		ForeignKey(
			entity = PlaylistEntity::class,
			parentColumns = ["playlist_id"],
			childColumns = ["playlist_id"],
			onDelete = ForeignKey.Companion.CASCADE // Si borras la playlist, se borran las uniones
		),
		ForeignKey(
			entity = SongEntity::class,
			parentColumns = ["song_id"], // Asumiendo que SongEntity tiene 'id'
			childColumns = ["song_id"],
			onDelete = ForeignKey.Companion.CASCADE
		)
	],
	indices = [
		Index(value = ["playlist_id"]),
		Index(value = ["song_id"]),
		Index(value = ["playlist_id", "sort_order"]) // Optimiza el ordenamiento
	]
)
data class PlaylistSongJoin(
	@ColumnInfo(name = "playlist_id") val playlistId: Long,
	@ColumnInfo(name = "song_id") val songId: Long,
	@ColumnInfo(name = "sort_order") val sortOrder: Int,
	@ColumnInfo(name = "added_at") val addedAt: Long = System.currentTimeMillis()
)