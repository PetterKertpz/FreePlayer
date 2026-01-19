package com.pmk.freeplayer.domain.model.audio

enum class EstadoIntegridad {
   CRUDO, // Metadatos extraídos sin procesar
   LIMPIO, // Metadatos procesados y validados
   ENRIQUECIDO, // Complementado con Genius + letra
}

enum class EstadoLetra {
   NO_BUSCADA,
   NO_ENCONTRADA,
   ENCONTRADA_INCRUSTADA, // Antes FuenteLetra.INCRUSTADA
   ENCONTRADA_LOCAL, // Antes FuenteLetra.ARCHIVO_LOCAL
   ENCONTRADA_ONLINE, // Antes FuenteLetra.EN_LINEA
}

enum class FormatoAudio(val extension: String) {
   MP3("mp3"),
   FLAC("flac"),
   M4A("m4a"),
   OGG("ogg"),
   WAV("wav"),
   AAC("aac"),
   WMA("wma"),
   DESCONOCIDO("");

   companion object {
      fun desdeExtension(ext: String): FormatoAudio =
         entries.find { it.extension.equals(ext, ignoreCase = true) } ?: DESCONOCIDO
   }
}
