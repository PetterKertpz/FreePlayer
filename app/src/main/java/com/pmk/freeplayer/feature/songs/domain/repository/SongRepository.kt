package com.pmk.freeplayer.feature.songs.domain.repository

import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.feature.songs.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {

   // ── Queries ───────────────────────────────────────────────────

   fun getSongs(query: String? = null, sortConfig: SortConfig? = null): Flow<List<Song>>

   fun getSongById(id: Long): Flow<Song?>

   fun getSongsByIds(ids: List<Long>): Flow<List<Song>>

   fun getSongsByAlbum(albumId: Long): Flow<List<Song>>

   fun getSongsByArtist(artistId: Long): Flow<List<Song>>

   fun getSongsByGenre(genreId: Long): Flow<List<Song>>

   fun getFavoriteSongs(): Flow<List<Song>>

   fun getRecentlyAdded(limit: Int = 20): Flow<List<Song>>

   fun getHiddenSongs(): Flow<List<Song>>

   // ── Writes ────────────────────────────────────────────────────

   /** Bulk insert — used exclusively by the Scanner. */
   suspend fun insertSongs(songs: List<Song>): List<Long>

   /**
    * Updates domain fields in Room AND writes metadata to the physical file. Throws
    * [IllegalStateException] if the file write fails (Room is not updated).
    */
   suspend fun updateSong(song: Song)

   suspend fun deleteSong(id: Long)

   suspend fun deleteSongsByIds(ids: List<Long>)

   /** Deletes the physical file via ContentResolver, then removes the Room record. */
   suspend fun deleteSongFromDevice(id: Long)

   suspend fun hideSong(id: Long, hidden: Boolean)

   suspend fun toggleFavoriteSong(songId: Long)

   suspend fun setFavoriteSong(songId: Long, isFavorite: Boolean)
}
