package com.pmk.freeplayer.feature.metadata.data.cleaner

import com.pmk.freeplayer.feature.songs.domain.model.VersionType

// feature/metadata/data/cleaner/VersionTypeDetector.kt

object VersionTypeDetector {
	
	// Orden importa: más específico primero
	private val RULES: List<Pair<Regex, VersionType>> = listOf(
		Regex("""radio\s*edit""",         RegexOption.IGNORE_CASE) to VersionType.RADIO_EDIT,
		Regex("""acoustic""",             RegexOption.IGNORE_CASE) to VersionType.ACOUSTIC,
		Regex("""instrumental""",         RegexOption.IGNORE_CASE) to VersionType.INSTRUMENTAL,
		Regex("""live(?:\s+at|\s+from|\s+in)?""", RegexOption.IGNORE_CASE) to VersionType.LIVE,
		Regex("""(?:official\s+)?remix""",RegexOption.IGNORE_CASE) to VersionType.REMIX,
		Regex("""cover""",                RegexOption.IGNORE_CASE) to VersionType.COVER,
		Regex("""demo""",                 RegexOption.IGNORE_CASE) to VersionType.DEMO,
	)
	
	fun detect(title: String): VersionType {
		val t = title.lowercase()
		return RULES.firstOrNull { (regex, _) -> regex.containsMatchIn(t) }?.second
			?: VersionType.ORIGINAL
	}
}