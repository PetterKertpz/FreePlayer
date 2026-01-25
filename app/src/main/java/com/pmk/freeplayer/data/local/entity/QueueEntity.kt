package com.pmk.freeplayer.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "queue", // Nombre corto y claro
	foreignKeys = [
		ForeignKey(
			entity = SongEntity::class,
			parentColumns = ["song_id"],
			childColumns = ["song_id"],
			onDelete = ForeignKey.CASCADE // Si borras la canción, desaparece de la cola
		)
	],
	indices = [
		Index(value = ["sort_order"]), // ⚡ CLAVE PARA LA VELOCIDAD: Ordenar rápido
		Index(value = ["song_id"])
	]
)
data class QueueEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	@ColumnInfo(name = "song_id") val songId: Long,
	
	// El único dato que importa: ¿En qué posición va? (0, 1, 2...)
	@ColumnInfo(name = "sort_order") val sortOrder: Int
)