package com.pmk.freeplayer.domain.useCase

import com.pmk.freeplayer.domain.model.Playlist
import com.pmk.freeplayer.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPlaylistsUseCase @Inject constructor(
	private val repository: PlaylistRepository
) {
	operator fun invoke(): Flow<List<Playlist>> = repository.getAllPlaylists()
}

// domain/useCase/playlist/GetPlaylistByIdUseCase.kt
class GetPlaylistByIdUseCase @Inject constructor(
	private val repository: PlaylistRepository
) {
	operator fun invoke(id: Long): Flow<Playlist?> = repository.getPlaylistById(id)
}

// domain/useCase/playlist/CreatePlaylistUseCase.kt
data class CreatePlaylistParams(
	val name: String,
	val description: String? = null
)

class CreatePlaylistUseCase @Inject constructor(
	private val repository: PlaylistRepository
) {
	suspend operator fun invoke(params: CreatePlaylistParams): Long {
		require(params.name.isNotBlank()) { "Playlist name cannot be blank" }
		return repository.createPlaylist(
			name = params.name.trim(),
			description = params.description?.trim()
		)
	}
}

// domain/useCase/playlist/UpdatePlaylistUseCase.kt
data class UpdatePlaylistParams(
	val id: Long,
	val name: String,
	val description: String?
)

class UpdatePlaylistUseCase @Inject constructor(
	private val repository: PlaylistRepository
) {
	suspend operator fun invoke(params: UpdatePlaylistParams) {
		require(params.name.isNotBlank()) { "Playlist name cannot be blank" }
		repository.updatePlaylist(
			id = params.id,
			name = params.name.trim(),
			description = params.description?.trim()
		)
	}
}

// domain/useCase/playlist/DeletePlaylistUseCase.kt
class DeletePlaylistUseCase @Inject constructor(
	private val repository: PlaylistRepository
) {
	suspend operator fun invoke(playlistId: Long) = repository.deletePlaylist(playlistId)
}

// domain/useCase/playlist/DuplicatePlaylistUseCase.kt
class DuplicatePlaylistUseCase @Inject constructor(
	private val repository: PlaylistRepository
) {
	suspend operator fun invoke(playlistId: Long, newName: String): Long {
		require(newName.isNotBlank()) { "New playlist name cannot be blank" }
		return repository.duplicatePlaylist(playlistId, newName.trim())
	}
}

// domain/useCase/playlist/AddSongsToPlaylistUseCase.kt
class AddSongsToPlaylistUseCase @Inject constructor(
	private val repository: PlaylistRepository
) {
	suspend operator fun invoke(playlistId: Long, songId: Long) {
		repository.addSongToPlaylist(playlistId, songId)
	}
	
	suspend fun multiple(playlistId: Long, songIds: List<Long>) {
		repository.addSongsToPlaylist(playlistId, songIds)
	}
}

// domain/useCase/playlist/RemoveSongFromPlaylistUseCase.kt
class RemoveSongFromPlaylistUseCase @Inject constructor(
	private val repository: PlaylistRepository
) {
	suspend operator fun invoke(playlistId: Long, songId: Long) {
		repository.removeSongFromPlaylist(playlistId, songId)
	}
}

// domain/useCase/playlist/ReorderPlaylistUseCase.kt
class ReorderPlaylistUseCase @Inject constructor(
	private val repository: PlaylistRepository
) {
	suspend operator fun invoke(playlistId: Long, from: Int, to: Int) {
		repository.moveSongInPlaylist(playlistId, from, to)
	}
}

// domain/useCase/playlist/CheckSongInPlaylistUseCase.kt
class CheckSongInPlaylistUseCase @Inject constructor(
	private val repository: PlaylistRepository
) {
	suspend operator fun invoke(playlistId: Long, songId: Long): Boolean {
		return repository.existsSongInPlaylist(playlistId, songId)
	}
}

// domain/useCase/playlist/UpdatePlaylistCoverUseCase.kt
class UpdatePlaylistCoverUseCase @Inject constructor(
	private val repository: PlaylistRepository
) {
	suspend operator fun invoke(playlistId: Long, coverUri: String?) {
		repository.updatePlaylistCover(playlistId, coverUri)
	}
}

// domain/useCase/playlist/SearchPlaylistsUseCase.kt
class SearchPlaylistsUseCase @Inject constructor(
	private val repository: PlaylistRepository
) {
	operator fun invoke(query: String): Flow<List<Playlist>> {
		if (query.isBlank()) return repository.getAllPlaylists()
		return repository.searchPlaylists(query.trim())
	}
}