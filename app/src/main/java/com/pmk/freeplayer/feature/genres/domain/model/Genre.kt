package com.pmk.freeplayer.feature.genres.domain.model

import java.util.Locale

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
	
	/**
	 * Nombre formateado para la UI.
	 * Convierte "rock metal" -> "Rock Metal" y maneja vacíos.
	 */
	val displayName: String
		get() {
			if (name.isBlank()) return "Desconocido"
			
			return name.trim()
				.lowercase()
				.split(" ")
				.joinToString(" ") { word ->
					word.replaceFirstChar {
						if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
					}
				}
		}
	
	companion object {
		// Objeto vacío por defecto para evitar nulls peligrosos en la UI
		val UNKNOWN = Genre(
			id = -1,
			name = "Desconocido",
			description = null,
			hexColor = "#808080", // Gris neutro
			iconUri = null,
			songCount = 0,
			playCount = 0,
			originDecade = null,
			originCountry = null
		)
	}
}