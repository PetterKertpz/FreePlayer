package com.pmk.freeplayer.feature.genres.domain.usecase

import com.pmk.freeplayer.feature.genres.domain.model.Genre
import com.pmk.freeplayer.feature.genres.domain.repository.GenreRepository
import com.pmk.freeplayer.feature.genres.domain.repository.GenreWithSongs
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ── Query use cases ───────────────────────────────────────────────────────────

class GetGenresUseCase @Inject constructor(private val repository: GenreRepository) {
	operator fun invoke(query: String? = null): Flow<List<Genre>> = repository.getGenres(query)
}

class GetGenreByIdUseCase @Inject constructor(private val repository: GenreRepository) {
	operator fun invoke(id: Long): Flow<Genre?> = repository.getGenreById(id)
}

class GetGenreWithSongsUseCase @Inject constructor(private val repository: GenreRepository) {
	operator fun invoke(genreId: Long): Flow<GenreWithSongs?> = repository.getGenreWithSongs(genreId)
}

class GetGenresByMostSongsUseCase @Inject constructor(private val repository: GenreRepository) {
	operator fun invoke(limit: Int = 20): Flow<List<Genre>> = repository.getByMostSongs(limit)
}

class GetGenresCountUseCase @Inject constructor(private val repository: GenreRepository) {
	suspend operator fun invoke(): Int = repository.count()
}

// ── Mutation use cases ────────────────────────────────────────────────────────

class CreateGenreUseCase @Inject constructor(private val repository: GenreRepository) {
	suspend operator fun invoke(genre: Genre): Long = repository.createGenre(genre)
}

class UpdateGenreUseCase @Inject constructor(private val repository: GenreRepository) {
	suspend operator fun invoke(genre: Genre) = repository.updateGenre(genre)
}

class DeleteGenreUseCase @Inject constructor(private val repository: GenreRepository) {
	suspend operator fun invoke(id: Long) = repository.deleteGenre(id)
}