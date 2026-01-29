package com.pmk.freeplayer.core.service.mapper

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.pmk.freeplayer.domain.model.Song

fun Song.toMediaItem(): MediaItem {
	val metadata = MediaMetadata.Builder()
		.setTitle(title)
		.setArtist(artistName)
		.setAlbumTitle("Album ID: $albumId") // O el nombre real si lo tuvieras
		.setDisplayTitle(title)
		.setArtworkUri(null) // Aquí iría la URI de la carátula si la tienes
		.setIsBrowsable(false)
		.setIsPlayable(true)
		.build()
	
	return MediaItem.Builder()
		.setMediaId(id.toString())
		.setUri(filePath) // O content:// uri si usas Scoped Storage
		.setMediaMetadata(metadata)
		.setMimeType(mimeType)
		.build()
}

fun List<Song>.toMediaItems(): List<MediaItem> = map { it.toMediaItem() }