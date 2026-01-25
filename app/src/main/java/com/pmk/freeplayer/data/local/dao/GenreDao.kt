package com.pmk.freeplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pmk.freeplayer.data.local.entity.GenreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
	
	// ==================== INSERTS & UPDATES ====================
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(genre: GenreEntity): Long
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(genres: List<GenreEntity>)
	
	@Update
	suspend fun update(genre: GenreEntity)
	
	// Actualización rápida de UI (Color/Icono)
	@Query("""
        UPDATE genres
        SET description = :description,
            hex_color = :hexColor,
            last_updated = :timestamp
        WHERE genre_id = :id
    """)
	suspend fun updateVisuals(
		id: Long,
		description: String?,
		hexColor: String?,
		timestamp: Long = System.currentTimeMillis()
	)
	
	// ==================== DELETES ====================
	
	@Delete
	suspend fun delete(genre: GenreEntity)
	
	@Query("DELETE FROM genres WHERE genre_id = :id")
	suspend fun deleteById(id: Long)
	
	// Limpieza de géneros vacíos (fantasmas)
	@Query("DELETE FROM genres WHERE song_count = 0")
	suspend fun deleteEmptyGenres()
	
	// ==================== QUERIES BÁSICAS ====================
	
	@Query("SELECT * FROM genres WHERE genre_id = :id")
	fun getGenreById(id: Long): Flow<GenreEntity?>
	
	@Query("SELECT * FROM genres ORDER BY name ASC")
	fun getAllGenres(): Flow<List<GenreEntity>>
	
	@Query("SELECT * FROM genres ORDER BY name ASC")
	suspend fun getAllGenresSync(): List<GenreEntity>
	
	// ==================== BÚSQUEDA & FILTROS ====================
	
	@Query("""
        SELECT * FROM genres
        WHERE name LIKE '%' || :query || '%'
        ORDER BY
            CASE WHEN name LIKE :query || '%' THEN 1 ELSE 2 END,
            name ASC
    """)
	fun searchGenres(query: String): Flow<List<GenreEntity>>
	
	@Query("SELECT * FROM genres WHERE name = :name COLLATE NOCASE LIMIT 1")
	suspend fun findGenreByName(name: String): GenreEntity?
	
	// ==================== RANKINGS ====================
	
	@Query("SELECT * FROM genres ORDER BY song_count DESC LIMIT :limit")
	fun getMostPopularGenres(limit: Int = 20): Flow<List<GenreEntity>>
	
	@Query("SELECT * FROM genres ORDER BY play_count DESC LIMIT :limit")
	fun getMostPlayedGenres(limit: Int = 20): Flow<List<GenreEntity>>
	
	// ==================== ESTADÍSTICAS (Lógica de Negocio) ====================
	
	@Query("UPDATE genres SET play_count = play_count + 1 WHERE genre_id = :id")
	suspend fun incrementPlayCount(id: Long)
	
	/**
	 * Recalcula contadores basándose en la tabla 'songs'.
	 * CRÍTICO: Asume que la tabla se llama 'songs' y tiene 'genre_id'.
	 */
	@Query("""
        UPDATE genres
        SET song_count = (SELECT COUNT(*) FROM songs WHERE songs.genre_id = genres.genre_id),
            artist_count = (SELECT COUNT(DISTINCT artist_id) FROM songs WHERE songs.genre_id = genres.genre_id),
            album_count = (SELECT COUNT(DISTINCT album_id) FROM songs WHERE songs.genre_id = genres.genre_id),
            last_updated = :timestamp
        WHERE genre_id = :id
    """)
	suspend fun refreshGenreStats(id: Long, timestamp: Long = System.currentTimeMillis())
	
	@Transaction
	suspend fun refreshAllGenreStats() {
		resetCounters()
		updateSongCounts()
	}
	
	@Query("UPDATE genres SET song_count = 0, artist_count = 0, album_count = 0")
	suspend fun resetCounters()
	
	@Query("""
        UPDATE genres
        SET song_count = (
            SELECT COUNT(*) FROM songs WHERE songs.genre_id = genres.genre_id
        )
        WHERE EXISTS (SELECT 1 FROM songs WHERE songs.genre_id = genres.genre_id)
    """)
	suspend fun updateSongCounts()
	
	// ==================== SCANNER HELPERS ====================
	
	/**
	 * Busca el género por nombre. Si no existe, lo crea.
	 * Esencial para importar canciones masivamente.
	 */
	@Transaction
	suspend fun getOrCreateGenreId(name: String): Long {
		val existing = findGenreByName(name)
		return if (existing != null) {
			existing.genreId
		} else {
			// Lógica simple de normalización (Primera letra Mayúscula)
			val cleanName = name.trim()
				.lowercase()
				.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
			
			val newGenre = GenreEntity(
				name = cleanName,
				normalizedName = cleanName.lowercase(),
				dateAdded = System.currentTimeMillis()
			)
			insert(newGenre)
		}
	}
}