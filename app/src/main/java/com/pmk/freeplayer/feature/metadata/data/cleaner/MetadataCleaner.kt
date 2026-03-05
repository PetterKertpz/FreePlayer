package com.pmk.freeplayer.feature.metadata.data.cleaner

import com.pmk.freeplayer.feature.metadata.domain.model.CleanedSongData
import javax.inject.Inject

// feature/metadata/data/cleaner/MetadataCleaner.kt
// Orquestador — llama a los cleaners individuales en orden correcto

class MetadataCleaner @Inject constructor() {
	
	fun clean(rawTitle: String, rawArtist: String): CleanedSongData {
		// 1. Detectar y corregir campo-swap ANTES de cualquier limpieza
		val swapResult = ArtistFieldSwapDetector.detect(rawTitle, rawArtist)
		
		// 2. Extraer feat. del título ya corregido
		val featResult = FeatExtractor.extract(swapResult.title)
		
		// 3. Limpiar ruido (sufijos, plataformas, etc.)
		val cleanedTitle = TitleCleaner.clean(featResult.cleanTitle)
		
		// 4. Detectar VersionType sobre el título limpio
		val versionType = VersionTypeDetector.detect(cleanedTitle)
		
		// 5. Eliminar indicadores de versión del título final
		val finalTitle = removeVersionIndicators(cleanedTitle)
		
		// 6. Title Case
		val titledTitle = TitleCleaner.toTitleCase(finalTitle.trim())
		val titledArtist = TitleCleaner.toTitleCase(swapResult.artist.trim())
		
		return CleanedSongData(
			songId = 0L, // se setea en el use case con el ID real
			cleanTitle = titledTitle,
			cleanArtist = titledArtist,
			featuringArtists = featResult.featuring,
			versionType = versionType,
			fieldSwapApplied = swapResult.swapApplied,
		)
	}
	
	private fun removeVersionIndicators(title: String): String {
		val patterns = listOf(
			Regex("""[\(\[]\s*(?:remix|live|acoustic|cover|radio\s*edit|instrumental|demo)[^\)\]]*[\)\]]""", RegexOption.IGNORE_CASE),
			Regex("""\s*-\s*(?:remix|live\s+version|acoustic\s+version|cover|radio\s*edit)$""", RegexOption.IGNORE_CASE),
		)
		var result = title
		for (p in patterns) result = result.replace(p, "").trim()
		return result
	}
}