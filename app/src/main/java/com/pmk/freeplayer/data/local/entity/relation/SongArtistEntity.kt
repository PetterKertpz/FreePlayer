package com.pmk.freeplayer.data.local.entity.relation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.pmk.freeplayer.data.local.entity.ArtistEntity
import com.pmk.freeplayer.data.local.entity.SongEntity

@Entity(
	tableName = "song_artists",
	primaryKeys = ["song_id", "artist_id"], // Clave compuesta: Evita duplicados exactos
	foreignKeys = [
		ForeignKey(
			entity = SongEntity::class,
			parentColumns = ["song_id"],
			childColumns = ["song_id"],
			onDelete = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = ArtistEntity::class,
			parentColumns = ["artist_id"],
			childColumns = ["artist_id"],
			onDelete = ForeignKey.CASCADE
		)
	],
	indices = [
		Index(value = ["song_id"]),
		Index(value = ["artist_id"]) // Vital para: "Dame todas las canciones donde aparece Bad Bunny"
	]
)
data class SongArtistEntity(
	@ColumnInfo(name = "song_id") val songId: Long,
	@ColumnInfo(name = "artist_id") val artistId: Long,
	
	// "MAIN", "FEATURED", "REMIXER", "PRODUCER", "COMPOSER"
	@ColumnInfo(name = "role") val role: String = "MAIN"
)