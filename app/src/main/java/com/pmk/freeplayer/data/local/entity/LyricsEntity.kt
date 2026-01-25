package com.pmk.freeplayer.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "lyrics",
	foreignKeys = [
		ForeignKey(
			entity = SongEntity::class,
			parentColumns = ["song_id"], // Debe coincidir con SongEntity
			childColumns = ["song_id"],
			onDelete = ForeignKey.CASCADE // Si borras la canción, adiós letras
		)
	],
	indices = [
		Index(value = ["song_id"], unique = true), // 1 Canción = 1 Letra principal
		Index(value = ["source"])
	]
)
data class LyricsEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "lyrics_id") val lyricsId: Long = 0,
	
	@ColumnInfo(name = "song_id") val songId: Long,
	
	// --- Contenido ---
	@ColumnInfo(name = "plain_lyrics") val plainLyrics: String?, // Texto para leer
	@ColumnInfo(name = "synced_lyrics") val syncedLyrics: String? = null, // Formato LRC nativo
	
	// --- Metadatos ---
	@ColumnInfo(name = "source") val source: String = "MANUAL", // "GENIUS", "LOCAL", "MUSIXMATCH"
	@ColumnInfo(name = "source_url") val sourceUrl: String? = null,
	@ColumnInfo(name = "language") val language: String? = null, // "en", "es"
	
	// --- Flags ---
	@ColumnInfo(name = "is_synced") val isSynced: Boolean = false,
	@ColumnInfo(name = "is_translation") val isTranslation: Boolean = false,
	
	@ColumnInfo(name = "date_added") val dateAdded: Long = System.currentTimeMillis()
)