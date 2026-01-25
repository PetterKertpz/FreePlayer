package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.dto.LibraryStatsDto
import com.pmk.freeplayer.data.local.dto.TopItemDto
import com.pmk.freeplayer.domain.model.DashboardItem
import com.pmk.freeplayer.domain.model.LibrarySummary

fun LibraryStatsDto.toDomain(): LibrarySummary {
	return LibrarySummary(
		songCount = this.totalSongs,
		artistCount = this.totalArtists,
		albumCount = this.totalAlbums,
		genreCount = this.totalGenres,
		// Aquí podrías usar una utilidad para formatear milisegundos a texto
		totalDurationFormatted = formatDuration(this.totalDurationMs)
	)
}

fun TopItemDto.toDomain(): DashboardItem {
	return DashboardItem(
		id = this.id,
		title = this.name,
		subtitle = this.subtitle,
		score = this.value,
		image = this.imageUri
	)
}

// Utilidad simple (o usa tu clase TrackDuration)
private fun formatDuration(ms: Long): String {
	val hours = ms / 3600000
	val minutes = (ms % 3600000) / 60000
	return "${hours}h ${minutes}m"
}