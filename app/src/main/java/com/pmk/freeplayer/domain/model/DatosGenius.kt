package com.pmk.freeplayer.domain.model

data class DatosGenius(
	val geniusId: Long,
	val titulo: String,
	val artista: String,
	val artistasSecundarios: List<String>,
	val album: String?,
	val fechaLanzamiento: String?,
	val url: String,
	val portadaUrl: String?,
	val descripcion: String?
)