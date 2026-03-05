package com.pmk.freeplayer.feature.statistics.domain.repository

import com.pmk.freeplayer.feature.statistics.domain.model.EntityRank
import com.pmk.freeplayer.feature.statistics.domain.model.EntityStats
import com.pmk.freeplayer.feature.statistics.domain.model.EntityType
import com.pmk.freeplayer.feature.statistics.domain.model.PlayEvent
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
	
	/**
	 * Records a play and atomically updates all affected aggregates.
	 * This is the ONLY write entry-point for the entire statistics feature.
	 */
	suspend fun recordPlay(event: PlayEvent)
	
	// ── Aggregate reads ───────────────────────────────────────────
	
	fun getStats(type: EntityType, id: Long): Flow<EntityStats?>
	suspend fun getStatsOnce(type: EntityType, id: Long): EntityStats?
	
	// ── Rankings ──────────────────────────────────────────────────
	
	fun getMostPlayed(type: EntityType, limit: Int = 20): Flow<List<EntityRank>>
	fun getRecentlyPlayed(type: EntityType, limit: Int = 20): Flow<List<EntityRank>>
	
	// ── Global listening metrics ──────────────────────────────────
	
	/** Total ms listened since [sinceTimestamp]. */
	fun getTotalListenedMs(sinceTimestamp: Long): Flow<Long>
	
	/** Total play events since [sinceTimestamp]. */
	fun getPlayCount(sinceTimestamp: Long): Flow<Int>
	
	// ── Maintenance ───────────────────────────────────────────────
	
	suspend fun pruneOldEvents(beforeTimestamp: Long)
	suspend fun recomputeAllAggregates()
}