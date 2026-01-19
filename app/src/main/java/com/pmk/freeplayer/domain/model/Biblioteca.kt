package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.audio.EstadoIntegridad
import com.pmk.freeplayer.domain.model.audio.EstadoLetra
import com.pmk.freeplayer.domain.model.audio.Genero

data class Cancion(
	val id: Long,
	
	// --- Metadatos ---
	val titulo: String,
	val artista: String,
	val album: String,
	val albumId: Long,
	val albumArtista: String?,
	val numeroPista: Int?,
	val anio: Int?,
	val genero: Genero, // Usa el nuevo Value Class
	val duracion: Duracion,
	val portadaUri: String?,
	
	// --- Archivo ---
	val ruta: String,
	val nombreArchivo: String,
	val tamanioArchivo: Tamanio,
	val tipoMime: String,
	val tasaBits: Int,
	val tasaMuestreo: Int,
	val hash: String,
	
	// --- Estado e Integridad (ACTUALIZADO) ---
	val estadoIntegridad: EstadoIntegridad = EstadoIntegridad.CRUDO, // Antes EstadoCancion
	val estadoLetra: EstadoLetra = EstadoLetra.NO_BUSCADA,
	
	// --- Datos Externos ---
	val geniusId: Long? = null,
	val geniusUrl: String? = null,
	val letra: String? = null, // Letra cruda
	
	// --- Timestamps ---
	val fechaEscaneo: Long,
	val fechaModificado: Long,
	
	// --- Usuario ---
	val esFavorito: Boolean = false,
	val vecesReproducido: Int = 0,
	val ultimaReproduccion: Long? = null
) {
	// Helpers lógicos (no visuales)
	val estaLimpia: Boolean get() = estadoIntegridad != EstadoIntegridad.CRUDO
	val tieneLetra: Boolean get() = estadoLetra != EstadoLetra.NO_BUSCADA && estadoLetra != EstadoLetra.NO_ENCONTRADA
}

data class Album(
	val id: Long,
	val nombre: String,
	val artista: String,
	val cantidadCanciones: Int,
	val duracionTotal: Duracion,
	val anio: Int?,
	val portadaUri: String?,
	val fechaAgregado: Long,
	val genero: Genero?
)

data class Artista(
	val id: Long,
	val nombre: String,
	val cantidadCanciones: Int,
	val cantidadAlbumes: Int,
	val duracionTotal: Duracion,
	val imagenUri: String?
)

data class Carpeta(
	val ruta: String,
	val nombre: String,
	val cantidadCanciones: Int,
	val tamanioTotal: Tamanio,
	val estaOculta: Boolean = false
)