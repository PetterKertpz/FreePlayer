package com.pmk.freeplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pmk.freeplayer.data.local.entity.ArtistSocialLinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistSocialLinkDao {
	
	// ==================== INSERTS & UPDATES ====================
	
	/**
	 * Inserta un link.
	 * Gracias al índice único ["artist_id", "platform"], si intentas insertar
	 * otro link de "INSTAGRAM" para el mismo artista, sobrescribirá el anterior
	 * (útil para actualizar la URL o seguidores automáticamente).
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(link: ArtistSocialLinkEntity): Long
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(links: List<ArtistSocialLinkEntity>)
	
	@Update
	suspend fun update(link: ArtistSocialLinkEntity)
	
	// ==================== DELETES ====================
	
	@Delete
	suspend fun delete(link: ArtistSocialLinkEntity)
	
	@Query("DELETE FROM artist_social_links WHERE artist_id = :artistId")
	suspend fun deleteAllLinksForArtist(artistId: Long)
	
	// ==================== QUERIES ====================
	
	/**
	 * Obtiene todos los links de un artista para mostrarlos en su perfil.
	 */
	@Query("SELECT * FROM artist_social_links WHERE artist_id = :artistId ORDER BY platform ASC")
	fun getLinksByArtistId(artistId: Long): Flow<List<ArtistSocialLinkEntity>>
	
	/**
	 * Obtiene un link específico (ej: Para abrir directamente Instagram).
	 */
	@Query("""
        SELECT * FROM artist_social_links
        WHERE artist_id = :artistId AND platform = :platform
        LIMIT 1
    """)
	suspend fun getLinkByPlatform(artistId: Long, platform: String): ArtistSocialLinkEntity?
	
	/**
	 * Verifica si el artista ya tiene este link verificado.
	 */
	@Query("""
        SELECT is_verified FROM artist_social_links
        WHERE artist_id = :artistId AND platform = :platform
    """)
	suspend fun isVerifiedOnPlatform(artistId: Long, platform: String): Boolean?
}