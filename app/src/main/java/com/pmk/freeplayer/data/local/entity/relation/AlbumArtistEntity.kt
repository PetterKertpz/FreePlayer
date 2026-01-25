package com.pmk.freeplayer.data.local.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.pmk.freeplayer.data.local.entity.AlbumEntity
import com.pmk.freeplayer.data.local.entity.ArtistEntity

/**
 * 📦 POJO DE RELACIÓN: Álbum + Artista
 * Usado por AlbumDao y AlbumMapper.
 */
data class AlbumArtistEntity(
	@Embedded val album: AlbumEntity,
	
	@Relation(
		parentColumn = "artist_id",
		entityColumn = "artist_id"
	)
	val artist: ArtistEntity?
) {
	// Helper para el Mapper
	val artistName: String?
		get() = artist?.name
}