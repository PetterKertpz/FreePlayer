package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.QueueEntity
import com.pmk.freeplayer.data.local.entity.relation.QueueItemSong
import com.pmk.freeplayer.domain.model.Queue
import com.pmk.freeplayer.domain.model.Song

/**
 * 🔄 QUEUE MAPPER
 *
 * Construye el estado de la cola (Queue) a partir de la base de datos
 * y el estado actual del reproductor.
 */

// ==================== RELATION -> DOMAIN ====================

/**
 * Convierte la lista de items de la cola (con sus canciones relacionadas)
 * en el objeto Queue del dominio.
 *
 * @param currentSongId ID de la canción que se está reproduciendo actualmente
 *                      (generalmente viene de DataStore o del estado del reproductor)
 * @return Queue con la lista de canciones y el índice actual, o Queue.VACIA si está vacía
 */
fun List<QueueItemSong>.toDomain(currentSongId: Long): Queue {
	if (this.isEmpty()) return Queue.VACIA
	
	// 1. Convertir cada item de la BD a una canción de dominio
	val domainSongs = this.map { it.toDomain() }
	
	// 2. Buscar la posición de la canción actual
	// Si no se encuentra (ej: canción eliminada), empezar desde 0
	val currentIndex = domainSongs
		.indexOfFirst { it.id == currentSongId }
		.coerceAtLeast(0)
	
	// 3. Retornar el objeto de estado listo para la UI
	return Queue(
		canciones = domainSongs,
		indiceActual = currentIndex
	)
}

/**
 * Sobrecarga para cuando no hay una canción actual definida.
 * Inicia la cola en el primer elemento (índice 0).
 */
fun List<QueueItemSong>.toDomain(): Queue {
	if (this.isEmpty()) return Queue.VACIA
	
	val domainSongs = this.map { it.toDomain() }
	return Queue(
		canciones = domainSongs,
		indiceActual = 0
	)
}

/**
 * Extrae la canción del QueueItemSong.
 * Utiliza el mapper de SongEntity con nombre de artista por defecto.
 */
fun QueueItemSong.toDomain(): Song {
	// Si tienes un JOIN con Artist, aquí podrías usar:
	// return this.song.toDomain(artistName = this.artist?.name ?: "Unknown Artist")
	return this.song.toDomain(artistName = "Unknown Artist")
}

// ==================== DOMAIN -> ENTITY ====================

/**
 * Convierte una lista de Song a una lista de QueueEntity.
 * Asigna automáticamente el orden (sortOrder) basado en la posición en la lista.
 *
 * @return Lista de QueueEntity ordenada y lista para insertar en la BD
 */
fun List<Song>.toQueueEntities(): List<QueueEntity> {
	return this.mapIndexed { index, song ->
		QueueEntity(
			id = 0, // Autogenerado
			songId = song.id,
			sortOrder = index
		)
	}
}

/**
 * Convierte un Song individual a QueueEntity con un orden específico.
 *
 * @param sortOrder Posición en la cola (0, 1, 2, ...)
 */
fun Song.toQueueEntity(sortOrder: Int): QueueEntity {
	return QueueEntity(
		id = 0, // Autogenerado
		songId = this.id,
		sortOrder = sortOrder
	)
}

/**
 * Convierte el objeto Queue completo a una lista de QueueEntity.
 * Preserva el orden actual de las canciones.
 */
fun Queue.toEntities(): List<QueueEntity> {
	return this.canciones.toQueueEntities()
}

// ==================== HELPERS DE MANIPULACIÓN ====================

/**
 * Reordena una lista de QueueEntity existente según nuevas posiciones.
 * Útil cuando el usuario reordena manualmente la cola.
 *
 * @param newOrder Mapa de [queueId -> nuevaPosición]
 * @return Lista actualizada con nuevos sortOrder
 */
fun List<QueueEntity>.reorder(newOrder: Map<Long, Int>): List<QueueEntity> {
	return this.map { entity ->
		val newSortOrder = newOrder[entity.id] ?: entity.sortOrder
		entity.copy(sortOrder = newSortOrder)
	}.sortedBy { it.sortOrder }
}

/**
 * Inserta una nueva canción en una posición específica de la cola.
 * Ajusta el sortOrder de las canciones siguientes.
 *
 * @param songId ID de la canción a insertar
 * @param position Posición donde insertar (0 = principio)
 * @return Lista actualizada con la nueva canción insertada
 */
fun List<QueueEntity>.insertAt(songId: Long, position: Int): List<QueueEntity> {
	val adjustedPosition = position.coerceIn(0, this.size)
	
	// Crear la nueva entrada
	val newEntity = QueueEntity(
		id = 0,
		songId = songId,
		sortOrder = adjustedPosition
	)
	
	// Reordenar las existentes
	val reordered = this.map { entity ->
		if (entity.sortOrder >= adjustedPosition) {
			entity.copy(sortOrder = entity.sortOrder + 1)
		} else {
			entity
		}
	}
	
	return (reordered + newEntity).sortedBy { it.sortOrder }
}

/**
 * Agrega canciones al final de la cola.
 *
 * @param songIds Lista de IDs de canciones a agregar
 * @return Lista actualizada con las nuevas canciones al final
 */
fun List<QueueEntity>.append(songIds: List<Long>): List<QueueEntity> {
	val startingSortOrder = this.maxOfOrNull { it.sortOrder }?.plus(1) ?: 0
	
	val newEntities = songIds.mapIndexed { index, songId ->
		QueueEntity(
			id = 0,
			songId = songId,
			sortOrder = startingSortOrder + index
		)
	}
	
	return this + newEntities
}

/**
 * Elimina una canción de la cola por su ID.
 * Reajusta el sortOrder de las canciones siguientes.
 *
 * @param queueId ID del registro de cola a eliminar
 * @return Lista actualizada sin la canción eliminada
 */
fun List<QueueEntity>.removeById(queueId: Long): List<QueueEntity> {
	val entityToRemove = this.firstOrNull { it.id == queueId } ?: return this
	val removedSortOrder = entityToRemove.sortOrder
	
	return this
		.filter { it.id != queueId }
		.map { entity ->
			if (entity.sortOrder > removedSortOrder) {
				entity.copy(sortOrder = entity.sortOrder - 1)
			} else {
				entity
			}
		}
}

/**
 * Limpia la cola manteniendo solo las canciones después de una posición.
 * Útil para "Limpiar cola hasta aquí".
 *
 * @param fromPosition Posición desde donde mantener (inclusive)
 * @return Lista filtrada y reordenada
 */
fun List<QueueEntity>.clearBefore(fromPosition: Int): List<QueueEntity> {
	return this
		.filter { it.sortOrder >= fromPosition }
		.mapIndexed { index, entity ->
			entity.copy(sortOrder = index)
		}
}

/**
 * Limpia la cola manteniendo solo las canciones antes de una posición.
 * Útil para "Limpiar cola desde aquí".
 *
 * @param toPosition Posición hasta donde mantener (exclusive)
 * @return Lista filtrada
 */
fun List<QueueEntity>.clearAfter(toPosition: Int): List<QueueEntity> {
	return this.filter { it.sortOrder < toPosition }
}

/**
 * Mezcla aleatoriamente la cola manteniendo la canción actual en su posición.
 *
 * @param currentSortOrder Posición de la canción que no debe moverse
 * @return Lista mezclada con sortOrder actualizado
 */
fun List<QueueEntity>.shuffle(currentSortOrder: Int): List<QueueEntity> {
	val current = this.firstOrNull { it.sortOrder == currentSortOrder }
	val others = this.filter { it.sortOrder != currentSortOrder }.shuffled()
	
	return (listOfNotNull(current) + others).mapIndexed { index, entity ->
		entity.copy(sortOrder = index)
	}
}

// ==================== EXTENSION FUNCTIONS PARA QUEUE ====================

/**
 * Obtiene los IDs de todas las canciones en la cola.
 */
fun Queue.getSongIds(): List<Long> = canciones.map { it.id }

/**
 * Obtiene el ID de la canción actual.
 */
fun Queue.getCurrentSongId(): Long? = songActual?.id

/**
 * Verifica si una canción específica está en la cola.
 */
fun Queue.contains(songId: Long): Boolean = canciones.any { it.id == songId }

/**
 * Obtiene la posición de una canción en la cola.
 * @return Índice de la canción, o -1 si no está en la cola
 */
fun Queue.indexOf(songId: Long): Int = canciones.indexOfFirst { it.id == songId }