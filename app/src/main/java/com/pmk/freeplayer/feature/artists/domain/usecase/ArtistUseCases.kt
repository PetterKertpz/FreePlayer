package com.pmk.freeplayer.feature.artists.domain.usecase

import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.feature.artists.domain.model.Artist
import com.pmk.freeplayer.feature.artists.domain.model.SocialLinks
import com.pmk.freeplayer.feature.artists.domain.repository.ArtistRepository
import com.pmk.freeplayer.feature.artists.domain.repository.ArtistWithDetails
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ═════════════════════════════════════════════════════════════════════════════
// QUERIES
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Consultas de artistas.
 *
 * Uso en ViewModel:
 * ```kotlin
 * getArtistsUseCase()
 * getArtistsUseCase(query = "Beatles")
 * getArtistsUseCase.byId(artistId)
 * getArtistsUseCase.withDetails(artistId)
 * getArtistsUseCase.totalCount()
 * ```
 */
class GetArtistsUseCase @Inject constructor(
	private val repository: ArtistRepository,
) {
	operator fun invoke(
		query: String? = null,
		sortConfig: SortConfig? = null,
	): Flow<List<Artist>> = repository.getArtists(query, sortConfig)
	
	fun byId(id: Long): Flow<Artist?> =
		repository.getArtistById(id)
	
	/**
	 * Artista con canciones, álbumes y top canciones.
	 * Usado en la pantalla de detalle.
	 */
	fun withDetails(artistId: Long): Flow<ArtistWithDetails?> =
		repository.getArtistWithDetails(artistId)
	
	suspend fun totalCount(): Int =
		repository.getTotalArtistsCount()
}

// ═════════════════════════════════════════════════════════════════════════════
// MUTATIONS
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Mutaciones de artistas: edición, canciones, favoritos y links externos.
 *
 * Uso en ViewModel:
 * ```kotlin
 * manageArtistsUseCase.update(artist)
 * manageArtistsUseCase.delete(artistId)
 * manageArtistsUseCase.addSong(songId, artistId)
 * manageArtistsUseCase.removeSong(songId, artistId)
 * manageArtistsUseCase.toggleFavorite(artistId)
 * manageArtistsUseCase.updateSocialLinks(artistId, links)
 * ```
 */
class ManageArtistsUseCase @Inject constructor(
	private val repository: ArtistRepository,
) {
	/** Actualiza metadatos del artista (nombre, biografía, foto). */
	suspend fun update(artist: Artist) =
		repository.updateArtist(artist)
	
	/**
	 * Elimina el registro del artista en Room.
	 * No elimina las canciones asociadas del dispositivo.
	 */
	suspend fun delete(id: Long) =
		repository.deleteArtist(id)
	
	suspend fun addSong(songId: Long, artistId: Long) =
		repository.addSongToArtist(songId, artistId)
	
	suspend fun removeSong(songId: Long, artistId: Long) =
		repository.removeSongFromArtist(songId, artistId)
	
	suspend fun toggleFavorite(artistId: Long) =
		repository.toggleFavoriteArtist(artistId)
	
	/** Actualiza los links externos: Genius, YouTube, Instagram, etc. */
	suspend fun updateSocialLinks(artistId: Long, socialLinks: SocialLinks) =
		repository.updateSocialLinks(artistId, socialLinks)
}