package com.pmk.freeplayer.feature.player.domain.model

import com.pmk.freeplayer.core.domain.model.Song

data class Queue(val songs: List<Song>, val currentIndex: Int) {
	val currentSong: Song?
		get() = songs.getOrNull(currentIndex)
	
	val hasNext: Boolean
		get() = currentIndex < songs.size - 1
	
	val hasPrevious: Boolean
		get() = currentIndex > 0
	
	val isEmpty: Boolean
		get() = songs.isEmpty()
	
	val totalCount: Int
		get() = songs.size
	
	val remainingSongs: Int
		get() = (songs.size - currentIndex - 1).coerceAtLeast(0)
	
	companion object {
		val EMPTY = Queue(emptyList(), 0)
	}
}