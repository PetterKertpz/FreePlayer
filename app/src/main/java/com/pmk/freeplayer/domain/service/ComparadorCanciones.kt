package com.pmk.freeplayer.domain.service

import com.pmk.freeplayer.domain.model.Cancion
import com.pmk.freeplayer.domain.model.ResultadoBusquedaGenius
import com.pmk.freeplayer.domain.model.ResultadoComparacion

/**
 * Servicio de dominio para comparar canciones locales con resultados de Genius.
 * La implementación estará en la capa data/.
 */
interface ComparadorCanciones {
	
	/**
	 * Calcula el nivel de coincidencia entre una canción local y un resultado de Genius.
	 *
	 * @param cancion Canción local de la biblioteca
	 * @param resultado Resultado de búsqueda de Genius
	 * @return ResultadoComparacion con puntuaciones y nivel de confianza
	 */
	fun calcularCoincidencia(
		cancion: Cancion,
		resultado: ResultadoBusquedaGenius
	): ResultadoComparacion
}