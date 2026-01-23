package com.pmk.freeplayer.domain.useCase

import com.pmk.freeplayer.domain.model.SocialLink
import com.pmk.freeplayer.domain.repository.BibliotecaRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class SaveArtistSocialLinkUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(artistId: Long, link: SocialLink) {
		// Aquí podrías agregar validaciones, ej:
		// if (link.url.isBlank()) throw IllegalArgumentException("URL requerida")
		repository.saveSocialLink(artistId, link)
	}
}

class DeleteArtistSocialLinkUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	suspend operator fun invoke(linkId: Long) {
		repository.deleteSocialLink(linkId)
	}
}

class GetArtistSocialLinksUseCase @Inject constructor(
	private val repository: BibliotecaRepository
) {
	operator fun invoke(artistId: Long): Flow<List<SocialLink>> {
		return repository.getSocialLinks(artistId)
	}
}