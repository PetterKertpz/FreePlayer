package com.pmk.freeplayer.data.remote.scraper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject

class GeniusScraperDataSource @Inject constructor() {
	
	suspend fun fetchLyricsFromUrl(url: String): String? {
		return withContext(Dispatchers.IO) {
			try {
				// 1. Conectar a la web simulando ser un navegador real
				val doc = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
					.get()
				
				// 2. Genius usa contenedores dinámicos para las letras.
				// Buscamos div que contenga "Lyrics__Container" (clase actual de Genius)
				val lyricsDivs = doc.select("div[class*='Lyrics__Container']")
				
				if (lyricsDivs.isNotEmpty()) {
					// Procesar HTML: Reemplazar <br> con saltos de línea reales
					val sb = StringBuilder()
					lyricsDivs.forEach { element ->
						// Reemplaza <br> por \n antes de extraer texto
						element.select("br").append("\\n")
						sb.append(element.text().replace("\\n", "\n"))
						sb.append("\n")
					}
					return@withContext sb.toString().trim()
				}
				
				// Fallback: selectores antiguos por si acaso
				val oldContainer = doc.select(".lyrics").first()
				return@withContext oldContainer?.text()
			} catch (e: Exception) {
				e.printStackTrace()
				null
			}
		}
	}
}