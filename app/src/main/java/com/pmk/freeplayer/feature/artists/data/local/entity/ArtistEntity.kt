package com.pmk.freeplayer.feature.artists.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "artists", // Inglés plural
	indices = [
		Index(value = ["name"], unique = true), // Búsqueda rápida por nombre
		Index(value = ["genius_id"]),
		Index(value = ["spotify_id"])
	]
)
data class ArtistEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "artist_id") val artistId: Long = 0, // Long es estándar
	
	// --- Identidad ---
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "real_name") val realName: String? = null, // Ej: Eminem -> Marshall Mathers
	
	// --- Ubicación ---
	@ColumnInfo(name = "country") val country: String? = null,
	@ColumnInfo(name = "city") val city: String? = null,
	
	// --- Textos (Rich UI) ---
	// 'description': Frase corta o tagline. 'biography': Texto largo completo.
	@ColumnInfo(name = "description") val description: String? = null,
	@ColumnInfo(name = "biography") val biography: String? = null,
	
	// --- Fechas ---
	@ColumnInfo(name = "birth_date") val birthDate: Long? = null,
	@ColumnInfo(name = "career_start") val careerStartYear: Int? = null, // Basta con el año (Int)
	
	// --- Multimedia ---
	@ColumnInfo(name = "image_url") val remoteImageUrl: String? = null,     // Foto de perfil remota
	@ColumnInfo(name = "image_path") val localImagePath: String? = null,    // Foto de perfil local
	@ColumnInfo(name = "header_url") val remoteHeaderUrl: String? = null,   // Banner/Fondo
	
	// --- Enlaces y IDs Externos ---
	@ColumnInfo(name = "genius_id") val geniusId: String? = null,
	@ColumnInfo(name = "spotify_id") val spotifyId: String? = null,
	@ColumnInfo(name = "website_url") val websiteUrl: String? = null,
	
	// --- Clasificación ---
	@ColumnInfo(name = "genres") val genres: String? = null, // CSV: "Rap, Hip-Hop"
	@ColumnInfo(name = "artist_type") val type: String? = null, // "SOLO", "BAND", "ORCHESTRA"
	
	// --- Flags ---
	@ColumnInfo(name = "is_verified") val isVerified: Boolean = false,
	@ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false, // Agregado para consistencia
	
	// --- Estadísticas (Cacheadas para ordenar rápido) ---
	@ColumnInfo(name = "total_songs") val totalSongs: Int = 0,
	@ColumnInfo(name = "total_albums") val totalAlbums: Int = 0,
	@ColumnInfo(name = "play_count") val playCount: Int = 0,
	
	// --- Metadatos del Sistema ---
	@ColumnInfo(name = "date_added") val dateAdded: Long = System.currentTimeMillis(),
	@ColumnInfo(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis()
)