package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Album
import com.pmk.freeplayer.domain.model.enums.SortConfiguration
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {

   fun getAllAlbums(): Flow<List<Album>>

   fun getAlbumById(id: Long): Flow<Album?>

   fun getAlbumsByArtist(artist: String): Flow<List<Album>>

   fun getSortedAlbums(sortOrder: SortConfiguration): Flow<List<Album>>

   fun searchAlbums(query: String): Flow<List<Album>>

   suspend fun getTotalAlbumCount(): Int
}
