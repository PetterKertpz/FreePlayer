package com.pmk.freeplayer.domain.model

data class ListaReproduccion(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val canciones: List<Cancion>,
    val portadaUri: String?,                // portada personalizada
    val tipoSistema: TipoListaSistema?,     // null si es creada por usuario
    val creadoEn: Long,
    val actualizadoEn: Long
) {
    val cantidadCanciones: Int
        get() = canciones.size

    val duracionTotal: Long
        get() = canciones.sumOf { it.duracion }

    val estaVacia: Boolean
        get() = canciones.isEmpty()

    val esDelSistema: Boolean
        get() = tipoSistema != null

    val duracionFormateada: String
        get() {
            val horas = (duracionTotal / 1000) / 3600
            val minutos = ((duracionTotal / 1000) % 3600) / 60
            return if (horas > 0) "${horas}h ${minutos}min" else "${minutos} min"
        }
}

// Listas predefinidas del sistema
enum class TipoListaSistema(val nombreMostrar: String, val icono: String) {
    FAVORITOS("Favoritos", "heart"),
    REPRODUCIDAS_RECIENTEMENTE("Reproducidas recientemente", "history"),
    MAS_REPRODUCIDAS("Más reproducidas", "trending_up"),
    AGREGADAS_RECIENTEMENTE("Agregadas recientemente", "schedule")
}