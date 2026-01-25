package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Lyrics
import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.enums.LyricsStatus
import kotlinx.coroutines.flow.Flow

interface LyricsRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Get and manage lyrics
	// ─────────────────────────────────────────────────────────────
	fun getLyrics(songId: Long): Flow<Lyrics?>
	
	suspend fun saveLyrics(lyrics: Lyrics)
	
	suspend fun deleteLyrics(songId: Long)
	
	suspend fun hasLyrics(songId: Long): Boolean
	
	// ─────────────────────────────────────────────────────────────
	// Search lyrics
	// ─────────────────────────────────────────────────────────────
	suspend fun searchLyricsOnline(title: String, artist: String): Lyrics
	
	suspend fun searchLocalLrcFile(songPath: String): Lyrics
	
	// ─────────────────────────────────────────────────────────────
	// Lyrics status
	// ─────────────────────────────────────────────────────────────
	suspend fun updateLyricsStatus(songId: Long, status: LyricsStatus)
	
	suspend fun saveLyricsWithStatus(songId: Long, lyrics: String, geniusUrl: String?)
	
	suspend fun markLyricsNotFound(songId: Long)
	
	fun getSongsWithoutSearchedLyrics(limit: Int = 50): Flow<List<Song>>
	
	suspend fun countByLyricsStatus(status: LyricsStatus): Int
}