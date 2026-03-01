package com.pmk.freeplayer.feature.playlists.domain.usecase

// ─────────────────────────────────────────────────────────────────────────────
// ABSORBE (eliminados):
//   GetAllPlaylistsUseCase, GetPlaylistByIdUseCase, SearchPlaylistsUseCase
//   CreatePlaylistUseCase, UpdatePlaylistUseCase, DeletePlaylistUseCase
//   DuplicatePlaylistUseCase, AddSongsToPlaylistUseCase,
//   RemoveSongFromPlaylistUseCase, ReorderPlaylistUseCase,
//   CheckSongInPlaylistUseCase, UpdatePlaylistCoverUseCase
//
// CONSERVA: lógica de validación (require) y los data class de parámetros
// ─────────────────────────────────────────────────────────────────────────────

import com.pmk.freeplayer.feature.playlists.domain.model.Playlist
import com.pmk.freeplayer.feature.playlists.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ═════════════════════════════════════════════════════════════════════════════
// PARÁMETROS — Value objects para operaciones con validación
// ═════════════════════════════════════════════════════════════════════════════

data class CreatePlaylistParams(
	val name: String,
	val description: String? = null,
)

data class UpdatePlaylistParams(
	val id: Long,
	val name: String,
	val description: String?,
)

// ═════════════════════════════════════════════════════════════════════════════
// CONSULTAS
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Consultas de playlists.
 *
 * Uso en ViewModel:
 * ```kotlin
 * getPlaylistsUseCase()                  // todas
 * getPlaylistsUseCase(query = "Chill")   // búsqueda (si blank → devuelve todas)
 * getPlaylistsUseCase.byId(1L)
 * ```
 */
class GetPlaylistsUseCase @Inject constructor(
	private val repository: PlaylistRepository,
) {
	/**
	 * Devuelve todas las playlists filtrando por [query] si no está vacío.
	 * Reemplaza: GetAllPlaylistsUseCase + SearchPlaylistsUseCase.
	 */
	operator fun invoke(query: String? = null): Flow<List<Playlist>> =
		if (query.isNullOrBlank()) repository.getAllPlaylists()
		else repository.searchPlaylists(query.trim())
	
	fun byId(id: Long): Flow<Playlist?> = repository.getPlaylistById(id)
}

// ═════════════════════════════════════════════════════════════════════════════
// MUTACIONES
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Mutaciones de playlists y gestión de sus canciones.
 *
 * Uso en ViewModel:
 * ```kotlin
 * managePlaylistUseCase.create(CreatePlaylistParams("Workout"))
 * managePlaylistUseCase.update(UpdatePlaylistParams(id, "Nuevo nombre", null))
 * managePlaylistUseCase.delete(playlistId)
 * managePlaylistUseCase.duplicate(playlistId, "Copia de Workout")
 * managePlaylistUseCase.addSong(playlistId, songId)
 * managePlaylistUseCase.addSongs(playlistId, songIds)
 * managePlaylistUseCase.removeSong(playlistId, songId)
 * managePlaylistUseCase.moveSong(playlistId, from = 2, to = 0)
 * managePlaylistUseCase.hasSong(playlistId, songId)
 * managePlaylistUseCase.updateCover(playlistId, uri)
 * ```
 */
class ManagePlaylistUseCase @Inject constructor(
	private val repository: PlaylistRepository,
) {
	
	// ─── CRUD ────────────────────────────────────────────────────
	
	suspend fun create(params: CreatePlaylistParams): Long {
		require(params.name.isNotBlank()) { "Playlist name cannot be blank" }
		return repository.createPlaylist(
			name = params.name.trim(),
			description = params.description?.trim(),
		)
	}
	
	suspend fun update(params: UpdatePlaylistParams) {
		require(params.name.isNotBlank()) { "Playlist name cannot be blank" }
		repository.updatePlaylist(
			id = params.id,
			name = params.name.trim(),
			description = params.description?.trim(),
		)
	}
	
	suspend fun delete(playlistId: Long) = repository.deletePlaylist(playlistId)
	
	suspend fun duplicate(playlistId: Long, newName: String): Long {
		require(newName.isNotBlank()) { "New playlist name cannot be blank" }
		return repository.duplicatePlaylist(playlistId, newName.trim())
	}
	
	suspend fun updateCover(playlistId: Long, coverUri: String?) =
		repository.updatePlaylistCover(playlistId, coverUri)
	
	// ─── Gestión de canciones ────────────────────────────────────
	
	suspend fun addSong(playlistId: Long, songId: Long) =
		repository.addSongToPlaylist(playlistId, songId)
	
	suspend fun addSongs(playlistId: Long, songIds: List<Long>) =
		repository.addSongsToPlaylist(playlistId, songIds)
	
	suspend fun removeSong(playlistId: Long, songId: Long) =
		repository.removeSongFromPlaylist(playlistId, songId)
	
	suspend fun moveSong(playlistId: Long, from: Int, to: Int) =
		repository.moveSongInPlaylist(playlistId, from, to)
	
	suspend fun hasSong(playlistId: Long, songId: Long): Boolean =
		repository.existsSongInPlaylist(playlistId, songId)
}