package com.pmk.freeplayer.feature.albums.domain.repository

import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.feature.albums.domain.model.Album
import com.pmk.freeplayer.feature.songs.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
	
	// ── Queries ───────────────────────────────────────────────────
	
	fun getAlbums(query: String? = null, sortConfig: SortConfig? = null): Flow<List<Album>>
	fun getAlbumById(id: Long): Flow<Album?>
	fun getAlbumsByArtist(artistId: Long): Flow<List<Album>>
	fun getAlbumWithSongs(albumId: Long): Flow<AlbumWithSongs?>
	fun getRecentlyAdded(limit: Int = 10): Flow<List<Album>>
	suspend fun count(): Int
	
	// ── Writes ────────────────────────────────────────────────────
	
	suspend fun updateAlbum(album: Album)
	suspend fun deleteAlbum(id: Long)
	suspend fun toggleFavorite(albumId: Long)
	suspend fun setRating(albumId: Long, rating: Float)
}

/** Album with its full song list — used on the detail screen. */
data class AlbumWithSongs(
	val album: Album,
	val songs: List<Song>,
)