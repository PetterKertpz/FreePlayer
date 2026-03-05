import com.google.gson.annotations.SerializedName

// feature/metadata/data/remote/api/dto/GeniusSongDto.kt

data class GeniusSearchResponse(
	@SerializedName("meta")     val meta: GeniusMeta,
	@SerializedName("response") val response: GeniusSearchBody,
)

data class GeniusMeta(@SerializedName("status") val status: Int)

data class GeniusSearchBody(
	@SerializedName("hits") val hits: List<GeniusHit>,
)

data class GeniusHit(
	@SerializedName("type")   val type: String,   // "song"
	@SerializedName("result") val result: GeniusSongDto,
)

// ─────────────────────────────────────────────────────────────────────────────

data class GeniusSongDetailResponse(
	@SerializedName("meta")     val meta: GeniusMeta,
	@SerializedName("response") val response: GeniusSongBody,
)

data class GeniusSongBody(
	@SerializedName("song") val song: GeniusSongDto,
)

// ─────────────────────────────────────────────────────────────────────────────

data class GeniusSongDto(
	@SerializedName("id")                   val id: Int,
	@SerializedName("title")                val title: String,
	@SerializedName("full_title")           val fullTitle: String,
	@SerializedName("title_with_featured")  val titleWithFeatured: String?,
	@SerializedName("url")                  val url: String,
	@SerializedName("api_path")             val apiPath: String,
	@SerializedName("language")             val language: String?,
	@SerializedName("release_date")         val releaseDate: String?,
	@SerializedName("recording_location")   val recordingLocation: String?,
	@SerializedName("lyrics_state")         val lyricsState: String?,
	@SerializedName("apple_music_id")       val appleMusicId: String?,
	
	// ── Images ────────────────────────────────────────────────────
	@SerializedName("song_art_image_url")           val songArtImageUrl: String?,
	@SerializedName("song_art_image_thumbnail_url") val songArtThumbnailUrl: String?,
	@SerializedName("header_image_url")             val headerImageUrl: String?,
	
	// ── Colors ────────────────────────────────────────────────────
	@SerializedName("song_art_primary_color")   val primaryColor: String?,
	@SerializedName("song_art_secondary_color") val secondaryColor: String?,
	@SerializedName("song_art_text_color")      val textColor: String?,
	
	// ── Primary artist ────────────────────────────────────────────
	@SerializedName("primary_artist")       val primaryArtist: GeniusArtistDto?,
	@SerializedName("primary_artist_names") val primaryArtistNames: String?,
	
	// ── Credits ──────────────────────────────────────────────────
	@SerializedName("featured_artists")  val featuredArtists: List<GeniusArtistDto>?,
	@SerializedName("producer_artists")  val producerArtists: List<GeniusArtistDto>?,
	@SerializedName("writer_artists")    val writerArtists: List<GeniusArtistDto>?,
	
	// ── Album ─────────────────────────────────────────────────────
	@SerializedName("album") val album: GeniusAlbumDto?,
)

data class GeniusArtistDto(
	@SerializedName("id")              val id: Int,
	@SerializedName("name")            val name: String,
	@SerializedName("image_url")       val imageUrl: String?,
	@SerializedName("description")     val description: GeniusDescriptionDto?,
)

data class GeniusAlbumDto(
	@SerializedName("id")               val id: Int,
	@SerializedName("name")             val name: String,
	@SerializedName("cover_art_url")    val coverArtUrl: String?,
	@SerializedName("release_date")     val releaseDate: String?,
	@SerializedName("artist")           val artist: GeniusArtistDto?,
)

data class GeniusDescriptionDto(
	@SerializedName("plain") val plain: String?,
)