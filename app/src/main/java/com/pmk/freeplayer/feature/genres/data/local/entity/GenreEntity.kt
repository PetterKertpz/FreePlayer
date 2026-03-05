package com.pmk.freeplayer.feature.genres.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
   tableName = "genres",
   indices =
      [
         Index(value = ["name"], unique = true),
         Index(value = ["normalized_name"]), // case-insensitive search via UPPER()
         Index(value = ["song_count"]), // ORDER BY popularity
      ],
)
data class GenreEntity(
   @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "genre_id") val genreId: Long = 0,

   // ── Identity ──────────────────────────────────────────────────
   @ColumnInfo(name = "name") val name: String,
   @ColumnInfo(name = "normalized_name") val normalizedName: String, // always UPPERCASE
   @ColumnInfo(name = "description") val description: String? = null,

   // ── Visual ────────────────────────────────────────────────────
   @ColumnInfo(name = "hex_color") val hexColor: String? = null,
   @ColumnInfo(name = "icon_url") val remoteIconUrl: String? = null,
   @ColumnInfo(name = "icon_path") val localIconPath: String? = null,

   // ── Structural cache ──────────────────────────────────────────
   // FIX: play_count removed — behavioral metric delegated to feature/statistics
   @ColumnInfo(name = "song_count") val songCount: Int = 0,
   @ColumnInfo(name = "artist_count") val artistCount: Int = 0,
   @ColumnInfo(name = "album_count") val albumCount: Int = 0,

   // ── Extra metadata ────────────────────────────────────────────
   @ColumnInfo(name = "origin_decade") val originDecade: String? = null,
   @ColumnInfo(name = "origin_country") val originCountry: String? = null,

   // ── Timestamps (FIX: no System.currentTimeMillis() defaults) ──
   @ColumnInfo(name = "date_added") val dateAdded: Long,
   @ColumnInfo(name = "last_updated") val lastUpdated: Long,
)
