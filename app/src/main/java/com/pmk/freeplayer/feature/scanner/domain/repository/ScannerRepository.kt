package com.pmk.freeplayer.feature.scanner.domain.repository

import com.pmk.freeplayer.core.domain.model.state.MediaProcessingState
import com.pmk.freeplayer.feature.scanner.domain.model.ScanMode
import com.pmk.freeplayer.feature.scanner.domain.model.ScanResult
import kotlinx.coroutines.flow.StateFlow

interface ScannerRepository {
	
	/** Current pipeline state — observed by UI and other features. */
	val scanState: StateFlow<MediaProcessingState>
	
	/** Last completed scan result. Null if no scan has run yet. */
	val lastScanResult: StateFlow<ScanResult?>
	
	/**
	 * Launches a scan with the given [mode].
	 * Emits [MediaProcessingState] updates during execution.
	 * Returns [ScanResult] on completion.
	 */
	suspend fun scan(mode: ScanMode): ScanResult
}