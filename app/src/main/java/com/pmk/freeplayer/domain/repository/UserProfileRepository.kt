package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Genre
import com.pmk.freeplayer.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
	
	// ─────────────────────────────────────────────────────────────
	// User profile
	// ─────────────────────────────────────────────────────────────
	fun getProfile(): Flow<User>
	
	suspend fun updateProfileName(name: String)
	
	suspend fun updateProfileAvatar(uri: String?)
	
	suspend fun incrementListeningTime(milliseconds: Long)
	
	suspend fun incrementPlayedSongsCount()
	
	suspend fun updateFavoriteGenre(genre: Genre)
	
	suspend fun updateFavoriteArtist(artist: String)
	
	suspend fun resetProfileStats()
}