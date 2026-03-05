package com.pmk.freeplayer.feature.statistics.domain.model

/**
 * Immutable fact: a single playback occurrence.
 * Never mutated after creation — the log is append-only.
 */
data class PlayEvent(
	val id: Long = 0,
	
	// ── What was played ───────────────────────────────────────────
	val songId: Long,
	val artistId: Long?,
	val albumId: Long?,
	val genreId: Long?,
	val playlistId: Long?,          // null if not played from a playlist
	
	// ── When & how long ───────────────────────────────────────────
	val playedAt: Long,             // epoch ms
	val listenedMs: Long,           // actual time the user listened
	val songDurationMs: Long,       // snapshot of song duration at play time
	
	// ── Behavior signals ──────────────────────────────────────────
	val source: PlaySource,
	val wasSkipped: Boolean,        // skipped before 80% completion
	
	// ── Derived (computed at record time, not on read) ─────────────
	val completionRatio: Float,     // listenedMs / songDurationMs, clamped 0..1
)

/** A play counts as meaningful if listened past 80%. */
val PlayEvent.isComplete: Boolean get() = completionRatio >= 0.80f