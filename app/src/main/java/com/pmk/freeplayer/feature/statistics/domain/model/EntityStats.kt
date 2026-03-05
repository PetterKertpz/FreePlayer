package com.pmk.freeplayer.feature.statistics.domain.model

/**
 * Materialized aggregate for any entity type.
 * Read from StatsAggregateEntity — O(1) lookup, never recomputed from raw events.
 */
data class EntityStats(
	val entityId: Long,
	val entityType: EntityType,
	val playCount: Long,
	val totalListenedMs: Long,
	val skipCount: Long,
	val lastPlayedAt: Long?,
	val firstPlayedAt: Long?,
) {
	val avgCompletionMs: Long
		get() = if (playCount > 0) totalListenedMs / playCount else 0L
	
	val skipRate: Float
		get() = if (playCount > 0) skipCount.toFloat() / playCount else 0f
}

/** Lightweight projection for ranking lists — avoids loading full EntityStats. */
data class EntityRank(
	val entityId: Long,
	val entityType: EntityType,
	val playCount: Long,
	val lastPlayedAt: Long?,
)