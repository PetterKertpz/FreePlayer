package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.LyricsEntity
import com.pmk.freeplayer.domain.model.Lyrics
// Asegúrate de que este Enum esté en el paquete correcto (recomendado: domain.model)
import com.pmk.freeplayer.domain.model.enums.LyricsStatus

/**
 * 🔄 LYRICS MAPPER
 * Convierte entre la base de datos (Entity) y la UI (Domain Model).
 */

// ==================== ENTITY -> DOMAIN ====================

fun LyricsEntity.toDomain(): Lyrics {
	return Lyrics(
		id = this.lyricsId,
		songId = this.songId,
		
		// Manejo de nulos: Si la BD tiene null, devolvemos string vacío
		// para cumplir el contrato del modelo de dominio.
		plainText = this.plainLyrics ?: "",
		syncedText = this.syncedLyrics,
		
		source = this.source,
		url = this.sourceUrl,
		language = this.language,
		
		// Si viene de la BD (Entity), significa que la letra existe (FOUND)
		status = LyricsStatus.FOUND
	)
}

// ==================== DOMAIN -> ENTITY ====================

fun Lyrics.toEntity(): LyricsEntity {
	return LyricsEntity(
		lyricsId = this.id,
		songId = this.songId,
		
		plainLyrics = this.plainText,
		syncedLyrics = this.syncedText,
		
		source = this.source,
		sourceUrl = this.url,
		language = this.language,
		
		// Calculamos los flags automáticamente al guardar
		isSynced = !this.syncedText.isNullOrBlank(),
		isTranslation = false, // Por defecto false, salvo que agregues lógica para esto
		
		// Actualizamos la fecha al guardar/editar
		dateAdded = System.currentTimeMillis()
	)
}

// ==================== LISTAS ====================

fun List<LyricsEntity>.toDomain(): List<Lyrics> = map { it.toDomain() }