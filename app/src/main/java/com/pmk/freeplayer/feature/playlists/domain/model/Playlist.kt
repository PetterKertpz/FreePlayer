package com.pmk.freeplayer.feature.playlists.domain.model

import com.pmk.freeplayer.core.common.utils.TrackDuration

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