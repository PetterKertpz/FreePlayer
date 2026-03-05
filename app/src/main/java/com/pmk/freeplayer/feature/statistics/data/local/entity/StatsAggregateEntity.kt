package com.pmk.freeplayer.feature.statistics.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import com.pmk.freeplayer.feature.statistics.domain.model.EntityType

/**
 * Materialized aggregate — updated atomically inside the same @Transaction
 * that inserts a PlayEventEntity. This is the read path for all counts.
 *
 * PK is composite (entity_type + entity_id) to support all entity types
 * in a single table without type-specific tables.
 */
@Entity(
	tableName = "stats_aggregates",
	primaryKeys = ["entity_type", "entity_id"],
	indices = [
		Index(value = ["play_count"]),      // ORDER BY play_count DESC
		Index(value = ["last_played_at"]),  // ORDER BY recently played
	]
)
data class StatsAggregateEntity(
	@ColumnInfo(name = "entity_type")       val entityType: EntityType,
	@ColumnInfo(name = "entity_id")         val entityId: Long,
	
	@ColumnInfo(name = "play_count")        val playCount: Long       = 0,
	@ColumnInfo(name = "total_listened_ms") val totalListenedMs: Long = 0,
	@ColumnInfo(name = "skip_count")        val skipCount: Long       = 0,
	@ColumnInfo(name = "last_played_at")    val lastPlayedAt: Long?   = null,
	@ColumnInfo(name = "first_played_at")   val firstPlayedAt: Long?  = null,
)