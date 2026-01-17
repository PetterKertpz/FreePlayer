package com.pmk.freeplayer.domain.model

data class ResultadoComparacion(
	val puntuacionTitulo: Float,
	val puntuacionArtista: Float,
	val puntuacionAlbum: Float,
	val puntuacionTotal: Float,
	val esConfiable: Boolean
) {
	companion object {
		const val UMBRAL_CONFIABLE = 0.85f
	}
}
