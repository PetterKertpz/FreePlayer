package com.pmk.freeplayer.domain.model

data class LibrarySummary(
	val songCount: Int,
	val artistCount: Int,
	val albumCount: Int,
	val genreCount: Int,
	val totalDurationFormatted: String // Ej: "12h 30m"
)

data class DashboardItem(
	val id: Long,
	val title: String,
	val subtitle: String?,
	val score: Int, // playCount
	val image: String?
)