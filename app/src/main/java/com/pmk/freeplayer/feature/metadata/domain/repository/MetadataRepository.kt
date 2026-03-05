package com.pmk.freeplayer.feature.metadata.domain.repository

import com.pmk.freeplayer.feature.metadata.domain.model.CleanedSongData
import com.pmk.freeplayer.feature.metadata.domain.model.GeniusSongResult
import com.pmk.freeplayer.feature.metadata.domain.model.MetadataConfig
import com.pmk.freeplayer.feature.songs.domain.model.MetadataStatus
import kotlinx.coroutines.flow.Flow

// feature/metadata/domain/repository/MetadataRepository.kt

interface MetadataRepository {
	
	/** Punto de entrada del pipeline. Llamado por el Player al reproducir. */
	suspend fun processSongOnPlayback(songId: Long): Result<Unit>
	
	/** Stage 1 — offline-safe. */
	suspend fun cleanSongMetadata(songId: Long): Result<CleanedSongData>
	
	/** Stage 2 — requiere red. */
	suspend fun enrichSongFromGenius(songId: Long): Result<GeniusSongResult>
	
	/** Marca una canción como Skipped. No se vuelve a procesar. */
	suspend fun skipSong(songId: Long): Result<Unit>
	
	/** Reinicia el estado a Raw para forzar reprocesamiento. */
	suspend fun resetSong(songId: Long): Result<Unit>
	
	/** Cuenta canciones pendientes por estado — para UI de progreso en Settings. */
	suspend fun countByStatus(status: MetadataStatus): Int
	
	val config: Flow<MetadataConfig>
}