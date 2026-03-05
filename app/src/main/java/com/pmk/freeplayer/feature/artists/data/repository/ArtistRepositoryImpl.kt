package com.pmk.freeplayer.feature.artists.data.repository

import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.core.domain.model.enums.SortDirection
import com.pmk.freeplayer.core.domain.model.enums.SortField
import com.pmk.freeplayer.feature.albums.data.local.dao.AlbumDao
import com.pmk.freeplayer.feature.albums.data.mapper.toDomain
import com.pmk.freeplayer.feature.artists.data.local.dao.ArtistDao
import com.pmk.freeplayer.feature.artists.data.mapper.toDomain
import com.pmk.freeplayer.feature.artists.data.mapper.toEntity
import com.pmk.freeplayer.feature.artists.domain.model.Artist
import com.pmk.freeplayer.feature.artists.domain.model.SocialLinks
import com.pmk.freeplayer.feature.artists.domain.repository.ArtistRepository
import com.pmk.freeplayer.feature.artists.domain.repository.ArtistWithDetails
import com.pmk.freeplayer.feature.songs.data.local.dao.SongDao
import com.pmk.freeplayer.feature.songs.data.mapper.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class ArtistRepositoryImpl @Inject constructor(
	private val artistDao: ArtistDao,
	private val albumDao: AlbumDao,
	private val songDao: SongDao,
	@Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : ArtistRepository {
	
	// ── Queries ───────────────────────────────────────────────────
	
	override fun getArtists(query: String?, sortConfig: SortConfig?): Flow<List<Artist>> {
		val base: Flow<List<Artist>> = if (!query.isNullOrBlank()) {
			artistDao.search(query).map { it.toDomain() }
		} else {
			artistDao.getAll().map { it.toDomain() }
		}
		return if (sortConfig != null) base.map { it.applySortConfig(sortConfig) } else base
	}
	
	override fun getArtistById(id: Long): Flow<Artist?> =
		artistDao.getByIdFlow(id).map { it?.toDomain() }
	
	override fun getArtistWithDetails(artistId: Long): Flow<ArtistWithDetails?> =
		combine(
			artistDao.getByIdFlow(artistId),
			songDao.getByArtist(artistId),
			albumDao.getByArtist(artistId),
		) { artistEntity, songEntities, albumEntities ->
			artistEntity?.let {
				ArtistWithDetails(
					artist = it.toDomain(),
					songs  = songEntities.toDomain(),
					albums = albumEntities.toDomain(),
				)
			}
		}
	
	override suspend fun count(): Int = artistDao.count()
	
	// ── Writes ────────────────────────────────────────────────────
	
	override suspend fun updateArtist(artist: Artist) = withContext(ioDispatcher) {
		val original = requireNotNull(artistDao.getById(artist.id)) {
			"Artist not found: id=${artist.id}"
		}
		artistDao.update(artist.toEntity(original = original, now = System.currentTimeMillis()))
	}
	
	override suspend fun deleteArtist(id: Long) = withContext(ioDispatcher) {
		artistDao.deleteById(id)
	}
	
	override suspend fun toggleFavoriteArtist(artistId: Long) = withContext(ioDispatcher) {
		artistDao.toggleFavorite(artistId)
	}
	
	override suspend fun updateSocialLinks(artistId: Long, socialLinks: SocialLinks) =
		withContext(ioDispatcher) {
			val original = requireNotNull(artistDao.getById(artistId)) {
				"Artist not found: id=$artistId"
			}
			val updated = original.toDomain().copy(socialLinks = socialLinks)
			artistDao.update(updated.toEntity(original = original, now = System.currentTimeMillis()))
		}
	
	// ── Private helpers ───────────────────────────────────────────
	
	private fun List<Artist>.applySortConfig(config: SortConfig): List<Artist> {
		val comparator: Comparator<Artist> = when (config.field) {
			SortField.NAME        -> compareBy { it.name.lowercase() }
			SortField.ALBUM_COUNT -> compareBy { it.albumCount }
			SortField.SONG_COUNT  -> compareBy { it.songCount }
			else                  -> compareBy { it.name.lowercase() }
		}
		return if (config.direction == SortDirection.DESCENDING) sortedWith(comparator.reversed())
		else sortedWith(comparator)
	}
}