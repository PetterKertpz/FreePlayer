package com.pmk.freeplayer.feature.scanner.domain.usecase

import com.pmk.freeplayer.core.domain.model.state.MediaProcessingState
import com.pmk.freeplayer.feature.scanner.domain.model.ScanMode
import com.pmk.freeplayer.feature.scanner.domain.model.ScanResult
import com.pmk.freeplayer.feature.scanner.domain.repository.ScannerRepository
import com.pmk.freeplayer.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class RunScanUseCase @Inject constructor(
	private val repository: ScannerRepository,
) {
	suspend operator fun invoke(mode: ScanMode = ScanMode.Manual): ScanResult =
		repository.scan(mode)
}
class ObserveScanStateUseCase @Inject constructor(
	private val repository: ScannerRepository,
) {
	operator fun invoke(): StateFlow<MediaProcessingState> = repository.scanState
}
class GetLastScanResultUseCase @Inject constructor(
	private val repository: ScannerRepository,
) {
	operator fun invoke(): StateFlow<ScanResult?> = repository.lastScanResult
}
class ManageExcludedPathsUseCase @Inject constructor(
	private val settingsRepository: SettingsRepository,
) {
	suspend fun add(path: String)    = settingsRepository.addExcludedPath(path)
	suspend fun remove(path: String) = settingsRepository.removeExcludedPath(path)
}