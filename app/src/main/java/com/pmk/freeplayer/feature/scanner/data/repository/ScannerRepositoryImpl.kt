package com.pmk.freeplayer.feature.scanner.data.repository

import com.pmk.freeplayer.app.di.IoDispatcher
import com.pmk.freeplayer.core.domain.model.state.MediaProcessingState
import com.pmk.freeplayer.feature.scanner.data.scanner.ScanOrchestrator
import com.pmk.freeplayer.feature.scanner.domain.model.ScanConfig
import com.pmk.freeplayer.feature.scanner.domain.model.ScanMode
import com.pmk.freeplayer.feature.scanner.domain.model.ScanResult
import com.pmk.freeplayer.feature.scanner.domain.repository.ScannerRepository
import com.pmk.freeplayer.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScannerRepositoryImpl @Inject constructor(
	private val orchestrator: ScanOrchestrator,
	private val settingsRepository: SettingsRepository,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ScannerRepository {
	
	private val _scanState    = MutableStateFlow<MediaProcessingState>(MediaProcessingState.Idle)
	private val _lastResult   = MutableStateFlow<ScanResult?>(null)
	private val mutex         = Mutex() // prevents concurrent scans
	
	override val scanState:      StateFlow<MediaProcessingState> = _scanState.asStateFlow()
	override val lastScanResult: StateFlow<ScanResult?>          = _lastResult.asStateFlow()
	
	override suspend fun scan(mode: ScanMode): ScanResult = mutex.withLock {
		withContext(ioDispatcher) {
			try {
				val prefs  = settingsRepository.getPreferences().first()
				val config = ScanConfig.from(
					minDurationSeconds = prefs.minDurationSeconds,
					excludedPaths      = prefs.excludedPaths,
				)
				
				val result = orchestrator.execute(mode, config, _scanState)
				_lastResult.value = result
				_scanState.value  = MediaProcessingState.Completed
				result
			} catch (e: Exception) {
				val failure = MediaProcessingState.Failed(
					code    = "SCAN_ERROR",
					message = e.message ?: "Unknown scan error",
					cause   = e,
				)
				_scanState.value = failure
				throw e
			}
		}
	}
}