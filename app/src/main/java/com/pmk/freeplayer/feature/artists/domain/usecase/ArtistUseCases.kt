package com.pmk.freeplayer.feature.artists.domain.usecase

import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.feature.artists.domain.model.Artist
import com.pmk.freeplayer.feature.artists.domain.model.SocialLinks
import com.pmk.freeplayer.feature.artists.domain.repository.ArtistRepository
import com.pmk.freeplayer.feature.artists.domain.repository.ArtistWithDetails
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ── Query use cases ───────────────────────────────────────────────────────────

class GetArtistsUseCase @Inject constructor(private val repository: ArtistRepository) {
	operator fun invoke(query: String? = null, sortConfig: SortConfig? = null): Flow<List<Artist>> =
		repository.getArtists(query, sortConfig)
}

class GetArtistByIdUseCase @Inject constructor(private val repository: ArtistRepository) {
	operator fun invoke(id: Long): Flow<Artist?> = repository.getArtistById(id)
}

class GetArtistWithDetailsUseCase @Inject constructor(private val repository: ArtistRepository) {
	operator fun invoke(artistId: Long): Flow<ArtistWithDetails?> =
		repository.getArtistWithDetails(artistId)
}

class GetArtistsCountUseCase @Inject constructor(private val repository: ArtistRepository) {
	suspend operator fun invoke(): Int = repository.count()
}

// ── Mutation use cases ────────────────────────────────────────────────────────

class UpdateArtistUseCase @Inject constructor(private val repository: ArtistRepository) {
	suspend operator fun invoke(artist: Artist) = repository.updateArtist(artist)
}

class DeleteArtistUseCase @Inject constructor(private val repository: ArtistRepository) {
	suspend operator fun invoke(id: Long) = repository.deleteArtist(id)
}

class ToggleFavoriteArtistUseCase @Inject constructor(private val repository: ArtistRepository) {
	suspend operator fun invoke(artistId: Long) = repository.toggleFavoriteArtist(artistId)
}

class UpdateArtistSocialLinksUseCase @Inject constructor(private val repository: ArtistRepository) {
	suspend operator fun invoke(artistId: Long, socialLinks: SocialLinks) =
		repository.updateSocialLinks(artistId, socialLinks)
}