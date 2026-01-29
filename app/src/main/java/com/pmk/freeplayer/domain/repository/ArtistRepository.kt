package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Album
import com.pmk.freeplayer.domain.model.Artist
import com.pmk.freeplayer.domain.model.SocialLinks
import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.enums.SortConfig
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// ARTISTS - QUERIES
	// ═══════════════════════════════════════════════════════════════
	
	fun getAllArtists(): Flow<List<Artist>>
	fun getArtistById(id: Long): Flow<Artist?>
	fun getSortedArtists(sortConfig: SortConfig): Flow<List<Artist>>
	fun searchArtists(query: String): Flow<List<Artist>>
	
	suspend fun getTotalArtistsCount(): Int
	
	// ═══════════════════════════════════════════════════════════════
	// ARTISTS - MUTATIONS
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun updateArtist(artist: Artist)
	suspend fun toggleFavorite(artistId: Long)
	
	// ═══════════════════════════════════════════════════════════════
	// ARTIST DETAILS (Aggregated data for detail screen)
	// ═══════════════════════════════════════════════════════════════
	
	fun getArtistWithDetails(artistId: Long): Flow<ArtistWithDetails?>
	
	// ═══════════════════════════════════════════════════════════════
	// SOCIAL LINKS (Embedded in Artist, but may need separate update)
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun updateSocialLinks(artistId: Long, socialLinks: SocialLinks)
}

/**
 * Complete artist data for detail screen
 */
data class ArtistWithDetails(
	val artist: Artist,
	val songs: List<Song>,
	val albums: List<Album>,
	val topSongs: List<Song>  // Most played songs by this artist
)