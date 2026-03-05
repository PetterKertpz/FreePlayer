package com.pmk.freeplayer.feature.albums.domain.model

enum class AlbumType {
   ALBUM,
   EP,
   SINGLE,
   COMPILATION,
   LIVE,
   SOUNDTRACK,
   MIXTAPE,
   DEMO,
   UNKNOWN;

   companion object {
      fun from(value: String?): AlbumType =
         if (value.isNullOrBlank()) UNKNOWN
         else entries.find { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN

      fun inferFromStats(songCount: Int, durationMinutes: Int): AlbumType =
         when {
            songCount == 1 -> SINGLE
            songCount <= 6 && durationMinutes < 30 -> EP
            else -> ALBUM
         }
   }
}
