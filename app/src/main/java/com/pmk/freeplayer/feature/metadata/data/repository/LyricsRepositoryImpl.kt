package com.pmk.freeplayer.feature.metadata.data.repository

import com.pmk.freeplayer.app.di.IoDispatcher
import com.pmk.freeplayer.feature.metadata.data.local.dao.LyricsDao
import com.pmk.freeplayer.feature.metadata.data.mapper.toDomain
import com.pmk.freeplayer.feature.metadata.data.mapper.toEntity
import com.pmk.freeplayer.feature.metadata.domain.model.LyricsData
import com.pmk.freeplayer.feature.metadata.domain.repository.LyricsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

// feature/metadata/data/repository/LyricsRepositoryImpl.kt

class LyricsRepositoryImpl @Inject constructor(
	private val lyricsDao: LyricsDao,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : LyricsRepository {
	
	override fun observeLyrics(songId: Long): Flow<LyricsData?> =
		lyricsDao.observe(songId).map { it?.toDomain() }
	
	override suspend fun getLyrics(songId: Long): Result<LyricsData?> =
		withContext(ioDispatcher) {
			runCatching { lyricsDao.getForSong(songId)?.toDomain() }
		}
	
	override suspend fun saveLyrics(data: LyricsData): Result<Unit> =
		withContext(ioDispatcher) {
			runCatching { lyricsDao.upsert(data.toEntity()) }
		}
	
	override suspend fun deleteLyrics(songId: Long): Result<Unit> =
		withContext(ioDispatcher) {
			runCatching { lyricsDao.deleteForSong(songId) }
		}
}