// app/initializer/AppStartupInitializer.kt
package com.pmk.freeplayer.app

import com.pmk.freeplayer.app.di.IoDispatcher
import com.pmk.freeplayer.feature.scanner.data.scanner.ContentObserverManager
import com.pmk.freeplayer.feature.scanner.domain.model.ScanMode
import com.pmk.freeplayer.feature.scanner.domain.repository.ScannerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppStartupInitializer @Inject constructor(
	private val scannerRepository: ScannerRepository,
	private val contentObserverManager: ContentObserverManager,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
	private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
	
	fun initialize() {
		// 1. Auto scan — solo si hay delta
		scope.launch {
			scannerRepository.scan(ScanMode.Auto)
		}
		
		// 2. Smart scan — observa cambios en MediaStore en tiempo real
		contentObserverManager.audioChanges
			.onEach { scannerRepository.scan(ScanMode.Smart) }
			.launchIn(scope)
	}
}