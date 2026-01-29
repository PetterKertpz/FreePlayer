package com.pmk.freeplayer.data.mapper

import com.pmk.freeplayer.data.local.entity.LyricsEntity
import com.pmk.freeplayer.domain.model.Lyrics
import com.pmk.freeplayer.domain.model.enums.LyricsStatus

/**
 * 📄 LYRICS MAPPER
 *
 * Convierte entre la capa de datos (LyricsEntity) y el modelo de dominio (Lyrics).
 *
 * **Mapeo Source/Status:**
 * - Entity usa `source` (String): "MANUAL", "GENIUS", "MUSIXMATCH", "EMBEDDED", "LOCAL"
 * - Domain usa `status` (Enum): LyricsStatus.FOUND_ONLINE, FOUND_EMBEDDED, etc.
 * - Este mapper maneja la conversión bidireccional automáticamente
 *
 * **Contenido:**
 * - plainText: Siempre tiene contenido (nunca null en domain)
 * - syncedText: Opcional, formato LRC "[00:12.50] Hello..."
 * - isSynced: Se calcula automáticamente desde syncedText
 */

// ══════════════════════════════════════════════════════════════════════════════
// ENTITY -> DOMAIN (Para presentar en la UI)
// ══════════════════════════════════════════════════════════════════════════════

fun LyricsEntity.toDomain(): Lyrics {
	return Lyrics(
		id = this.lyricsId,
		songId = this.songId,
		plainText = this.plainLyrics ?: "",
		syncedText = this.syncedLyrics,
		sourceUrl = this.sourceUrl,
		language = this.language,
		status = this.source.toLyricsStatus()
	)
}

fun List<LyricsEntity>.toDomain(): List<Lyrics> = map { it.toDomain() }

// ══════════════════════════════════════════════════════════════════════════════
// DOMAIN -> ENTITY (Para guardar en la base de datos)
// ══════════════════════════════════════════════════════════════════════════════

fun Lyrics.toEntity(preserveDateAdded: Long? = null): LyricsEntity {
	return LyricsEntity(
		lyricsId = this.id,
		songId = this.songId,
		plainLyrics = this.plainText.takeIf { it.isNotBlank() },
		syncedLyrics = this.syncedText,
		source = this.status.toSourceString(),
		sourceUrl = this.sourceUrl,
		language = this.language,
		isSynced = !this.syncedText.isNullOrBlank(),
		isTranslation = this.language?.let { it != "en" && it.isNotBlank() } ?: false,
		dateAdded = preserveDateAdded ?: System.currentTimeMillis()
	)
}

fun List<Lyrics>.toEntity(): List<LyricsEntity> = map { it.toEntity() }

// ══════════════════════════════════════════════════════════════════════════════
// ACTUALIZACIÓN DE ENTIDADES EXISTENTES
// ══════════════════════════════════════════════════════════════════════════════

fun Lyrics.toUpdatedEntity(existingEntity: LyricsEntity): LyricsEntity {
	return LyricsEntity(
		lyricsId = this.id,
		songId = this.songId,
		plainLyrics = this.plainText.takeIf { it.isNotBlank() },
		syncedLyrics = this.syncedText,
		source = this.status.toSourceString(),
		sourceUrl = this.sourceUrl,
		language = this.language,
		isSynced = !this.syncedText.isNullOrBlank(),
		isTranslation = this.language?.let { it != "en" && it.isNotBlank() } ?: false,
		dateAdded = existingEntity.dateAdded
	)
}

fun LyricsEntity.updateSyncedContent(syncedLrc: String?): LyricsEntity {
	return this.copy(
		syncedLyrics = syncedLrc,
		isSynced = !syncedLrc.isNullOrBlank()
	)
}

fun LyricsEntity.updateTranslation(translatedText: String, targetLanguage: String): LyricsEntity {
	return this.copy(
		plainLyrics = translatedText,
		language = targetLanguage,
		isTranslation = true
	)
}

fun LyricsEntity.markAsManuallyEdited(): LyricsEntity {
	return this.copy(source = "LOCAL")
}

// ══════════════════════════════════════════════════════════════════════════════
// CONVERSIÓN SOURCE <-> STATUS (Ahora públicas para uso en Repository)
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Convierte string source (Entity) a LyricsStatus (Domain).
 */
fun String?.toLyricsStatus(): LyricsStatus {
	return when (this?.uppercase()) {
		"EMBEDDED" -> LyricsStatus.FOUND_EMBEDDED
		"LOCAL" -> LyricsStatus.FOUND_LOCAL
		"ONLINE", "GENIUS", "MUSIXMATCH", "LRCLIB", "AZLYRICS" -> LyricsStatus.FOUND_ONLINE
		"NOT_SEARCHED" -> LyricsStatus.NOT_SEARCHED
		"SEARCHING" -> LyricsStatus.SEARCHING
		"NOT_FOUND" -> LyricsStatus.NOT_FOUND
		"ERROR" -> LyricsStatus.ERROR
		null, "" -> LyricsStatus.NOT_SEARCHED
		else -> LyricsStatus.FOUND_ONLINE
	}
}

/**
 * Convierte LyricsStatus (Domain) a string source (Entity).
 */
fun LyricsStatus.toSourceString(): String {
	return when (this) {
		LyricsStatus.FOUND_EMBEDDED -> "EMBEDDED"
		LyricsStatus.FOUND_LOCAL -> "LOCAL"
		LyricsStatus.FOUND_ONLINE -> "ONLINE"
		LyricsStatus.NOT_SEARCHED -> "NOT_SEARCHED"
		LyricsStatus.SEARCHING -> "SEARCHING"
		LyricsStatus.NOT_FOUND -> "NOT_FOUND"
		LyricsStatus.ERROR -> "ERROR"
	}
}

// ══════════════════════════════════════════════════════════════════════════════
// FUNCIONES DE CREACIÓN RÁPIDA
// ══════════════════════════════════════════════════════════════════════════════

fun createManualLyrics(songId: Long, plainText: String, language: String? = null): LyricsEntity {
	return LyricsEntity(
		lyricsId = 0,
		songId = songId,
		plainLyrics = plainText.takeIf { it.isNotBlank() },
		syncedLyrics = null,
		source = "LOCAL",
		sourceUrl = null,
		language = language,
		isSynced = false,
		isTranslation = false,
		dateAdded = System.currentTimeMillis()
	)
}

fun createSyncedLyrics(
	songId: Long,
	lrcContent: String,
	plainFallback: String? = null,
	source: String = "LOCAL"
): LyricsEntity {
	return LyricsEntity(
		lyricsId = 0,
		songId = songId,
		plainLyrics = plainFallback,
		syncedLyrics = lrcContent,
		source = source,
		sourceUrl = null,
		language = null,
		isSynced = true,
		isTranslation = false,
		dateAdded = System.currentTimeMillis()
	)
}

fun createOnlineLyrics(
	songId: Long,
	plainText: String,
	sourceUrl: String,
	sourceName: String = "ONLINE",
	language: String? = null
): LyricsEntity {
	return LyricsEntity(
		lyricsId = 0,
		songId = songId,
		plainLyrics = plainText.takeIf { it.isNotBlank() },
		syncedLyrics = null,
		source = sourceName.uppercase(),
		sourceUrl = sourceUrl,
		language = language,
		isSynced = false,
		isTranslation = false,
		dateAdded = System.currentTimeMillis()
	)
}

fun createNotSearchedPlaceholder(songId: Long): LyricsEntity {
	return LyricsEntity(
		lyricsId = 0,
		songId = songId,
		plainLyrics = null,
		syncedLyrics = null,
		source = "NOT_SEARCHED",
		sourceUrl = null,
		language = null,
		isSynced = false,
		isTranslation = false,
		dateAdded = System.currentTimeMillis()
	)
}

fun createSearchingPlaceholder(songId: Long): LyricsEntity {
	return LyricsEntity(
		lyricsId = 0,
		songId = songId,
		plainLyrics = null,
		syncedLyrics = null,
		source = "SEARCHING",
		sourceUrl = null,
		language = null,
		isSynced = false,
		isTranslation = false,
		dateAdded = System.currentTimeMillis()
	)
}

fun createNotFoundRecord(songId: Long): LyricsEntity {
	return LyricsEntity(
		lyricsId = 0,
		songId = songId,
		plainLyrics = null,
		syncedLyrics = null,
		source = "NOT_FOUND",
		sourceUrl = null,
		language = null,
		isSynced = false,
		isTranslation = false,
		dateAdded = System.currentTimeMillis()
	)
}

// ══════════════════════════════════════════════════════════════════════════════
// UTILIDADES
// ══════════════════════════════════════════════════════════════════════════════

val LyricsEntity.hasContent: Boolean
	get() = !plainLyrics.isNullOrBlank() || !syncedLyrics.isNullOrBlank()

val LyricsEntity.hasSyncedContent: Boolean
	get() = isSynced && !syncedLyrics.isNullOrBlank()

val LyricsEntity.isFromOnlineSource: Boolean
	get() = source.uppercase() !in listOf(
		"EMBEDDED", "LOCAL", "NOT_SEARCHED", "SEARCHING", "NOT_FOUND", "ERROR", ""
	)

val LyricsEntity.sourceDomain: String?
	get() = sourceUrl?.let { url ->
		runCatching {
			url.removePrefix("https://")
				.removePrefix("http://")
				.substringBefore("/")
				.substringBefore("?")
		}.getOrNull()
	}