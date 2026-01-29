package com.pmk.freeplayer.data.repository

import android.util.Log
import com.pmk.freeplayer.core.cleaner.MetadataCleaner
import com.pmk.freeplayer.data.local.dao.ScannerDao
import com.pmk.freeplayer.data.local.dao.SongDao
import com.pmk.freeplayer.data.local.source.DeviceMusicDataSource
import com.pmk.freeplayer.data.mapper.toDomain
import com.pmk.freeplayer.data.mapper.toEntity
import com.pmk.freeplayer.domain.model.TrackDuration
import com.pmk.freeplayer.domain.model.scanner.CleaningResult
import com.pmk.freeplayer.domain.model.scanner.EnrichmentResult
import com.pmk.freeplayer.domain.model.scanner.ScanPhase
import com.pmk.freeplayer.domain.model.scanner.ScanProgress
import com.pmk.freeplayer.domain.model.scanner.ScanResult
import com.pmk.freeplayer.domain.repository.ScannerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScannerRepositoryImpl @Inject constructor(
	private val scannerDao: ScannerDao,
	private val songDao: SongDao,
	private val deviceScanner: DeviceMusicDataSource,
	private val metadataCleaner: MetadataCleaner
) : ScannerRepository {
	
	companion object {
		private const val TAG = "ScannerRepository"
		private const val BATCH_SIZE = 50
	}
	
	// ═══════════════════════════════════════════════════════════════
	// SCAN STATE (Observable)
	// ═══════════════════════════════════════════════════════════════
	
	private val _scanProgress = MutableStateFlow(ScanProgress.idle())
	override val scanProgress: StateFlow<ScanProgress> = _scanProgress.asStateFlow()
	
	private val _isScanning = MutableStateFlow(false)
	override val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()
	
	private val scanMutex = Mutex()
	
	// ═══════════════════════════════════════════════════════════════
	// FULL SCAN EXECUTION
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun performFullScan(): Result<ScanResult> = withContext(Dispatchers.IO) {
		// Prevent concurrent scans
		if (!scanMutex.tryLock()) {
			return@withContext Result.failure(ScanInProgressException())
		}
		
		_isScanning.value = true
		val startTime = System.currentTimeMillis()
		var errorCount = 0
		
		try {
			// ─────────────────────────────────────────────────────────
			// PHASE 1: Scan device files
			// ─────────────────────────────────────────────────────────
			updateProgress(ScanPhase.SCANNING, 0f, "Escaneando dispositivo...")
			
			val deviceSongs = deviceScanner.scanLocalSongs { progress ->
				updateProgress(ScanPhase.SCANNING, progress * 0.3f, "Leyendo archivos...")
			}.getOrElse { e ->
				Log.e(TAG, "Device scan failed", e)
				errorCount++
				emptyList()
			}
			
			if (deviceSongs.isEmpty()) {
				val emptyResult = buildScanResult(startTime, 0, 0, 0, 0, 0, errorCount)
				saveScanResult(emptyResult)
				updateProgress(ScanPhase.COMPLETED, 1f, "No se encontraron canciones")
				return@withContext Result.success(emptyResult)
			}
			
			val totalFound = deviceSongs.size
			Log.i(TAG, "Found $totalFound songs on device")
			
			// ─────────────────────────────────────────────────────────
			// PHASE 2: Load existing database state
			// ─────────────────────────────────────────────────────────
			updateProgress(ScanPhase.ANALYZING, 0.3f, "Analizando biblioteca...")
			
			val existingScanInfo = songDao.getScanInfo().associateBy { it.filePath }
			val scannedPaths = deviceSongs.map { it.filePath }.toSet()
			
			// ─────────────────────────────────────────────────────────
			// PHASE 3: Categorize songs
			// ─────────────────────────────────────────────────────────
			val newSongs = deviceSongs.filter { it.filePath !in existingScanInfo }
			val existingSongs = deviceSongs.filter { it.filePath in existingScanInfo }
			val pathsToDelete = existingScanInfo.keys - scannedPaths
			
			Log.i(TAG, "New: ${newSongs.size}, Existing: ${existingSongs.size}, To delete: ${pathsToDelete.size}")
			
			// ─────────────────────────────────────────────────────────
			// PHASE 4: Insert new songs (batch)
			// ─────────────────────────────────────────────────────────
			updateProgress(ScanPhase.INSERTING, 0.4f, "Agregando nuevas canciones...")
			
			var insertedCount = 0
			val totalNewBatches = (newSongs.size / BATCH_SIZE) + 1
			
			newSongs.chunked(BATCH_SIZE).forEachIndexed { batchIndex, batch ->
				yield() // Allow cancellation
				
				try {
					songDao.insertAll(batch)
					insertedCount += batch.size
				} catch (e: Exception) {
					Log.e(TAG, "Batch insert failed, trying one by one", e)
					// Fallback: insert one by one
					batch.forEach { song ->
						try {
							songDao.insert(song)
							insertedCount++
						} catch (e2: Exception) {
							Log.w(TAG, "Failed to insert: ${song.title}", e2)
							errorCount++
						}
					}
				}
				
				val progress = 0.4f + (batchIndex.toFloat() / totalNewBatches) * 0.2f
				updateProgress(ScanPhase.INSERTING, progress, "Agregando: $insertedCount/${newSongs.size}")
			}
			
			// ─────────────────────────────────────────────────────────
			// PHASE 5: Update existing songs (smart update)
			// ─────────────────────────────────────────────────────────
			updateProgress(ScanPhase.UPDATING, 0.6f, "Actualizando metadatos...")
			
			var updatedCount = 0
			val totalExistingBatches = (existingSongs.size / BATCH_SIZE) + 1
			
			existingSongs.chunked(BATCH_SIZE).forEachIndexed { batchIndex, batch ->
				yield()
				
				batch.forEach { scannedSong ->
					try {
						val existing = existingScanInfo[scannedSong.filePath]
						// Only update if file was modified (size changed)
						if (existing != null && existing.fileSize != scannedSong.size) {
							songDao.safeUpsert(scannedSong)
							updatedCount++
						}
					} catch (e: Exception) {
						Log.w(TAG, "Failed to update: ${scannedSong.title}", e)
						errorCount++
					}
				}
				
				val progress = 0.6f + (batchIndex.toFloat() / totalExistingBatches) * 0.2f
				updateProgress(ScanPhase.UPDATING, progress, "Actualizando: $updatedCount")
			}
			
			// ─────────────────────────────────────────────────────────
			// PHASE 6: Delete orphaned songs
			// ─────────────────────────────────────────────────────────
			updateProgress(ScanPhase.CLEANING, 0.8f, "Limpiando biblioteca...")
			
			var deletedCount = 0
			
			if (pathsToDelete.isNotEmpty()) {
				pathsToDelete.chunked(BATCH_SIZE).forEach { batch ->
					yield()
					try {
						songDao.deleteByPaths(batch.toList())
						deletedCount += batch.size
					} catch (e: Exception) {
						Log.e(TAG, "Failed to delete batch", e)
						errorCount++
					}
				}
			}
			
			// Also clean invalid entries
			try {
				songDao.cleanInvalidSongs()
			} catch (e: Exception) {
				Log.w(TAG, "Failed to clean invalid songs", e)
			}
			updateProgress(ScanPhase.FINALIZING, 0.9f, "Limpiando metadatos...")
			
			val cleaningResult = metadataCleaner.cleanBatch(limit = insertedCount)
			
			Log.i(TAG, "Cleaning: ${cleaningResult.cleaned} cleaned, ${cleaningResult.unchanged} unchanged")
			// ─────────────────────────────────────────────────────────
			// PHASE 7: Finalize and save result
			// ─────────────────────────────────────────────────────────
			updateProgress(ScanPhase.FINALIZING, 0.95f, "Finalizando...")
			
			val result = buildScanResult(
				startTime = startTime,
				totalFound = totalFound,
				newCount = insertedCount,
				updatedCount = updatedCount,
				duplicatesIgnored = newSongs.size - insertedCount,
				deletedCount = deletedCount,
				errorCount = errorCount
			)
			
			saveScanResult(result)
			updateProgress(ScanPhase.COMPLETED, 1f, "Completado: ${result.cancionesNuevas} nuevas")
			
			Log.i(TAG, "Scan completed: $result")
			Result.success(result)
			
		} catch (e: Exception) {
			Log.e(TAG, "Scan failed unexpectedly", e)
			updateProgress(ScanPhase.ERROR, 0f, e.message ?: "Error desconocido")
			Result.failure(e)
		} finally {
			_isScanning.value = false
			scanMutex.unlock()
		}
	}
	
	// ═══════════════════════════════════════════════════════════════
	// QUICK SCAN (Only new files)
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun performQuickScan(): Result<ScanResult> = withContext(Dispatchers.IO) {
		if (!scanMutex.tryLock()) {
			return@withContext Result.failure(ScanInProgressException())
		}
		
		_isScanning.value = true
		val startTime = System.currentTimeMillis()
		
		try {
			updateProgress(ScanPhase.SCANNING, 0.2f, "Escaneo rápido...")
			
			val deviceSongs = deviceScanner.scanLocalSongs().getOrThrow()
			val existingPaths = songDao.getAllFilePaths().toSet()
			val newSongs = deviceSongs.filter { it.filePath !in existingPaths }
			
			updateProgress(ScanPhase.INSERTING, 0.6f, "Agregando ${newSongs.size} canciones...")
			
			var insertedCount = 0
			if (newSongs.isNotEmpty()) {
				newSongs.chunked(BATCH_SIZE).forEach { batch ->
					songDao.insertAll(batch)
					insertedCount += batch.size
				}
			}
			
			val result = buildScanResult(
				startTime = startTime,
				totalFound = deviceSongs.size,
				newCount = insertedCount,
				updatedCount = 0,
				duplicatesIgnored = 0,
				deletedCount = 0,
				errorCount = 0
			)
			
			saveScanResult(result)
			updateProgress(ScanPhase.COMPLETED, 1f, "Completado")
			
			Result.success(result)
		} catch (e: Exception) {
			Log.e(TAG, "Quick scan failed", e)
			updateProgress(ScanPhase.ERROR, 0f, e.message ?: "Error")
			Result.failure(e)
		} finally {
			_isScanning.value = false
			scanMutex.unlock()
		}
	}
	
	// ═══════════════════════════════════════════════════════════════
	// HELPERS
	// ═══════════════════════════════════════════════════════════════
	
	private fun buildScanResult(
		startTime: Long,
		totalFound: Int,
		newCount: Int,
		updatedCount: Int,
		duplicatesIgnored: Int,
		deletedCount: Int,
		errorCount: Int
	): ScanResult {
		val durationMs = System.currentTimeMillis() - startTime
		return ScanResult(
			id = 0,
			fecha = System.currentTimeMillis(),
			archivosDetectados = totalFound,
			cancionesNuevas = newCount,
			cancionesActualizadas = updatedCount,
			duplicadosIgnorados = duplicatesIgnored,
			archivosEliminados = deletedCount,
			errores = errorCount,
			tiempoMs = TrackDuration(durationMs)
		)
	}
	
	private fun updateProgress(phase: ScanPhase, progress: Float, message: String) {
		_scanProgress.value = ScanProgress(
			phase = phase,
			progress = progress.coerceIn(0f, 1f),
			message = message
		)
	}
	
	// ═══════════════════════════════════════════════════════════════
	// SCAN HISTORY
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun saveScanResult(result: ScanResult): Long =
		scannerDao.insertScanResult(result.toEntity())
	
	override fun getScanHistory(limit: Int): Flow<List<ScanResult>> =
		scannerDao.getScanHistory(limit).map { list -> list.map { it.toDomain() } }
	
	override suspend fun getLastScan(): ScanResult? =
		scannerDao.getLastScan()?.toDomain()
	
	override suspend fun clearScanHistory(keepLast: Int) =
		scannerDao.clearScanHistory(keepLast)
	
	// ═══════════════════════════════════════════════════════════════
	// CLEANING HISTORY
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun saveCleaningResult(result: CleaningResult): Long =
		scannerDao.insertCleaningResult(result.toEntity())
	
	override fun getCleaningHistory(limit: Int): Flow<List<CleaningResult>> =
		scannerDao.getCleaningHistory(limit).map { list -> list.map { it.toDomain() } }
	
	override suspend fun getLastCleaning(): CleaningResult? =
		scannerDao.getLastCleaning()?.toDomain()
	
	override suspend fun clearCleaningHistory(keepLast: Int) =
		scannerDao.clearCleaningHistory(keepLast)
	
	// ═══════════════════════════════════════════════════════════════
	// ENRICHMENT HISTORY
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun saveEnrichmentResult(result: EnrichmentResult): Long =
		scannerDao.insertEnrichmentResult(result.toEntity())
	
	override fun getEnrichmentHistory(limit: Int): Flow<List<EnrichmentResult>> =
		scannerDao.getEnrichmentHistory(limit).map { list -> list.map { it.toDomain() } }
	
	override suspend fun getLastEnrichment(): EnrichmentResult? =
		scannerDao.getLastEnrichment()?.toDomain()
	
	override suspend fun clearEnrichmentHistory(keepLast: Int) =
		scannerDao.clearEnrichmentHistory(keepLast)
	
	// ═══════════════════════════════════════════════════════════════
	// ACCUMULATED STATISTICS
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun getTotalScans(): Int = scannerDao.getTotalScans()
	override suspend fun getTotalScannedSongs(): Int = scannerDao.getTotalScannedSongs()
	override suspend fun getTotalCleanedSongs(): Int = scannerDao.getTotalCleanedSongs()
	override suspend fun getTotalEnrichedSongs(): Int = scannerDao.getTotalEnrichedSongs()
	override suspend fun getTotalLyricsObtained(): Int = scannerDao.getTotalLyricsObtained()
	
	// ═══════════════════════════════════════════════════════════════
	// PROCESSING TIME
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun getTotalProcessingTime(): TrackDuration =
		TrackDuration(scannerDao.getTotalProcessingTime())
	
	override suspend fun getScanProcessingTime(): TrackDuration =
		TrackDuration(scannerDao.getScanProcessingTime())
	
	override suspend fun getCleaningProcessingTime(): TrackDuration =
		TrackDuration(scannerDao.getCleaningProcessingTime())
	
	override suspend fun getEnrichmentProcessingTime(): TrackDuration =
		TrackDuration.ZERO
}

// ═══════════════════════════════════════════════════════════════
// EXCEPTIONS
// ═══════════════════════════════════════════════════════════════

class ScanInProgressException : Exception("Ya hay un escaneo en progreso")