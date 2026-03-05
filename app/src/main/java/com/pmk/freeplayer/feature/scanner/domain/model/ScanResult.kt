package com.pmk.freeplayer.feature.scanner.domain.model

data class ScanResult(
	val mode: ScanMode,
	val added: Int        = 0,
	val updated: Int      = 0,
	val removed: Int      = 0,
	val skipped: Int      = 0,
	val durationMs: Long  = 0L,
) {
	val total: Int get() = added + updated + removed
	val isEmpty: Boolean get() = total == 0
}