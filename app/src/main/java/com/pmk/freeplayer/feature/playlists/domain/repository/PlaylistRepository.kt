package com.pmk.freeplayer.feature.playlists.domain.repository

import com.pmk.freeplayer.feature.playlists.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
	
	// ─────────────────────────────────────────────────────────────
	// CRUD operations
	// ─────────────────────────────────────────────────────────────
	fun getAllPlaylists(): Flow<List<Playlist>>
	
	fun getPlaylistById(id: Long): Flow<Playlist?>
	
	suspend fun createPlaylist(name: String, description: String? = null): Long
	
	suspend fun updatePlaylist(id: Long, name: String, description: String?)
	
	suspend fun deletePlaylist(id: Long)
	
	suspend fun duplicatePlaylist(id: Long, newName: String): Long
	
	// ─────────────────────────────────────────────────────────────
	// Song management in playlist
	// ─────────────────────────────────────────────────────────────
	suspend fun addSongToPlaylist(playlistId: Long, songId: Long)
	
	suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>)
	
	suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)
	
	suspend fun moveSongInPlaylist(playlistId: Long, fromPosition: Int, toPosition: Int)
	
	suspend fun existsSongInPlaylist(playlistId: Long, songId: Long): Boolean
	
	// ─────────────────────────────────────────────────────────────
	// Playlist cover
	// ─────────────────────────────────────────────────────────────
	suspend fun updatePlaylistCover(id: Long, uri: String?)
	
	// ─────────────────────────────────────────────────────────────
	// Search playlists
	// ─────────────────────────────────────────────────────────────
	fun searchPlaylists(query: String): Flow<List<Playlist>>
}