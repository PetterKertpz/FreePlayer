package com.pmk.freeplayer.feature.metadata.data.remote.scraper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URLEncoder
import javax.inject.Inject

// feature/metadata/data/remote/scraper/GeniusSearchScraper.kt

class GeniusSearchScraper @Inject constructor(
	private val rateLimiter: GeniusRateLimiter,
) {
	companion object {
		private const val BASE = "https://genius.com"
		private const val SEARCH_URL = "$BASE/search?q="
		private val USER_AGENT = "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 " +
				"(KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
	}
	
	data class SearchHit(
		val geniusId: Int,
		val url: String,
		val title: String,
		val artist: String,
		val thumbnailUrl: String?,
	)
	
	suspend fun search(query: String): List<SearchHit> = withContext(Dispatchers.IO) {
		rateLimiter.acquire()
		val encodedQuery = URLEncoder.encode(query, "UTF-8")
		val doc = Jsoup.connect("$SEARCH_URL$encodedQuery")
			.userAgent(USER_AGENT)
			.timeout(10_000)
			.get()
		
		// Genius renderiza resultados en <mini-song-card> o en divs con data-id
		doc.select("mini-song-card, [data-type='song']").mapNotNull { el ->
			runCatching {
				val link = el.selectFirst("a[href*='/lyrics']")
					?: el.selectFirst("a[href*='genius.com']")
					?: return@mapNotNull null
				val href = link.attr("href")
				val idFromPath = Regex("""/songs/(\d+)""").find(href)?.groupValues?.get(1)?.toIntOrNull()
				SearchHit(
					geniusId     = idFromPath ?: 0,
					url          = if (href.startsWith("http")) href else "$BASE$href",
					title        = el.selectFirst("[class*='title'], h3, h2")?.text().orEmpty(),
					artist       = el.selectFirst("[class*='artist']")?.text().orEmpty(),
					thumbnailUrl = el.selectFirst("img")?.attr("src"),
				)
			}.getOrNull()
		}
	}
}