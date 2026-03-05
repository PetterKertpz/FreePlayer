package com.pmk.freeplayer.feature.player.domain.repository

import com.pmk.freeplayer.core.domain.model.enums.RepeatMode
import com.pmk.freeplayer.feature.player.domain.model.PlayerState
import com.pmk.freeplayer.feature.player.domain.model.QueueItem
import kotlinx.coroutines.flow.StateFlow

interface PlayerRepository {
	
	/** Single source of truth for all playback state. */
	val playerState: StateFlow<PlayerState>
	
	// ── Queue ─────────────────────────────────────────────────────
	fun playQueue(items: List<QueueItem>, startIndex: Int = 0)
	fun addToQueue(item: QueueItem)
	fun removeFromQueue(index: Int)
	fun moveQueueItem(from: Int, to: Int)
	fun clearQueue()
	
	// ── Playback controls ─────────────────────────────────────────
	fun play()
	fun pause()
	fun seekTo(positionMs: Long)
	fun skipToNext()
	fun skipToPrevious()
	fun skipToIndex(index: Int)
	
	// ── Mode controls ─────────────────────────────────────────────
	fun setRepeatMode(mode: RepeatMode)
	fun setShuffleEnabled(enabled: Boolean)
	fun setPlaybackSpeed(speed: Float)
	
	// ── Equalizer ─────────────────────────────────────────────────
	fun setEqualizerEnabled(enabled: Boolean)
	fun setBassBoost(level: Int)
	fun setVirtualizer(level: Int)
	
	// ── Lifecycle ─────────────────────────────────────────────────
	fun release()
}