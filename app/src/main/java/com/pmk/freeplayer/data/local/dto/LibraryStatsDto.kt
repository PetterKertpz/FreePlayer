package com.pmk.freeplayer.data.local.dto

import androidx.room.ColumnInfo

// Único e irrepetible: El resumen total de tu app
data class LibraryStatsDto(
   @ColumnInfo(name = "total_songs") val totalSongs: Int,
   @ColumnInfo(name = "total_artists") val totalArtists: Int,
   @ColumnInfo(name = "total_albums") val totalAlbums: Int,
   @ColumnInfo(name = "total_genres") val totalGenres: Int,
   @ColumnInfo(name = "total_duration_ms") val totalDurationMs: Long,

   // Conteos específicos
   @ColumnInfo(name = "count_crude") val countCrude: Int,
   @ColumnInfo(name = "count_clean") val countClean: Int,
   @ColumnInfo(name = "count_enriched") val countEnriched: Int,
   @ColumnInfo(name = "count_lyrics") val countLyrics: Int,
   @ColumnInfo(name = "last_added") val lastAdded: Long?,
   @ColumnInfo(name = "total_size") val totalSizeBytes: Long,
)

// Optimización: Para listas de "Lo más escuchado" donde no quieres cargar objetos pesados
data class TopItemDto(
   @ColumnInfo(name = "id") val id: Long,
   @ColumnInfo(name = "name") val name: String,
   @ColumnInfo(name = "subtitle") val subtitle: String?, // Ej: Nombre del artista en una canción
   @ColumnInfo(name = "count") val value: Int, // Puede ser playCount, songCount, etc.
   @ColumnInfo(name = "image_uri") val imageUri: String?,
)
