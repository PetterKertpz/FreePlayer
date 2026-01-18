package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.enums.Genero

data class DatosLimpieza(
	val titulo: String,
	val artista: String,
	val album: String,
	val albumArtista: String? = null,
	val genero: Genero? = null,
	val anio: Int? = null,
	val numeroPista: Int? = null
)