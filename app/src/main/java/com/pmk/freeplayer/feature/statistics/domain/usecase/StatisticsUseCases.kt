package com.pmk.freeplayer.feature.statistics.domain.usecase

import com.pmk.freeplayer.feature.statistics.domain.model.EntityRank
import com.pmk.freeplayer.feature.statistics.domain.model.EntityStats
import com.pmk.freeplayer.feature.statistics.domain.model.EntityType
import com.pmk.freeplayer.feature.statistics.domain.model.PlayEvent
import com.pmk.freeplayer.feature.statistics.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecordPlayUseCase @Inject constructor(private val repository: StatisticsRepository) {
	suspend operator fun invoke(event: PlayEvent) = repository.recordPlay(event)
}

class GetEntityStatsUseCase @Inject constructor(private val repository: StatisticsRepository) {
	operator fun invoke(type: EntityType, id: Long): Flow<EntityStats?> =
		repository.getStats(type, id)
}

class GetMostPlayedUseCase @Inject constructor(private val repository: StatisticsRepository) {
	operator fun invoke(type: EntityType, limit: Int = 20): Flow<List<EntityRank>> =
		repository.getMostPlayed(type, limit)
}

class GetRecentlyPlayedUseCase @Inject constructor(private val repository: StatisticsRepository) {
	operator fun invoke(type: EntityType, limit: Int = 20): Flow<List<EntityRank>> =
		repository.getRecentlyPlayed(type, limit)
}

class GetListeningTimeUseCase @Inject constructor(private val repository: StatisticsRepository) {
	operator fun invoke(sinceTimestamp: Long): Flow<Long> =
		repository.getTotalListenedMs(sinceTimestamp)
}

class PruneOldEventsUseCase @Inject constructor(private val repository: StatisticsRepository) {
	suspend operator fun invoke(beforeTimestamp: Long) =
		repository.pruneOldEvents(beforeTimestamp)
}