package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Queue
import com.pmk.freeplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface QueueRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Active queue management (In Memory)
	// ─────────────────────────────────────────────────────────────
	fun getCurrentQueue(): Flow<Queue>
	
	suspend fun setQueue(songs: List<Song>, initialIndex: Int = 0)
	
	suspend fun updateCurrentIndex(newIndex: Int)
	
	suspend fun updateQueue(songs: List<Song>, currentIndex: Int? = null)
	
	suspend fun addToQueue(songs: List<Song>)
	
	suspend fun addToEnd(song: Song)
	
	suspend fun addNext(song: Song) // "Play Next"
	
	suspend fun removeFromQueue(index: Int)
	
	suspend fun moveInQueue(from: Int, to: Int)
	
	suspend fun clearQueue()
	
	// ─────────────────────────────────────────────────────────────
	// Queue navigation
	// ─────────────────────────────────────────────────────────────
	suspend fun goToNext(): Song?
	
	suspend fun goToPrevious(): Song?
	
	suspend fun goToIndex(index: Int): Song?
	
	fun getCurrentIndex(): Flow<Int>
	
	// ─────────────────────────────────────────────────────────────
	// Shuffle
	// ─────────────────────────────────────────────────────────────
	suspend fun shuffleQueue()
	
	suspend fun restoreOriginalOrder()
	
	fun hasOriginalOrder(): Flow<Boolean>
}