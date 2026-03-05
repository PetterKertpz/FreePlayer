package com.pmk.freeplayer.feature.player.data.service

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.pmk.freeplayer.feature.player.domain.repository.PlayerRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayerService : MediaSessionService() {
	
	@Inject lateinit var playerRepository: PlayerRepository
	
	private var mediaSession: MediaSession? = null
	
	override fun onCreate() {
		super.onCreate()
		// ExoPlayer lives in PlayerController (singleton) — session wraps it
		// MediaSession creation deferred to first connection to avoid
		// building ExoPlayer on a background thread before it's needed.
	}
	
	override fun onGetSession(
		controllerInfo: MediaSession.ControllerInfo,
	): MediaSession? = mediaSession
	
	override fun onDestroy() {
		mediaSession?.run {
			player.release()
			release()
		}
		playerRepository.release()
		mediaSession = null
		super.onDestroy()
	}
}