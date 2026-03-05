package com.pmk.freeplayer.feature.metadata.domain.model

import com.pmk.freeplayer.feature.songs.domain.model.VersionType

data class CleanedSongData(
	val songId: Long,
	val cleanTitle: String,
	val cleanArtist: String,
	val featuringArtists: List<String>,
	val versionType: VersionType,
	val fieldSwapApplied: Boolean,  // true si se detectó artista en título o viceversa
)