package com.pmk.freeplayer.feature.albums.domain.model

import com.pmk.freeplayer.feature.genres.domain.model.Genre
import com.pmk.freeplayer.core.common.utils.TrackDuration
import com.pmk.freeplayer.core.domain.model.enums.AlbumType

data class Album(
	// --- 1. Identidad ---
	val id: Long,
	val title: String,
	
	// --- 2. Artista (Relación) ---
	val artistId: Long,
	val artistName: String,
	
	// --- 3. Multimedia ---
	val coverUri: String?, // La UI decidirá si muestra un placeholder si es null
	
	// --- 4. Metadatos Básicos ---
	val type: AlbumType,   // Album, Single, EP...
	val genre: Genre?,     // Rock, Pop...
	val year: Int?,        // 2024
	val dateAdded: Long,   // Para ordenar por "Agregados recientemente"
	
	// --- 5. Metadatos Ricos (Lo que pediste) ---
	val description: String?,  // "Considerado el mejor álbum de..."
	val producer: String?,     // "Rick Rubin"
	val recordLabel: String?,  // "Sony Music"
	
	// --- 6. Estadísticas y Valoración ---
	val songCount: Int,
	val totalDuration: TrackDuration, // Objeto inteligente (sumas, restas, formato)
	val playCount: Int,      // Para "Más escuchados"
	val rating: Float,       // 0.0 a 5.0 (Estrellas)
	val isFavorite: Boolean, // Corazón activado/desactivado
	
	// --- 7. Referencias Externas (Opcional pero Pro) ---
	// Útil si quieres poner un botón "Ver letra en Genius" o "Abrir en Spotify"
	val geniusId: String?,
	val spotifyId: String?
) {
	
	// --- LÓGICA DE NEGOCIO (Propiedades Computadas) ---
	
	val isLive: Boolean
		get() = type == AlbumType.LIVE
	
	val isEp: Boolean
		get() = type == AlbumType.EP
	
	// Lógica visual: Si no hay description, podemos devolver un texto por defecto o vacío
	fun hasDescription(): Boolean = !description.isNullOrBlank()
}