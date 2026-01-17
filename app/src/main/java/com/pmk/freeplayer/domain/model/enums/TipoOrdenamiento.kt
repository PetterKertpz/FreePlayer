package com.pmk.freeplayer.domain.model.enums

enum class TipoOrdenamiento(val nombreMostrar: String) {
    TITULO_ASC("Título A-Z"),
    TITULO_DESC("Título Z-A"),
    ARTISTA_ASC("Artista A-Z"),
    ARTISTA_DESC("Artista Z-A"),
    ALBUM_ASC("Álbum A-Z"),
    ALBUM_DESC("Álbum Z-A"),
    DURACION_ASC("Duración (cortas primero)"),
    DURACION_DESC("Duración (largas primero)"),
    FECHA_AGREGADO_ASC("Fecha (antiguas primero)"),
    FECHA_AGREGADO_DESC("Fecha (recientes primero)"),
    ANIO_ASC("Año (antiguas primero)"),
    ANIO_DESC("Año (recientes primero)"),
    MAS_REPRODUCIDAS("Más reproducidas"),
    ULTIMA_REPRODUCCION("Última reproducción"),
    TAMANIO_ASC("Tamaño (pequeños primero)"),
    TAMANIO_DESC("Tamaño (grandes primero)")
}