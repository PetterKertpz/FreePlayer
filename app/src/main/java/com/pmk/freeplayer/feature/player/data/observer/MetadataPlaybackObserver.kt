package com.pmk.freeplayer.feature.player.data.observer

import com.pmk.freeplayer.app.di.IoDispatcher
import com.pmk.freeplayer.feature.metadata.domain.usecase.ProcessSongOnPlaybackUseCase
import com.pmk.freeplayer.feature.player.domain.model.PlayerState
import com.pmk.freeplayer.feature.player.domain.repository.PlayerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// feature/player/data/observer/MetadataPlaybackObserver.kt

@Singleton
class MetadataPlaybackObserver @Inject constructor(
	private val playerRepository: PlayerRepository,
	private val processSongOnPlayback: ProcessSongOnPlaybackUseCase,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
	private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
	private var lastProcessedId = -1L
	
	fun start() {
		scope.launch {
			playerRepository.playerState
				.filterIsInstance<PlayerState.Playing>()
				.map { it.currentItem.songId }
				.distinctUntilChanged()           // solo dispara cuando cambia la canción
				.filter { it != lastProcessedId }
				.collect { songId ->
					lastProcessedId = songId
					// Fire-and-forget — no bloquea la reproducción
					launch { processSongOnPlayback(songId) }
				}
		}
	}
	
	fun stop() = scope.cancel()
}