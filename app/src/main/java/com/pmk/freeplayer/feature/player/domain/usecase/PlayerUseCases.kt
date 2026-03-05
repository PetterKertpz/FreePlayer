package com.pmk.freeplayer.feature.player.domain.usecase

import com.pmk.freeplayer.core.domain.model.enums.RepeatMode
import com.pmk.freeplayer.feature.player.domain.model.PlayerState
import com.pmk.freeplayer.feature.player.domain.model.QueueItem
import com.pmk.freeplayer.feature.player.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObservePlayerStateUseCase @Inject constructor(
	private val repository: PlayerRepository,
) {
	operator fun invoke(): StateFlow<PlayerState> = repository.playerState
}
class PlayQueueUseCase @Inject constructor(
	private val repository: PlayerRepository,
) {
	operator fun invoke(items: List<QueueItem>, startIndex: Int = 0) {
		require(items.isNotEmpty()) { "Queue cannot be empty" }
		require(startIndex in items.indices) { "startIndex out of bounds" }
		repository.playQueue(items, startIndex)
	}
}
class PlayerControlsUseCase @Inject constructor(
	private val repository: PlayerRepository,
) {
	fun play()                           = repository.play()
	fun pause()                          = repository.pause()
	fun seekTo(positionMs: Long)         = repository.seekTo(positionMs)
	fun skipToNext()                     = repository.skipToNext()
	fun skipToPrevious()                 = repository.skipToPrevious()
	fun skipToIndex(index: Int)          = repository.skipToIndex(index)
	fun setRepeatMode(mode: RepeatMode)  = repository.setRepeatMode(mode)
	fun setShuffleEnabled(enabled: Boolean) = repository.setShuffleEnabled(enabled)
	fun setPlaybackSpeed(speed: Float)   = repository.setPlaybackSpeed(speed)
}
class GetCurrentQueueUseCase @Inject constructor(
	private val repository: PlayerRepository,
) {
	operator fun invoke(): Flow<List<QueueItem>> = repository.playerState.map { state ->
		when (state) {
			is PlayerState.Playing -> state.queue
			is PlayerState.Paused  -> state.queue
			else                   -> emptyList()
		}
	}
}