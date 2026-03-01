package com.pmk.freeplayer.feature.artists.domain.repository

import com.pmk.freeplayer.core.domain.model.Song
import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.feature.albums.domain.model.Album
import com.pmk.freeplayer.feature.artists.domain.model.Artist
import com.pmk.freeplayer.feature.artists.domain.model.SocialLinks
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio del agregado [Artist].
 *
 * Fuente de verdad: Room (ArtistDao).
 * Los artistas se crean automáticamente desde los metadatos de las canciones
 * durante el escaneo. Pueden enriquecerse vía API (Genius, MusicBrainz).
 */
interface ArtistRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// CONSULTAS
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Devuelve todos los artistas, opcionalmente filtrados por [query]
	 * y ordenados por [sortConfig].
	 */
	fun getArtists(
		query: String? = null,
		sortConfig: SortConfig? = null,
	): Flow<List<Artist>>
	
	fun getArtistById(id: Long): Flow<Artist?>
	
	/**
	 * Artista con canciones, álbumes y top canciones más reproducidas.
	 * Usado en la pantalla de detalle.
	 */
	fun getArtistWithDetails(artistId: Long): Flow<ArtistWithDetails?>
	
	suspend fun getTotalArtistsCount(): Int
	
	// ═══════════════════════════════════════════════════════════════
	// ESCRITURA
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun updateArtist(artist: Artist)
	
	/**
	 * Elimina el registro del artista en Room.
	 * No elimina las canciones asociadas del dispositivo.
	 */
	suspend fun deleteArtist(id: Long)
	
	suspend fun addSongToArtist(songId: Long, artistId: Long)
	
	suspend fun removeSongFromArtist(songId: Long, artistId: Long)
	
	suspend fun toggleFavoriteArtist(artistId: Long)
	
	suspend fun updateSocialLinks(artistId: Long, socialLinks: SocialLinks)
}

// ─────────────────────────────────────────────────────────────────────────────
// VALUE OBJECTS
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Artista completo con canciones, álbumes y top reproducidas.
 * Usado en la pantalla de detalle.
 */
data class ArtistWithDetails(
	val artist: Artist,
	val songs: List<Song>,
	val albums: List<Album>,
	/** Las canciones más reproducidas del artista. */
	val topSongs: List<Song>,
)