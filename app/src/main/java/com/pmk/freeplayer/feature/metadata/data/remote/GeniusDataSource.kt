package com.pmk.freeplayer.feature.metadata.data.remote

import GeniusArtistDto
import GeniusSongDto
import com.pmk.freeplayer.feature.metadata.data.cleaner.ArtistFieldSwapDetector
import com.pmk.freeplayer.feature.metadata.data.remote.api.GeniusApiService
import com.pmk.freeplayer.feature.metadata.data.remote.scraper.GeniusLyricsScraper
import com.pmk.freeplayer.feature.metadata.data.remote.scraper.GeniusSearchScraper
import javax.inject.Inject

// feature/metadata/data/remote/GeniusDataSource.kt

class GeniusDataSource @Inject constructor(
	private val apiService: GeniusApiService,
	private val searchScraper: GeniusSearchScraper,
	private val lyricsScraper: GeniusLyricsScraper,
	private val rateLimiter: GeniusRateLimiter,
) {
	companion object {
		private const val SIMILARITY_THRESHOLD = 0.75f
	}
	
	/**
	 * Busca la canción en Genius. Usa API si hay token, scraping si no.
	 * Retorna null si no encuentra match con confidence suficiente.
	 */
	suspend fun findSong(
		cleanTitle: String,
		cleanArtist: String,
		accessToken: String?,
	): GeniusSongDto? {
		val query = "$cleanArtist $cleanTitle"
		return if (!accessToken.isNullOrBlank()) {
			findViaApi(query, cleanTitle, cleanArtist, accessToken)
		} else {
			findViaScraping(query, cleanTitle, cleanArtist)
		}
	}
	
	suspend fun fetchLyrics(geniusUrl: String): String? =
		lyricsScraper.scrapeLyrics(geniusUrl)
	
	// ── Private ───────────────────────────────────────────────────
	
	private suspend fun findViaApi(
		query: String,
		cleanTitle: String,
		cleanArtist: String,
		token: String,
	): GeniusSongDto? = runCatching {
		val results = apiService.search(query)
		val hit = results.response.hits
			.filter { it.type == "song" }
			.maxByOrNull { scoreSong(it.result, cleanTitle, cleanArtist) }
			?: return null
		val score = scoreSong(hit.result, cleanTitle, cleanArtist)
		if (score < SIMILARITY_THRESHOLD) return null
		// Fetch full song details
		rateLimiter.acquire()
		apiService.getSong(hit.result.id).response.song
			.copy() // ya es full DTO con album, credits, etc.
	}.getOrNull()
	
	private suspend fun findViaScraping(
		query: String,
		cleanTitle: String,
		cleanArtist: String,
	): GeniusSongDto? = runCatching {
		val hits = searchScraper.search(query)
		val best = hits.maxByOrNull { hit ->
			val titleScore = ArtistFieldSwapDetector.similarity(hit.title, cleanTitle)
			val artistScore = ArtistFieldSwapDetector.similarity(hit.artist, cleanArtist)
			(titleScore + artistScore) / 2f
		} ?: return null
		
		val titleScore = ArtistFieldSwapDetector.similarity(best.title, cleanTitle)
		val artistScore = ArtistFieldSwapDetector.similarity(best.artist, cleanArtist)
		val score = (titleScore + artistScore) / 2f
		if (score < SIMILARITY_THRESHOLD) return null
		
		// Construir DTO mínimo desde scraping (sin API key no tenemos JSON completo)
		GeniusSongDto(
			id = best.geniusId,
			title = best.title,
			fullTitle = "${best.title} by ${best.artist}",
			titleWithFeatured = null,
			url = best.url,
			apiPath = "/songs/${best.geniusId}",
			language = null,
			releaseDate = null,
			recordingLocation = null,
			lyricsState = null,
			appleMusicId = null,
			songArtImageUrl = best.thumbnailUrl,
			songArtThumbnailUrl = best.thumbnailUrl,
			headerImageUrl = null,
			primaryColor = null,
			secondaryColor = null,
			textColor = null,
			primaryArtist = GeniusArtistDto(id = 0, name = best.artist, imageUrl = null, description = null),
			primaryArtistNames = best.artist,
			featuredArtists = null,
			producerArtists = null,
			writerArtists = null,
			album = null,
		)
	}.getOrNull()
	
	private fun scoreSong(dto: GeniusSongDto, cleanTitle: String, cleanArtist: String): Float {
		val titleScore = ArtistFieldSwapDetector.similarity(dto.title, cleanTitle)
		val artistScore = ArtistFieldSwapDetector.similarity(
			dto.primaryArtistNames.orEmpty(), cleanArtist
		)
		return (titleScore * 0.6f) + (artistScore * 0.4f) // título pesa más
	}
}