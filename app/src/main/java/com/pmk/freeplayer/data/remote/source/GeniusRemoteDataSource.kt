package com.pmk.freeplayer.data.remote.source

import com.pmk.freeplayer.data.remote.api.GeniusService
import com.pmk.freeplayer.data.remote.dto.GeniusDto
import com.pmk.freeplayer.data.remote.dto.GeniusSongRaw
import javax.inject.Inject

class GeniusRemoteDataSource @Inject constructor(
	private val api: GeniusService
) {
	
	/**
	 * Busca una canción y devuelve el DTO limpio listo para usar.
	 * Retorna null si hay error de red o no se encuentra nada.
	 */
	suspend fun searchSong(artist: String, title: String): GeniusDto? {
		// Limpieza básica del query para mejorar resultados en Genius
		val query = formatQuery(artist, title)
		
		return try {
			val response = api.searchSong(query)
			
			// 1. Verificaciones HTTP y de la API (Meta status 200)
			if (response.isSuccessful && response.body()?.meta?.status == 200) {
				
				val hits = response.body()?.response?.hits ?: emptyList()
				
				if (hits.isEmpty()) return null
				
				// 2. Lógica de "Mejor Coincidencia":
				// A veces Genius devuelve un cover o remix primero.
				// Intentamos encontrar uno donde el artista coincida con el buscado.
				val bestMatch: GeniusSongRaw? = hits
					.map { it.result }
					.firstOrNull { rawSong ->
						// Compara ignorando mayúsculas si el nombre del artista está contenido
						rawSong.primaryArtist.name.contains(artist, ignoreCase = true)
					} ?: hits.first().result // Fallback: Si no coincide exacto, toma el primero
				
				// 3. Conversión: Raw -> Clean DTO
				bestMatch?.let { GeniusDto.fromRaw(it) }
				
			} else {
				// Podrías agregar logs aquí si response.code() != 200
				null
			}
		} catch (e: Exception) {
			e.printStackTrace() // En producción usar Timber o Log
			null
		}
	}
	
	/**
	 * Formatea la búsqueda para el endpoint /search.
	 * Elimina espacios extra y junta artista + titulo.
	 */
	private fun formatQuery(artist: String, title: String): String {
		return "$artist $title".trim()
	}
}