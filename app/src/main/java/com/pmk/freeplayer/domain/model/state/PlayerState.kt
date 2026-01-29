package com.pmk.freeplayer.domain.model.state

import com.pmk.freeplayer.domain.model.Queue
import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.TrackDuration
import com.pmk.freeplayer.domain.model.enums.RepeatMode

data class PlaybackState(
	val currentSong: Song?,
	val isPlaying: Boolean,
	val currentPosition: TrackDuration,
	val totalDuration: TrackDuration,
	val repeatMode: RepeatMode,
	val shuffleEnabled: Boolean,
	val playbackSpeed: Float,
) {
	companion object {
		val EMPTY = PlaybackState(
			currentSong = null,
			isPlaying = false,
			currentPosition = TrackDuration.ZERO,
			totalDuration = TrackDuration.ZERO,
			repeatMode = RepeatMode.OFF,
			shuffleEnabled = false,
			playbackSpeed = 1f,
		)
	}
	
	// ═══════════════════════════════════════════════════════════════
	// COMPUTED PROPERTIES
	// ═══════════════════════════════════════════════════════════════
	
	val hasCurrentSong: Boolean
		get() = currentSong != null
	
	val progress: Float
		get() = if (totalDuration.millis > 0) {
			currentPosition.millis.toFloat() / totalDuration.millis.toFloat()
		} else 0f
	
	val remainingTime: TrackDuration
		get() = totalDuration - currentPosition
}

data class SavedPlayerState(
	val songId: Long?,
	val position: TrackDuration,
	val queue: Queue,
	val repeatMode: RepeatMode,
	val shuffleEnabled: Boolean,
)