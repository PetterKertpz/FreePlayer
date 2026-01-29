package com.pmk.freeplayer.data.repository

import com.pmk.freeplayer.data.local.dao.AlbumDao
import com.pmk.freeplayer.data.local.dao.ArtistDao
import com.pmk.freeplayer.data.local.dao.SongDao
import com.pmk.freeplayer.data.mapper.toAlbumDomainList
import com.pmk.freeplayer.data.mapper.toArtistDomain
import com.pmk.freeplayer.data.mapper.toDomain
import com.pmk.freeplayer.data.mapper.toEntity
import com.pmk.freeplayer.domain.model.Artist
import com.pmk.freeplayer.domain.model.SocialLinks
import com.pmk.freeplayer.domain.model.enums.SortConfig
import com.pmk.freeplayer.domain.model.enums.SortDirection
import com.pmk.freeplayer.domain.model.enums.SortField
import com.pmk.freeplayer.domain.repository.ArtistRepository
import com.pmk.freeplayer.domain.repository.ArtistWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
	private val artistDao: ArtistDao,
	private val albumDao: AlbumDao,
	private val songDao: SongDao
) : ArtistRepository {
	
	override fun getAllArtists(): Flow<List<Artist>> {
		return artistDao.getAllArtists().map { entities ->
			entities.map { it.toArtistDomain() }
		}
	}
	
	override fun getArtistById(id: Long): Flow<Artist?> {
		return artistDao.getArtistById(id).map { entity ->
			entity?.toArtistDomain()
		}
	}
	
	override fun getSortedArtists(sortConfig: SortConfig): Flow<List<Artist>> {
		return getAllArtists().map { artists ->
			when (sortConfig.field) {
				SortField.NAME -> {
					if (sortConfig.direction == SortDirection.ASCENDING) {
						artists.sortedBy { it.name }
					} else {
						artists.sortedByDescending { it.name }
					}
				}
				SortField.ALBUM_COUNT -> {
					if (sortConfig.direction == SortDirection.ASCENDING) {
						artists.sortedBy { it.albumCount }
					} else {
						artists.sortedByDescending { it.albumCount }
					}
				}
				SortField.SONG_COUNT -> {
					if (sortConfig.direction == SortDirection.ASCENDING) {
						artists.sortedBy { it.songCount }
					} else {
						artists.sortedByDescending { it.songCount }
					}
				}
				SortField.PLAY_COUNT -> {
					if (sortConfig.direction == SortDirection.ASCENDING) {
						artists.sortedBy { it.playCount }
					} else {
						artists.sortedByDescending { it.playCount }
					}
				}
				else -> artists
			}
		}
	}
	
	override fun searchArtists(query: String): Flow<List<Artist>> {
		return artistDao.searchArtists(query).map { entities ->
			entities.map { it.toArtistDomain() }
		}
	}
	
	override suspend fun getTotalArtistsCount(): Int {
		return artistDao.getTotalArtistsCount()
	}
	
	override suspend fun updateArtist(artist: Artist) {
		val originalEntity = artistDao.getArtistById(artist.id).firstOrNull()
		val updatedEntity = artist.toEntity(originalEntity = originalEntity)
		artistDao.update(updatedEntity)
	}
	
	override suspend fun toggleFavorite(artistId: Long) {
		artistDao.toggleFavorite(artistId)
	}
	
	override fun getArtistWithDetails(artistId: Long): Flow<ArtistWithDetails?> {
		return combine(
			artistDao.getArtistById(artistId),
			songDao.getSongsByArtist(artistId),
			albumDao.getAlbumsByArtistId(artistId)
		) { artistEntity, songEntities, albumEntities ->
			artistEntity?.let { entity ->
				val artist = entity.toArtistDomain()
				val songs = songEntities.toDomain(defaultArtistName = artist.name)
				val albums = albumEntities.toAlbumDomainList()
				val topSongs = songs.sortedByDescending { it.playCount }.take(10)
				
				ArtistWithDetails(
					artist = artist,
					songs = songs,
					albums = albums,
					topSongs = topSongs
				)
			}
		}
	}
	
	override suspend fun updateSocialLinks(artistId: Long, socialLinks: SocialLinks) {
		val currentEntity = artistDao.getArtistById(artistId).firstOrNull() ?: return
		val currentArtist = currentEntity.toArtistDomain()
		val updatedArtist = currentArtist.copy(socialLinks = socialLinks)
		val updatedEntity = updatedArtist.toEntity(originalEntity = currentEntity)
		artistDao.update(updatedEntity)
	}
}