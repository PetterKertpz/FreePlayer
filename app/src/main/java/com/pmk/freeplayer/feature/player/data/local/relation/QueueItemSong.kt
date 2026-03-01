package com.pmk.freeplayer.feature.player.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.pmk.freeplayer.feature.player.data.local.entity.QueueEntity
import com.pmk.freeplayer.feature.songs.data.local.entity.SongEntity

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