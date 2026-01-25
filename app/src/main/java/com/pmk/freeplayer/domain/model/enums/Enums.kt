package com.pmk.freeplayer.domain.model.enums

enum class IntegrityStatus {
   CRUDO, // Metadatos extraídos sin procesar
   LIMPIO, // Metadatos procesados y validados
   ENRIQUECIDO, // Complementado con Genius + letra
}

enum class LetterStatus {
   NO_BUSCADA,
   NO_ENCONTRADA,
   ENCONTRADA_INCRUSTADA, // Antes FuenteLetra.INCRUSTADA
   ENCONTRADA_LOCAL, // Antes FuenteLetra.ARCHIVO_LOCAL
   ENCONTRADA_ONLINE, // Antes FuenteLetra.EN_LINEA
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
   INSTAGRAM,
   TWITTER, // O "X"
   FACEBOOK,
   YOUTUBE,
   SPOTIFY,
   SOUNDCLOUD,
   WEBSITE,
   TIKTOK,
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

enum class LyricsStatus {
   SEARCHING, // "Buscando..."
   FOUND, // "Letra encontrada"
   NOT_FOUND, // "No se encontró letra"
   ERROR, // "Error de conexión"
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
enum class SortCryterio {
   TITULO,
   ARTISTA,
   ALBUM,
   DURACION,
   FECHA_AGREGADO,
   ANIO,
   REPRODUCCIONES,
   ULTIMA_REPRODUCCION,
   TAMANIO,
}

enum class SortDirection {
   ASCENDENTE,
   DESCENDENTE,
}

data class SortConfiguration(
	val criterio: SortCryterio,
	val direccion: SortDirection = SortDirection.ASCENDENTE,
)

enum class TipoListaSistema {
   FAVORITOS,
   RECIENTES,
   MAS_REPRODUCIDAS,
   AGREGADAS_RECIENTE,
}

// --- Reproducción y Audio ---
enum class RepeatMode {
   DESACTIVADO,
   UNA,
   TODO,
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
