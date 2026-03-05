package com.pmk.freeplayer.feature.metadata.data.remote.scraper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode
import javax.inject.Inject

// feature/metadata/data/remote/scraper/GeniusLyricsScraper.kt

class GeniusLyricsScraper @Inject constructor(
	private val rateLimiter: GeniusRateLimiter,
) {
	companion object {
		private val USER_AGENT = "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 " +
				"(KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
	}
	
	suspend fun scrapeLyrics(geniusUrl: String): String? = withContext(Dispatchers.IO) {
		rateLimiter.acquire()
		runCatching {
			val doc = Jsoup.connect(geniusUrl)
				.userAgent(USER_AGENT)
				.timeout(15_000)
				.get()
			
			// Genius usa divs con atributo data-lyrics-container="true"
			val containers = doc.select("[data-lyrics-container='true']")
			if (containers.isEmpty()) return@runCatching null
			
			containers.joinToString("\n\n") { container ->
				// Reemplazar <br> con newlines antes de extraer texto
				container.select("br").forEach { br -> br.replaceWith(TextNode("\n")) }
				// Eliminar anotaciones (links internos de Genius)
				container.select("a").forEach { a ->
					a.replaceWith(TextNode(a.text()))
				}
				container.text()
			}.trim()
		}.getOrNull()
	}
}