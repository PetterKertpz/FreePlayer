package com.pmk.freeplayer.feature.songs.domain.usecase

import com.pmk.freeplayer.core.domain.model.enums.SortConfig
import com.pmk.freeplayer.feature.songs.domain.model.Song
import com.pmk.freeplayer.feature.songs.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ── Query use cases (SRP: one operator fun invoke each) ───────────────────────

class GetSongsUseCase @Inject constructor(private val repository: SongRepository) {
   operator fun invoke(query: String? = null, sortConfig: SortConfig? = null): Flow<List<Song>> =
      repository.getSongs(query, sortConfig)
}

class GetSongByIdUseCase @Inject constructor(private val repository: SongRepository) {
   operator fun invoke(id: Long): Flow<Song?> = repository.getSongById(id)
}

class GetSongsByIdsUseCase @Inject constructor(private val repository: SongRepository) {
   operator fun invoke(ids: List<Long>): Flow<List<Song>> = repository.getSongsByIds(ids)
}

class GetSongsByAlbumUseCase @Inject constructor(private val repository: SongRepository) {
   operator fun invoke(albumId: Long): Flow<List<Song>> = repository.getSongsByAlbum(albumId)
}

class GetSongsByArtistUseCase @Inject constructor(private val repository: SongRepository) {
   operator fun invoke(artistId: Long): Flow<List<Song>> = repository.getSongsByArtist(artistId)
}

class GetSongsByGenreUseCase @Inject constructor(private val repository: SongRepository) {
   operator fun invoke(genreId: Long): Flow<List<Song>> = repository.getSongsByGenre(genreId)
}

class GetFavoriteSongsUseCase @Inject constructor(private val repository: SongRepository) {
   operator fun invoke(): Flow<List<Song>> = repository.getFavoriteSongs()
}

class GetRecentlyAddedSongsUseCase @Inject constructor(private val repository: SongRepository) {
   operator fun invoke(limit: Int = 20): Flow<List<Song>> = repository.getRecentlyAdded(limit)
}

class GetHiddenSongsUseCase @Inject constructor(private val repository: SongRepository) {
   operator fun invoke(): Flow<List<Song>> = repository.getHiddenSongs()
}

// ── Mutation use cases ────────────────────────────────────────────────────────

class InsertSongsUseCase @Inject constructor(private val repository: SongRepository) {
   suspend operator fun invoke(songs: List<Song>): List<Long> = repository.insertSongs(songs)
}

class UpdateSongUseCase @Inject constructor(private val repository: SongRepository) {
   suspend operator fun invoke(song: Song) = repository.updateSong(song)
}

class DeleteSongUseCase @Inject constructor(private val repository: SongRepository) {
   suspend operator fun invoke(id: Long) = repository.deleteSong(id)
}

class DeleteSongsByIdsUseCase @Inject constructor(private val repository: SongRepository) {
   suspend operator fun invoke(ids: List<Long>) = repository.deleteSongsByIds(ids)
}

class DeleteSongFromDeviceUseCase @Inject constructor(private val repository: SongRepository) {
   suspend operator fun invoke(id: Long) = repository.deleteSongFromDevice(id)
}

class HideSongUseCase @Inject constructor(private val repository: SongRepository) {
   suspend operator fun invoke(id: Long, hidden: Boolean) = repository.hideSong(id, hidden)
}

class ToggleFavoriteSongUseCase @Inject constructor(private val repository: SongRepository) {
   suspend operator fun invoke(songId: Long) = repository.toggleFavoriteSong(songId)
}

class SetFavoriteSongUseCase @Inject constructor(private val repository: SongRepository) {
   suspend operator fun invoke(songId: Long, isFavorite: Boolean) =
      repository.setFavoriteSong(songId, isFavorite)
}
