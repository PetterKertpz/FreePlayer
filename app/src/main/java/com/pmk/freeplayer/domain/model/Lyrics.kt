package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.audio.LyricsStatus

data class Lyrics(
	val id: Long,
	val songId: Long,
	
	// Contenido
	val plainText: String,    // Siempre debe haber algo aquí (aunque sea el LRC sucio)
	val syncedText: String?,  // El string LRC crudo ("[00:12.50] Hola...")
	
	// Metadatos
	val source: String,
	val url: String?,
	val language: String?,
	
	// Estado (Tu enum EstadoLetra es útil para la UI de carga)
	val status: LyricsStatus = LyricsStatus.FOUND
) {
	// Helper para saber si activamos la vista de Karaoke
	val isSynced: Boolean
		get() = !syncedText.isNullOrBlank()
	
	// Helper para parsear LRC a objetos (se puede mover a un UseCase si es complejo)
	// Pero tenerlo aquí es práctico para validaciones rápidas.
}

