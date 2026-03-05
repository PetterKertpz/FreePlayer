package com.pmk.freeplayer.feature.genres.domain.repository

import com.pmk.freeplayer.feature.genres.domain.model.Genre
import com.pmk.freeplayer.feature.songs.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
	
	// ── Queries ───────────────────────────────────────────────────
	
	fun getGenres(query: String? = null): Flow<List<Genre>>
	fun getGenreById(id: Long): Flow<Genre?>
	fun getGenreWithSongs(genreId: Long): Flow<GenreWithSongs?>
	fun getByMostSongs(limit: Int = 20): Flow<List<Genre>>
	suspend fun count(): Int
	
	// ── Writes ────────────────────────────────────────────────────
	
	suspend fun createGenre(genre: Genre): Long
	suspend fun updateGenre(genre: Genre)
	suspend fun deleteGenre(id: Long)
}

/** Genre with its full song list — used on the detail screen. */
data class GenreWithSongs(
	val genre: Genre,
	val songs: List<Song>,
)