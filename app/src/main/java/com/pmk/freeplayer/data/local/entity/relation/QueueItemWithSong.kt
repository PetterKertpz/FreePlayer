package com.pmk.freeplayer.data.local.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.pmk.freeplayer.data.local.entity.SongEntity
import com.pmk.freeplayer.data.local.entity.QueueEntity

data class QueueItemWithSong(
	@Embedded val queueItem: QueueEntity,
	
	@Relation(
		parentColumn = "song_id",
		entityColumn = "song_id"
	)
	val song: SongEntity // Room llena esto automáticamente
)