package com.pmk.freeplayer.data.local.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.pmk.freeplayer.data.local.entity.AlbumEntity
import com.pmk.freeplayer.data.local.entity.ArtistEntity
import com.pmk.freeplayer.data.local.entity.SongEntity

/**
 * 📦 POJO DE RELACIÓN: Canción + Artista + Álbum
 * Usado por SongDao y SongMapper.
 */
data class SongArtistEntity(
	// La entidad base (Canción)
	@Embedded val song: SongEntity,
	
	// Relación 1:1 -> Una canción tiene UN artista principal
	@Relation(
		parentColumn = "artist_id",
		entityColumn = "artist_id"
	)
	val artist: ArtistEntity?,
	
	// Relación 1:1 -> Una canción pertenece a UN álbum
	@Relation(
		parentColumn = "album_id",
		entityColumn = "album_id"
	)
	val album: AlbumEntity?
) {
	//Helpers para que tu Mapper funcione sin cambios (usando nombres en español/inglés mixtos si es necesario)
	val artistaNombre: String?
		get() = artist?.name
	
	val albumNombre: String?
		get() = album?.title
	
	val portadaPath: String?
		get() = album?.localCoverPath ?: album?.remoteCoverUrl
}