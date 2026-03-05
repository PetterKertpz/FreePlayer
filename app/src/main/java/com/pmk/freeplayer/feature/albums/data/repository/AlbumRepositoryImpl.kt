package com.pmk.freeplayer.feature.albums.data.repository

import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.core.domain.model.enums.SortDirection
import com.pmk.freeplayer.core.domain.model.enums.SortField
import com.pmk.freeplayer.feature.albums.data.local.dao.AlbumDao
import com.pmk.freeplayer.feature.albums.data.mapper.toDomain
import com.pmk.freeplayer.feature.albums.data.mapper.toEntity
import com.pmk.freeplayer.feature.albums.domain.model.Album
import com.pmk.freeplayer.feature.albums.domain.repository.AlbumRepository
import com.pmk.freeplayer.feature.albums.domain.repository.AlbumWithSongs
import com.pmk.freeplayer.feature.songs.data.local.dao.SongDao
import com.pmk.freeplayer.feature.songs.data.mapper.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class AlbumRepositoryImpl @Inject constructor(
	private val albumDao: AlbumDao,
	private val songDao: SongDao,
	@Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : AlbumRepository {
	
	// ── Queries ───────────────────────────────────────────────────
	
	override fun getAlbums(query: String?, sortConfig: SortConfig?): Flow<List<Album>> {
		val base: Flow<List<Album>> = if (!query.isNullOrBlank()) {
			albumDao.search(query).map { it.toDomain() }
		} else {
			albumDao.getAll().map { it.toDomain() }
		}
		return if (sortConfig != null) base.map { it.applySortConfig(sortConfig) } else base
	}
	
	override fun getAlbumById(id: Long): Flow<Album?> =
		albumDao.getByIdFlow(id).map { it?.toDomain() }
	
	override fun getAlbumsByArtist(artistId: Long): Flow<List<Album>> =
		albumDao.getByArtist(artistId).map { it.toDomain() }
	
	override fun getAlbumWithSongs(albumId: Long): Flow<AlbumWithSongs?> =
		combine(
			albumDao.getByIdFlow(albumId),
			songDao.getByAlbum(albumId),
		) { albumEntity, songEntities ->
			albumEntity?.let {
				AlbumWithSongs(
					album = it.toDomain(),
					songs = songEntities.toDomain(),
				)
			}
		}
	
	override fun getRecentlyAdded(limit: Int): Flow<List<Album>> =
		albumDao.getRecentlyAdded(limit).map { it.toDomain() }
	
	override suspend fun count(): Int = albumDao.count()
	
	// ── Writes ────────────────────────────────────────────────────
	
	override suspend fun updateAlbum(album: Album) = withContext(ioDispatcher) {
		val original = requireNotNull(albumDao.getById(album.id)) {
			"Album not found: id=${album.id}"
		}
		albumDao.update(album.toEntity(original = original, now = System.currentTimeMillis()))
	}
	
	override suspend fun deleteAlbum(id: Long) = withContext(ioDispatcher) {
		albumDao.deleteById(id)
	}
	
	override suspend fun toggleFavorite(albumId: Long) = withContext(ioDispatcher) {
		albumDao.toggleFavorite(albumId)
	}
	
	override suspend fun setRating(albumId: Long, rating: Float) = withContext(ioDispatcher) {
		albumDao.setRating(albumId, rating)
	}
	
	// ── Private helpers ───────────────────────────────────────────
	
	private fun List<Album>.applySortConfig(config: SortConfig): List<Album> {
		val comparator: Comparator<Album> = when (config.field) {
			SortField.NAME        -> compareBy { it.title.lowercase() }
			SortField.ARTIST_NAME -> compareBy { it.artistName.lowercase() }
			SortField.YEAR        -> compareBy { it.year ?: 0 }
			SortField.DATE_ADDED  -> compareBy { it.dateAdded }
			SortField.DURATION    -> compareBy { it.totalDuration.millis }
			SortField.SONG_COUNT  -> compareBy { it.songCount }
			else                  -> compareBy { it.title.lowercase() }
		}
		return if (config.direction == SortDirection.DESCENDING) sortedWith(comparator.reversed())
		else sortedWith(comparator)
	}
}