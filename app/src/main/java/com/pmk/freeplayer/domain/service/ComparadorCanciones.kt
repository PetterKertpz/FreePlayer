package com.pmk.freeplayer.domain.service

import com.pmk.freeplayer.domain.model.Cancion
import com.pmk.freeplayer.domain.model.GeniusMetadata
import com.pmk.freeplayer.domain.model.ResultadoComparacion

interface ComparadorCanciones {
	fun calcularCoincidencia(
		cancion: Cancion,
		candidato: GeniusMetadata
	): ResultadoComparacion
}