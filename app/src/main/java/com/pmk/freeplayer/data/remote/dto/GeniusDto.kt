package com.pmk.freeplayer.data.remote.dto

// Esto solo sirve para recibir datos de Internet
data class GeniusDto(
	val id: Long, // ID de Genius, no el tuyo
	val title: String,
	val artist: String,
	val album: String?,
	val releaseDate: String?,
	val url: String,
	val coverUrl: String?,
	
	// El scraping a veces devuelve partes separadas
	val rawHtmlLyrics: String? = null,
	val plainLyrics: String? = null
) {
	// Función para convertir DTO -> Entity
	// Necesitas pasarle TU songId local porque Genius no lo conoce
	fun toEntity(localSongId: Long): com.pmk.freeplayer.data.local.entity.LyricsEntity {
		return com.pmk.freeplayer.data.local.entity.LyricsEntity(
			songId = localSongId,
			plainLyrics = this.plainLyrics ?: "",
			source = "GENIUS",
			sourceUrl = this.url,
			// Genius casi nunca da LRC sincronizado, así que synced es null
			syncedLyrics = null,
			isSynced = false
		)
	}
}