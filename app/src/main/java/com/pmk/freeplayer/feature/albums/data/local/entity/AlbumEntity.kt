package com.pmk.freeplayer.feature.albums.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pmk.freeplayer.feature.albums.domain.model.AlbumType

@Entity(
	tableName = "albums",
	indices = [
		Index(value = ["title", "artist_id"], unique = true),
		Index(value = ["artist_id"]),
		Index(value = ["year"]),
	]
	// FIX: @ForeignKey to ArtistEntity removed — cross-module physical FK
	// breaks feature isolation. Referential integrity enforced logically via Scanner.
)
data class AlbumEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "album_id")          val albumId: Long       = 0,
	
	// ── Relations ─────────────────────────────────────────────────
	@ColumnInfo(name = "artist_id")         val artistId: Long,
	@ColumnInfo(name = "artist_name")       val artistName: String, // denormalized for JOIN-free reads
	
	// ── Core metadata ─────────────────────────────────────────────
	@ColumnInfo(name = "title")             val title: String,
	@ColumnInfo(name = "album_type")        val type: AlbumType     = AlbumType.ALBUM, // FIX: enum not String
	@ColumnInfo(name = "year")              val year: Int?          = null,
	@ColumnInfo(name = "release_timestamp") val releaseDate: Long?  = null,
	
	// ── Genre (denormalized) ──────────────────────────────────────
	@ColumnInfo(name = "genre_id")          val genreId: Long?      = null,
	@ColumnInfo(name = "genre_name")        val genreName: String?  = null, // FIX: replaces CSV genres field
	
	// ── Multimedia ────────────────────────────────────────────────
	@ColumnInfo(name = "cover_path")        val localCoverPath: String?  = null,
	@ColumnInfo(name = "cover_url")         val remoteCoverUrl: String?  = null,
	
	// ── Rich metadata ─────────────────────────────────────────────
	@ColumnInfo(name = "record_label")      val recordLabel: String? = null, // FIX: unified naming (was "label")
	@ColumnInfo(name = "producer")          val producer: String?    = null,
	@ColumnInfo(name = "description")       val description: String? = null,
	
	// ── Structural cache ──────────────────────────────────────────
	@ColumnInfo(name = "total_songs")       val totalSongs: Int      = 0,
	@ColumnInfo(name = "total_duration_ms") val totalDurationMs: Long = 0,
	
	// ── User preferences ──────────────────────────────────────────
	@ColumnInfo(name = "is_favorite")       val isFavorite: Boolean  = false,
	@ColumnInfo(name = "rating")            val rating: Float        = 0f,
	
	// ── External references ───────────────────────────────────────
	@ColumnInfo(name = "genius_id")         val geniusId: String?   = null,
	@ColumnInfo(name = "spotify_id")        val spotifyId: String?  = null,
	
	// ── Timestamps (FIX: no System.currentTimeMillis() defaults) ──
	@ColumnInfo(name = "date_added")        val dateAdded: Long,
	@ColumnInfo(name = "last_updated")      val lastUpdated: Long,
)