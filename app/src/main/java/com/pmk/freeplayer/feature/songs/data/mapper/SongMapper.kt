package com.pmk.freeplayer.feature.songs.data.mapper

import com.pmk.freeplayer.core.common.utils.FileSize
import com.pmk.freeplayer.core.common.utils.TrackDuration
import com.pmk.freeplayer.feature.songs.data.local.entity.SongEntity
import com.pmk.freeplayer.feature.songs.domain.model.AudioQuality
import com.pmk.freeplayer.feature.songs.domain.model.MetadataStatus
import com.pmk.freeplayer.feature.songs.domain.model.Song
import com.pmk.freeplayer.feature.songs.domain.model.SourceType
import com.pmk.freeplayer.feature.songs.domain.model.VersionType

// ── Entity → Domain ───────────────────────────────────────────────────────────

fun SongEntity.toDomain(): Song =
   Song(
      id = songId,
      title = title,
      artistName = artistName,
      filePath = filePath,
      trackNumber = trackNumber,
      discNumber = discNumber,
      year = year,
      artistId = artistId,
      albumId = albumId,
      genreId = genreId,
      duration = TrackDuration(durationMs),
      size = FileSize(sizeBytes),
      mimeType = mimeType,
      bitrate = bitrate,
      sampleRate = sampleRate,
      audioQuality = audioQuality,
      sourceType = sourceType,
      originalTitle = originalTitle,
      originalArtist = originalArtist,
      versionType = versionType,
      featuringArtists = featuringArtists ?: emptyList(),
      geniusUrl = geniusUrl,
      externalIds = externalIds,
      isFavorite = isFavorite,
      rating = rating,
      hasLyrics = hasLyrics,
      metadataStatus = metadataStatus,
      dateAdded = dateAdded,
      dateModified = dateModified,
   )

fun List<SongEntity>.toDomain(): List<Song> = map { it.toDomain() }

// ── Domain → Entity ───────────────────────────────────────────────────────────

/**
 * Pure domain→entity conversion. Data-layer-only fields (fileHash, geniusId, hasCover,
 * confidenceScore, geniusTitle) are preserved via read-modify-write in the repository; they are not
 * part of the domain model.
 */
fun Song.toEntity(): SongEntity =
   SongEntity(
      songId = id,
      title = title,
      artistName = artistName,
      durationMs = duration.millis,
      sizeBytes = size.bytes,
      artistId = artistId,
      albumId = albumId,
      genreId = genreId,
      filePath = filePath,
      trackNumber = trackNumber,
      discNumber = discNumber,
      year = year,
      mimeType = mimeType,
      bitrate = bitrate,
      sampleRate = sampleRate,
      audioQuality = audioQuality,
      sourceType = sourceType,
      originalTitle = originalTitle,
      originalArtist = originalArtist,
      versionType = versionType,
      featuringArtists = featuringArtists.ifEmpty { null },
      geniusUrl = geniusUrl,
      externalIds = externalIds,
      isFavorite = isFavorite,
      rating = rating,
      hasLyrics = hasLyrics,
      metadataStatus = metadataStatus,
      dateAdded = dateAdded,
      dateModified = dateModified,
   )

// ── Scanner factory ───────────────────────────────────────────────────────────

fun createScannedSongEntity(
   filePath: String,
   title: String,
   artistName: String,
   durationMs: Long,
   sizeBytes: Long,
   mimeType: String,
   artistId: Long? = null,
   albumId: Long? = null,
   genreId: Long? = null,
   trackNumber: Int? = null,
   discNumber: Int? = 1,
   year: Int? = null,
   bitrate: Int? = null,
   sampleRate: Int? = null,
   fileHash: String? = null,
   hasCover: Boolean = false,
   now: Long = System.currentTimeMillis(),
): SongEntity =
   SongEntity(
      songId = 0,
      artistId = artistId,
      albumId = albumId,
      genreId = genreId,
      artistName = artistName,
      title = title,
      durationMs = durationMs,
      trackNumber = trackNumber,
      discNumber = discNumber,
      year = year,
      originalTitle = title,
      originalArtist = artistName,
      versionType = detectVersionType(title),
      sourceType = SourceType.LOCAL,
      filePath = filePath,
      sizeBytes = sizeBytes,
      fileHash = fileHash,
      mimeType = mimeType,
      bitrate = bitrate,
      sampleRate = sampleRate,
      audioQuality = resolveAudioQuality(bitrate, mimeType),
      isFavorite = false,
      rating = 0f,
      dateAdded = now,
      hasLyrics = false,
      hasCover = hasCover,
      metadataStatus = MetadataStatus.Raw,
      confidenceScore = 0f,
   )

// ── Domain extension properties ───────────────────────────────────────────────

val Song.isHighQuality: Boolean
   get() = audioQuality == AudioQuality.LOSSLESS || audioQuality == AudioQuality.HI_RES

val Song.isLowQuality: Boolean
   get() = audioQuality == AudioQuality.LOW || (bitrate != null && bitrate < 192)

val Song.ratingPercentage: Int
   get() = ((rating / 5f) * 100).toInt().coerceIn(0, 100)

fun Song.versionLabel(): String? =
   when (versionType) {
      VersionType.LIVE -> "🎤 Live"
      VersionType.REMIX -> "🎛️ Remix"
      VersionType.ACOUSTIC -> "🎸 Acoustic"
      VersionType.COVER -> "🎵 Cover"
      VersionType.RADIO_EDIT -> "📻 Radio Edit"
      else -> null
   }

// ── Private helpers ───────────────────────────────────────────────────────────

private fun detectVersionType(title: String): VersionType {
   val t = title.lowercase()
   return when {
      t.contains("live") -> VersionType.LIVE
      t.contains("remix") -> VersionType.REMIX
      t.contains("acoustic") -> VersionType.ACOUSTIC
      t.contains("cover") -> VersionType.COVER
      t.contains("radio edit") -> VersionType.RADIO_EDIT
      t.contains("instrumental") -> VersionType.INSTRUMENTAL
      t.contains("demo") -> VersionType.DEMO
      else -> VersionType.ORIGINAL
   }
}

private fun resolveAudioQuality(bitrate: Int?, mimeType: String): AudioQuality =
   when {
      bitrate == null -> AudioQuality.UNKNOWN
      bitrate >= 1411 -> AudioQuality.HI_RES
      bitrate >= 320 && mimeType.contains("flac", ignoreCase = true) -> AudioQuality.LOSSLESS
      bitrate >= 320 -> AudioQuality.HIGH
      bitrate >= 192 -> AudioQuality.MEDIUM
      else -> AudioQuality.LOW
   }
