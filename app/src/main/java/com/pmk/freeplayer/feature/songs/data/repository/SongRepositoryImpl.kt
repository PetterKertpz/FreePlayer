package com.pmk.freeplayer.feature.songs.data.repository

import com.pmk.freeplayer.app.di.IoDispatcher
import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.core.domain.model.enums.SortDirection
import com.pmk.freeplayer.core.domain.model.enums.SortField
import com.pmk.freeplayer.feature.songs.data.local.dao.SongDao
import com.pmk.freeplayer.feature.songs.data.mapper.toDomain
import com.pmk.freeplayer.feature.songs.data.mapper.toEntity
import com.pmk.freeplayer.feature.songs.data.mediasource.AudioFileDataSource
import com.pmk.freeplayer.feature.songs.domain.model.Song
import com.pmk.freeplayer.feature.songs.domain.repository.SongRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SongRepositoryImpl
@Inject
constructor(
	private val songDao: SongDao,
	private val audioFileDataSource: AudioFileDataSource,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : SongRepository {

   // ── Queries ───────────────────────────────────────────────────

   override fun getSongs(query: String?, sortConfig: SortConfig?): Flow<List<Song>> {
      val base =
         if (!query.isNullOrBlank()) {
            songDao.search(query).map { it.toDomain() }
         } else {
            songDao.getAll().map { it.toDomain() }
         }
      return if (sortConfig != null) base.map { it.applySortConfig(sortConfig) } else base
   }

   override fun getSongById(id: Long): Flow<Song?> = songDao.getByIdFlow(id).map { it?.toDomain() }

   override fun getSongsByIds(ids: List<Long>): Flow<List<Song>> =
      songDao.getByIds(ids).map {
         it.toDomain()
      } // FIX: was loading all songs and filtering in-memory

   override fun getSongsByAlbum(albumId: Long): Flow<List<Song>> =
      songDao.getByAlbum(albumId).map { it.toDomain() }

   override fun getSongsByArtist(artistId: Long): Flow<List<Song>> =
      songDao.getByArtist(artistId).map { it.toDomain() }

   override fun getSongsByGenre(genreId: Long): Flow<List<Song>> =
      songDao.getByGenre(genreId).map { it.toDomain() }

   override fun getFavoriteSongs(): Flow<List<Song>> = songDao.getFavorites().map { it.toDomain() }

   override fun getRecentlyAdded(limit: Int): Flow<List<Song>> =
      songDao.getRecentlyAdded(limit).map { it.toDomain() }

   override fun getHiddenSongs(): Flow<List<Song>> = songDao.getHidden().map { it.toDomain() }

   // ── Writes ────────────────────────────────────────────────────

   override suspend fun insertSongs(songs: List<Song>): List<Long> =
      withContext(ioDispatcher) {
         songDao.insertAll(
            songs.map { it.toEntity() }
         ) // FIX: was discarding the returned List<Long>
      }

   override suspend fun updateSong(song: Song) =
      withContext(ioDispatcher) {
         // Fail-fast: if the file write fails, Room is never touched.
         check(audioFileDataSource.writeMetadataToFile(song)) {
            "Metadata write failed for: ${song.filePath}"
         }
         // Read-modify-write: preserve data-layer-only fields (fileHash, geniusId, hasCover…).
         val existing = requireNotNull(songDao.getById(song.id)) { "Song not found: id=${song.id}" }
         songDao.update(
            existing.copy(
               title = song.title,
               artistName = song.artistName,
               trackNumber = song.trackNumber,
               discNumber = song.discNumber,
               year = song.year,
               originalTitle = song.originalTitle,
               originalArtist = song.originalArtist,
               versionType = song.versionType,
               sourceType = song.sourceType,
               durationMs = song.duration.millis,
               sizeBytes = song.size.bytes,
               mimeType = song.mimeType,
               bitrate = song.bitrate,
               sampleRate = song.sampleRate,
               audioQuality = song.audioQuality,
               featuringArtists = song.featuringArtists.ifEmpty { null },
               geniusUrl = song.geniusUrl,
               externalIds = song.externalIds,
               isFavorite = song.isFavorite,
               rating = song.rating,
               hasLyrics = song.hasLyrics,
               metadataStatus = song.metadataStatus,
               dateModified = System.currentTimeMillis(),
            )
         )
      }

   override suspend fun deleteSong(id: Long) = withContext(ioDispatcher) { songDao.deleteById(id) }

   override suspend fun deleteSongsByIds(ids: List<Long>) =
      withContext(ioDispatcher) {
         songDao.deleteByIds(ids) // FIX: was O(N) scan + N getScanInfoByPath calls
      }

   override suspend fun deleteSongFromDevice(id: Long) =
      withContext(ioDispatcher) {
         val scan = songDao.getAllScanInfo().find { it.songId == id } ?: return@withContext
         if (audioFileDataSource.deleteFileFromDevice(scan.filePath)) {
            songDao.deleteById(id)
         }
      }

   override suspend fun hideSong(id: Long, hidden: Boolean) =
      withContext(ioDispatcher) { songDao.setHidden(id, hidden) }

   override suspend fun toggleFavoriteSong(songId: Long) =
      withContext(ioDispatcher) { songDao.toggleFavorite(songId) }

   override suspend fun setFavoriteSong(songId: Long, isFavorite: Boolean) =
      withContext(ioDispatcher) { songDao.setFavorite(songId, isFavorite) }

   // ── Private helpers ───────────────────────────────────────────

   private fun List<Song>.applySortConfig(config: SortConfig): List<Song> {
      val comparator: Comparator<Song> =
         when (config.field) {
            SortField.NAME -> compareBy { it.title.lowercase() }
            SortField.ARTIST_NAME -> compareBy { it.artistName.lowercase() }
            SortField.DURATION -> compareBy { it.duration.millis }
            SortField.DATE_ADDED -> compareBy { it.dateAdded }
            SortField.YEAR -> compareBy { it.year ?: 0 }
            SortField.SIZE -> compareBy { it.size.bytes }
            else -> compareBy { it.title.lowercase() }
         }
      return if (config.direction == SortDirection.DESCENDING) sortedWith(comparator.reversed())
      else sortedWith(comparator)
   }
}
