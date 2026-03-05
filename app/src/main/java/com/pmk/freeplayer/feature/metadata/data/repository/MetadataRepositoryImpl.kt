package com.pmk.freeplayer.feature.metadata.data.repository

import com.pmk.freeplayer.app.di.IoDispatcher
import com.pmk.freeplayer.feature.albums.data.local.dao.AlbumDao
import com.pmk.freeplayer.feature.artists.data.local.dao.ArtistDao
import com.pmk.freeplayer.feature.metadata.data.cleaner.ArtistFieldSwapDetector
import com.pmk.freeplayer.feature.metadata.data.cleaner.MetadataCleaner
import com.pmk.freeplayer.feature.metadata.data.local.dao.LyricsDao
import com.pmk.freeplayer.feature.metadata.data.local.entity.LyricsEntity
import com.pmk.freeplayer.feature.metadata.data.local.entity.LyricsSource
import com.pmk.freeplayer.feature.metadata.data.mapper.toDomain
import com.pmk.freeplayer.feature.metadata.data.remote.GeniusDataSource
import com.pmk.freeplayer.feature.metadata.data.remote.store.MetadataConfigStore
import com.pmk.freeplayer.feature.metadata.domain.model.CleanedSongData
import com.pmk.freeplayer.feature.metadata.domain.model.GeniusSongResult
import com.pmk.freeplayer.feature.metadata.domain.model.MetadataConfig
import com.pmk.freeplayer.feature.metadata.domain.repository.MetadataRepository
import com.pmk.freeplayer.feature.songs.data.local.dao.SongDao
import com.pmk.freeplayer.feature.songs.data.mapper.toDomain
import com.pmk.freeplayer.feature.songs.data.mediasource.AudioFileDataSource
import com.pmk.freeplayer.feature.songs.domain.model.MetadataStatus
import com.pmk.freeplayer.feature.songs.domain.model.MetadataStatus.Companion.isRetryable
import com.pmk.freeplayer.feature.songs.domain.model.MetadataStatus.Companion.isTerminal
import com.pmk.freeplayer.feature.songs.domain.model.MetadataStatus.Companion.storageKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

// feature/metadata/data/repository/MetadataRepositoryImpl.kt

class MetadataRepositoryImpl @Inject constructor(
	private val songDao: SongDao,
	private val albumDao: AlbumDao,
	private val artistDao: ArtistDao,
	private val lyricsDao: LyricsDao,
	private val metadataCleaner: MetadataCleaner,
	private val geniusDataSource: GeniusDataSource,
	private val audioFileDataSource: AudioFileDataSource,
	private val metadataConfigStore: MetadataConfigStore,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : MetadataRepository {
	
	override val config: Flow<MetadataConfig> = metadataConfigStore.config
	
	// ── Pipeline entry point ──────────────────────────────────────
	
	override suspend fun processSongOnPlayback(songId: Long): Result<Unit> =
		withContext(ioDispatcher) {
			runCatching {
				val entity = songDao.getById(songId) ?: return@runCatching
				when {
					entity.metadataStatus.isTerminal() -> return@runCatching
					entity.metadataStatus is MetadataStatus.Failed &&
							!(entity.metadataStatus as MetadataStatus.Failed).isRetryable() -> return@runCatching
				}
				
				// Stage 1: siempre ejecutar (offline-safe)
				if (entity.metadataStatus == MetadataStatus.Raw) {
					cleanSongMetadata(songId).getOrThrow()
				}
				
				// Stage 2: solo si hay red
				if (isNetworkAvailable()) {
					enrichSongFromGenius(songId).getOrThrow()
				}
				// Si no hay red → se queda en Clean, se reintentará en próxima reproducción
			}
		}
	
	// ── Stage 1: Cleaning ─────────────────────────────────────────
	
	override suspend fun cleanSongMetadata(songId: Long): Result<CleanedSongData> =
		withContext(ioDispatcher) {
			runCatching {
				val entity = requireNotNull(songDao.getById(songId))
				val cleaned = metadataCleaner.clean(entity.title, entity.artistName)
					.copy(songId = songId)
				
				val now = System.currentTimeMillis()
				songDao.markAsClean(
					id          = songId,
					cleanTitle  = cleaned.cleanTitle,
					cleanArtist = cleaned.cleanArtist,
					confidence  = 1f,
					timestamp   = now,
				)
				// Actualizar featuringArtists y versionType (markAsClean no los cubre)
				val updated = entity.copy(
					featuringArtists = cleaned.featuringArtists.ifEmpty { null },
					versionType      = cleaned.versionType,
					dateModified     = now,
				)
				songDao.update(updated)
				
				// Escribir al archivo si Settings lo indica
				val cfg = metadataConfigStore.getCurrent()
				if (cfg.writeMetadataToFile) {
					audioFileDataSource.writeMetadataToFile(updated.toDomain())
				}
				
				cleaned
			}.recoverWithRetry(songId)
		}
	
	// ── Stage 2: Enrichment ───────────────────────────────────────
	
	override suspend fun enrichSongFromGenius(songId: Long): Result<GeniusSongResult> =
		withContext(ioDispatcher) {
			runCatching {
				val entity = requireNotNull(songDao.getById(songId))
				val cfg    = metadataConfigStore.getCurrent()
				
				val geniusDto = geniusDataSource.findSong(
					cleanTitle   = entity.title,
					cleanArtist  = entity.artistName,
					accessToken  = cfg.geniusAccessToken,
				) ?: run {
					songDao.updateMetadataStatus(songId, MetadataStatus.NotFound.storageKey())
					error("Not found on Genius: $songId")
				}
				
				val confidence = ArtistFieldSwapDetector.similarity(
					geniusDto.fullTitle, "${entity.title} by ${entity.artistName}"
				)
				val result = geniusDto.toDomain(confidence)
				
				// Letras
				val lyrics = geniusDataSource.fetchLyrics(result.geniusUrl)
				
				val now = System.currentTimeMillis()
				
				// Actualizar songs
				songDao.markAsEnriched(
					id         = songId,
					hasLyrics  = !lyrics.isNullOrBlank(),
					geniusId   = result.geniusId.toString(),
					geniusUrl  = result.geniusUrl,
					confidence = confidence,
					timestamp  = now,
				)
				// Actualizar externalIds (apple_music_id, recording_location, etc.)
				val existingSong = requireNotNull(songDao.getById(songId))
				val extraIds = buildMap {
					result.appleMusicId?.let { put("apple_music_id", it) }
					result.recordingLocation?.let { put("recording_location", it) }
					result.language?.let { put("language", it) }
				}
				songDao.update(existingSong.copy(
					externalIds  = (existingSong.externalIds ?: emptyMap()) + extraIds,
					dateModified = now,
				))
				
				// Persistir letras
				if (!lyrics.isNullOrBlank()) {
					lyricsDao.upsert(
						LyricsEntity(
							songId = songId,
							plainText = lyrics,
							language = result.language,
							source = LyricsSource.GENIUS_SCRAPE,
							fetchedAt = now,
							lastUpdated = now,
						)
					)
				}
				
				// Actualizar Album si hay datos
				result.albumId?.let { gAlbumId ->
					entity.albumId?.let { localAlbumId ->
						albumDao.getById(localAlbumId)?.let { album ->
							albumDao.update(album.copy(
								remoteCoverUrl = result.albumCoverUrl ?: album.remoteCoverUrl,
								producer       = result.producerArtists.firstOrNull() ?: album.producer,
								lastUpdated    = now,
							))
						}
					}
				}
				
				// Actualizar Artist si hay bio/imagen
				entity.artistId?.let { localArtistId ->
					if (!result.primaryArtistBio.isNullOrBlank() || result.primaryArtistImageUrl != null) {
						artistDao.getById(localArtistId)?.let { artist ->
							artistDao.update(artist.copy(
								biography      = result.primaryArtistBio?.take(2000) ?: artist.biography,
								remoteImageUrl = result.primaryArtistImageUrl ?: artist.remoteImageUrl,
								lastUpdated    = now,
							))
						}
					}
				}
				
				result
			}.recoverWithRetry(songId)
		}
	
	// ── Control ───────────────────────────────────────────────────
	
	override suspend fun skipSong(songId: Long): Result<Unit> =
		withContext(ioDispatcher) {
			runCatching {
				songDao.updateMetadataStatus(songId, MetadataStatus.Skipped.storageKey())
			}
		}
	
	override suspend fun resetSong(songId: Long): Result<Unit> =
		withContext(ioDispatcher) {
			runCatching {
				songDao.updateMetadataStatus(songId, MetadataStatus.Raw.storageKey(), confidence = 0f)
			}
		}
	
	override suspend fun countByStatus(status: MetadataStatus): Int =
		withContext(ioDispatcher) {
			songDao.countByStatus(status.storageKey())
		}
	
	// ── Retry helper ──────────────────────────────────────────────
	
	private suspend fun <T> Result<T>.recoverWithRetry(songId: Long): Result<T> {
		if (isSuccess) return this
		val entity = songDao.getById(songId) ?: return this
		val currentStatus = entity.metadataStatus
		val attempt = if (currentStatus is MetadataStatus.Failed) currentStatus.attempt else 0
		return if (attempt < 3) {
			songDao.updateMetadataStatus(
				songId,
				MetadataStatus.Failed(attempt + 1).storageKey(),
			)
			this
		} else {
			// 3 intentos fallidos → NotFound permanente
			songDao.updateMetadataStatus(songId, MetadataStatus.NotFound.storageKey())
			this
		}
	}
	
	private fun isNetworkAvailable(): Boolean = true // inyectar NetworkMonitor real
}