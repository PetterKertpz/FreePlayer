package com.pmk.freeplayer.data.remote.dto

// Tu DTO actual (ligeramente modificado para ser instanciado desde el Raw)
data class GeniusDto(
	val id: Long,
	val title: String,
	val artist: String,
	val album: String? = null, // La búsqueda simple a veces no trae esto
	val releaseDate: String?,
	val url: String,
	val coverUrl: String?,
	
	// Var porque los llenaremos DESPUÉS con el scraping
	var rawHtmlLyrics: String? = null,
	var plainLyrics: String? = null
) {
	// Función de conversión Entities (La que ya tenías)
	fun toEntity(localSongId: Long): com.pmk.freeplayer.data.local.entity.LyricsEntity {
		return com.pmk.freeplayer.data.local.entity.LyricsEntity(
			songId = localSongId,
			plainLyrics = this.plainLyrics ?: "",
			source = "GENIUS",
			sourceUrl = this.url,
			syncedLyrics = null,
			isSynced = false
		)
	}
	
	companion object {
		// Mapeador: Convierte lo que llega de Retrofit a TU DTO
		fun fromRaw(raw: GeniusSongRaw): GeniusDto {
			return GeniusDto(
				id = raw.id,
				title = raw.title,
				artist = raw.primaryArtist.name,
				album = null,
				releaseDate = raw.releaseDate,
				url = raw.url,
				coverUrl = raw.coverUrl
			)
		}
	}
}