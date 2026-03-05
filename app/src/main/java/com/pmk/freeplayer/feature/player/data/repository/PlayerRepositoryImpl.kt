package com.pmk.freeplayer.feature.player.data.repository

import com.pmk.freeplayer.core.domain.model.enums.RepeatMode
import com.pmk.freeplayer.feature.player.data.player.PlayerController
import com.pmk.freeplayer.feature.player.domain.model.PlayerState
import com.pmk.freeplayer.feature.player.domain.model.QueueItem
import com.pmk.freeplayer.feature.player.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepositoryImpl @Inject constructor(private val controller: PlayerController) :
   PlayerRepository {

   override val playerState: StateFlow<PlayerState> = controller.state

   override fun playQueue(items: List<QueueItem>, startIndex: Int) =
      controller.loadQueue(items, startIndex)

   override fun addToQueue(item: QueueItem) = controller.queueManager.add(item)

   override fun removeFromQueue(index: Int) = controller.queueManager.remove(index)

   override fun moveQueueItem(from: Int, to: Int) = controller.queueManager.move(from, to)

   override fun clearQueue() = controller.queueManager.clear()

   override fun play() = controller.play()

   override fun pause() = controller.pause()

   override fun seekTo(positionMs: Long) = controller.seekTo(positionMs)

   override fun skipToNext() = controller.skipToNext()

   override fun skipToPrevious() = controller.skipToPrevious()

   override fun skipToIndex(index: Int) = controller.skipToIndex(index)

   override fun setRepeatMode(mode: RepeatMode) = controller.setRepeatMode(mode)

   override fun setShuffleEnabled(enabled: Boolean) = controller.setShuffleEnabled(enabled)

   override fun setPlaybackSpeed(speed: Float) = controller.setPlaybackSpeed(speed)

   override fun setEqualizerEnabled(enabled: Boolean) = controller.setEqualizerEnabled(enabled)

   override fun setBassBoost(level: Int) = controller.setBassBoost(level)

   override fun setVirtualizer(level: Int) = controller.setVirtualizer(level)

   override fun setBandLevel(band: Short, levelMilliBel: Short) =
      controller.audioEffectController.setBandLevel(band, levelMilliBel)

   override fun getBandLevels(): List<Pair<Short, Short>> =
      controller.audioEffectController.getBandLevels()

   override fun release() = controller.release()
}
