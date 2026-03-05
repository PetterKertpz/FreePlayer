package com.pmk.freeplayer.core.domain.model.state

/**
 * Shared processing state for scanner and metadata features.
 * Emitted via StateFlow — consumed by any UI observing background work.
 */
sealed interface MediaProcessingState {
	
	data object Idle : MediaProcessingState
	
	// ── Scanner states ────────────────────────────────────────────
	data class Scanning(val filesProcessed: Int) : MediaProcessingState
	data class Saving(val itemsUpdated: Int) : MediaProcessingState
	
	// ── Metadata pipeline states ──────────────────────────────────
	data class ExtractingMetadata(val currentFile: String) : MediaProcessingState
	data class Cleaning(val field: String) : MediaProcessingState
	data class FetchingFromGenius(val songTitle: String) : MediaProcessingState
	data class ScrapingLyrics(val source: String) : MediaProcessingState
	
	// ── Terminal states ───────────────────────────────────────────
	data class Failed(
		val code: String,
		val message: String,
		val cause: Throwable? = null,
	) : MediaProcessingState
	
	data object Completed : MediaProcessingState
}