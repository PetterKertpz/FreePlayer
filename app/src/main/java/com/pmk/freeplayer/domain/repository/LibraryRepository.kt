package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Album
import com.pmk.freeplayer.domain.model.Genre
import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.enums.SortConfig
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// SONGS
	// ═══════════════════════════════════════════════════════════════
	
	fun getAllSongs(): Flow<List<Song>>
	fun getSongById(id: Long): Flow<Song?>
	fun getSongsByIds(ids: List<Long>): Flow<List<Song>>
	fun getSortedSongs(sortConfig: SortConfig): Flow<List<Song>>
	fun searchSongs(query: String): Flow<List<Song>>
	
	suspend fun insertSongs(songs: List<Song>)
	suspend fun updateSong(song: Song)
	suspend fun deleteSong(id: Long)
	suspend fun deleteSongsByIds(ids: List<Long>)
	
	// Songs by relation
	fun getSongsByAlbum(albumId: Long): Flow<List<Song>>
	fun getSongsByArtist(artistId: Long): Flow<List<Song>>
	fun getSongsByGenre(genreId: Long): Flow<List<Song>>
	
	// ═══════════════════════════════════════════════════════════════
	// FAVORITES
	// ═══════════════════════════════════════════════════════════════
	
	fun getFavoriteSongs(): Flow<List<Song>>
	suspend fun toggleFavorite(songId: Long)
	suspend fun setFavorite(songId: Long, isFavorite: Boolean)
	
	// ═══════════════════════════════════════════════════════════════
	// ALBUMS
	// ═══════════════════════════════════════════════════════════════
	
	fun getAllAlbums(): Flow<List<Album>>
	fun getAlbumById(id: Long): Flow<Album?>
	fun getSortedAlbums(sortConfig: SortConfig): Flow<List<Album>>
	fun searchAlbums(query: String): Flow<List<Album>>
	fun getAlbumsByArtist(artistId: Long): Flow<List<Album>>
	
	fun getAlbumWithSongs(albumId: Long): Flow<AlbumWithSongs?>
	
	// ═══════════════════════════════════════════════════════════════
	// GENRES
	// ═══════════════════════════════════════════════════════════════
	
	fun getAllGenres(): Flow<List<Genre>>
	fun getGenreById(id: Long): Flow<Genre?>
	fun getGenresWithSongCount(): Flow<List<GenreWithCount>>
	
	// ═══════════════════════════════════════════════════════════════
	// RECENTLY ADDED
	// ═══════════════════════════════════════════════════════════════
	
	fun getRecentlyAddedSongs(limit: Int = 20): Flow<List<Song>>
	fun getRecentlyAddedAlbums(limit: Int = 10): Flow<List<Album>>
	
	// ═══════════════════════════════════════════════════════════════
	// MOST PLAYED
	// ═══════════════════════════════════════════════════════════════
	
	fun getMostPlayedSongs(limit: Int = 20): Flow<List<Song>>
	fun getMostPlayedAlbums(limit: Int = 10): Flow<List<Album>>
	
}

/**
 * Album with its songs - for detail screens
 */
data class AlbumWithSongs(
	val album: Album,
	val songs: List<Song>
)

/**
 * Genre with song count - for list displays
 */
data class GenreWithCount(
	val genre: Genre,
	val songCount: Int
)