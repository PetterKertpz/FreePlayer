package com.pmk.freeplayer.feature.player.data.service

import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommands
import com.pmk.freeplayer.feature.player.data.observer.MetadataPlaybackObserver
import com.pmk.freeplayer.feature.player.data.player.PlayerController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// feature/player/data/service/PlayerService.kt — reemplaza el actual

@AndroidEntryPoint
class PlayerService : MediaSessionService() {
	
	@Inject
	lateinit var controller: PlayerController
	@Inject lateinit var metadataObserver: MetadataPlaybackObserver  // ← nuevo
	private var mediaSession: MediaSession? = null
	
	override fun onCreate() {
		super.onCreate()
		// ExoPlayer es @Singleton — ya existe, solo lo envolvemos en MediaSession
		mediaSession = MediaSession.Builder(this, controller.exoPlayer)
			.setCallback(MediaSessionCallback())
			.build()
		metadataObserver.start()    // ← nuevo
		
	}
	
	override fun onGetSession(
		controllerInfo: MediaSession.ControllerInfo,
	): MediaSession? = mediaSession
	
	override fun onDestroy() {
		metadataObserver.stop()
		mediaSession?.run {
			player.release()
			release()
		}
		controller.release()
		mediaSession = null
		super.onDestroy()
	}
	
	// Permite a controles externos (Bluetooth, wearables, widgets) operar el player
	private inner class MediaSessionCallback : MediaSession.Callback {
		@OptIn(UnstableApi::class)
		override fun onConnect(
			session: MediaSession,
			controller: MediaSession.ControllerInfo,
		): MediaSession.ConnectionResult = MediaSession.ConnectionResult.accept(
			SessionCommands.EMPTY,
			Player.Commands.Builder().addAllCommands().build(),
		)
	}
}