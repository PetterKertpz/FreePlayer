package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.enums.TipoListaSistema

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
	
	val duracionTotal: Duracion
		get() = canciones.sumarDuracion { it.duracion }

    val estaVacia: Boolean
        get() = canciones.isEmpty()

    val esDelSistema: Boolean
        get() = tipoSistema != null
	
	val duracionFormateada: String
		get() = duracionTotal.formatoLargo()
}
