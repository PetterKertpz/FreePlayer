package com.pmk.freeplayer.core.domain.model.enums

// ─────────────────────────────────────────────────────────────────────────────
// Rule: only enums used by 2+ distinct features live here.
// ─────────────────────────────────────────────────────────────────────────────

// ── songs, albums, scanner ────────────────────────────────────────────────────
enum class AudioFormat(val extension: String) {
   MP3("mp3"),
   FLAC("flac"),
   M4A("m4a"),
   OGG("ogg"),
   WAV("wav"),
   AAC("aac"),
   WMA("wma"),
   UNKNOWN("") {
      override fun toString() = "Unknown"
   };

   companion object {
      fun from(ext: String): AudioFormat =
         entries.find { it.extension.equals(ext, ignoreCase = true) } ?: UNKNOWN
   }
}

// ── songs, albums, artists, genres, playlists (list sorting) ─────────────────
enum class SortField {
   NAME,
   ARTIST_NAME,
   ALBUM_COUNT,
   SONG_COUNT,
   DURATION,
   DATE_ADDED,
   YEAR,
   SIZE,
   // PLAY_COUNT  → removed: use GetMostPlayedUseCase(EntityType) from statistics
   // LAST_PLAYED → removed: use GetRecentlyPlayedUseCase(EntityType) from statistics
}

enum class SortDirection {
   ASCENDING,
   DESCENDING,
}

data class SortConfig(val field: SortField, val direction: SortDirection = SortDirection.ASCENDING)

// ── auth, statistics ──────────────────────────────────────────────────────────
enum class AuthType {
   LOCAL,
   GOOGLE,
   GUEST,
   UNKNOWN,
}

// ── feature/settings + feature/player ────────────────────────────────────────
// Moved to core because both features reference them.
// settings stores the default; player uses the runtime value.
enum class RepeatMode {
   OFF,
   ONE,
   ALL,
}

enum class EqualizerPreset {
   FLAT,
   ROCK,
   POP,
   JAZZ,
   CLASSICAL,
   BASS_BOOST,
   VOCAL,
   CUSTOM,
}

// ── feature/settings ─────────────────────────────────────────────────────────
enum class ThemeMode {
   LIGHT,
   DARK,
   SYSTEM,
}

enum class Language(val isoCode: String) {
   SYSTEM("system"),
   SPANISH("es"),
   ENGLISH("en"),
}

enum class FontSize {
   SMALL,
   MEDIUM,
   LARGE,
   EXTRA_LARGE,
}

enum class AccentColor {
   DEFAULT,
   RED,
   PINK,
   BLUE,
   CYAN,
   TEAL,
   GREEN,
   ORANGE,
   AMBER,
}
