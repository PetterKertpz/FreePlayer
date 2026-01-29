package com.pmk.freeplayer.domain.useCase

import com.pmk.freeplayer.domain.model.Album
import com.pmk.freeplayer.domain.model.enums.SortConfig
import com.pmk.freeplayer.domain.repository.AlbumWithSongs
import com.pmk.freeplayer.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllAlbumsUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	operator fun invoke(sortConfig: SortConfig? = null): Flow<List<Album>> {
		return sortConfig?.let { repository.getSortedAlbums(it) }
			?: repository.getAllAlbums()
	}
}

// domain/useCase/album/GetAlbumByIdUseCase.kt
class GetAlbumByIdUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	operator fun invoke(albumId: Long): Flow<Album?> = repository.getAlbumById(albumId)
}

// domain/useCase/album/GetAlbumWithSongsUseCase.kt
class GetAlbumWithSongsUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	operator fun invoke(albumId: Long): Flow<AlbumWithSongs?> =
		repository.getAlbumWithSongs(albumId)
}

// domain/useCase/album/SearchAlbumsUseCase.kt
class SearchAlbumsUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	operator fun invoke(query: String): Flow<List<Album>> {
		if (query.isBlank()) return repository.getAllAlbums()
		return repository.searchAlbums(query.trim())
	}
}

// domain/useCase/album/GetAlbumsByArtistUseCase.kt
class GetAlbumsByArtistUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	operator fun invoke(artistId: Long): Flow<List<Album>> =
		repository.getAlbumsByArtist(artistId)
}

// ═══════════════════════════════════════════════════════════════
// GENRE USE CASES
// ═══════════════════════════════════════════════════════════════

class GetAllGenresUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	operator fun invoke() = repository.getAllGenres()
}

// domain/useCase/genre/GetGenresWithCountUseCase.kt
class GetGenresWithCountUseCase @Inject constructor(
	private val repository: LibraryRepository
) {
	operator fun invoke() = repository.getGenresWithSongCount()
}