package com.pmk.freeplayer.feature.genres.domain.repository

import com.pmk.freeplayer.core.domain.model.Song
import com.pmk.freeplayer.feature.genres.domain.model.Genre
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio del agregado [Genre].
 *
 * Fuente de verdad: Room (GenreDao).
 * Los géneros se extraen automáticamente de los metadatos durante el escaneo
 * y pueden crearse/editarse manualmente desde la UI.
 */
interface GenreRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// CONSULTAS
	// ═══════════════════════════════════════════════════════════════
	
	fun getGenres(): Flow<List<Genre>>
	
	fun getGenreById(id: Long): Flow<Genre?>
	
	/** Géneros con conteo de canciones — para subtítulos en listas. */
	fun getGenresWithSongCount(): Flow<List<GenreWithCount>>
	
	/** Género con su lista de canciones asociadas. */
	fun getGenreWithSongs(genreId: Long): Flow<GenreWithSongs?>
	
	// ═══════════════════════════════════════════════════════════════
	// ESCRITURA
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun createGenre(genre: Genre): Long
	
	suspend fun updateGenre(genre: Genre)
	
	/**
	 * Elimina el registro del género en Room.
	 * No elimina las canciones asociadas.
	 */
	suspend fun deleteGenre(id: Long)
	
	suspend fun addSongToGenre(songId: Long, genreId: Long)
	
	suspend fun removeSongFromGenre(songId: Long, genreId: Long)
}

// ─────────────────────────────────────────────────────────────────────────────
// VALUE OBJECTS
// ─────────────────────────────────────────────────────────────────────────────

/** Género con conteo de canciones — para listas con subtítulo. */
data class GenreWithCount(
	val genre: Genre,
	val songCount: Int,
)

/** Género con su lista completa de canciones — para pantalla de detalle. */
data class GenreWithSongs(
	val genre: Genre,
	val songs: List<Song>,
)