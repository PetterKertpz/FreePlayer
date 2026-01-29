package com.pmk.freeplayer.domain.useCase

import com.pmk.freeplayer.domain.model.Lyrics
import com.pmk.freeplayer.domain.model.enums.LyricsStatus
import com.pmk.freeplayer.domain.repository.LyricsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLyricsUseCase @Inject constructor(
	private val repository: LyricsRepository
) {
	operator fun invoke(songId: Long): Flow<Lyrics?> = repository.getLyrics(songId)
	
	suspend fun hasLyrics(songId: Long): Boolean = repository.hasLyrics(songId)
}

// domain/useCase/lyrics/SearchLyricsUseCase.kt
sealed class LyricsSearchResult {
	data class Found(val lyrics: Lyrics) : LyricsSearchResult()
	data object NotFound : LyricsSearchResult()
	data class Error(val message: String) : LyricsSearchResult()
}

class SearchLyricsUseCase @Inject constructor(
	private val repository: LyricsRepository
) {
	/**
	 * Busca letras primero en archivo local, luego online
	 */
	suspend operator fun invoke(
		title: String,
		artist: String,
		songPath: String? = null
	): LyricsSearchResult {
		return try {
			// 1. Intentar archivo .lrc local
			songPath?.let { path ->
				repository.searchLocalLrcFile(path)?.let {
					return LyricsSearchResult.Found(it)
				}
			}
			
			// 2. Buscar online (Genius, etc.)
			val lyrics = repository.searchOnline(title, artist)
			
			if (lyrics != null) {
				LyricsSearchResult.Found(lyrics)
			} else {
				LyricsSearchResult.NotFound
			}
		} catch (e: Exception) {
			LyricsSearchResult.Error(e.message ?: "Unknown error")
		}
	}
}

// domain/useCase/lyrics/SaveLyricsUseCase.kt
class SaveLyricsUseCase @Inject constructor(
	private val repository: LyricsRepository
) {
	suspend operator fun invoke(lyrics: Lyrics) = repository.saveLyrics(lyrics)
}

// domain/useCase/lyrics/DeleteLyricsUseCase.kt
class DeleteLyricsUseCase @Inject constructor(
	private val repository: LyricsRepository
) {
	suspend operator fun invoke(songId: Long) = repository.deleteLyrics(songId)
}

// domain/useCase/lyrics/UpdateLyricsStatusUseCase.kt
class UpdateLyricsStatusUseCase @Inject constructor(
	private val repository: LyricsRepository
) {
	suspend fun markAsFound(songId: Long, lyrics: String, sourceUrl: String? = null) {
		repository.markAsFound(songId, lyrics, sourceUrl)
	}
	
	suspend fun markAsNotFound(songId: Long) {
		repository.markAsNotFound(songId)
	}
}

// domain/useCase/lyrics/GetPendingLyricsSongsUseCase.kt
class GetPendingLyricsSongsUseCase @Inject constructor(
	private val repository: LyricsRepository
) {
	operator fun invoke(limit: Int = 50) = repository.getSongsPendingLyricsSearch(limit)
}

// domain/useCase/lyrics/GetLyricsStatsUseCase.kt
class GetLyricsStatsUseCase @Inject constructor(
	private val repository: LyricsRepository
) {
	suspend fun countByStatus(status: LyricsStatus): Int =
		repository.countByStatus(status)
}