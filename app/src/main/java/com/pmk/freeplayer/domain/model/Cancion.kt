package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.enums.EstadoCancion
import com.pmk.freeplayer.domain.model.enums.EstadoLetra
import com.pmk.freeplayer.domain.model.enums.Genero

data class Cancion(
	val id: Long,
	
	// --- Metadatos de contenido ---
	val titulo: String,
	val artista: String,
	val album: String,
	val albumId: Long,
	val albumArtista: String?,              // A veces difiere del artista
	val numeroPista: Int?,
	val anio: Int?,
	val genero: Genero,
	val duracion: Duracion,                     // milisegundos
	val portadaUri: String?,
	
	// --- Datos del archivo ---
	val ruta: String,
	val nombreArchivo: String,
	val tamanioArchivo: Tamanio,               // bytes
	val tipoMime: String,                   // audio/mpeg, audio/flac, etc.
	val tasaBits: Int,                      // kbps
	val tasaMuestreo: Int,                  // Hz
	val hash: String,                       // Para detectar duplicados
	
	// --- Estados ---
	val estado: EstadoCancion = EstadoCancion.CRUDO,
	val estadoLetra: EstadoLetra = EstadoLetra.NO_BUSCADA,
	
	// --- Genius (null hasta ENRIQUECIDO) ---
	val geniusId: Long? = null,
	val geniusUrl: String? = null,
	
	// --- Letra (null hasta ENCONTRADA) ---
	val letra: String? = null,
	
	// --- Timestamps ---
	val fechaEscaneo: Long,
	val fechaModificado: Long,
	val fechaLimpieza: Long? = null,
	val fechaEnriquecimiento: Long? = null,
	
	// --- Reproducción ---
	val esFavorito: Boolean = false,
	val vecesReproducido: Int = 0,
	val ultimaReproduccion: Long? = null
) {
	val duracionFormateada: String get() = duracion.formatoCorto()
	val tamanioFormateado: String get() = tamanioArchivo.formateado()
	
	// Helpers útiles
	val estaLimpia: Boolean get() = estado != EstadoCancion.CRUDO
	val estaEnriquecida: Boolean get() = estado == EstadoCancion.ENRIQUECIDO
	val tieneLetra: Boolean get() = estadoLetra == EstadoLetra.ENCONTRADA
}