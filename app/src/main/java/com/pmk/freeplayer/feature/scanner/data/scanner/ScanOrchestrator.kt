package com.pmk.freeplayer.feature.scanner.data.scanner

import com.pmk.freeplayer.core.domain.model.state.MediaProcessingState
import com.pmk.freeplayer.feature.albums.data.local.dao.AlbumDao
import com.pmk.freeplayer.feature.artists.data.local.dao.ArtistDao
import com.pmk.freeplayer.feature.genres.data.local.dao.GenreDao
import com.pmk.freeplayer.feature.scanner.domain.model.ScanConfig
import com.pmk.freeplayer.feature.scanner.domain.model.ScanMode
import com.pmk.freeplayer.feature.scanner.domain.model.ScanResult
import com.pmk.freeplayer.feature.songs.data.local.dao.SongDao
import com.pmk.freeplayer.feature.songs.data.mapper.createScannedSongEntity
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanOrchestrator @Inject constructor(
	private val mediaStoreReader: MediaStoreReader,
	private val coverExtractor: CoverExtractor,
	private val songDao: SongDao,
	private val artistDao: ArtistDao,
	private val albumDao: AlbumDao,
	private val genreDao: GenreDao,
) {
	
	/**
	 * Executes the full scan pipeline for the given [mode] and [config].
	 * Emits [MediaProcessingState] updates into [stateFlow] throughout execution.
	 * Returns a [ScanResult] with final counters.
	 */
	suspend fun execute(
		mode: ScanMode,
		config: ScanConfig,
		stateFlow: MutableStateFlow<MediaProcessingState>,
	): ScanResult {
		val now = System.currentTimeMillis()
		var added = 0; var updated = 0; var removed = 0; var skipped = 0
		
		// ── 1. Read filesystem ────────────────────────────────────
		stateFlow.value = MediaProcessingState.Scanning(0)
		val rawFiles = mediaStoreReader.readAll(config)
		
		// ── 2. Load DB index for delta detection ──────────────────
		val dbIndex: Map<String, SongDao.SongScanInfo> = songDao
			.getAllScanInfo()
			.associateBy { it.filePath }
		
		val fsPathSet = rawFiles.map { it.filePath }.toHashSet()
		
		// ── 3. Detect removed files ───────────────────────────────
		val removedPaths = dbIndex.keys.filter { it !in fsPathSet }
		if (removedPaths.isNotEmpty()) {
			songDao.deleteByPaths(removedPaths)
			removed = removedPaths.size
		}
		
		// ── 4. Determine files to process based on ScanMode ───────
		val toProcess = when (mode) {
			ScanMode.Auto -> rawFiles.filter { raw ->
				val existing = dbIndex[raw.filePath]
				existing == null || existing.dateModified != raw.dateModified
			}
			ScanMode.Smart -> rawFiles.filter { raw ->
				dbIndex[raw.filePath] == null
			}
			ScanMode.Manual -> rawFiles.filter { raw ->
				dbIndex[raw.filePath] == null
			}
		}
		
		skipped = rawFiles.size - toProcess.size
		
		// ── 5. Process each file ──────────────────────────────────
		toProcess.forEachIndexed { index, raw ->
			stateFlow.value = MediaProcessingState.Scanning(index + 1)
			
			// Resolve artist / album / genre — getOrCreate is @Transaction safe
			val artistId = artistDao.getOrCreate(raw.artistName, now)
			val albumId  = albumDao.getOrCreate(raw.albumName, artistId, raw.artistName, raw.year, now)
			val genreId  = raw.genreName?.let { genreDao.getOrCreate(it, now) }
			
			// Extract cover art
			val coverPath = coverExtractor.extract(raw.filePath)
			
			val entity = createScannedSongEntity(
				filePath    = raw.filePath,
				title       = raw.title,
				artistName  = raw.artistName,
				durationMs  = raw.durationMs,
				sizeBytes   = raw.sizeBytes,
				mimeType    = raw.mimeType,
				artistId    = artistId,
				albumId     = albumId,
				genreId     = genreId,
				trackNumber = raw.trackNumber,
				discNumber  = raw.discNumber,
				year        = raw.year,
				bitrate     = raw.bitrate,
				sampleRate  = raw.sampleRate,
				hasCover    = coverPath != null,
				now         = now,
			).copy(
				albumName    = raw.albumName,
				dateModified = raw.dateModified,
			)
			
			// safeUpsert preserves isFavorite / rating on re-scan
			val isNew = dbIndex[raw.filePath] == null
			songDao.safeUpsert(entity)
			if (isNew) added++ else updated++
		}
		
		// ── 6. Refresh structural counters ────────────────────────
		stateFlow.value = MediaProcessingState.Saving(added + updated)
		artistDao.refreshAllStats(now)
		albumDao.refreshAllStats(now)
		genreDao.refreshAllStats(now)
		
		// Clean up entities with no songs left after deletions
		if (removed > 0) {
			artistDao.deleteEmpty()
			albumDao.deleteEmpty()
			genreDao.deleteEmpty()
		}
		
		return ScanResult(
			mode     = mode,
			added    = added,
			updated  = updated,
			removed  = removed,
			skipped  = skipped,
			durationMs = System.currentTimeMillis() - now,
		)
	}
}