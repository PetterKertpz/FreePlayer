package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.PlaybackHistory
import com.pmk.freeplayer.domain.model.Queue
import com.pmk.freeplayer.domain.model.SleepTimer
import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.TrackDuration
import com.pmk.freeplayer.domain.model.enums.RepeatMode
import com.pmk.freeplayer.domain.model.state.PlaybackState
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// PLAYBACK STATE
	// ═══════════════════════════════════════════════════════════════
	
	fun getCurrentSong(): Flow<Song?>
	fun getCurrentPosition(): Flow<TrackDuration>
	fun isPlaying(): Flow<Boolean>
	fun getPlaybackState(): Flow<PlaybackState>
	
	// ═══════════════════════════════════════════════════════════════
	// PLAYBACK CONFIGURATION
	// ═══════════════════════════════════════════════════════════════
	
	fun getRepeatMode(): Flow<RepeatMode>
	suspend fun setRepeatMode(mode: RepeatMode)
	
	fun isShuffleEnabled(): Flow<Boolean>
	suspend fun setShuffleEnabled(enabled: Boolean)
	
	// ═══════════════════════════════════════════════════════════════
	// QUEUE MANAGEMENT
	// ═══════════════════════════════════════════════════════════════
	
	fun getCurrentQueue(): Flow<Queue>
	fun getCurrentIndex(): Flow<Int>
	
	suspend fun setQueue(songs: List<Song>, initialIndex: Int = 0)
	suspend fun updateCurrentIndex(newIndex: Int)
	suspend fun updateQueue(songs: List<Song>, currentIndex: Int? = null)
	
	suspend fun addToQueue(songs: List<Song>)
	suspend fun addToEnd(song: Song)
	suspend fun addNext(song: Song)
	suspend fun removeFromQueue(index: Int)
	suspend fun moveInQueue(from: Int, to: Int)
	suspend fun clearQueue()
	
	// ═══════════════════════════════════════════════════════════════
	// QUEUE NAVIGATION
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun goToNext(): Song?
	suspend fun goToPrevious(): Song?
	suspend fun goToIndex(index: Int): Song?
	
	// ═══════════════════════════════════════════════════════════════
	// SHUFFLE
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun shuffleQueue()
	suspend fun restoreOriginalOrder()
	fun hasOriginalOrder(): Flow<Boolean>
	
	// ═══════════════════════════════════════════════════════════════
	// SESSION PERSISTENCE
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun saveSessionState(
		songId: Long?,
		positionMs: Long,
		queueIds: List<Long>,
		currentIndex: Int
	)
	
	fun getLastSongId(): Flow<Long?>
	fun getLastPosition(): Flow<Long>
	fun getLastIndex(): Flow<Int>
	fun getLastQueueIds(): Flow<List<Long>>
	
	// ═══════════════════════════════════════════════════════════════
	// PLAYBACK HISTORY
	// ═══════════════════════════════════════════════════════════════
	
	fun getHistory(limit: Int = 100): Flow<List<PlaybackHistory>>
	fun getHistoryByDateRange(startDate: Long, endDate: Long): Flow<List<PlaybackHistory>>
	
	suspend fun recordPlayback(songId: Long, listenedDuration: Long, completed: Boolean)
	
	suspend fun clearHistory()
	suspend fun deleteHistoryEntry(id: Long)
	
	suspend fun getTotalListeningTime(): Long
	suspend fun getSongsPlayedToday(): Int
	
	// ═══════════════════════════════════════════════════════════════
	// SLEEP TIMER
	// ═══════════════════════════════════════════════════════════════
	
	fun getSleepTimerState(): Flow<SleepTimer?>
	suspend fun setSleepTimer(timer: SleepTimer)
	suspend fun cancelSleepTimer()
	suspend fun extendSleepTimer(extraMinutes: Int)
}