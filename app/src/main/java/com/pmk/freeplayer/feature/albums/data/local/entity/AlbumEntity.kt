package com.pmk.freeplayer.feature.albums.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pmk.freeplayer.feature.artists.data.local.entity.ArtistEntity

@Entity(
	tableName = "albums", // Inglés plural
	foreignKeys = [
		ForeignKey(
			entity = ArtistEntity::class,
			parentColumns = ["artist_id"], // Asumiendo que cambiaste ArtistEntity
			childColumns = ["artist_id"],
			onDelete = ForeignKey.Companion.CASCADE
		)
	],
	indices = [
		Index(value = ["title", "artist_id"], unique = true),
		Index(value = ["artist_id"]),
		Index(value = ["year"])
	]
)
data class AlbumEntity(
	// ID Long es el estándar
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "album_id") val albumId: Long = 0,
	
	@ColumnInfo(name = "artist_id") val artistId: Long,
	
	@ColumnInfo(name = "title") val title: String,
	
	// Rutas de imagen
	@ColumnInfo(name = "cover_path") val localCoverPath: String? = null,
	@ColumnInfo(name = "cover_url") val remoteCoverUrl: String? = null,
	
	// Metadatos Temporales
	@ColumnInfo(name = "year") val year: Int?,
	@ColumnInfo(name = "release_timestamp") val releaseDate: Long? = null,
	@ColumnInfo(name = "date_added") val dateAdded: Long = System.currentTimeMillis(),
	@ColumnInfo(name = "album_type") val type: String = "ALBUM",
	@ColumnInfo(name = "genres") val genres: String? = null,
	@ColumnInfo(name = "total_duration_ms") val totalDurationMs: Long = 0,
	
	// Datos extra (Scraping)
	@ColumnInfo(name = "record_label") val label: String? = null,
	@ColumnInfo(name = "producer") val producer: String? = null,
	@ColumnInfo(name = "description") val description: String? = null,
	
	// Estadísticas
	@ColumnInfo(name = "total_songs") val totalSongs: Int = 0,
	@ColumnInfo(name = "play_count") val playCount: Int = 0,
	@ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false,
	
	// IDs Externos (Para sincronización)
	@ColumnInfo(name = "genius_id") val geniusId: String? = null,
	@ColumnInfo(name = "spotify_id") val spotifyId: String? = null,
	
	// Calificaciones
	@ColumnInfo(name = "rating_average") val rating: Float = 0f
)