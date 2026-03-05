package com.pmk.freeplayer.feature.statistics.data.observer

import com.pmk.freeplayer.app.di.IoDispatcher
import com.pmk.freeplayer.feature.player.domain.model.PlayerState
import com.pmk.freeplayer.feature.player.domain.repository.PlayerRepository
import com.pmk.freeplayer.feature.statistics.domain.model.PlayEvent
import com.pmk.freeplayer.feature.statistics.domain.repository.StatisticsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackEventObserver @Inject constructor(
	private val playerRepository: PlayerRepository,
	private val statisticsRepository: StatisticsRepository,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
	private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
	
	/**
	 * Starts observing [PlayerRepository.playerState].
	 * Called once at app startup from [StatisticsModule].
	 * Each [PlayerState.TrackEnded] is converted to a [PlayEvent] and persisted.
	 */
	fun start() {
		playerRepository.playerState
			.filterIsInstance<PlayerState.TrackEnded>()
			.onEach { ended -> recordEvent(ended) }
			.launchIn(scope)
	}
	
	private fun recordEvent(ended: PlayerState.TrackEnded) {
		scope.launch {
			val item     = ended.item
			val duration = item.durationMs.coerceAtLeast(1L)
			val listened = ended.listenedMs.coerceAtMost(duration)
			
			val event = PlayEvent(
				songId          = item.songId,
				artistId        = item.artistId,
				albumId         = item.albumId,
				genreId         = item.genreId,
				playlistId      = item.playlistId,
				playedAt        = System.currentTimeMillis(),
				listenedMs      = listened,
				songDurationMs  = duration,
				source          = item.source,
				wasSkipped      = ended.wasSkipped,
				completionRatio = (listened / duration.toFloat()).coerceIn(0f, 1f),
			)
			statisticsRepository.recordPlay(event)
		}
	}
}