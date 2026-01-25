package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Artist
import com.pmk.freeplayer.domain.model.SocialLink
import com.pmk.freeplayer.domain.model.enums.SortConfiguration
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
	
	fun getAllArtists(): Flow<List<Artist>>
	
	fun getArtistById(id: Long): Flow<Artist?>
	
	fun getSortedArtists(sortOrder: SortConfiguration): Flow<List<Artist>>
	
	fun searchArtists(query: String): Flow<List<Artist>>
	
	suspend fun getTotalArtistCount(): Int
	
	// ─────────────────────────────────────────────────────────────
	// Social media
	// ─────────────────────────────────────────────────────────────
	suspend fun saveSocialLink(artistId: Long, link: SocialLink)
	
	suspend fun deleteSocialLink(linkId: Long)
	
	fun getSocialLinks(artistId: Long): Flow<List<SocialLink>>
}