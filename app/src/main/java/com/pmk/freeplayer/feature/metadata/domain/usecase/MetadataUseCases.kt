package com.pmk.freeplayer.feature.metadata.domain.usecase

import com.pmk.freeplayer.feature.metadata.domain.model.CleanedSongData
import com.pmk.freeplayer.feature.metadata.domain.model.GeniusSongResult
import com.pmk.freeplayer.feature.metadata.domain.model.LyricsData
import com.pmk.freeplayer.feature.metadata.domain.repository.LyricsRepository
import com.pmk.freeplayer.feature.metadata.domain.repository.MetadataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProcessSongOnPlaybackUseCase @Inject constructor(
	private val metadataRepository: MetadataRepository,
) {
	suspend operator fun invoke(songId: Long): Result<Unit> =
		metadataRepository.processSongOnPlayback(songId)
}
class CleanSongMetadataUseCase @Inject constructor(
	private val metadataRepository: MetadataRepository,
) {
	suspend operator fun invoke(songId: Long): Result<CleanedSongData> =
		metadataRepository.cleanSongMetadata(songId)
}

class EnrichSongFromGeniusUseCase @Inject constructor(
	private val metadataRepository: MetadataRepository,
) {
	suspend operator fun invoke(songId: Long): Result<GeniusSongResult> =
		metadataRepository.enrichSongFromGenius(songId)
}
class GetLyricsUseCase @Inject constructor(
	private val lyricsRepository: LyricsRepository,
) {
	fun observe(songId: Long): Flow<LyricsData?> =
		lyricsRepository.observeLyrics(songId)
	
	suspend operator fun invoke(songId: Long): Result<LyricsData?> =
		lyricsRepository.getLyrics(songId)
}
class SkipSongMetadataUseCase @Inject constructor(
	private val metadataRepository: MetadataRepository,
) {
	suspend operator fun invoke(songId: Long): Result<Unit> =
		metadataRepository.skipSong(songId)
}
class ResetSongMetadataUseCase @Inject constructor(
	private val metadataRepository: MetadataRepository,
) {
	suspend operator fun invoke(songId: Long): Result<Unit> =
		metadataRepository.resetSong(songId)
}