package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.scanner.EnrichmentResult

interface EnrichmentRepository {
	
	/**
	 * Busca metadatos y letras en fuentes externas (Genius), valida la coincidencia
	 * y guarda los resultados localmente.
	 *
	 * @param song La canción local que se desea enriquecer.
	 * @return El resultado del proceso de enriquecimiento (éxito/fallo, scores, etc).
	 */
	suspend fun enrichSong(song: Song): EnrichmentResult
}