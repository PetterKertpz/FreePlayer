package com.pmk.freeplayer.feature.scanner.domain.model

sealed interface ScanMode {
	/** On app start — compares DB vs MediaStore, only processes delta. */
	data object Auto : ScanMode
	
	/** Real-time — triggered by ContentObserver on MediaStore.Audio.Media. */
	data object Smart : ScanMode
	
	/** User-triggered — full scan ignoring already-indexed paths. */
	data object Manual : ScanMode
}