package com.pmk.freeplayer.feature.metadata.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pmk.freeplayer.feature.metadata.data.local.entity.LyricsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LyricsDao {
	
	@Query("SELECT * FROM lyrics WHERE song_id = :songId")
	fun observe(songId: Long): Flow<LyricsEntity?>
	
	@Query("SELECT * FROM lyrics WHERE song_id = :songId")
	suspend fun getForSong(songId: Long): LyricsEntity?
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun upsert(entity: LyricsEntity)
	
	@Query("DELETE FROM lyrics WHERE song_id = :songId")
	suspend fun deleteForSong(songId: Long)
	
	@Query("SELECT COUNT(*) FROM lyrics")
	suspend fun count(): Int
}