package com.pmk.freeplayer.domain.model

import com.pmk.freeplayer.domain.model.enums.Genero

data class Cancion(
    val id: Long,
    val titulo: String,
    val artista: String,
    val album: String,
    val albumId: Long,
    val duracion: Long,                     // milisegundos
    val ruta: String,                       // ruta del archivo en el dispositivo
    val nombreArchivo: String,
    val tamanioArchivo: Long,               // bytes
    val tipoMime: String,                   // audio/mpeg, audio/flac, etc.
    val tasaBits: Int,                      // kbps (bitrate)
    val tasaMuestreo: Int,                  // Hz (44100, 48000, etc.)
    val numeroPista: Int?,                  // número de pista en el álbum
    val anio: Int?,                         // año de lanzamiento
    val genero: Genero,
    val portadaUri: String?,                // URI de la portada del álbum
    val fechaAgregado: Long,                // timestamp
    val fechaModificado: Long,
    val esFavorito: Boolean = false,
    val vecesReproducido: Int = 0,
    val ultimaReproduccion: Long? = null
) {
    // Duración formateada (ej: "3:45")
    val duracionFormateada: String
        get() {
            val minutos = (duracion / 1000) / 60
            val segundos = (duracion / 1000) % 60
            return "$minutos:${segundos.toString().padStart(2, '0')}"
        }

    // Tamaño formateado (ej: "4.5 MB")
    val tamanioFormateado: String
        get() {
            val mb = tamanioArchivo / (1024.0 * 1024.0)
            return "%.1f MB".format(mb)
        }
}