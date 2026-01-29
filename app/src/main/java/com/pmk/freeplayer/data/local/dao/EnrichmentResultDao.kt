package com.pmk.freeplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.pmk.freeplayer.data.local.entity.EnrichmentResultEntity

@Dao
interface EnrichmentResultDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(result: EnrichmentResultEntity)
}