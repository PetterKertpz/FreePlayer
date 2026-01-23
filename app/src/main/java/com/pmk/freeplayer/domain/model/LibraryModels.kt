package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.audio.AlbumType
import com.pmk.freeplayer.domain.model.audio.Genre
import com.pmk.freeplayer.domain.model.audio.SocialPlatform

data class Song(
	val id: Long,
	val title: String,
	val artistName: String,
	// --- Ubicación y Relaciones ---
	val filePath: String,
	val trackNumber: Int?,
	val discNumber: Int?,
	val year: Int?,
	
	val artistId: Long?,
	val albumId: Long?,
	val genreId: Long?,
	
	// --- Objetos de Valor (Value Classes) ---
	val duration: TrackDuration,
	val size: FileSize,
	
	// ==================== LO QUE FALTABA ====================
	
	// --- 1. Detalles Técnicos (Audio Quality) ---
	val mimeType: String,      // "audio/mpeg"
	val bitrate: Int?,         // 320 (kbps)
	val sampleRate: Int?,      // 44100 (Hz)
	val audioQuality: String?, // "LOSSLESS", "HI-RES"
	
	// --- 2. Auditoría (Para editar etiquetas) ---
	// Útil para mostrar: "Editado (Original: Track 01)"
	val originalTitle: String?,
	val originalArtist: String?,
	
	// --- 3. Metadatos Ricos ---
	val versionType: String?, // "Remix", "Live"
	val dateAdded: Long,      // Para ordenar por "Recientes"
	val dateModified: Long?,  // Para saber si el archivo cambió
	
	// --- 4. Genius & Info Externa ---
	val geniusUrl: String?,   // Para el botón "Ver en Genius"
	val isHot: Boolean,       // Fueguito en la UI 🔥
	val externalIds: String?, // JSON string (opcional, por si quieres parsear IDs)
	
	// --- 5. Estado y Stats ---
	val isFavorite: Boolean,
	val playCount: Int,
	val rating: Float,
	val hasLyrics: Boolean,
	val metadataStatus: String // "PENDING", "VERIFIED"
) {
	val featuringArtists: List<String> = emptyList()
	// ==================== HELPERS PARA UI ====================
	// Lógica de presentación directamente en el modelo
	
	/**
	 * Devuelve la extensión del archivo en mayúsculas. Ej: "MP3", "FLAC"
	 */
	val fileExtension: String
		get() = filePath.substringAfterLast('.', "").uppercase()
	
	/**
	 * Genera una etiqueta de calidad bonita.
	 * Ej: "FLAC • 44.1kHz" o "MP3 • 320kbps"
	 */
	val qualityLabel: String
		get() {
			val parts = mutableListOf<String>()
			parts.add(fileExtension)
			
			if (bitrate != null && bitrate > 0) {
				parts.add("${bitrate}kbps")
			}
			// Solo mostramos Hz si es alta calidad para no saturar
			if (sampleRate != null && sampleRate > 48000) {
				parts.add("${sampleRate / 1000}kHz")
			}
			return parts.joinToString(" • ")
		}
	
	/**
	 * Verifica si la canción ha sido editada por el usuario
	 */
	val isEdited: Boolean
		get() = (originalTitle != null && originalTitle != title) ||
				(originalArtist != null) // Asumiendo que guardas originalArtist solo si cambia
}

data class Playlist(
	val id: Long,
	val name: String,
	val description: String?,
	val coverUri: String?,
	val hexColor: String?,
	
	// Tipos
	val isSystem: Boolean, // ¿Es de sistema o de usuario?
	val isPinned: Boolean, // ¿Está fijada arriba?
	
	// Stats (Pre-calculados en la Entity)
	val songCount: Int,
	val totalDuration: TrackDuration, // Usamos tu clase bonita
	
	val createdAt: Long,
	val updatedAt: Long
) {
	// Lógica visual: Si no tiene portada, la UI generará un collage
	val hasCustomCover: Boolean get() = !coverUri.isNullOrBlank()
}

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

data class Artist(
	val id: Long,
	val name: String,
	
	// Identidad
	val realName: String?, // "Marshall Mathers"
	val isVerified: Boolean, // Check azul
	
	// Ubicación (Útil para mostrar: "Banda de Londres, UK")
	val country: String?,
	val city: String?,
	
	// --- MULTIMEDIA ---
	val coverUri: String?,   // Foto de perfil (Circular en UI)
	val headerUri: String?,  // Banner de fondo (Rectangular en UI)
	
	// --- TEXTOS (Tu requerimiento) ---
	// Aquí es donde va el texto para tu sección aparte.
	// Al ser Nullable (String?), si el artista no tiene bio, tu UI simplemente oculta la sección.
	val biography: String?,
	val description: String?, // Una frase corta: "La banda de rock más grande..."
	
	// --- DATOS TÉCNICOS ---
	val type: String, // "SOLO", "BAND" (Podrías hacer un Enum ArtistType luego)
	val genre: Genre?,
	
	// --- ESTADÍSTICAS ---
	val songCount: Int,
	val albumCount: Int,
	val playCount: Int,      // "2.5M reproducciones"
	val isFavorite: Boolean,
	
	// --- FECHAS ---
	val careerStartYear: Int?, // "Activo desde 1995"
	val birthDate: Long?,      // Para mostrar "Cumpleaños: 17 de Octubre"
	
	// --- ENLACES EXTERNOS (Botones en el perfil) ---
	val websiteUrl: String?,
	val spotifyId: String?,
	val geniusId: String?
) {
	
	// --- LÓGICA DE UI ---
	/**
	 * Devuelve true si tenemos información suficiente para mostrar
	 * la sección "Acerca de" en la UI.
	 */
	val hasDetails: Boolean
		get() = !biography.isNullOrBlank() || !description.isNullOrBlank()
	
	/**
	 * Formatea el origen. Ej: "Londres, UK" o solo "UK".
	 */
	val locationText: String?
		get() = when {
			!city.isNullOrBlank() && !country.isNullOrBlank() -> "$city, $country"
			!country.isNullOrBlank() -> country
			else -> null
		}
}
data class SocialLink(
	val id: Long,
	val platform: SocialPlatform, // Enum (útil para decidir qué icono mostrar)
	val username: String,         // "@usuario"
	val url: String,              // Acción al hacer click
	val isVerified: Boolean,
	val followerCount: Int?       // Opcional
)

data class Genre(
	val id: Long,
	val name: String,
	
	// --- Visual ---
	val description: String?,
	val hexColor: String?,    // La UI lo convertirá a Color()
	val iconUri: String?,     // Local > Remoto
	
	// --- Stats ---
	val songCount: Int,
	val playCount: Int,
	
	// --- Info Extra ---
	val originDecade: String?,
	val originCountry: String?
) {
	// Recuperamos tu lógica de visualización bonita aquí
	val displayName: String
		get() = name.ifBlank { "Desconocido" }
	
	companion object {
		// Objeto vacío por defecto para evitar nulls peligrosos
		val UNKNOWN = Genre(
			id = -1,
			name = "Desconocido",
			description = null,
			hexColor = "#808080", // Gris
			iconUri = null,
			songCount = 0,
			playCount = 0,
			originDecade = null,
			originCountry = null
		)
	}
}

data class Carpeta(
	val ruta: String,
	val nombre: String,
	val cantidadCanciones: Int,
	val tamanioTotal: FileSize,
	val estaOculta: Boolean = false
)