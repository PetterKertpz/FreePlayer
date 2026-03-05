package com.pmk.freeplayer.feature.metadata.data.cleaner// feature/metadata/data/cleaner/FeatExtractor.kt

object FeatExtractor {
	
	// Cubre: feat., ft., featuring, with, &, x (entre artistas)
	private val FEAT_PATTERNS = listOf(
		Regex("""[\(\[]\s*(?:feat|ft|featuring)\.?\s+([^\)\]]+)[\)\]]""", RegexOption.IGNORE_CASE),
		Regex("""\s+(?:feat|ft|featuring)\.?\s+(.+)$""", RegexOption.IGNORE_CASE),
	)
	
	data class ExtractionResult(
		val cleanTitle: String,
		val featuring: List<String>,
	)
	
	fun extract(rawTitle: String): ExtractionResult {
		for (pattern in FEAT_PATTERNS) {
			val match = pattern.find(rawTitle) ?: continue
			val featuring = match.groupValues[1]
				.split(Regex("""\s*[,&]\s*|\s+and\s+|\s+x\s+""", RegexOption.IGNORE_CASE))
				.map { it.trim() }
				.filter { it.isNotBlank() }
			val cleanTitle = rawTitle.replace(match.value, "").trim()
			return ExtractionResult(cleanTitle, featuring)
		}
		return ExtractionResult(rawTitle, emptyList())
	}
}