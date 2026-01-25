package com.pmk.freeplayer.domain.repository

import com.pmk.freeplayer.domain.model.ScanResult
import kotlinx.coroutines.flow.Flow

interface ScanRepository {
	
	// ─────────────────────────────────────────────────────────────
	// Scan results
	// ─────────────────────────────────────────────────────────────
	suspend fun saveScanResult(result: ScanResult): Long
	
	fun getScanHistory(limit: Int = 20): Flow<List<ScanResult>>
	
	suspend fun getLastScan(): ScanResult?
	
	// ─────────────────────────────────────────────────────────────
	// Accumulated statistics
	// ─────────────────────────────────────────────────────────────
	suspend fun getTotalScans(): Int
	
	suspend fun getTotalScannedSongs(): Int
	
	// ─────────────────────────────────────────────────────────────
	// Cleanup
	// ─────────────────────────────────────────────────────────────
	suspend fun clearScanHistory(keepLast: Int = 10)
}