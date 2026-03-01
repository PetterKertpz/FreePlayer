package com.pmk.freeplayer.feature.genres.domain.usecase

import com.pmk.freeplayer.feature.genres.domain.model.Genre
import com.pmk.freeplayer.feature.genres.domain.repository.GenreRepository
import com.pmk.freeplayer.feature.genres.domain.repository.GenreWithCount
import com.pmk.freeplayer.feature.genres.domain.repository.GenreWithSongs
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ═════════════════════════════════════════════════════════════════════════════
// QUERIES
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Consultas de géneros.
 *
 * Uso en ViewModel:
 * ```kotlin
 * getGenresUseCase()
 * getGenresUseCase.byId(genreId)
 * getGenresUseCase.withCount()
 * getGenresUseCase.withSongs(genreId)
 * ```
 */
class GetGenresUseCase @Inject constructor(
	private val repository: GenreRepository,
) {
	operator fun invoke(): Flow<List<Genre>> =
		repository.getGenres()
	
	fun byId(id: Long): Flow<Genre?> =
		repository.getGenreById(id)
	
	/** Géneros con conteo de canciones — para subtítulos en listas. */
	fun withCount(): Flow<List<GenreWithCount>> =
		repository.getGenresWithSongCount()
	
	/** Género con su lista completa de canciones — para pantalla de detalle. */
	fun withSongs(genreId: Long): Flow<GenreWithSongs?> =
		repository.getGenreWithSongs(genreId)
}

// ═════════════════════════════════════════════════════════════════════════════
// MUTATIONS
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Mutaciones de géneros: creación, edición, eliminación y asignación de canciones.
 *
 * Uso en ViewModel:
 * ```kotlin
 * manageGenresUseCase.create(genre)
 * manageGenresUseCase.update(genre)
 * manageGenresUseCase.delete(genreId)
 * manageGenresUseCase.addSong(songId, genreId)
 * manageGenresUseCase.removeSong(songId, genreId)
 * ```
 */
class ManageGenresUseCase @Inject constructor(
	private val repository: GenreRepository,
) {
	/**
	 * Crea un género nuevo. Retorna el id generado.
	 * Los géneros del scanner se crean automáticamente;
	 * este método es para géneros creados manualmente por el usuario.
	 */
	suspend fun create(genre: Genre): Long =
		repository.createGenre(genre)
	
	suspend fun update(genre: Genre) =
		repository.updateGenre(genre)
	
	/**
	 * Elimina el registro del género en Room.
	 * No elimina las canciones asociadas.
	 */
	suspend fun delete(id: Long) =
		repository.deleteGenre(id)
	
	suspend fun addSong(songId: Long, genreId: Long) =
		repository.addSongToGenre(songId, genreId)
	
	suspend fun removeSong(songId: Long, genreId: Long) =
		repository.removeSongFromGenre(songId, genreId)
}