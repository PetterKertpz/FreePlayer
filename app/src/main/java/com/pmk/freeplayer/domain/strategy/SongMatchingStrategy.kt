package com.pmk.freeplayer.domain.strategy

import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.Genius
import com.pmk.freeplayer.domain.pipeline.ResultadoComparacion

interface SongMatchingStrategy {
	fun calcularCoincidencia(
		song: Song,
		candidato: Genius
	): ResultadoComparacion
}