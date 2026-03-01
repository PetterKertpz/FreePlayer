package com.pmk.freeplayer.core.domain.model

import com.pmk.freeplayer.core.domain.model.enums.IntegrityStatus
import com.pmk.freeplayer.core.common.utils.FileSize
import com.pmk.freeplayer.core.common.utils.TrackDuration

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
	val mimeType: String,      // "enums/mpeg"
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
	val metadataStatus: IntegrityStatus
) {
	val featuringArtists: List<String> = emptyList()
	
	/**
	 * Verifica si la canción ha sido editada por el usuario
	 */
	val isEdited: Boolean
		get() = (originalTitle != null && originalTitle != title) ||
				(originalArtist != null) // Asumiendo que guardas originalArtist solo si cambia
}