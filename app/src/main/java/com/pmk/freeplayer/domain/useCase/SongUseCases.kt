// ═══════════════════════════════════════════════════════════════
// domain/useCase/song/GetAllSongsUseCase.kt
// ═══════════════════════════════════════════════════════════════
package com.pmk.freeplayer.domain.useCase

import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.enums.SortConfig
import com.pmk.freeplayer.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSongsUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	operator fun invoke(sortConfig: SortConfig? = null): Flow<List<Song>> {
		return sortConfig?.let { repository.getSortedSongs(it) }
			?: repository.getAllSongs()
	}
}

// ═══════════════════════════════════════════════════════════════
// domain/useCase/song/GetSongByIdUseCase.kt
// ═══════════════════════════════════════════════════════════════
class GetSongByIdUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	operator fun invoke(songId: Long): Flow<Song?> = repository.getSongById(songId)
}

// ═══════════════════════════════════════════════════════════════
// domain/useCase/song/GetSongsByIdsUseCase.kt
// ═══════════════════════════════════════════════════════════════
class GetSongsByIdsUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	operator fun invoke(ids: List<Long>): Flow<List<Song>> = repository.getSongsByIds(ids)
}

// ═══════════════════════════════════════════════════════════════
// domain/useCase/song/SearchSongsUseCase.kt
// ═══════════════════════════════════════════════════════════════
class SearchSongsUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	operator fun invoke(query: String): Flow<List<Song>> {
		if (query.isBlank()) return repository.getAllSongs()
		return repository.searchSongs(query.trim())
	}
}

// ═══════════════════════════════════════════════════════════════
// domain/useCase/song/ToggleFavoriteSongUseCase.kt
// ═══════════════════════════════════════════════════════════════
class ToggleFavoriteSongUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	suspend operator fun invoke(songId: Long) = repository.toggleFavorite(songId)
}

// ═══════════════════════════════════════════════════════════════
// domain/useCase/song/GetFavoriteSongsUseCase.kt
// ═══════════════════════════════════════════════════════════════
class GetFavoriteSongsUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	operator fun invoke(): Flow<List<Song>> = repository.getFavoriteSongs()
}

// ═══════════════════════════════════════════════════════════════
// domain/useCase/song/GetRecentlyAddedSongsUseCase.kt
// ═══════════════════════════════════════════════════════════════
class GetRecentlyAddedSongsUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	operator fun invoke(limit: Int = 20): Flow<List<Song>> =
		repository.getRecentlyAddedSongs(limit)
}

// ═══════════════════════════════════════════════════════════════
// domain/useCase/song/GetMostPlayedSongsUseCase.kt
// ═══════════════════════════════════════════════════════════════
class GetMostPlayedSongsUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	operator fun invoke(limit: Int = 20): Flow<List<Song>> =
		repository.getMostPlayedSongs(limit)
}

// ═══════════════════════════════════════════════════════════════
// domain/useCase/song/GetSongsByRelationUseCase.kt
// ═══════════════════════════════════════════════════════════════
class GetSongsByRelationUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	fun byAlbum(albumId: Long): Flow<List<Song>> = repository.getSongsByAlbum(albumId)
	fun byArtist(artistId: Long): Flow<List<Song>> = repository.getSongsByArtist(artistId)
	fun byGenre(genreId: Long): Flow<List<Song>> = repository.getSongsByGenre(genreId)
}

// ═══════════════════════════════════════════════════════════════
// domain/useCase/song/DeleteSongsUseCase.kt
// ═══════════════════════════════════════════════════════════════
class DeleteSongsUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	suspend operator fun invoke(songId: Long) = repository.deleteSong(songId)
	suspend fun multiple(ids: List<Long>) = repository.deleteSongsByIds(ids)
}