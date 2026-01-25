package com.pmk.freeplayer.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "genres",
	indices = [
		Index(value = ["name"], unique = true), // Nombres únicos
		Index(value = ["normalized_name"]),     // Para búsquedas rápidas (rock == ROCK)
		Index(value = ["song_count"])           // Para ordenar por popularidad
	]
)
data class GenreEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "genre_id") val genreId: Long = 0,
	
	// --- Identidad ---
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "normalized_name") val normalizedName: String, // Guardamos la versión limpia aquí
	@ColumnInfo(name = "description") val description: String? = null,
	
	// --- Visualización (Rich UI) ---
	@ColumnInfo(name = "hex_color") val hexColor: String? = null, // "#FF5733"
	@ColumnInfo(name = "icon_url") val remoteIconUrl: String? = null,
	@ColumnInfo(name = "icon_path") val localIconPath: String? = null,
	
	// --- Estadísticas ---
	@ColumnInfo(name = "song_count") val songCount: Int = 0,
	@ColumnInfo(name = "artist_count") val artistCount: Int = 0,
	@ColumnInfo(name = "album_count") val albumCount: Int = 0,
	@ColumnInfo(name = "play_count") val playCount: Int = 0,
	
	// --- Clasificación Extra ---
	@ColumnInfo(name = "origin_decade") val originDecade: String? = null, // "80s", "90s"
	@ColumnInfo(name = "origin_country") val originCountry: String? = null,
	
	// --- Metadata ---
	@ColumnInfo(name = "date_added") val dateAdded: Long = System.currentTimeMillis(),
	@ColumnInfo(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis()
)