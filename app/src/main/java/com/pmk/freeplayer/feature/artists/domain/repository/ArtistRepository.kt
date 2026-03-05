package com.pmk.freeplayer.feature.artists.domain.repository

import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.feature.albums.domain.model.Album
import com.pmk.freeplayer.feature.artists.domain.model.Artist
import com.pmk.freeplayer.feature.artists.domain.model.SocialLinks
import com.pmk.freeplayer.feature.songs.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
	
	// ── Queries ───────────────────────────────────────────────────
	
	fun getArtists(query: String? = null, sortConfig: SortConfig? = null): Flow<List<Artist>>
	fun getArtistById(id: Long): Flow<Artist?>
	fun getArtistWithDetails(artistId: Long): Flow<ArtistWithDetails?>
	suspend fun count(): Int
	
	// ── Writes ────────────────────────────────────────────────────
	
	suspend fun updateArtist(artist: Artist)
	suspend fun deleteArtist(id: Long)
	suspend fun toggleFavoriteArtist(artistId: Long)
	suspend fun updateSocialLinks(artistId: Long, socialLinks: SocialLinks)
}

/**
 * Artist detail aggregate: songs and albums associated with this artist.
 * NOTE: topSongs (play-count ranking) is intentionally excluded — it belongs to
 * feature/statistics and must be assembled there via the statistics repository.
 */
data class ArtistWithDetails(
	val artist: Artist,
	val songs: List<Song>,
	val albums: List<Album>,
)