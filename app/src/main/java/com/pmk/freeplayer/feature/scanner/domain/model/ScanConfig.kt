package com.pmk.freeplayer.feature.scanner.domain.model

/**
 * Immutable snapshot of user preferences captured at scan start.
 * Passed through the entire pipeline so mid-scan preference changes
 * don't cause inconsistent behavior.
 */
data class ScanConfig(
	val minDurationMs: Long,
	val excludedPaths: Set<String>,
) {
	companion object {
		fun from(minDurationSeconds: Int, excludedPaths: Set<String>) = ScanConfig(
			minDurationMs = minDurationSeconds * 1_000L,
			excludedPaths = excludedPaths,
		)
	}
}