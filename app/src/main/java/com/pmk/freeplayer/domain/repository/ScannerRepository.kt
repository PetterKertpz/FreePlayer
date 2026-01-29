package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.TrackDuration
import com.pmk.freeplayer.domain.model.scanner.CleaningResult
import com.pmk.freeplayer.domain.model.scanner.EnrichmentResult
import com.pmk.freeplayer.domain.model.scanner.ScanProgress
import com.pmk.freeplayer.domain.model.scanner.ScanResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ScannerRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// SCAN STATE (NEW)
	// ═══════════════════════════════════════════════════════════════
	
	/** Current scan progress - observable from UI */
	val scanProgress: StateFlow<ScanProgress>
	
	/** Whether a scan is currently running */
	val isScanning: StateFlow<Boolean>
	
	// ═══════════════════════════════════════════════════════════════
	// SCAN OPERATIONS (NEW)
	// ═══════════════════════════════════════════════════════════════
	
	/**
	 * Performs a full scan:
	 * - Detects new files on device
	 * - Updates existing entries if file changed
	 * - Removes entries for deleted files
	 */
	suspend fun performFullScan(): Result<ScanResult>
	
	/**
	 * Performs a quick scan:
	 * - Only adds new files
	 * - Does NOT update or clean existing entries
	 * - Faster for routine syncs
	 */
	suspend fun performQuickScan(): Result<ScanResult>
	
	// ═══════════════════════════════════════════════════════════════
	// SCAN HISTORY
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun saveScanResult(result: ScanResult): Long
	fun getScanHistory(limit: Int = 20): Flow<List<ScanResult>>
	suspend fun getLastScan(): ScanResult?
	suspend fun clearScanHistory(keepLast: Int = 10)
	
	// ═══════════════════════════════════════════════════════════════
	// CLEANING
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun saveCleaningResult(result: CleaningResult): Long
	fun getCleaningHistory(limit: Int = 20): Flow<List<CleaningResult>>
	suspend fun getLastCleaning(): CleaningResult?
	suspend fun clearCleaningHistory(keepLast: Int = 10)
	
	// ═══════════════════════════════════════════════════════════════
	// ENRICHMENT
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun saveEnrichmentResult(result: EnrichmentResult): Long
	fun getEnrichmentHistory(limit: Int = 20): Flow<List<EnrichmentResult>>
	suspend fun getLastEnrichment(): EnrichmentResult?
	suspend fun clearEnrichmentHistory(keepLast: Int = 10)
	
	// ═══════════════════════════════════════════════════════════════
	// ACCUMULATED STATISTICS
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun getTotalScans(): Int
	suspend fun getTotalScannedSongs(): Int
	suspend fun getTotalCleanedSongs(): Int
	suspend fun getTotalEnrichedSongs(): Int
	suspend fun getTotalLyricsObtained(): Int
	
	// ═══════════════════════════════════════════════════════════════
	// PROCESSING TIME
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun getTotalProcessingTime(): TrackDuration
	suspend fun getScanProcessingTime(): TrackDuration
	suspend fun getCleaningProcessingTime(): TrackDuration
	suspend fun getEnrichmentProcessingTime(): TrackDuration
	
	// ═══════════════════════════════════════════════════════════════
	// BULK CLEANUP
	// ═══════════════════════════════════════════════════════════════
	
	suspend fun clearAllHistory(keepLast: Int = 10) {
		clearScanHistory(keepLast)
		clearCleaningHistory(keepLast)
		clearEnrichmentHistory(keepLast)
	}
}