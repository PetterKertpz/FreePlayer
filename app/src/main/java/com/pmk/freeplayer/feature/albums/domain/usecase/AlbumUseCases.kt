package com.pmk.freeplayer.feature.albums.domain.usecase

import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.feature.albums.domain.model.Album
import com.pmk.freeplayer.feature.albums.domain.repository.AlbumRepository
import com.pmk.freeplayer.feature.albums.domain.repository.AlbumWithSongs
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ── Query use cases ───────────────────────────────────────────────────────────

class GetAlbumsUseCase @Inject constructor(private val repository: AlbumRepository) {
	operator fun invoke(query: String? = null, sortConfig: SortConfig? = null): Flow<List<Album>> =
		repository.getAlbums(query, sortConfig)
}

class GetAlbumByIdUseCase @Inject constructor(private val repository: AlbumRepository) {
	operator fun invoke(id: Long): Flow<Album?> = repository.getAlbumById(id)
}

class GetAlbumsByArtistUseCase @Inject constructor(private val repository: AlbumRepository) {
	operator fun invoke(artistId: Long): Flow<List<Album>> = repository.getAlbumsByArtist(artistId)
}

class GetAlbumWithSongsUseCase @Inject constructor(private val repository: AlbumRepository) {
	operator fun invoke(albumId: Long): Flow<AlbumWithSongs?> = repository.getAlbumWithSongs(albumId)
}

class GetRecentlyAddedAlbumsUseCase @Inject constructor(private val repository: AlbumRepository) {
	operator fun invoke(limit: Int = 10): Flow<List<Album>> = repository.getRecentlyAdded(limit)
}

class GetAlbumsCountUseCase @Inject constructor(private val repository: AlbumRepository) {
	suspend operator fun invoke(): Int = repository.count()
}

// ── Mutation use cases ────────────────────────────────────────────────────────

class UpdateAlbumUseCase @Inject constructor(private val repository: AlbumRepository) {
	suspend operator fun invoke(album: Album) = repository.updateAlbum(album)
}

class DeleteAlbumUseCase @Inject constructor(private val repository: AlbumRepository) {
	suspend operator fun invoke(id: Long) = repository.deleteAlbum(id)
}

class ToggleFavoriteAlbumUseCase @Inject constructor(private val repository: AlbumRepository) {
	suspend operator fun invoke(albumId: Long) = repository.toggleFavorite(albumId)
}

class SetAlbumRatingUseCase @Inject constructor(private val repository: AlbumRepository) {
	suspend operator fun invoke(albumId: Long, rating: Float) = repository.setRating(albumId, rating)
}