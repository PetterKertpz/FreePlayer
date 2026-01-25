package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.PlaybackState
import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.TrackDuration
import kotlinx.coroutines.flow.Flow

interface PlaybackStateRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Current state
	// ─────────────────────────────────────────────────────────────
	fun getCurrentSong(): Flow<Song?>
	
	fun getCurrentPosition(): Flow<TrackDuration>
	
	fun isPlaying(): Flow<Boolean>
	
	fun getPlaybackState(): Flow<PlaybackState>
}