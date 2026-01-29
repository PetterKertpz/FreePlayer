
package com.pmk.freeplayer.domain.useCase

import com.pmk.freeplayer.domain.model.SleepTimer
import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.enums.RepeatMode
import com.pmk.freeplayer.domain.model.scanner.EnrichmentResult
import com.pmk.freeplayer.domain.model.state.PlaybackState
import com.pmk.freeplayer.domain.repository.EnrichmentRepository
import com.pmk.freeplayer.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlaybackStateUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	operator fun invoke(): Flow<PlaybackState> = repository.getPlaybackState()
}

// domain/useCase/player/playback/GetCurrentSongUseCase.kt
class GetCurrentSongUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	operator fun invoke() = repository.getCurrentSong()
}

// domain/useCase/player/playback/ObservePlaybackUseCase.kt
class ObservePlaybackUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	fun isPlaying() = repository.isPlaying()
	fun currentPosition() = repository.getCurrentPosition()
}

// ═══════════════════════════════════════════════════════════════
// QUEUE
// ═══════════════════════════════════════════════════════════════


class GetCurrentQueueUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	operator fun invoke() = repository.getCurrentQueue()
	fun currentIndex() = repository.getCurrentIndex()
}

// domain/useCase/player/queue/SetQueueUseCase.kt
class SetQueueUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend operator fun invoke(songs: List<Song>, startIndex: Int = 0) {
		repository.setQueue(songs, startIndex)
	}
}

// domain/useCase/player/queue/AddToQueueUseCase.kt
class AddToQueueUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend fun addNext(song: Song) = repository.addNext(song)
	suspend fun addToEnd(song: Song) = repository.addToEnd(song)
	suspend fun addMultiple(songs: List<Song>) = repository.addToQueue(songs)
}

// domain/useCase/player/queue/RemoveFromQueueUseCase.kt
class RemoveFromQueueUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend operator fun invoke(index: Int) = repository.removeFromQueue(index)
}

// domain/useCase/player/queue/MoveInQueueUseCase.kt
class MoveInQueueUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend operator fun invoke(from: Int, to: Int) = repository.moveInQueue(from, to)
}

// domain/useCase/player/queue/ClearQueueUseCase.kt
class ClearQueueUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend operator fun invoke() = repository.clearQueue()
}

// domain/useCase/player/queue/ShuffleQueueUseCase.kt
class ShuffleQueueUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend fun shuffle() = repository.shuffleQueue()
	suspend fun restore() = repository.restoreOriginalOrder()
	fun hasOriginalOrder() = repository.hasOriginalOrder()
}

// ═══════════════════════════════════════════════════════════════
// NAVIGATION
// ═══════════════════════════════════════════════════════════════


class GoToNextSongUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend operator fun invoke(): Song? = repository.goToNext()
}

// domain/useCase/player/navigation/GoToPreviousSongUseCase.kt
class GoToPreviousSongUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend operator fun invoke(): Song? = repository.goToPrevious()
}

// domain/useCase/player/navigation/GoToIndexUseCase.kt
class GoToIndexUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend operator fun invoke(index: Int): Song? = repository.goToIndex(index)
}

// ═══════════════════════════════════════════════════════════════
// CONFIG
// ═══════════════════════════════════════════════════════════════


class SetRepeatModeUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend operator fun invoke(mode: RepeatMode) = repository.setRepeatMode(mode)
	fun observe() = repository.getRepeatMode()
}

// domain/useCase/player/config/SetShuffleEnabledUseCase.kt
class SetShuffleEnabledUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend operator fun invoke(enabled: Boolean) = repository.setShuffleEnabled(enabled)
	fun observe() = repository.isShuffleEnabled()
}

// ═══════════════════════════════════════════════════════════════
// SESSION
// ═══════════════════════════════════════════════════════════════


data class SessionState(
	val songId: Long?,
	val positionMs: Long,
	val queueIds: List<Long>,
	val currentIndex: Int
)

class SaveSessionStateUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend operator fun invoke(state: SessionState) {
		repository.saveSessionState(
			songId = state.songId,
			positionMs = state.positionMs,
			queueIds = state.queueIds,
			currentIndex = state.currentIndex
		)
	}
}

// domain/useCase/player/session/RestoreSessionStateUseCase.kt
data class RestoredSession(
	val songId: Long?,
	val position: Long,
	val queueIds: List<Long>,
	val index: Int
)

class RestoreSessionStateUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	fun lastSongId() = repository.getLastSongId()
	fun lastPosition() = repository.getLastPosition()
	fun lastQueueIds() = repository.getLastQueueIds()
	fun lastIndex() = repository.getLastIndex()
}

// ═══════════════════════════════════════════════════════════════
// HISTORY
// ═══════════════════════════════════════════════════════════════


class GetPlaybackHistoryUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	operator fun invoke(limit: Int = 100) = repository.getHistory(limit)
	
	fun byDateRange(startDate: Long, endDate: Long) =
		repository.getHistoryByDateRange(startDate, endDate)
}

// domain/useCase/player/history/RecordPlaybackUseCase.kt
data class PlaybackRecord(
	val songId: Long,
	val listenedDuration: Long,
	val completed: Boolean
)

class RecordPlaybackUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend operator fun invoke(record: PlaybackRecord) {
		repository.recordPlayback(
			songId = record.songId,
			listenedDuration = record.listenedDuration,
			completed = record.completed
		)
	}
}

// domain/useCase/player/history/GetListeningStatsUseCase.kt
data class ListeningStats(
	val totalListeningTimeMs: Long,
	val songsPlayedToday: Int
)

class GetListeningStatsUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend operator fun invoke(): ListeningStats {
		return ListeningStats(
			totalListeningTimeMs = repository.getTotalListeningTime(),
			songsPlayedToday = repository.getSongsPlayedToday()
		)
	}
}

// domain/useCase/player/history/ClearHistoryUseCase.kt
class ClearHistoryUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend operator fun invoke() = repository.clearHistory()
	suspend fun deleteEntry(id: Long) = repository.deleteHistoryEntry(id)
}

// ═══════════════════════════════════════════════════════════════
// SLEEP TIMER
// ═══════════════════════════════════════════════════════════════


class SetSleepTimerUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend operator fun invoke(timer: SleepTimer) = repository.setSleepTimer(timer)
	fun observe() = repository.getSleepTimerState()
}

// domain/useCase/player/timer/CancelSleepTimerUseCase.kt
class CancelSleepTimerUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend operator fun invoke() = repository.cancelSleepTimer()
}

// domain/useCase/player/timer/ExtendSleepTimerUseCase.kt
class ExtendSleepTimerUseCase @Inject constructor(
	private val repository: PlayerRepository
) {
	suspend operator fun invoke(extraMinutes: Int) =
		repository.extendSleepTimer(extraMinutes)
}

//ENRICH
class EnrichSongUseCase @Inject constructor(
	private val repository: EnrichmentRepository
) {
	// Usamos operator invoke para llamarlo como función
	suspend operator fun invoke(song: Song): EnrichmentResult {
		return repository.enrichSong(song)
	}
}