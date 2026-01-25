package com.pmk.freeplayer.data.local.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.pmk.freeplayer.data.local.entity.QueueEntity
import com.pmk.freeplayer.data.local.entity.SongEntity

/**
 * 📦 POJO DE RELACIÓN: Ítem de Cola + Canción
 * Usado por QueueDao y QueueMapper.
 */
data class QueueItemSong(
	@Embedded val queueItem: QueueEntity,
	
	@Relation(
		parentColumn = "song_id",
		entityColumn = "song_id"
	)
	val song: SongEntity
)