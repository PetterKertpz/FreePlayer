package com.pmk.freeplayer.domain.model.config

// --- Preferencias Generales ---
enum class ModoTema {
   CLARO,
   OSCURO,
   SISTEMA,
}

enum class IdiomaApp(val codigoIso: String) {
   SISTEMA("system"),
   ESPANOL("es"),
   INGLES("en"),
   PORTUGUES("pt"),
}

// --- Visualización y Listas ---
enum class CriterioOrdenamiento {
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

enum class DireccionOrdenamiento {
   ASCENDENTE,
   DESCENDENTE,
}

data class Ordenamiento(
   val criterio: CriterioOrdenamiento,
   val direccion: DireccionOrdenamiento = DireccionOrdenamiento.ASCENDENTE,
)

enum class TipoListaSistema {
   FAVORITOS,
   RECIENTES,
   MAS_REPRODUCIDAS,
   AGREGADAS_RECIENTE,
}

// --- Reproducción y Audio ---
enum class ModoRepeticion {
   DESACTIVADO,
   UNA,
   TODO,
}

enum class PresetEcualizador {
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
enum class ColorAcento {
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

enum class TamanioFuente {
   PEQUENIO,
   MEDIANO,
   GRANDE,
   MUY_GRANDE,
}
