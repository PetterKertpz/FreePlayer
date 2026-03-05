package com.pmk.freeplayer.feature.metadata.domain.repository

import com.pmk.freeplayer.feature.metadata.domain.model.LyricsData
import kotlinx.coroutines.flow.Flow

// feature/metadata/domain/repository/LyricsRepository.kt

interface LyricsRepository {
	
	/** Flow reactivo — el Player observa esto en tiempo real. */
	fun observeLyrics(songId: Long): Flow<LyricsData?>
	
	/** Fetch puntual — usado por la UI de detalle de canción. */
	suspend fun getLyrics(songId: Long): Result<LyricsData?>
	
	suspend fun saveLyrics(data: LyricsData): Result<Unit>
	
	suspend fun deleteLyrics(songId: Long): Result<Unit>
}