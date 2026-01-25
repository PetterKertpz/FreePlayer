package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Genre
import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.enums.IntegrityStatus
import com.pmk.freeplayer.domain.model.enums.SortConfiguration
import kotlinx.coroutines.flow.Flow

interface SongRepository {

   // ─────────────────────────────────────────────────────────────
   // Get songs
   // ─────────────────────────────────────────────────────────────
   fun getAllSongs(): Flow<List<Song>>

   fun getSongById(id: Long): Flow<Song?>

   fun getSongsByAlbum(albumId: Long): Flow<List<Song>>

   fun getSongsByArtist(artist: String): Flow<List<Song>>

   fun getSongsByGenre(genre: Genre): Flow<List<Song>>

   fun getSongsByFolder(path: String): Flow<List<Song>>

   fun getSortedSongs(sortOrder: SortConfiguration): Flow<List<Song>>

   // ─────────────────────────────────────────────────────────────
   // Search songs
   // ─────────────────────────────────────────────────────────────
   fun searchSongs(query: String): Flow<List<Song>>

   // ─────────────────────────────────────────────────────────────
   // Favorites
   // ─────────────────────────────────────────────────────────────
   fun getFavoriteSongs(): Flow<List<Song>>

   suspend fun markSongAsFavorite(id: Long, isFavorite: Boolean)

   suspend fun toggleSongFavorite(id: Long)

   // ─────────────────────────────────────────────────────────────
   // Playback statistics
   // ─────────────────────────────────────────────────────────────
   fun getMostPlayedSongs(limit: Int = 50): Flow<List<Song>>

   fun getRecentlyPlayedSongs(limit: Int = 50): Flow<List<Song>>

   fun getRecentlyAddedSongs(limit: Int = 50): Flow<List<Song>>

   suspend fun incrementSongPlayCount(id: Long)

   suspend fun updateSongLastPlayed(id: Long, timestamp: Long)

   // ─────────────────────────────────────────────────────────────
   // General song statistics
   // ─────────────────────────────────────────────────────────────
   suspend fun getTotalSongCount(): Int

   // ─────────────────────────────────────────────────────────────
   // State management
   // ─────────────────────────────────────────────────────────────
   fun getSongsByState(state: IntegrityStatus): Flow<List<Song>>

   fun getRawSongs(): Flow<List<Song>>

   fun getCleanSongs(): Flow<List<Song>>

   fun getEnrichedSongs(): Flow<List<Song>>

   suspend fun countSongsByState(state: IntegrityStatus): Int

   // ─────────────────────────────────────────────────────────────
   // State updates
   // ─────────────────────────────────────────────────────────────
   suspend fun updateIntegrityState(id: Long, state: IntegrityStatus)

   suspend fun markSongAsClean(
      id: Long,
      title: String,
      artist: String,
      album: String,
      albumArtist: String?,
      genre: Genre?,
      year: Int?,
      trackNumber: Int?,
   )

   suspend fun markSongAsEnriched(
      id: Long,
      geniusId: Long,
      geniusUrl: String,
      updatedData: Map<String, String>?,
   )

   // ─────────────────────────────────────────────────────────────
   // Scan and sync
   // ─────────────────────────────────────────────────────────────
   suspend fun insertRawSong(song: Song): Long

   suspend fun insertRawSongs(songs: List<Song>): List<Long>

   suspend fun existsSongByHash(hash: String): Boolean

   suspend fun existsSongByPath(path: String): Boolean

   suspend fun getExistingSongHashes(): Set<String>

   suspend fun deleteSongByPath(path: String)

   suspend fun deleteNonExistentSongs(currentPaths: Set<String>): Int
}
