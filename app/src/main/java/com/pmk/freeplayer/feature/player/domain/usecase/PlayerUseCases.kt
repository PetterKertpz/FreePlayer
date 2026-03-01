package com.pmk.freeplayer.feature.player.domain.usecase

import com.pmk.freeplayer.core.common.utils.TrackDuration
import com.pmk.freeplayer.core.domain.model.Song
import com.pmk.freeplayer.core.domain.model.enums.RepeatMode
import com.pmk.freeplayer.feature.player.domain.model.PlaybackState
import com.pmk.freeplayer.feature.player.domain.model.Queue
import com.pmk.freeplayer.feature.player.domain.model.SleepTimer
import com.pmk.freeplayer.feature.player.domain.repository.PlayerRepository
import com.pmk.freeplayer.feature.statistics.domain.model.PlaybackHistory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ═════════════════════════════════════════════════════════════════════════════
// ESTADO DE REPRODUCCIÓN (solo lectura / observación)
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Observa el estado actual del reproductor.
 *
 * Uso en ViewModel:
 * ```kotlin
 * observePlayerUseCase()              // PlaybackState completo
 * observePlayerUseCase.song()
 * observePlayerUseCase.position()
 * observePlayerUseCase.isPlaying()
 * ```
 */
class ObservePlayerUseCase @Inject constructor(
	private val repository: PlayerRepository,
) {
	/** Emite el [PlaybackState] completo. Útil para la UI principal del player. */
	operator fun invoke(): Flow<PlaybackState> = repository.getPlaybackState()
	
	fun song(): Flow<Song?> = repository.getCurrentSong()
	
	fun position(): Flow<TrackDuration> = repository.getCurrentPosition()
	
	fun isPlaying(): Flow<Boolean> = repository.isPlaying()
	
	fun repeatMode(): Flow<RepeatMode> = repository.getRepeatMode()
	
	fun isShuffleEnabled(): Flow<Boolean> = repository.isShuffleEnabled()
}

// ═════════════════════════════════════════════════════════════════════════════
// COLA DE REPRODUCCIÓN
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Consulta y observación de la cola.
 *
 * Uso en ViewModel:
 * ```kotlin
 * observeQueueUseCase()           // Flow<Queue>
 * observeQueueUseCase.index()     // Flow<Int>
 * ```
 */
class ObserveQueueUseCase @Inject constructor(
	private val repository: PlayerRepository,
) {
	operator fun invoke(): Flow<Queue> = repository.getCurrentQueue()
	
	fun index(): Flow<Int> = repository.getCurrentIndex()
	
	fun hasOriginalOrder(): Flow<Boolean> = repository.hasOriginalOrder()
}

/**
 * Mutaciones sobre la cola de reproducción.
 *
 * Uso en ViewModel:
 * ```kotlin
 * manageQueueUseCase.set(songs, initialIndex = 0)
 * manageQueueUseCase.addNext(song)
 * manageQueueUseCase.addToEnd(song)
 * manageQueueUseCase.remove(index)
 * manageQueueUseCase.move(from, to)
 * manageQueueUseCase.shuffle()
 * manageQueueUseCase.goToNext()
 * ```
 */
class ManageQueueUseCase @Inject constructor(
	private val repository: PlayerRepository,
) {
	suspend fun set(songs: List<Song>, initialIndex: Int = 0) =
		repository.setQueue(songs, initialIndex)
	
	suspend fun update(songs: List<Song>, currentIndex: Int? = null) =
		repository.updateQueue(songs, currentIndex)
	
	suspend fun add(songs: List<Song>) = repository.addToQueue(songs)
	
	suspend fun addNext(song: Song) = repository.addNext(song)
	
	suspend fun addToEnd(song: Song) = repository.addToEnd(song)
	
	suspend fun remove(index: Int) = repository.removeFromQueue(index)
	
	suspend fun move(from: Int, to: Int) = repository.moveInQueue(from, to)
	
	suspend fun clear() = repository.clearQueue()
	
	suspend fun goToNext(): Song? = repository.goToNext()
	
	suspend fun goToPrevious(): Song? = repository.goToPrevious()
	
	suspend fun goToIndex(index: Int): Song? = repository.goToIndex(index)
	
	suspend fun shuffle() = repository.shuffleQueue()
	
	suspend fun restoreOrder() = repository.restoreOriginalOrder()
	
	suspend fun setRepeatMode(mode: RepeatMode) = repository.setRepeatMode(mode)
	
	suspend fun setShuffleEnabled(enabled: Boolean) = repository.setShuffleEnabled(enabled)
}

// ═════════════════════════════════════════════════════════════════════════════
// SESIÓN — Persistencia entre arranques
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Guarda y recupera el estado de la sesión de reproducción.
 *
 * Uso en ViewModel / Service:
 * ```kotlin
 * sessionUseCase.save(songId, positionMs, queueIds, currentIndex)
 * sessionUseCase.lastSongId()
 * sessionUseCase.lastQueueIds()
 * ```
 */
class PlayerSessionUseCase @Inject constructor(
	private val repository: PlayerRepository,
) {
	suspend fun save(
		songId: Long?,
		positionMs: Long,
		queueIds: List<Long>,
		currentIndex: Int,
	) = repository.saveSessionState(songId, positionMs, queueIds, currentIndex)
	
	fun lastSongId(): Flow<Long?> = repository.getLastSongId()
	
	fun lastPosition(): Flow<Long> = repository.getLastPosition()
	
	fun lastIndex(): Flow<Int> = repository.getLastIndex()
	
	fun lastQueueIds(): Flow<List<Long>> = repository.getLastQueueIds()
}

// ═════════════════════════════════════════════════════════════════════════════
// HISTORIAL DE REPRODUCCIÓN
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Acceso al historial de escucha.
 *
 * Uso en ViewModel:
 * ```kotlin
 * playbackHistoryUseCase()                              // últimas 100
 * playbackHistoryUseCase.byDateRange(start, end)
 * playbackHistoryUseCase.record(songId, duration, completed)
 * playbackHistoryUseCase.totalListeningTime()
 * ```
 */
class PlaybackHistoryUseCase @Inject constructor(
	private val repository: PlayerRepository,
) {
	operator fun invoke(limit: Int = 100): Flow<List<PlaybackHistory>> =
		repository.getHistory(limit)
	
	fun byDateRange(startDate: Long, endDate: Long): Flow<List<PlaybackHistory>> =
		repository.getHistoryByDateRange(startDate, endDate)
	
	suspend fun record(songId: Long, listenedDuration: Long, completed: Boolean) =
		repository.recordPlayback(songId, listenedDuration, completed)
	
	suspend fun clear() = repository.clearHistory()
	
	suspend fun deleteEntry(id: Long) = repository.deleteHistoryEntry(id)
	
	suspend fun totalListeningTime(): Long = repository.getTotalListeningTime()
	
	suspend fun songsPlayedToday(): Int = repository.getSongsPlayedToday()
}

// ═════════════════════════════════════════════════════════════════════════════
// TEMPORIZADOR DE SUEÑO
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Gestión del Sleep Timer.
 *
 * Uso en ViewModel:
 * ```kotlin
 * sleepTimerUseCase()                  // Flow<SleepTimer?>
 * sleepTimerUseCase.set(timer)
 * sleepTimerUseCase.extend(15)
 * sleepTimerUseCase.cancel()
 * ```
 */
class SleepTimerUseCase @Inject constructor(
	private val repository: PlayerRepository,
) {
	operator fun invoke(): Flow<SleepTimer?> = repository.getSleepTimerState()
	
	suspend fun set(timer: SleepTimer) = repository.setSleepTimer(timer)
	
	suspend fun cancel() = repository.cancelSleepTimer()
	
	suspend fun extend(extraMinutes: Int) = repository.extendSleepTimer(extraMinutes)
}