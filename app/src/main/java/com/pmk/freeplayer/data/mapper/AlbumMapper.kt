package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.AlbumEntity
import com.pmk.freeplayer.data.local.entity.relation.AlbumArtistEntity

import com.pmk.freeplayer.domain.model.Album
import com.pmk.freeplayer.domain.model.Genre
import com.pmk.freeplayer.domain.model.TrackDuration
import com.pmk.freeplayer.domain.model.enums.AlbumType

/** 🔄 ALBUM MAPPER Convierte entre la base de datos (Entity) y la UI (Domain Model). */

// ==================== ENTITY -> DOMAIN ====================

/**
 * Mapeo principal cuando usamos relaciones (Mejor práctica para listas). Asume que tienes una
 * clase @Relation 'AlbumWithArtist' (ver abajo).
 */
fun AlbumArtistEntity.toDomain(): Album {
   return this.album.toDomain(artistName = this.artistName ?: "Unknown Artist")
}

/**
 * Mapeo manual cuando ya conocemos el nombre del artista externamente. Prioridad de portada:
 * Local > Remota.
 */
fun AlbumEntity.toDomain(artistName: String): Album {
   return Album(
      // --- 1. Identidad ---
      id = this.albumId,
      title = this.title,

      // --- 2. Artista ---
      artistId = this.artistId,
      artistName = artistName,

      // --- 3. Multimedia ---
      // Si hay portada descargada (local), úsala. Si no, usa la URL remota.
      coverUri = this.localCoverPath ?: this.remoteCoverUrl,

      // --- 4. Metadatos Básicos ---
      type = mapStringToAlbumType(this.type),
      genre = mapStringToGenre(this.genres), // Convierte string CSV a Objeto Genre
      year = this.year,
      dateAdded = this.dateAdded,

      // --- 5. Metadatos Ricos ---
      description = this.description,
      producer = this.producer,
      recordLabel = this.label,

      // --- 6. Estadísticas ---
      songCount = this.totalSongs,
      totalDuration = TrackDuration(this.totalDurationMs),
      playCount = this.playCount,
      rating = this.rating,
      isFavorite = this.isFavorite,

      // --- 7. Referencias ---
      geniusId = this.geniusId,
      spotifyId = this.spotifyId,
   )
}

// ==================== DOMAIN -> ENTITY ====================

/**
 * Mapeo inverso: Útil si editas un álbum en la UI y quieres guardar cambios. Nota: No
 * sobreescribimos campos que no existen en el dominio (como path local original).
 */
fun Album.toEntity(originalLocalPath: String? = null): AlbumEntity {
   return AlbumEntity(
      albumId = this.id,
      artistId = this.artistId,
      title = this.title,

      // Mantenemos la lógica de persistencia
      localCoverPath =
         originalLocalPath
            ?: if (!this.coverUri.isNullOrEmpty() && !this.coverUri.startsWith("http"))
               this.coverUri
            else null,
      remoteCoverUrl = if (this.coverUri?.startsWith("http") == true) this.coverUri else null,
      year = this.year,
      type = this.type.name, // Enum a String
      genres = this.genre?.name, // Objeto a String
      totalDurationMs = this.totalDuration.millis,
      label = this.recordLabel,
      producer = this.producer,
      description = this.description,
      totalSongs = this.songCount,
      playCount = this.playCount,
      isFavorite = this.isFavorite,
      rating = this.rating,
      geniusId = this.geniusId,
      spotifyId = this.spotifyId,
   )
}

// ==================== HELPERS PRIVADOS ====================

/** Convierte el String de la DB ("SINGLE", "EP") al Enum de Dominio. */
private fun mapStringToAlbumType(typeStr: String): AlbumType {
   return try {
      AlbumType.valueOf(typeStr.uppercase())
   } catch (e: Exception) {
      // Fallback seguro si la base de datos tiene basura o es null
      AlbumType.ALBUM
   }
}

/**
 * Crea un objeto Genre temporal desde el String de la BD. Como la Entity solo guarda un String (ej:
 * "Rock, Pop"), creamos un objeto Genre simple.
 */
private fun mapStringToGenre(genresStr: String?): Genre? {
	// 1. Validación de seguridad
	if (genresStr.isNullOrBlank()) return null
	
	// 2. Tu lógica para sacar el nombre principal es correcta ✅
	val mainGenreName = genresStr.split(",").first().trim()
	
	// 3. Debes rellenar TODOS los campos obligatorios del data class
	return Genre(
		id = -1, // ID temporal/desconocido
		name = mainGenreName,
		
		// --- Campos Visuales (Nulos porque no vienen del AlbumEntity) ---
		description = null,
		hexColor = null,
		iconUri = null,
		
		// --- Estadísticas (Cero porque no las sabemos aquí) ---
		songCount = 0,
		playCount = 0,
		
		// --- Extra ---
		originDecade = null,
		originCountry = null
	)
}
