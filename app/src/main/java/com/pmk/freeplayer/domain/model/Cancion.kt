
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
	val duracion: Long,                     // milisegundos
	val portadaUri: String?,
	
	// --- Datos del archivo ---
	val ruta: String,
	val nombreArchivo: String,
	val tamanioArchivo: Long,               // bytes
	val tipoMime: String,                   // audio/mpeg, audio/flac, etc.
	val tasaBits: Int,                      // kbps
	val tasaMuestreo: Int,                  // Hz
	val hash: String,                       // NUEVO: Para detectar duplicados
	
	// --- Estados ---
	val estado: EstadoCancion = EstadoCancion.CRUDO,
	val estadoLetra: EstadoLetra = EstadoLetra.NO_BUSCADA,
	
	// --- Genius (null hasta ENRIQUECIDO) ---
	val geniusId: Long? = null,
	val geniusUrl: String? = null,
	
	// --- Letra (null hasta ENCONTRADA) ---
	val letra: String? = null,
	
	// --- Timestamps ---
	val fechaEscaneo: Long,                 // Antes: fechaAgregado
	val fechaModificado: Long,
	val fechaLimpieza: Long? = null,        // NUEVO
	val fechaEnriquecimiento: Long? = null, // NUEVO
	
	// --- Reproducción ---
	val esFavorito: Boolean = false,
	val vecesReproducido: Int = 0,
	val ultimaReproduccion: Long? = null
) {
	val duracionFormateada: String
		get() {
			val minutos = (duracion / 1000) / 60
			val segundos = (duracion / 1000) % 60
			return "$minutos:${segundos.toString().padStart(2, '0')}"
		}
	
	val tamanioFormateado: String
		get() {
			val mb = tamanioArchivo / (1024.0 * 1024.0)
			return "%.1f MB".format(mb)
		}
	
	// NUEVO: Helpers útiles
	val estaLimpia: Boolean get() = estado != EstadoCancion.CRUDO
	val estaEnriquecida: Boolean get() = estado == EstadoCancion.ENRIQUECIDO
	val tieneLetra: Boolean get() = estadoLetra == EstadoLetra.ENCONTRADA
}