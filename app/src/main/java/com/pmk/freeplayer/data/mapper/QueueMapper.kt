package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.relation.QueueItemSong
import com.pmk.freeplayer.domain.model.Queue
import com.pmk.freeplayer.domain.model.Song

/**
 * 🔄 QUEUE MAPPER
 * Construye el estado de la cola (Queue) a partir de la BD y el estado del player.
 */

// ==================== LISTA DB -> DOMAIN OBJECT ====================

/**
 * Convierte la lista cruda de la base de datos en el objeto de dominio inteligente.
 * * @param currentSongId El ID de la canción que se está reproduciendo actualmente
 * (generalmente viene de DataStore o ExoPlayer).
 */
fun List<QueueItemSong>.toDomain(currentSongId: Long): Queue {
	if (this.isEmpty()) return Queue.VACIA
	
	// 1. Convertimos cada item de la DB a una canción de Dominio
	val domainSongs = this.map { it.toDomain() }
	
	// 2. Buscamos en qué posición está la canción actual.
	// Si no la encontramos (ej: cola nueva o canción borrada), empezamos en 0.
	val index = domainSongs.indexOfFirst { it.id == currentSongId }.coerceAtLeast(0)
	
	// 3. Retornamos el objeto de estado listo para la UI
	return Queue(
		canciones = domainSongs,
		indiceActual = index
	)
}

// ==================== HELPER: RELACIÓN -> SONG ====================

/**
 * Extrae la canción del item de la cola.
 */
fun QueueItemSong.toDomain(): Song {
	// QueueItemSong contiene la Entity. Usamos el mapper de SongEntity.
	// Nota: Si QueueItemSong tuviera un JOIN con artista, usaríamos ese nombre.
	// Por ahora, asumimos "Unknown" o lo que venga en la SongEntity.
	return this.song.toDomain(artistName = "Unknown Artist")
}