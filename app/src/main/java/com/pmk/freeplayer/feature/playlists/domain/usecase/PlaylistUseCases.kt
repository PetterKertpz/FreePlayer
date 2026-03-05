package com.pmk.freeplayer.feature.playlists.domain.usecase

import com.pmk.freeplayer.feature.playlists.domain.model.Playlist
import com.pmk.freeplayer.feature.playlists.domain.repository.PlaylistRepository
import com.pmk.freeplayer.feature.playlists.domain.repository.PlaylistWithSongs
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ── Parameter value objects ───────────────────────────────────────────────────

data class CreatePlaylistParams(val name: String, val description: String? = null)
data class UpdatePlaylistParams(val id: Long, val name: String, val description: String?)

// ── Query use cases ───────────────────────────────────────────────────────────

class GetPlaylistsUseCase @Inject constructor(private val repository: PlaylistRepository) {
	operator fun invoke(query: String? = null): Flow<List<Playlist>> =
		repository.getPlaylists(query)
}

class GetUserPlaylistsUseCase @Inject constructor(private val repository: PlaylistRepository) {
	operator fun invoke(): Flow<List<Playlist>> = repository.getUserPlaylists()
}

class GetSystemPlaylistsUseCase @Inject constructor(private val repository: PlaylistRepository) {
	operator fun invoke(): Flow<List<Playlist>> = repository.getSystemPlaylists()
}

class GetPlaylistByIdUseCase @Inject constructor(private val repository: PlaylistRepository) {
	operator fun invoke(id: Long): Flow<Playlist?> = repository.getPlaylistById(id)
}

class GetPlaylistWithSongsUseCase @Inject constructor(private val repository: PlaylistRepository) {
	operator fun invoke(playlistId: Long): Flow<PlaylistWithSongs?> =
		repository.getPlaylistWithSongs(playlistId)
}

class CheckSongInPlaylistUseCase @Inject constructor(private val repository: PlaylistRepository) {
	suspend operator fun invoke(playlistId: Long, songId: Long): Boolean =
		repository.existsSongInPlaylist(playlistId, songId)
}

// ── Mutation use cases ────────────────────────────────────────────────────────

class CreatePlaylistUseCase @Inject constructor(private val repository: PlaylistRepository) {
	suspend operator fun invoke(params: CreatePlaylistParams): Long {
		require(params.name.isNotBlank()) { "Playlist name cannot be blank" }
		return repository.createPlaylist(params.name.trim(), params.description?.trim())
	}
}

class UpdatePlaylistUseCase @Inject constructor(private val repository: PlaylistRepository) {
	suspend operator fun invoke(params: UpdatePlaylistParams) {
		require(params.name.isNotBlank()) { "Playlist name cannot be blank" }
		repository.updatePlaylist(params.id, params.name.trim(), params.description?.trim())
	}
}

class DeletePlaylistUseCase @Inject constructor(private val repository: PlaylistRepository) {
	suspend operator fun invoke(id: Long) = repository.deletePlaylist(id)
}

class DuplicatePlaylistUseCase @Inject constructor(private val repository: PlaylistRepository) {
	suspend operator fun invoke(id: Long, newName: String): Long {
		require(newName.isNotBlank()) { "Playlist name cannot be blank" }
		return repository.duplicatePlaylist(id, newName.trim())
	}
}

class UpdatePlaylistCoverUseCase @Inject constructor(private val repository: PlaylistRepository) {
	suspend operator fun invoke(playlistId: Long, coverUri: String?) =
		repository.updatePlaylistCover(playlistId, coverUri)
}

class TogglePlaylistPinUseCase @Inject constructor(private val repository: PlaylistRepository) {
	suspend operator fun invoke(playlistId: Long) = repository.togglePin(playlistId)
}

class AddSongToPlaylistUseCase @Inject constructor(private val repository: PlaylistRepository) {
	suspend operator fun invoke(playlistId: Long, songId: Long) =
		repository.addSongToPlaylist(playlistId, songId)
}

class AddSongsToPlaylistUseCase @Inject constructor(private val repository: PlaylistRepository) {
	suspend operator fun invoke(playlistId: Long, songIds: List<Long>) =
		repository.addSongsToPlaylist(playlistId, songIds)
}

class RemoveSongFromPlaylistUseCase @Inject constructor(private val repository: PlaylistRepository) {
	suspend operator fun invoke(playlistId: Long, songId: Long) =
		repository.removeSongFromPlaylist(playlistId, songId)
}

class MoveSongInPlaylistUseCase @Inject constructor(private val repository: PlaylistRepository) {
	suspend operator fun invoke(playlistId: Long, from: Int, to: Int) =
		repository.moveSongInPlaylist(playlistId, from, to)
}