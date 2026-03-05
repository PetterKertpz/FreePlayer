package com.pmk.freeplayer.feature.songs.domain.model

import com.pmk.freeplayer.core.common.utils.FileSize
import com.pmk.freeplayer.core.common.utils.TrackDuration

data class Song(
   val id: Long,
   val title: String,
   val artistName: String,
   val filePath: String,
   val artistId: Long?,
   val albumId: Long?,
   val genreId: Long?,
   val trackNumber: Int?,
   val discNumber: Int?,
   val year: Int?,
   val duration: TrackDuration,
   val size: FileSize,
   val mimeType: String,
   val bitrate: Int?,
   val sampleRate: Int?,
   val audioQuality: AudioQuality,
   val sourceType: SourceType,
   val originalTitle: String?,
   val originalArtist: String?,
   val versionType: VersionType,
   val featuringArtists: List<String> = emptyList(),
   val geniusUrl: String?,
   val externalIds: Map<String, String>?,
   val dateAdded: Long,
   val dateModified: Long?,
   val isFavorite: Boolean,
   val rating: Float,
   val hasLyrics: Boolean,
   val metadataStatus: MetadataStatus,
) {
   val isEdited: Boolean
      get() =
         (originalTitle != null && originalTitle != title) ||
            (originalArtist != null && originalArtist != artistName)
}
