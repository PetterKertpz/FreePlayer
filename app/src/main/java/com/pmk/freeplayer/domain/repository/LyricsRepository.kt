package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Lyrics
import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.enums.LyricsStatus
import kotlinx.coroutines.flow.Flow

interface LyricsRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// GET & MANAGE LYRICS
	// ═══════════════════════════════════════════════════════════════
	
	fun getLyrics(songId: Long): Flow<Lyrics?>
	suspend fun saveLyrics(lyrics: Lyrics)
	suspend fun deleteLyrics(songId: Long)
	suspend fun hasLyrics(songId: Long): Boolean
	
	// ═══════════════════════════════════════════════════════════════
	// SEARCH LYRICS
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Searches for lyrics online.
	 * Implementation details (Genius, etc.) are hidden in the data layer.
	 */
	suspend fun searchOnline(title: String, artist: String): Lyrics?
	
	/**
	 * Searches for a local .lrc file matching the song path.
	 */
	suspend fun searchLocalLrcFile(songPath: String): Lyrics?
	
	// ═══════════════════════════════════════════════════════════════
	// LYRICS STATUS
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun updateStatus(songId: Long, status: LyricsStatus)
	suspend fun markAsFound(songId: Long, lyrics: String, sourceUrl: String? = null)
	suspend fun markAsNotFound(songId: Long)
	
	// ═══════════════════════════════════════════════════════════════
	// BATCH QUERIES
	// ═══════════════════════════════════════════════════════════════
	
	fun getSongsPendingLyricsSearch(limit: Int = 50): Flow<List<Song>>
	suspend fun countByStatus(status: LyricsStatus): Int
}