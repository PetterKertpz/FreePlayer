package com.pmk.freeplayer.domain.model.enums

enum class IntegrityStatus {
   CRUDO, // Metadatos extraídos sin procesar
   LIMPIO, // Metadatos procesados y validados
   ENRIQUECIDO, // Complementado con Genius + letra
}
enum class LyricsStatus {
	// Search states
	NOT_SEARCHED,  // Never searched
	SEARCHING,     // Currently searching...
	
	// Result states
	FOUND_EMBEDDED,  // Found in audio file tags
	FOUND_LOCAL,     // Found in .lrc file
	FOUND_ONLINE,    // Found via Genius/other API
	
	// Error states
	NOT_FOUND,       // Searched but not found
	ERROR,           // Connection/API error
}
enum class AudioFormat(val extension: String) {
   MP3("mp3"),
   FLAC("flac"),
   M4A("m4a"),
   OGG("ogg"),
   WAV("wav"),
   AAC("aac"),
   WMA("wma"),
   DESCONOCIDO("");

   companion object {
      fun desdeExtension(ext: String): AudioFormat =
         entries.find { it.extension.equals(ext, ignoreCase = true) } ?: DESCONOCIDO
   }
}

enum class AlbumType {
   ALBUM,
   EP,
   SINGLE,
   COMPILATION,
   LIVE,
   SOUNDTRACK,
   MIXTAPE,
   DEMO,
   UNKNOWN; // Valor de seguridad por defecto

   companion object {

      /**
       * Convierte un String (de la BD o tags) al Enum correspondiente de forma segura. Si no
       * encuentra coincidencia, devuelve UNKNOWN.
       */
      fun fromString(value: String?): AlbumType {
         if (value.isNullOrBlank()) return UNKNOWN
         return entries.find { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
      }

      /**
       * Regla de Negocio: Determina automáticamente el tipo de álbum basándose en la cantidad de
       * pistas y la duración total (en minutos). Útil cuando los metadatos del archivo no
       * especifican el tipo.
       */
      fun inferFromStats(songCount: Int, durationMinutes: Int): AlbumType {
         return when {
            songCount == 1 -> SINGLE
            songCount <= 6 && durationMinutes < 30 -> EP
            else -> ALBUM
         }
      }
   }
}

enum class SocialPlatform {
	// Streaming
	SPOTIFY,
	APPLE_MUSIC,
	YOUTUBE_MUSIC,
	SOUNDCLOUD,
	BANDCAMP,
	
	// Social media
	INSTAGRAM,
	TWITTER,
	TIKTOK,
	FACEBOOK,
	
	// Metadata
	GENIUS,
	DISCOGS,
	MUSICBRAINZ,
	
	// Other
	WEBSITE,
	UNKNOWN;

   companion object {
      fun fromString(value: String): SocialPlatform {
         return try {
            entries.first { it.name.equals(value, ignoreCase = true) }
         } catch (e: NoSuchElementException) {
            UNKNOWN
         }
      }
   }
}

enum class AuthType {
   LOCAL,
   GOOGLE,
   GUEST,
   UNKNOWN,
}

enum class PlaybackSource {
   LIBRARY,
   ALBUM,
   ARTIST,
   PLAYLIST,
   GENRE,
   SEARCH,
   FOLDER,
   FAVORITES,
   UNKNOWN,
}

enum class AudioOutput {
   SPEAKER,
   HEADPHONES,
   BLUETOOTH,
   CAST,
   AUX,
   UNKNOWN,
}

enum class ThemeMode {
   CLARO,
   OSCURO,
   SISTEMA,
}

enum class Language(val codigoIso: String) {
   SISTEMA("system"),
   ESPANOL("es"),
   INGLES("en"),
   PORTUGUES("pt"),
}

// --- Visualización y Listas ---
enum class SortField {
	NAME,
	ARTIST_NAME,      // Antes ARTIST
	ALBUM_COUNT,      // Antes ALBUM (Para artistas suele ser conteo)
	SONG_COUNT,       // Nuevo (Faltaba en el enum original pero se usaba en la lógica)
	DURATION,         // Antes DURACION
	DATE_ADDED,
	YEAR,             // Antes ANIO
	PLAY_COUNT,       // Antes REPRODUCCIONES
	LAST_PLAYED,      // Antes ULTIMA_REPRODUCCION
	SIZE              // Antes TAMANIO
}

enum class SortDirection {
	ASCENDING,
	DESCENDING
}

data class SortConfig(
	val field: SortField,
	val direction: SortDirection = SortDirection.ASCENDING,
)

enum class TipoListaSistema {
   FAVORITOS,
   RECIENTES,
   MAS_REPRODUCIDAS,
   AGREGADAS_RECIENTE,
}

// --- Reproducción y Audio ---
enum class RepeatMode {
   OFF,
   ONE,
   ALL,
}

enum class EqualizerPreset {
   PLANO,
   ROCK,
   POP,
   JAZZ,
   CLASICA,
   HIP_HOP,
   ELECTRONICA,
   BASS_BOOST,
   TREBLE_BOOST,
   VOCAL,
   PERSONALIZADO,
}

// --- Apariencia ---
enum class AccentColor {
   PREDETERMINADO,
   ROJO,
   ROSA,
   AZUL,
   CIAN,
   VERDE_AZULADO,
   VERDE,
   NARANJA,
   AMBAR,
}

enum class FontSize {
   PEQUENIO,
   MEDIANO,
   GRANDE,
   MUY_GRANDE,
}
enum class LogLevel {
	DEBUG,
	INFO,
	WARNING,
	ERROR,
}
