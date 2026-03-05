package com.pmk.freeplayer.feature.playlists.domain.repository

import com.pmk.freeplayer.feature.playlists.domain.model.Playlist
import com.pmk.freeplayer.feature.songs.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
	
	// ── Queries ───────────────────────────────────────────────────
	
	fun getPlaylists(query: String? = null): Flow<List<Playlist>>
	fun getUserPlaylists(): Flow<List<Playlist>>
	fun getSystemPlaylists(): Flow<List<Playlist>>
	fun getPlaylistById(id: Long): Flow<Playlist?>
	fun getPlaylistWithSongs(playlistId: Long): Flow<PlaylistWithSongs?>
	suspend fun existsSongInPlaylist(playlistId: Long, songId: Long): Boolean
	
	// ── Writes ────────────────────────────────────────────────────
	
	suspend fun createPlaylist(name: String, description: String? = null): Long
	suspend fun updatePlaylist(id: Long, name: String, description: String?)
	suspend fun updatePlaylistCover(id: Long, coverUri: String?)
	suspend fun togglePin(playlistId: Long)
	suspend fun deletePlaylist(id: Long)
	suspend fun duplicatePlaylist(id: Long, newName: String): Long
	
	// ── Song membership ───────────────────────────────────────────
	
	suspend fun addSongToPlaylist(playlistId: Long, songId: Long)
	suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>)
	suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)
	suspend fun moveSongInPlaylist(playlistId: Long, fromPosition: Int, toPosition: Int)
}

data class PlaylistWithSongs(
	val playlist: Playlist,
	val songs: List<Song>,
)