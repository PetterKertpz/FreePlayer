package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.enums.RepeatMode
import kotlinx.coroutines.flow.Flow

interface PlaybackConfigRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Playback configuration
	// ─────────────────────────────────────────────────────────────
	fun getRepeatMode(): Flow<RepeatMode>
	
	suspend fun setRepeatMode(mode: RepeatMode)
	
	fun isShuffleEnabled(): Flow<Boolean>
	
	suspend fun setShuffleEnabled(enabled: Boolean)
}