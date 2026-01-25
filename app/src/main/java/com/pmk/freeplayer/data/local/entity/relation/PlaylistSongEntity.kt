package com.pmk.freeplayer.data.local.entity.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.pmk.freeplayer.data.local.entity.PlaylistEntity
import com.pmk.freeplayer.data.local.entity.SongEntity

/**
 * 📦 POJO DE RELACIÓN MUCHOS-A-MUCHOS: Playlist + Canciones
 * Usa la tabla 'playlist_song_join' (PlaylistSongEntity) para unir.
 */
data class PlaylistSongEntity(
	@Embedded val playlist: PlaylistEntity,
	
	@Relation(
		parentColumn = "playlist_id",
		entityColumn = "song_id",
		associateBy = Junction(PlaylistSongEntity::class)
	)
	val songs: List<SongEntity>
)