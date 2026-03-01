package com.pmk.freeplayer.feature.albums.domain.usecase

import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.feature.albums.domain.model.Album
import com.pmk.freeplayer.feature.albums.domain.repository.AlbumRepository
import com.pmk.freeplayer.feature.albums.domain.repository.AlbumWithSongs
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ═════════════════════════════════════════════════════════════════════════════
// QUERIES
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Consultas de álbumes.
 *
 * Uso en ViewModel:
 * ```kotlin
 * getAlbumsUseCase()
 * getAlbumsUseCase(query = "Abbey Road")
 * getAlbumsUseCase.byId(1L)
 * getAlbumsUseCase.byArtist(artistId)
 * getAlbumsUseCase.withSongs(albumId)
 * getAlbumsUseCase.recentlyAdded()
 * getAlbumsUseCase.mostPlayed()
 * ```
 */
class GetAlbumsUseCase @Inject constructor(
	private val repository: AlbumRepository,
) {
	operator fun invoke(
		query: String? = null,
		sortConfig: SortConfig? = null,
	): Flow<List<Album>> = repository.getAlbums(query, sortConfig)
	
	fun byId(id: Long): Flow<Album?> =
		repository.getAlbumById(id)
	
	fun byArtist(artistId: Long): Flow<List<Album>> =
		repository.getAlbumsByArtist(artistId)
	
	/** Álbum con su lista completa de canciones — para pantalla de detalle. */
	fun withSongs(albumId: Long): Flow<AlbumWithSongs?> =
		repository.getAlbumWithSongs(albumId)
	
	fun recentlyAdded(limit: Int = 10): Flow<List<Album>> =
		repository.getRecentlyAddedAlbums(limit)
	
	fun mostPlayed(limit: Int = 10): Flow<List<Album>> =
		repository.getMostPlayedAlbums(limit)
}

// ═════════════════════════════════════════════════════════════════════════════
// MUTATIONS
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Mutaciones de álbumes: edición de metadatos, canciones y eliminación.
 *
 * Uso en ViewModel:
 * ```kotlin
 * manageAlbumsUseCase.update(album)
 * manageAlbumsUseCase.delete(albumId)
 * manageAlbumsUseCase.addSong(songId, albumId)
 * manageAlbumsUseCase.removeSong(songId, albumId)
 * ```
 */
class ManageAlbumsUseCase @Inject constructor(
	private val repository: AlbumRepository,
) {
	/** Actualiza metadatos del álbum (título, portada, año, artista). */
	suspend fun update(album: Album) =
		repository.updateAlbum(album)
	
	/**
	 * Elimina el registro del álbum en Room.
	 * No elimina las canciones asociadas del dispositivo.
	 */
	suspend fun delete(id: Long) =
		repository.deleteAlbum(id)
	
	suspend fun addSong(songId: Long, albumId: Long) =
		repository.addSongToAlbum(songId, albumId)
	
	suspend fun removeSong(songId: Long, albumId: Long) =
		repository.removeSongFromAlbum(songId, albumId)
}