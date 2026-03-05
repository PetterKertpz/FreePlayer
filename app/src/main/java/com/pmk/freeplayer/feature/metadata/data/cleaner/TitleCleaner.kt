package com.pmk.freeplayer.feature.metadata.data.cleaner// feature/metadata/data/cleaner/TitleCleaner.kt

object TitleCleaner {
	
	// Sufijos a eliminar — orden: más largo/específico primero
	private val NOISE_PATTERNS = listOf(
		// Video/streaming noise
		Regex("""[\(\[]\s*(?:official\s+)?(?:music\s+)?(?:video|audio|visualizer|lyric(?:s)?|hd|hq|4k|full\s+version|clip)\s*[\)\]]""", RegexOption.IGNORE_CASE),
		// Año entre corchetes/paréntesis: [2015], (2024)
		Regex("""[\(\[]\s*(?:19|20)\d{2}\s*[\)\]]"""),
		// Plataformas
		Regex("""[\(\[]\s*(?:youtube|soundcloud|spotify|tidal|vevo)\s*[\)\]]""", RegexOption.IGNORE_CASE),
		// Remaster genérico
		Regex("""[\(\[]\s*(?:\d{4}\s+)?remaster(?:ed)?\s*[\)\]]""", RegexOption.IGNORE_CASE),
		// Espacios múltiples residuales
		Regex("""\s{2,}"""),
	)
	
	fun clean(raw: String): String {
		var result = raw
		for (pattern in NOISE_PATTERNS) {
			result = if (pattern.pattern == """\s{2,}""") {
				result.replace(pattern, " ")
			} else {
				result.replace(pattern, "")
			}
		}
		return result.trim()
	}
	
	fun toTitleCase(input: String): String {
		val minorWords = setOf("a", "an", "the", "and", "but", "or", "for",
			"nor", "on", "at", "to", "by", "in", "of", "up")
		return input.split(" ").mapIndexed { index, word ->
			if (index == 0 || word.lowercase() !in minorWords)
				word.replaceFirstChar { it.titlecase() }
			else word.lowercase()
		}.joinToString(" ")
	}
}