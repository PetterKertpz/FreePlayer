package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.audio.EstadoLetra

data class LetraCancion(
	val cancionId: Long,
	val estado: EstadoLetra,
	val contenido: String?, // Texto plano
	val lineasSincronizadas: List<LineaLetra>?, // LRC
	val idioma: String?
) {
	val esSincronizada: Boolean get() = !lineasSincronizadas.isNullOrEmpty()
}

data class LineaLetra(
	val tiempoInicio: Long, // ms
	val texto: String,
	val traduccion: String? = null
)

// Datos crudos de API Genius
data class GeniusMetadata(
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