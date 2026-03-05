package com.pmk.freeplayer.feature.songs.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pmk.freeplayer.feature.songs.domain.model.AudioQuality
import com.pmk.freeplayer.feature.songs.domain.model.MetadataStatus
import com.pmk.freeplayer.feature.songs.domain.model.SourceType
import com.pmk.freeplayer.feature.songs.domain.model.VersionType

@Entity(
   tableName = "songs",
   indices =
      [
         Index(value = ["file_path"], unique = true),
         Index(value = ["artist_id"]),
         Index(value = ["album_id"]),
         Index(value = ["title"]),
      ],
)
data class SongEntity(
   @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "song_id") val songId: Long = 0,

   // ── Relations (logical; no cross-module FK coupling) ──────────
   @ColumnInfo(name = "artist_id") val artistId: Long?,
   @ColumnInfo(name = "album_id") val albumId: Long?,
   @ColumnInfo(name = "genre_id") val genreId: Long?,
   @ColumnInfo(name = "artist_name") val artistName: String,
   @ColumnInfo(name = "album_name") val albumName: String? = null,
   @ColumnInfo(name = "genre_name") val genreName: String? = null,

   // ── Core metadata ─────────────────────────────────────────────
   @ColumnInfo(name = "title") val title: String,
   @ColumnInfo(name = "duration") val durationMs: Long,
   @ColumnInfo(name = "track_number") val trackNumber: Int? = null,
   @ColumnInfo(name = "disc_number") val discNumber: Int? = null,
   @ColumnInfo(name = "year") val year: Int? = null,
   @ColumnInfo(name = "featuring_artists") val featuringArtists: List<String>? = null,
   @ColumnInfo(name = "original_title") val originalTitle: String? = null,
   @ColumnInfo(name = "original_artist") val originalArtist: String? = null,
   @ColumnInfo(name = "version_type") val versionType: VersionType = VersionType.ORIGINAL,
   @ColumnInfo(name = "source_type") val sourceType: SourceType = SourceType.LOCAL,

   // ── Physical file ─────────────────────────────────────────────
   @ColumnInfo(name = "file_path") val filePath: String,
   @ColumnInfo(name = "file_size") val sizeBytes: Long,
   @ColumnInfo(name = "file_hash") val fileHash: String? = null,
   @ColumnInfo(name = "mime_type") val mimeType: String,

   // ── Audio quality ─────────────────────────────────────────────
   @ColumnInfo(name = "bitrate") val bitrate: Int? = null,
   @ColumnInfo(name = "sample_rate") val sampleRate: Int? = null,
   @ColumnInfo(name = "audio_quality") val audioQuality: AudioQuality = AudioQuality.UNKNOWN,

   // ── Genius / external ─────────────────────────────────────────
   @ColumnInfo(name = "genius_id") val geniusId: String? = null,
   @ColumnInfo(name = "genius_url") val geniusUrl: String? = null,
   @ColumnInfo(name = "genius_full_title") val geniusTitle: String? = null,
   @ColumnInfo(name = "external_ids") val externalIds: Map<String, String>? = null,

   // ── User preferences ─────────────────────────────────────────
   @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false,
   @ColumnInfo(name = "is_hidden") val isHidden: Boolean = false,
   @ColumnInfo(name = "rating") val rating: Float = 0f,
   @ColumnInfo(name = "has_lyrics") val hasLyrics: Boolean = false,
   @ColumnInfo(name = "has_cover") val hasCover: Boolean = false,
   @ColumnInfo(name = "confidence_score") val confidenceScore: Float = 0f,
   @ColumnInfo(name = "metadata_status") val metadataStatus: MetadataStatus = MetadataStatus.Raw,

   // ── Timestamps ────────────────────────────────────────────────
   @ColumnInfo(name = "date_added") val dateAdded: Long,
   @ColumnInfo(name = "date_modified") val dateModified: Long? = null,
   @ColumnInfo(name = "cleaned_at") val cleanedAt: Long? = null,
   @ColumnInfo(name = "enriched_at") val enrichedAt: Long? = null,
)
