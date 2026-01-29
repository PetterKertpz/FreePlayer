package com.pmk.freeplayer.domain.useCase.artist

import com.pmk.freeplayer.domain.model.Artist
import com.pmk.freeplayer.domain.model.SocialLinks
import com.pmk.freeplayer.domain.model.enums.SortConfig
import com.pmk.freeplayer.domain.repository.ArtistRepository
import com.pmk.freeplayer.domain.repository.ArtistWithDetails
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllArtistsUseCase @Inject constructor(
	private val repository: ArtistRepository
) {
	operator fun invoke(sortConfig: SortConfig? = null): Flow<List<Artist>> {
		return sortConfig?.let { repository.getSortedArtists(it) }
			?: repository.getAllArtists()
	}
}

// domain/useCase/artist/GetArtistByIdUseCase.kt
class GetArtistByIdUseCase @Inject constructor(
	private val repository: ArtistRepository
) {
	operator fun invoke(artistId: Long): Flow<Artist?> = repository.getArtistById(artistId)
}

// domain/useCase/artist/GetArtistWithDetailsUseCase.kt
class GetArtistWithDetailsUseCase @Inject constructor(
	private val repository: ArtistRepository
) {
	operator fun invoke(artistId: Long): Flow<ArtistWithDetails?> =
		repository.getArtistWithDetails(artistId)
}

// domain/useCase/artist/SearchArtistsUseCase.kt
class SearchArtistsUseCase @Inject constructor(
	private val repository: ArtistRepository
) {
	operator fun invoke(query: String): Flow<List<Artist>> {
		if (query.isBlank()) return repository.getAllArtists()
		return repository.searchArtists(query.trim())
	}
}

// domain/useCase/artist/ToggleFavoriteArtistUseCase.kt
class ToggleFavoriteArtistUseCase @Inject constructor(
	private val repository: ArtistRepository
) {
	suspend operator fun invoke(artistId: Long) = repository.toggleFavorite(artistId)
}

// domain/useCase/artist/UpdateArtistSocialLinksUseCase.kt
class UpdateArtistSocialLinksUseCase @Inject constructor(
	private val repository: ArtistRepository
) {
	suspend operator fun invoke(artistId: Long, socialLinks: SocialLinks) {
		repository.updateSocialLinks(artistId, socialLinks)
	}
}