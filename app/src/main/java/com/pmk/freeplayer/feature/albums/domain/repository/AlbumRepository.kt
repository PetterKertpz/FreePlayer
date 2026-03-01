package com.pmk.freeplayer.feature.albums.domain.repository

import com.pmk.freeplayer.core.domain.model.Song
import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.feature.albums.domain.model.Album
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio del agregado [Album].
 *
 * Fuente de verdad: Room (AlbumDao).
 * Los álbumes se crean automáticamente durante el escaneo de canciones
 * y pueden editarse manualmente desde la UI.
 */
interface AlbumRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// CONSULTAS
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Devuelve todos los álbumes, opcionalmente filtrados por [query]
	 * y ordenados por [sortConfig].
	 */
	fun getAlbums(
		query: String? = null,
		sortConfig: SortConfig? = null,
	): Flow<List<Album>>
	
	fun getAlbumById(id: Long): Flow<Album?>
	
	fun getAlbumsByArtist(artistId: Long): Flow<List<Album>>
	
	fun getRecentlyAddedAlbums(limit: Int = 10): Flow<List<Album>>
	
	fun getMostPlayedAlbums(limit: Int = 10): Flow<List<Album>>
	
	/**
	 * Álbum con su lista completa de canciones.
	 * Usado en la pantalla de detalle.
	 */
	fun getAlbumWithSongs(albumId: Long): Flow<AlbumWithSongs?>
	
	// ═══════════════════════════════════════════════════════════════
	// ESCRITURA
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun updateAlbum(album: Album)
	
	/**
	 * Elimina el registro del álbum en Room.
	 * No elimina las canciones asociadas del dispositivo.
	 */
	suspend fun deleteAlbum(id: Long)
	
	suspend fun addSongToAlbum(songId: Long, albumId: Long)
	
	suspend fun removeSongFromAlbum(songId: Long, albumId: Long)
}

// ─────────────────────────────────────────────────────────────────────────────
// VALUE OBJECTS
// ─────────────────────────────────────────────────────────────────────────────

/** Álbum con su lista de canciones — usado en la pantalla de detalle. */
data class AlbumWithSongs(
	val album: Album,
	val songs: List<Song>,
)