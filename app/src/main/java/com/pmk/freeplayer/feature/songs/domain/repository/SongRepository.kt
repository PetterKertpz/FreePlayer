package com.pmk.freeplayer.feature.songs.domain.repository

import com.pmk.freeplayer.core.domain.model.Song
import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio del agregado [Song].
 *
 * Fuente de verdad: Room (SongDao) sincronizado desde MediaStore.
 * Toda escritura de metadatos al archivo físico se delega a AudioTagger
 * desde la implementación en la capa data.
 */
interface SongRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// CONSULTAS
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Devuelve todas las canciones visibles (no ocultas), opcionalmente
	 * filtradas por [query] y ordenadas por [sortConfig].
	 */
	fun getSongs(
		query: String? = null,
		sortConfig: SortConfig? = null,
	): Flow<List<Song>>
	
	fun getSongById(id: Long): Flow<Song?>
	
	fun getSongsByIds(ids: List<Long>): Flow<List<Song>>
	
	fun getSongsByAlbum(albumId: Long): Flow<List<Song>>
	
	fun getSongsByArtist(artistId: Long): Flow<List<Song>>
	
	fun getSongsByGenre(genreId: Long): Flow<List<Song>>
	
	fun getFavoriteSongs(): Flow<List<Song>>
	
	fun getRecentlyAddedSongs(limit: Int = 20): Flow<List<Song>>
	
	fun getMostPlayedSongs(limit: Int = 20): Flow<List<Song>>
	
	fun getHiddenSongs(): Flow<List<Song>>
	
	// ═══════════════════════════════════════════════════════════════
	// ESCRITURA
	// ═══════════════════════════════════════════════════════════════
	
	/** Inserta o reemplaza canciones (usado por el Scanner). */
	suspend fun insertSongs(songs: List<Song>)
	
	/** Actualiza metadatos de una canción en Room y en el archivo físico. */
	suspend fun updateSong(song: Song)
	
	/** Elimina el registro de Room. No borra el archivo del dispositivo. */
	suspend fun deleteSong(id: Long)
	
	suspend fun deleteSongsByIds(ids: List<Long>)
	
	/**
	 * Elimina el archivo físico del dispositivo mediante ContentResolver
	 * y luego elimina el registro de Room.
	 */
	suspend fun deleteSongFromDevice(id: Long)
	
	// ═══════════════════════════════════════════════════════════════
	// VISIBILIDAD
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun hideSong(id: Long, hidden: Boolean)
	
	// ═══════════════════════════════════════════════════════════════
	// FAVORITOS
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun toggleFavoriteSong(songId: Long)
	
	suspend fun setFavoriteSong(songId: Long, isFavorite: Boolean)
}