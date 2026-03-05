package com.pmk.freeplayer.feature.metadata.data.cleaner// feature/metadata/data/cleaner/ArtistFieldSwapDetector.kt

object ArtistFieldSwapDetector {
	
	data class SwapResult(
		val title: String,
		val artist: String,
		val swapApplied: Boolean,
	)
	
	/**
	 * Casos cubiertos:
	 * 1. Título contiene el artista como prefijo: "Lil Peep Star Shopping" + artist="Lil Peep"
	 *    → title="Star Shopping", artist="Lil Peep"
	 * 2. Título contiene el artista como sufijo: "Star Shopping Lil Peep" + artist="Lil Peep"
	 *    → title="Star Shopping", artist="Lil Peep"
	 * 3. Artista contiene el título: raro pero cubierto por simetría
	 * 4. Separadores comunes: " - ", " – ", " | ", " : "
	 */
	fun detect(rawTitle: String, rawArtist: String): SwapResult {
		val titleNorm = rawTitle.trim()
		val artistNorm = rawArtist.trim()
		
		// ── Caso: "Artista - Título" o "Artista – Título" en el campo título ──
		val separatorPattern = Regex("""^(.+?)\s*[-–|:]\s*(.+)$""")
		val separatorMatch = separatorPattern.find(titleNorm)
		if (separatorMatch != null) {
			val left = separatorMatch.groupValues[1].trim()
			val right = separatorMatch.groupValues[2].trim()
			// Si la parte izquierda es similar al artista conocido → swap
			if (similarity(left, artistNorm) >= 0.8f) {
				return SwapResult(title = right, artist = left, swapApplied = true)
			}
			// Si el artista está en la parte derecha (orden invertido)
			if (similarity(right, artistNorm) >= 0.8f) {
				return SwapResult(title = left, artist = right, swapApplied = true)
			}
		}
		
		// ── Caso: artista como prefijo en el título (sin separador) ──
		// "Lil Peep Star Shopping" → artist="Lil Peep", title="Star Shopping"
		if (titleNorm.startsWith(artistNorm, ignoreCase = true)) {
			val remainder = titleNorm.drop(artistNorm.length).trim()
			if (remainder.isNotBlank()) {
				return SwapResult(title = remainder, artist = artistNorm, swapApplied = true)
			}
		}
		
		// ── Caso: artista como sufijo en el título ──
		if (titleNorm.endsWith(artistNorm, ignoreCase = true)) {
			val remainder = titleNorm.dropLast(artistNorm.length).trim()
			if (remainder.isNotBlank()) {
				return SwapResult(title = remainder, artist = artistNorm, swapApplied = true)
			}
		}
		
		return SwapResult(title = titleNorm, artist = artistNorm, swapApplied = false)
	}
	
	// Levenshtein normalizado [0.0, 1.0]
	fun similarity(a: String, b: String): Float {
		val s1 = a.lowercase().trim()
		val s2 = b.lowercase().trim()
		if (s1 == s2) return 1f
		if (s1.isEmpty() || s2.isEmpty()) return 0f
		val maxLen = maxOf(s1.length, s2.length)
		return 1f - (levenshtein(s1, s2).toFloat() / maxLen)
	}
	
	private fun levenshtein(a: String, b: String): Int {
		val dp = Array(a.length + 1) { IntArray(b.length + 1) }
		for (i in 0..a.length) dp[i][0] = i
		for (j in 0..b.length) dp[0][j] = j
		for (i in 1..a.length) {
			for (j in 1..b.length) {
				dp[i][j] = if (a[i - 1] == b[j - 1]) dp[i - 1][j - 1]
				else 1 + minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
			}
		}
		return dp[a.length][b.length]
	}
}