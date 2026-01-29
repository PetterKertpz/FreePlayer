package com.pmk.freeplayer.domain.strategy

import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.scanner.ComparisonResult

/**
 * Strategy for matching songs against external metadata sources.
 *
 * The domain doesn't know about Genius, Spotify, MusicBrainz, etc. It only knows about generic
 * metadata to compare.
 */
interface SongMatchingStrategy {

   /**
    * Compare local song metadata against a candidate from any external source.
    *
    * @param localMetadata Metadata from the local song
    * @param candidateMetadata Metadata from external source (Genius, Spotify, etc.)
    * @return Comparison result with confidence scores
    */
   fun calculateMatch(
      localMetadata: SongMetadata,
      candidateMetadata: SongMetadata,
   ): ComparisonResult
}

/**
 * Generic song metadata for comparison. Both local songs and external candidates are converted to
 * this format.
 */
data class SongMetadata(
   val title: String,
   val artist: String,
   val album: String? = null,
   val duration: Long? = null,
   val year: Int? = null,
   val trackNumber: Int? = null,
) {
   companion object {
      fun fromSong(song: Song) =
         SongMetadata(
            title = song.title,
            artist = song.artistName,
            album = null,
            duration = song.duration.millis,
            year = song.year,
            trackNumber = song.trackNumber,
         )
   }
}

/** Default implementation using fuzzy string matching */
class FuzzySongMatchingStrategy(
	private val titleWeight: Float = 0.5f,
	private val artistWeight: Float = 0.35f,
	private val albumWeight: Float = 0.15f,
	private val confidenceThreshold: Float = ComparisonResult.UMBRAL_CONFIABLE,
) : SongMatchingStrategy {
	
	override fun calculateMatch(
		localMetadata: SongMetadata,
		candidateMetadata: SongMetadata,
	): ComparisonResult {
		// 1. Detectar si podemos comparar álbumes
		val hasAlbumInfo = !localMetadata.album.isNullOrBlank() && !candidateMetadata.album.isNullOrBlank()
		
		// 2. Ajustar pesos dinámicamente
		// Si no hay info de álbum, el peso del álbum se vuelve 0 y redistribuimos el resto.
		val (wTitle, wArtist, wAlbum) = if (hasAlbumInfo) {
			Triple(titleWeight, artistWeight, albumWeight)
		} else {
			renormalizeWeights(titleWeight, artistWeight)
		}
		
		// 3. Calcular similitudes individuales
		val titleScore = calculateSimilarity(localMetadata.title, candidateMetadata.title)
		val artistScore = calculateSimilarity(localMetadata.artist, candidateMetadata.artist)
		
		val albumScore = if (hasAlbumInfo) {
			calculateSimilarity(localMetadata.album!!, candidateMetadata.album!!)
		} else 0f
		
		// 4. Calcular total ponderado
		val totalScore = (titleScore * wTitle) + (artistScore * wArtist) + (albumScore * wAlbum)
		
		return ComparisonResult(
			puntuacionTitulo = titleScore,
			puntuacionArtista = artistScore,
			puntuacionAlbum = if (hasAlbumInfo) albumScore else -1f, // -1 indica que no se comparó
			puntuacionTotal = totalScore,
			esConfiable = totalScore >= confidenceThreshold,
		)
	}
	
	/**
	 * Redistribuye los pesos para que sumen 1.0 si eliminamos el álbum.
	 * Ejemplo: Si Title=0.5, Artist=0.35 (Total 0.85) -> Nuevos pesos serán aprox 0.58 y 0.41
	 */
	private fun renormalizeWeights(w1: Float, w2: Float): Triple<Float, Float, Float> {
		val currentTotal = w1 + w2
		if (currentTotal == 0f) return Triple(0f, 0f, 0f)
		
		val newW1 = w1 / currentTotal
		val newW2 = w2 / currentTotal
		return Triple(newW1, newW2, 0f) // El tercer peso (álbum) es 0
	}
	
	private fun calculateSimilarity(str1: String, str2: String): Float {
		// ... (Tu implementación existente de normalize y Jaccard aquí) ...
		return 0f // Placeholder para no copiar todo tu código anterior
	}
}
