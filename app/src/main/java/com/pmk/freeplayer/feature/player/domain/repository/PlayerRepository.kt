package com.pmk.freeplayer.feature.player.domain.repository

import com.pmk.freeplayer.core.common.utils.TrackDuration
import com.pmk.freeplayer.core.domain.model.Song
import com.pmk.freeplayer.core.domain.model.enums.RepeatMode
import com.pmk.freeplayer.feature.player.domain.model.PlaybackState
import com.pmk.freeplayer.feature.player.domain.model.Queue
import com.pmk.freeplayer.feature.player.domain.model.SleepTimer
import com.pmk.freeplayer.feature.statistics.domain.model.PlaybackHistory
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio raíz del agregado "Reproductor".
 *
 * Gestiona: estado de reproducción, configuración, cola, sesión,
 * historial de escucha y temporizador de sueño.
 */
interface PlayerRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// ESTADO DE REPRODUCCIÓN
	// ═══════════════════════════════════════════════════════════════
	
	fun getCurrentSong(): Flow<Song?>
	fun getCurrentPosition(): Flow<TrackDuration>
	fun isPlaying(): Flow<Boolean>
	fun getPlaybackState(): Flow<PlaybackState>
	
	// ═══════════════════════════════════════════════════════════════
	// CONFIGURACIÓN DE REPRODUCCIÓN
	// ═══════════════════════════════════════════════════════════════
	
	fun getRepeatMode(): Flow<RepeatMode>
	suspend fun setRepeatMode(mode: RepeatMode)
	
	fun isShuffleEnabled(): Flow<Boolean>
	suspend fun setShuffleEnabled(enabled: Boolean)
	
	// ═══════════════════════════════════════════════════════════════
	// COLA DE REPRODUCCIÓN
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
	// NAVEGACIÓN EN LA COLA
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun goToNext(): Song?
	suspend fun goToPrevious(): Song?
	suspend fun goToIndex(index: Int): Song?
	
	// ═══════════════════════════════════════════════════════════════
	// ALEATORIEDAD
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun shuffleQueue()
	suspend fun restoreOriginalOrder()
	fun hasOriginalOrder(): Flow<Boolean>
	
	// ═══════════════════════════════════════════════════════════════
	// PERSISTENCIA DE SESIÓN
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun saveSessionState(
		songId: Long?,
		positionMs: Long,
		queueIds: List<Long>,
		currentIndex: Int,
	)
	
	fun getLastSongId(): Flow<Long?>
	fun getLastPosition(): Flow<Long>
	fun getLastIndex(): Flow<Int>
	fun getLastQueueIds(): Flow<List<Long>>
	
	// ═══════════════════════════════════════════════════════════════
	// HISTORIAL DE REPRODUCCIÓN
	// ═══════════════════════════════════════════════════════════════
	
	fun getHistory(limit: Int = 100): Flow<List<PlaybackHistory>>
	fun getHistoryByDateRange(startDate: Long, endDate: Long): Flow<List<PlaybackHistory>>
	
	suspend fun recordPlayback(songId: Long, listenedDuration: Long, completed: Boolean)
	
	suspend fun clearHistory()
	suspend fun deleteHistoryEntry(id: Long)
	
	suspend fun getTotalListeningTime(): Long
	suspend fun getSongsPlayedToday(): Int
	
	// ═══════════════════════════════════════════════════════════════
	// TEMPORIZADOR DE SUEÑO
	// ═══════════════════════════════════════════════════════════════
	
	fun getSleepTimerState(): Flow<SleepTimer?>
	suspend fun setSleepTimer(timer: SleepTimer)
	suspend fun cancelSleepTimer()
	suspend fun extendSleepTimer(extraMinutes: Int)
}