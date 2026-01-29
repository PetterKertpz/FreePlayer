package com.pmk.freeplayer.data.repository

import com.pmk.freeplayer.data.local.dao.AlbumDao
import com.pmk.freeplayer.data.local.dao.GenreDao
import com.pmk.freeplayer.data.local.dao.SongDao
import com.pmk.freeplayer.data.mapper.toAlbumDomainList
import com.pmk.freeplayer.data.mapper.toDomain
import com.pmk.freeplayer.data.mapper.toEntity
import com.pmk.freeplayer.domain.model.Album
import com.pmk.freeplayer.domain.model.Genre
import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.enums.SortConfig
import com.pmk.freeplayer.domain.model.enums.SortDirection
import com.pmk.freeplayer.domain.model.enums.SortField
import com.pmk.freeplayer.domain.repository.AlbumWithSongs
import com.pmk.freeplayer.domain.repository.GenreWithCount
import com.pmk.freeplayer.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LibraryRepositoryImpl @Inject constructor(
	private val songDao: SongDao,
	private val albumDao: AlbumDao,
	private val genreDao: GenreDao
) : LibraryRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// SONGS
	// ═══════════════════════════════════════════════════════════════
	
	override fun getAllSongs(): Flow<List<Song>> {
		return songDao.getAllSongs().map { entities ->
			entities.toDomain()
		}
	}
	
	override fun getSongById(id: Long): Flow<Song?> {
		return songDao.getSongByIdFlow(id).map { entity ->
			entity?.toDomain()
		}
	}
	
	override fun getSongsByIds(ids: List<Long>): Flow<List<Song>> {
		return getAllSongs().map { songs ->
			songs.filter { it.id in ids }
		}
	}
	
	override fun getSortedSongs(sortConfig: SortConfig): Flow<List<Song>> {
		return getAllSongs().map { songs ->
			when (sortConfig.field) {
				SortField.NAME -> {
					if (sortConfig.direction == SortDirection.ASCENDING) {
						songs.sortedBy { it.title }
					} else {
						songs.sortedByDescending { it.title }
					}
				}
				SortField.ARTIST_NAME -> {
					if (sortConfig.direction == SortDirection.ASCENDING) {
						songs.sortedBy { it.artistName }
					} else {
						songs.sortedByDescending { it.artistName }
					}
				}
				SortField.DURATION -> {
					if (sortConfig.direction == SortDirection.ASCENDING) {
						songs.sortedBy { it.duration.millis }
					} else {
						songs.sortedByDescending { it.duration.millis }
					}
				}
				SortField.DATE_ADDED -> {
					if (sortConfig.direction == SortDirection.ASCENDING) {
						songs.sortedBy { it.dateAdded }
					} else {
						songs.sortedByDescending { it.dateAdded }
					}
				}
				SortField.YEAR -> {
					if (sortConfig.direction == SortDirection.ASCENDING) {
						songs.sortedBy { it.year ?: Int.MAX_VALUE }
					} else {
						songs.sortedByDescending { it.year ?: Int.MIN_VALUE }
					}
				}
				SortField.PLAY_COUNT -> {
					if (sortConfig.direction == SortDirection.ASCENDING) {
						songs.sortedBy { it.playCount }
					} else {
						songs.sortedByDescending { it.playCount }
					}
				}
				SortField.SIZE -> {
					if (sortConfig.direction == SortDirection.ASCENDING) {
						songs.sortedBy { it.size.bytes }
					} else {
						songs.sortedByDescending { it.size.bytes }
					}
				}
				else -> songs
			}
		}
	}
	
	override fun searchSongs(query: String): Flow<List<Song>> {
		return songDao.searchSongs(query).map { entities ->
			entities.toDomain()
		}
	}
	
	override suspend fun insertSongs(songs: List<Song>) {
		val entities = songs.map { it.toEntity() }
		songDao.insertAll(entities)
	}
	
	override suspend fun updateSong(song: Song) {
		val entity = song.toEntity()
		songDao.update(entity)
	}
	
	override suspend fun deleteSong(id: Long) {
		songDao.deleteById(id)
	}
	
	override suspend fun deleteSongsByIds(ids: List<Long>) {
		ids.forEach { songDao.deleteById(it) }
	}
	
	override fun getSongsByAlbum(albumId: Long): Flow<List<Song>> {
		return songDao.getSongsByAlbum(albumId).map { entities ->
			entities.toDomain()
		}
	}
	
	override fun getSongsByArtist(artistId: Long): Flow<List<Song>> {
		return songDao.getSongsByArtist(artistId).map { entities ->
			entities.toDomain()
		}
	}
	
	override fun getSongsByGenre(genreId: Long): Flow<List<Song>> {
		return songDao.getSongsByGenre(genreId).map { entities ->
			entities.toDomain()
		}
	}
	
	// ═══════════════════════════════════════════════════════════════
	// FAVORITES
	// ═══════════════════════════════════════════════════════════════
	
	override fun getFavoriteSongs(): Flow<List<Song>> {
		return songDao.getFavoriteSongs().map { entities ->
			entities.toDomain()
		}
	}
	
	override suspend fun toggleFavorite(songId: Long) {
		val currentSong = songDao.getSongById(songId) ?: return
		songDao.setFavorite(songId, !currentSong.isFavorite)
	}
	
	override suspend fun setFavorite(songId: Long, isFavorite: Boolean) {
		songDao.setFavorite(songId, isFavorite)
	}
	
	// ═══════════════════════════════════════════════════════════════
	// ALBUMS
	// ═══════════════════════════════════════════════════════════════
	
	override fun getAllAlbums(): Flow<List<Album>> {
		return albumDao.getAllAlbums().map { entities ->
			entities.toAlbumDomainList()
		}
	}
	
	override fun getAlbumById(id: Long): Flow<Album?> {
		return albumDao.getAlbumById(id).map { entity ->
			entity?.toDomain()
		}
	}
	
	override fun getSortedAlbums(sortConfig: SortConfig): Flow<List<Album>> {
		return when (sortConfig.field) {
			SortField.NAME -> {
				if (sortConfig.direction == SortDirection.ASCENDING) {
					albumDao.getAllSortedByTitleAsc()
				} else {
					albumDao.getAllSortedByTitleDesc()
				}
			}
			SortField.ARTIST_NAME -> {
				if (sortConfig.direction == SortDirection.ASCENDING) {
					albumDao.getAllSortedByArtistAsc()
				} else {
					albumDao.getAllSortedByArtistDesc()
				}
			}
			SortField.YEAR -> {
				if (sortConfig.direction == SortDirection.ASCENDING) {
					albumDao.getAllSortedByYearAsc()
				} else {
					albumDao.getAllSortedByYearDesc()
				}
			}
			SortField.DATE_ADDED -> {
				if (sortConfig.direction == SortDirection.ASCENDING) {
					albumDao.getAllSortedByDateAddedAsc()
				} else {
					albumDao.getAllSortedByDateAddedDesc()
				}
			}
			SortField.PLAY_COUNT -> {
				if (sortConfig.direction == SortDirection.ASCENDING) {
					albumDao.getAllSortedByPlayCountAsc()
				} else {
					albumDao.getAllSortedByPlayCountDesc()
				}
			}
			SortField.DURATION -> {
				if (sortConfig.direction == SortDirection.ASCENDING) {
					albumDao.getAllSortedByDurationAsc()
				} else {
					albumDao.getAllSortedByDurationDesc()
				}
			}
			else -> albumDao.getAllAlbums()
		}.map { entities ->
			entities.toAlbumDomainList()
		}
	}
	
	override fun searchAlbums(query: String): Flow<List<Album>> {
		return albumDao.searchAlbums(query).map { entities ->
			entities.toAlbumDomainList()
		}
	}
	
	override fun getAlbumsByArtist(artistId: Long): Flow<List<Album>> {
		return albumDao.getAlbumsByArtistId(artistId).map { entities ->
			entities.toAlbumDomainList()
		}
	}
	
	override fun getAlbumWithSongs(albumId: Long): Flow<AlbumWithSongs?> {
		return combine(
			albumDao.getAlbumById(albumId),
			songDao.getSongsByAlbum(albumId)
		) { albumEntity, songEntities ->
			albumEntity?.let { entity ->
				val album = entity.toDomain()
				val songs = songEntities.toDomain(defaultArtistName = album.artistName)
				AlbumWithSongs(
					album = album,
					songs = songs
				)
			}
		}
	}
	
	// ═══════════════════════════════════════════════════════════════
	// GENRES
	// ═══════════════════════════════════════════════════════════════
	
	override fun getAllGenres(): Flow<List<Genre>> {
		return genreDao.getAllGenres().map { entities ->
			entities.toDomain()
		}
	}
	
	override fun getGenreById(id: Long): Flow<Genre?> {
		return genreDao.getGenreById(id).map { entity ->
			entity?.toDomain()
		}
	}
	
	override fun getGenresWithSongCount(): Flow<List<GenreWithCount>> {
		return genreDao.getAllGenres().map { entities ->
			entities.map { entity ->
				GenreWithCount(
					genre = entity.toDomain(),
					songCount = entity.songCount
				)
			}
		}
	}
	
	// ═══════════════════════════════════════════════════════════════
	// RECENTLY ADDED
	// ═══════════════════════════════════════════════════════════════
	
	override fun getRecentlyAddedSongs(limit: Int): Flow<List<Song>> {
		return songDao.getRecentSongs(limit).map { entities ->
			entities.toDomain()
		}
	}
	
	override fun getRecentlyAddedAlbums(limit: Int): Flow<List<Album>> {
		return albumDao.getAllSortedByDateAddedDesc().map { entities ->
			entities.toAlbumDomainList().take(limit)
		}
	}
	
	// ═══════════════════════════════════════════════════════════════
	// MOST PLAYED
	// ═══════════════════════════════════════════════════════════════
	
	override fun getMostPlayedSongs(limit: Int): Flow<List<Song>> {
		return songDao.getMostPlayedSongs(limit).map { entities ->
			entities.toDomain()
		}
	}
	
	override fun getMostPlayedAlbums(limit: Int): Flow<List<Album>> {
		return albumDao.getAllSortedByPlayCountDesc().map { entities ->
			entities.toAlbumDomainList().take(limit)
		}
	}
}