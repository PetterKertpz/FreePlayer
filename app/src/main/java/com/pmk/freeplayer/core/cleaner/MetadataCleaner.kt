// core/cleaner/MetadataCleaner.kt
package com.pmk.freeplayer.core.cleaner

import com.pmk.freeplayer.data.local.dao.SongDao
import com.pmk.freeplayer.data.local.entity.SongEntity
import com.pmk.freeplayer.domain.model.enums.IntegrityStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetadataCleaner @Inject constructor(
	private val songDao: SongDao
) {
	
	/**
	 * Limpia los metadatos de una canción aplicando reglas de normalización.
	 * @return true si se aplicaron cambios
	 */
	suspend fun cleanSong(song: SongEntity): CleaningOutcome {
		val originalTitle = song.title
		var cleanedTitle = originalTitle
		var confidence = 1.0f
		val appliedRules = mutableListOf<String>()
		
		// ═══════════════════════════════════════════════════════════════
		// REGLA 1: Remover sufijos de video/audio
		// ═══════════════════════════════════════════════════════════════
		VIDEO_SUFFIXES.forEach { suffix ->
			if (cleanedTitle.contains(suffix, ignoreCase = true)) {
				cleanedTitle = cleanedTitle.replace(suffix, "", ignoreCase = true).trim()
				appliedRules.add("Removed: $suffix")
			}
		}
		
		// ═══════════════════════════════════════════════════════════════
		// REGLA 2: Remover indicadores de calidad
		// ═══════════════════════════════════════════════════════════════
		QUALITY_PATTERNS.forEach { pattern ->
			val match = pattern.find(cleanedTitle)
			if (match != null) {
				cleanedTitle = cleanedTitle.replace(match.value, "").trim()
				appliedRules.add("Removed quality: ${match.value}")
			}
		}
		
		// ═══════════════════════════════════════════════════════════════
		// REGLA 3: Extraer featuring del título
		// ═══════════════════════════════════════════════════════════════
		val featMatch = FEATURING_PATTERN.find(cleanedTitle)
		val featArtists = featMatch?.groupValues?.getOrNull(2)
		if (featMatch != null) {
			cleanedTitle = cleanedTitle.replace(featMatch.value, "").trim()
			appliedRules.add("Extracted feat: $featArtists")
		}
		
		// ═══════════════════════════════════════════════════════════════
		// REGLA 4: Normalizar espacios y caracteres
		// ═══════════════════════════════════════════════════════════════
		cleanedTitle = cleanedTitle
			.replace("_", " ")
			.replace("  ", " ")
			.replace(" - - ", " - ")
			.trim()
		
		// ═══════════════════════════════════════════════════════════════
		// REGLA 5: Capitalización correcta
		// ═══════════════════════════════════════════════════════════════
		cleanedTitle = cleanedTitle.split(" ").joinToString(" ") { word ->
			if (word.length <= 2 && word.lowercase() in LOWERCASE_WORDS) {
				word.lowercase()
			} else {
				word.replaceFirstChar { it.uppercase() }
			}
		}
		
		// Calcular confianza basada en cambios aplicados
		confidence = when {
			appliedRules.isEmpty() -> 1.0f
			appliedRules.size == 1 -> 0.95f
			appliedRules.size <= 3 -> 0.85f
			else -> 0.75f
		}
		
		val hasChanges = cleanedTitle != originalTitle
		
		// Actualizar en BD si hubo cambios
		if (hasChanges) {
			songDao.markAsCleaned(
				songId = song.songId,
				cleanTitle = cleanedTitle,
				confidence = confidence
			)
		} else {
			// Sin cambios, solo actualizar estado
			songDao.updateMetadataStatus(
				songId = song.songId,
				status = IntegrityStatus.LIMPIO.name,
				confidence = 1.0f
			)
		}
		
		return CleaningOutcome(
			songId = song.songId,
			originalTitle = originalTitle,
			cleanedTitle = cleanedTitle,
			hasChanges = hasChanges,
			appliedRules = appliedRules,
			confidence = confidence,
			featArtists = featArtists
		)
	}
	
	/**
	 * Procesa un lote de canciones pendientes de limpieza.
	 */
	suspend fun cleanBatch(limit: Int = 50): BatchCleaningResult {
		val pending = songDao.getSongsPendingCleaning(limit)
		var cleaned = 0
		var unchanged = 0
		var errors = 0
		
		pending.forEach { song ->
			try {
				val outcome = cleanSong(song)
				if (outcome.hasChanges) cleaned++ else unchanged++
			} catch (e: Exception) {
				errors++
			}
		}
		
		return BatchCleaningResult(
			processed = pending.size,
			cleaned = cleaned,
			unchanged = unchanged,
			errors = errors
		)
	}
	
	companion object {
		private val VIDEO_SUFFIXES = listOf(
			"(Official Video)", "(Official Audio)", "(Lyrics)",
			"(HD)", "(HQ)", "[Official]", "(Audio)",
			"(Visualizer)", "(Official Music Video)",
			"| Official Video", "- Official Video",
			"(Audio Oficial)", "(Video Oficial)"
		)
		
		private val QUALITY_PATTERNS = listOf(
			Regex("""\[?(320kbps|128kbps|FLAC|MP3|WAV|HQ|HD)\]?""", RegexOption.IGNORE_CASE),
			Regex("""\(?\d{4}\)?$""") // Año suelto al final
		)
		
		private val FEATURING_PATTERN = Regex(
			"""[\(\[]\s*(feat\.?|ft\.?|featuring)\s*([^)\]]+)[\)\]]""",
			RegexOption.IGNORE_CASE
		)
		
		private val LOWERCASE_WORDS = setOf("a", "an", "the", "of", "in", "on", "at", "to", "for")
	}
}

data class CleaningOutcome(
	val songId: Long,
	val originalTitle: String,
	val cleanedTitle: String,
	val hasChanges: Boolean,
	val appliedRules: List<String>,
	val confidence: Float,
	val featArtists: String?
)

data class BatchCleaningResult(
	val processed: Int,
	val cleaned: Int,
	val unchanged: Int,
	val errors: Int
)