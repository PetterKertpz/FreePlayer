package com.pmk.freeplayer.feature.genres.data.repository

import com.pmk.freeplayer.app.di.IoDispatcher
import com.pmk.freeplayer.feature.genres.data.local.dao.GenreDao
import com.pmk.freeplayer.feature.genres.data.mapper.createGenreEntity
import com.pmk.freeplayer.feature.genres.data.mapper.toDomain
import com.pmk.freeplayer.feature.genres.data.mapper.toEntity
import com.pmk.freeplayer.feature.genres.domain.model.Genre
import com.pmk.freeplayer.feature.genres.domain.repository.GenreRepository
import com.pmk.freeplayer.feature.genres.domain.repository.GenreWithSongs
import com.pmk.freeplayer.feature.songs.data.local.dao.SongDao
import com.pmk.freeplayer.feature.songs.data.mapper.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GenreRepositoryImpl @Inject constructor(
	private val genreDao: GenreDao,
	private val songDao: SongDao,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : GenreRepository {
	
	// ── Queries ───────────────────────────────────────────────────
	
	override fun getGenres(query: String?): Flow<List<Genre>> =
		if (!query.isNullOrBlank()) genreDao.search(query).map { it.toDomain() }
		else genreDao.getAll().map { it.toDomain() }
	
	override fun getGenreById(id: Long): Flow<Genre?> =
		genreDao.getByIdFlow(id).map { it?.toDomain() }
	
	override fun getGenreWithSongs(genreId: Long): Flow<GenreWithSongs?> =
		combine(
			genreDao.getByIdFlow(genreId),
			songDao.getByGenre(genreId),
		) { genreEntity, songEntities ->
			genreEntity?.let {
				GenreWithSongs(
					genre = it.toDomain(),
					songs = songEntities.toDomain(),
				)
			}
		}
	
	override fun getByMostSongs(limit: Int): Flow<List<Genre>> =
		genreDao.getByMostSongs(limit).map { it.toDomain() }
	
	override suspend fun count(): Int = genreDao.count()
	
	// ── Writes ────────────────────────────────────────────────────
	
	override suspend fun createGenre(genre: Genre): Long = withContext(ioDispatcher) {
		val now = System.currentTimeMillis()
		// If a genre with this name already exists, return its id without duplicating.
		val normalized = genre.name.trim().uppercase()
		genreDao.getByNormalizedName(normalized)?.genreId
			?: genreDao.insert(createGenreEntity(genre.name, now))
	}
	
	override suspend fun updateGenre(genre: Genre) = withContext(ioDispatcher) {
		val original = requireNotNull(genreDao.getById(genre.id)) {
			"Genre not found: id=${genre.id}"
		}
		genreDao.update(genre.toEntity(original = original, now = System.currentTimeMillis()))
	}
	
	override suspend fun deleteGenre(id: Long) = withContext(ioDispatcher) {
		genreDao.deleteById(id)
	}
}