package com.pmk.freeplayer.domain.model

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
