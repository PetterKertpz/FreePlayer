package com.pmk.freeplayer.domain.model

data class ResultadoBusquedaGenius(
	val geniusId: Long,
	val titulo: String,
	val artista: String,
	val album: String?,
	val url: String,
	val portadaUrl: String?,
	val fechaLanzamiento: String?
)