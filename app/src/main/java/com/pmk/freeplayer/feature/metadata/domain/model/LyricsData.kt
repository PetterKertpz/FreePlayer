package com.pmk.freeplayer.feature.metadata.domain.model

import com.pmk.freeplayer.feature.metadata.data.local.entity.LyricsSource

// feature/metadata/domain/model/LyricsData.kt

data class LyricsData(
	val songId: Long,
	val plainText: String,
	val language: String?,
	val source: LyricsSource,
	val fetchedAt: Long,
)