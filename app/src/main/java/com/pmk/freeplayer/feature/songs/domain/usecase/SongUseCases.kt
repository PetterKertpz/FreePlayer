package com.pmk.freeplayer.feature.songs.domain.usecase

import com.pmk.freeplayer.core.domain.model.Song
import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.feature.songs.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ═════════════════════════════════════════════════════════════════════════════
// QUERIES
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Consultas de canciones.
 *
 * Uso en ViewModel:
 * ```kotlin
 * getSongsUseCase()                          // todas
 * getSongsUseCase(query = "Bohemian")        // búsqueda
 * getSongsUseCase(sortConfig = BY_TITLE)     // ordenadas
 * getSongsUseCase.byId(42L)
 * getSongsUseCase.byAlbum(albumId)
 * getSongsUseCase.byArtist(artistId)
 * getSongsUseCase.byGenre(genreId)
 * getSongsUseCase.favorites()
 * getSongsUseCase.recentlyAdded()
 * getSongsUseCase.mostPlayed()
 * getSongsUseCase.hidden()
 * ```
 */
class GetSongsUseCase @Inject constructor(
	private val repository: SongRepository,
) {
	/** Lista general con filtro y orden opcionales. */
	operator fun invoke(
		query: String? = null,
		sortConfig: SortConfig? = null,
	): Flow<List<Song>> = repository.getSongs(query, sortConfig)
	
	fun byId(id: Long): Flow<Song?> =
		repository.getSongById(id)
	
	fun byIds(ids: List<Long>): Flow<List<Song>> =
		repository.getSongsByIds(ids)
	
	fun byAlbum(albumId: Long): Flow<List<Song>> =
		repository.getSongsByAlbum(albumId)
	
	fun byArtist(artistId: Long): Flow<List<Song>> =
		repository.getSongsByArtist(artistId)
	
	fun byGenre(genreId: Long): Flow<List<Song>> =
		repository.getSongsByGenre(genreId)
	
	fun favorites(): Flow<List<Song>> =
		repository.getFavoriteSongs()
	
	fun recentlyAdded(limit: Int = 20): Flow<List<Song>> =
		repository.getRecentlyAddedSongs(limit)
	
	fun mostPlayed(limit: Int = 20): Flow<List<Song>> =
		repository.getMostPlayedSongs(limit)
	
	/** Canciones ocultas de la biblioteca (no eliminadas). */
	fun hidden(): Flow<List<Song>> =
		repository.getHiddenSongs()
}

// ═════════════════════════════════════════════════════════════════════════════
// MUTATIONS
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Mutaciones de canciones: escritura, visibilidad y favoritos.
 *
 * Uso en ViewModel:
 * ```kotlin
 * manageSongsUseCase.update(song)
 * manageSongsUseCase.hide(songId, hidden = true)
 * manageSongsUseCase.deleteFromDevice(songId)
 * manageSongsUseCase.toggleFavorite(songId)
 * ```
 */
class ManageSongsUseCase @Inject constructor(
	private val repository: SongRepository,
) {
	/** Inserta o reemplaza canciones. Usado internamente por el Scanner. */
	suspend fun insert(songs: List<Song>) =
		repository.insertSongs(songs)
	
	/** Actualiza metadatos en Room y en el archivo físico vía AudioTagger. */
	suspend fun update(song: Song) =
		repository.updateSong(song)
	
	/** Elimina el registro de Room sin tocar el archivo del dispositivo. */
	suspend fun delete(id: Long) =
		repository.deleteSong(id)
	
	suspend fun deleteByIds(ids: List<Long>) =
		repository.deleteSongsByIds(ids)
	
	/**
	 * Elimina el archivo físico del dispositivo vía ContentResolver
	 * y luego elimina el registro de Room.
	 */
	suspend fun deleteFromDevice(id: Long) =
		repository.deleteSongFromDevice(id)
	
	/** Oculta o muestra una canción en la biblioteca sin eliminarla. */
	suspend fun hide(id: Long, hidden: Boolean) =
		repository.hideSong(id, hidden)
	
	suspend fun toggleFavorite(songId: Long) =
		repository.toggleFavoriteSong(songId)
	
	suspend fun setFavorite(songId: Long, isFavorite: Boolean) =
		repository.setFavoriteSong(songId, isFavorite)
}