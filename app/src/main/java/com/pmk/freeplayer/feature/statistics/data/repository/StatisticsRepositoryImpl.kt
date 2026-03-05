package com.pmk.freeplayer.feature.statistics.data.repository

import com.pmk.freeplayer.feature.statistics.data.local.dao.StatisticsDao
import com.pmk.freeplayer.feature.statistics.data.mapper.toDomain
import com.pmk.freeplayer.feature.statistics.data.mapper.toEntity
import com.pmk.freeplayer.feature.statistics.domain.model.EntityRank
import com.pmk.freeplayer.feature.statistics.domain.model.EntityStats
import com.pmk.freeplayer.feature.statistics.domain.model.EntityType
import com.pmk.freeplayer.feature.statistics.domain.model.PlayEvent
import com.pmk.freeplayer.feature.statistics.domain.repository.StatisticsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class StatisticsRepositoryImpl @Inject constructor(
	private val statisticsDao: StatisticsDao,
	@Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : StatisticsRepository {
	
	override suspend fun recordPlay(event: PlayEvent) = withContext(ioDispatcher) {
		statisticsDao.recordPlay(event.toEntity())
	}
	
	override fun getStats(type: EntityType, id: Long): Flow<EntityStats?> =
		statisticsDao.getStats(type, id).map { it?.toDomain() }
	
	override suspend fun getStatsOnce(type: EntityType, id: Long): EntityStats? =
		withContext(ioDispatcher) { statisticsDao.getStatsOnce(type, id)?.toDomain() }
	
	override fun getMostPlayed(type: EntityType, limit: Int): Flow<List<EntityRank>> =
		statisticsDao.getTopByPlayCount(type, limit)
	
	override fun getRecentlyPlayed(type: EntityType, limit: Int): Flow<List<EntityRank>> =
		statisticsDao.getRecentlyPlayed(type, limit)
	
	override fun getTotalListenedMs(sinceTimestamp: Long): Flow<Long> =
		statisticsDao.getTotalListenedMsSince(sinceTimestamp)
	
	override fun getPlayCount(sinceTimestamp: Long): Flow<Int> =
		statisticsDao.getPlayCountSince(sinceTimestamp)
	
	override suspend fun pruneOldEvents(beforeTimestamp: Long) = withContext(ioDispatcher) {
		statisticsDao.pruneOldEvents(beforeTimestamp)
	}
	
	override suspend fun recomputeAllAggregates() = withContext(ioDispatcher) {
		statisticsDao.recomputeSongAggregates()
		// repeat for ARTIST, ALBUM, GENRE, PLAYLIST with equivalent queries
	}
}