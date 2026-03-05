package com.pmk.freeplayer.feature.player.domain.model

import com.pmk.freeplayer.core.domain.model.enums.RepeatMode

data class PlaybackProgress(
	val positionMs: Long,
	val durationMs: Long,
) {
	val ratio: Float
		get() = if (durationMs > 0) positionMs / durationMs.toFloat() else 0f
}

sealed interface PlayerState {
	
	data object Idle : PlayerState
	
	data class Playing(
		val currentItem: QueueItem,
		val progress: PlaybackProgress,
		val queue: List<QueueItem>,
		val currentIndex: Int,
		val shuffleEnabled: Boolean,
		val repeatMode: RepeatMode,
	) : PlayerState
	
	data class Paused(
		val currentItem: QueueItem,
		val progress: PlaybackProgress,
		val queue: List<QueueItem>,
		val currentIndex: Int,
		val shuffleEnabled: Boolean,
		val repeatMode: RepeatMode,
	) : PlayerState
	
	/**
	 * Emitted when a track finishes naturally or is skipped.
	 * [listenedMs] and [wasSkipped] are consumed by feature/statistics
	 * to build a PlayEvent without touching ExoPlayer directly.
	 */
	data class TrackEnded(
		val item: QueueItem,
		val listenedMs: Long,
		val wasSkipped: Boolean,
	) : PlayerState
	
	data class Error(
		val code: String,
		val message: String,
	) : PlayerState
}

// ── Convenience extensions ────────────────────────────────────────────────────

val PlayerState.currentItem: QueueItem?
	get() = when (this) {
		is PlayerState.Playing  -> currentItem
		is PlayerState.Paused   -> currentItem
		else                    -> null
	}

val PlayerState.isActive: Boolean
	get() = this is PlayerState.Playing || this is PlayerState.Paused