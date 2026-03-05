package com.pmk.freeplayer.feature.artists.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pmk.freeplayer.feature.artists.domain.model.ArtistType

@Entity(
	tableName = "artists",
	indices = [
		Index(value = ["name"], unique = true),
		Index(value = ["genius_id"]),
		Index(value = ["spotify_id"]),
	]
)
data class ArtistEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "artist_id")    val artistId: Long = 0,
	
	// ── Identity ──────────────────────────────────────────────────
	@ColumnInfo(name = "name")         val name: String,
	@ColumnInfo(name = "real_name")    val realName: String?      = null,
	@ColumnInfo(name = "artist_type")  val type: ArtistType       = ArtistType.UNKNOWN,
	@ColumnInfo(name = "is_verified")  val isVerified: Boolean    = false,
	
	// ── Location ──────────────────────────────────────────────────
	@ColumnInfo(name = "country")      val country: String?       = null,
	@ColumnInfo(name = "city")         val city: String?          = null,
	
	// ── Biography ─────────────────────────────────────────────────
	@ColumnInfo(name = "description")  val description: String?   = null,
	@ColumnInfo(name = "biography")    val biography: String?     = null,
	
	// ── Genre (denormalized) ──────────────────────────────────────
	@ColumnInfo(name = "genre_id")     val genreId: Long?         = null,
	@ColumnInfo(name = "genre_name")   val genreName: String?     = null,
	
	// ── Dates ─────────────────────────────────────────────────────
	@ColumnInfo(name = "birth_date")   val birthDate: Long?       = null,
	@ColumnInfo(name = "career_start") val careerStartYear: Int?  = null,
	
	// ── Multimedia ────────────────────────────────────────────────
	@ColumnInfo(name = "image_url")    val remoteImageUrl: String? = null,
	@ColumnInfo(name = "image_path")   val localImagePath: String? = null,
	@ColumnInfo(name = "header_url")   val remoteHeaderUrl: String? = null,
	
	// ── External IDs ─────────────────────────────────────────────
	@ColumnInfo(name = "genius_id")    val geniusId: String?      = null,
	@ColumnInfo(name = "spotify_id")   val spotifyId: String?     = null,
	@ColumnInfo(name = "website_url")  val websiteUrl: String?    = null,
	
	// ── Social links (schema-ready; populated via migration) ──────
	@ColumnInfo(name = "instagram_username") val instagramUsername: String? = null,
	@ColumnInfo(name = "twitter_username")   val twitterUsername: String?   = null,
	@ColumnInfo(name = "tiktok_username")    val tiktokUsername: String?    = null,
	@ColumnInfo(name = "apple_music_url")    val appleMusicUrl: String?     = null,
	@ColumnInfo(name = "youtube_music_url")  val youtubeMusicUrl: String?   = null,
	@ColumnInfo(name = "facebook_url")       val facebookUrl: String?       = null,
	@ColumnInfo(name = "soundcloud_url")     val soundcloudUrl: String?     = null,
	@ColumnInfo(name = "bandcamp_url")       val bandcampUrl: String?       = null,
	@ColumnInfo(name = "discogs_url")        val discogsUrl: String?        = null,
	@ColumnInfo(name = "musicbrainz_id")     val musicBrainzId: String?     = null,
	
	// ── User preferences ─────────────────────────────────────────
	@ColumnInfo(name = "is_favorite")  val isFavorite: Boolean    = false,
	
	// ── Cached structural counts ──────────────────────────────────
	@ColumnInfo(name = "total_songs")  val totalSongs: Int        = 0,
	@ColumnInfo(name = "total_albums") val totalAlbums: Int       = 0,
	
	// ── Timestamps (FIX: no System.currentTimeMillis() defaults) ──
	@ColumnInfo(name = "date_added")   val dateAdded: Long,
	@ColumnInfo(name = "last_updated") val lastUpdated: Long,
)