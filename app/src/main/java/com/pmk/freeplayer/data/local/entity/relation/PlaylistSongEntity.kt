package com.pmk.freeplayer.data.local.entity.relation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pmk.freeplayer.data.local.entity.PlaylistEntity
import com.pmk.freeplayer.data.local.entity.SongEntity

@Entity(
	tableName = "playlist_songs",
	foreignKeys = [
		ForeignKey(
			entity = PlaylistEntity::class,
			parentColumns = ["playlist_id"],
			childColumns = ["playlist_id"],
			onDelete = ForeignKey.CASCADE // Si borras la playlist, se borran las relaciones
		),
		ForeignKey(
			entity = SongEntity::class,
			parentColumns = ["song_id"],
			childColumns = ["song_id"],
			onDelete = ForeignKey.CASCADE // Si borras la canción de la biblioteca, desaparece de las playlists
		)
	],
	indices = [
		Index(value = ["playlist_id", "sort_order"]), // Para cargar la lista ordenada
		Index(value = ["song_id"]) // Para saber en qué playlists está una canción
	]
)
data class PlaylistSongEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id") val id: Long = 0,
	
	@ColumnInfo(name = "playlist_id") val playlistId: Long,
	@ColumnInfo(name = "song_id") val songId: Long,
	
	// El orden específico dentro de ESTA playlist (0, 1, 2...)
	@ColumnInfo(name = "sort_order") val sortOrder: Int,
	
	@ColumnInfo(name = "added_at") val addedAt: Long = System.currentTimeMillis()
)