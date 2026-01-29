package com.pmk.freeplayer.core.service

import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.pmk.freeplayer.core.service.mapper.toMediaItems
import com.pmk.freeplayer.domain.repository.PlayerRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaLibraryService() {
	
	@Inject lateinit var playerRepository: PlayerRepository
	@Inject lateinit var sessionCallback: MusicSessionCallback
	
	private lateinit var player: ExoPlayer
	// CAMBIO: El tipo ahora es MediaLibrarySession
	private lateinit var mediaSession: MediaLibrarySession
	
	private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
	
	override fun onCreate() {
		super.onCreate()
		initializePlayer()
		observeRepository()
	}
	
	private fun initializePlayer() {
		player = ExoPlayer.Builder(this)
			.setAudioAttributes(
				AudioAttributes.Builder()
					.setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
					.setUsage(C.USAGE_MEDIA)
					.build(),
				true
			)
			.setHandleAudioBecomingNoisy(true)
			.build()
		
		player.addListener(object : Player.Listener {
			override fun onPlaybackStateChanged(playbackState: Int) {
				if (playbackState == Player.STATE_ENDED) {
					serviceScope.launch { playerRepository.goToNext() }
				}
			}
		})
		
		// CAMBIO: Usamos MediaLibrarySession.Builder
		mediaSession = MediaLibrarySession.Builder(this, player, sessionCallback)
			.build()
	}
	
	private fun observeRepository() {
		serviceScope.launch {
			playerRepository.getCurrentQueue().collectLatest { queue ->
				if (queue.canciones.isNotEmpty()) {
					val mediaItems = queue.canciones.toMediaItems()
					
					if (player.mediaItemCount == 0 || player.currentMediaItem?.mediaId != queue.songActual?.id.toString()) {
						// Importante: No resetear si ya estamos reproduciendo lo correcto
						val currentId = player.currentMediaItem?.mediaId
						val targetId = queue.songActual?.id.toString()
						
						if (currentId != targetId) {
							player.setMediaItems(mediaItems, queue.indiceActual, C.TIME_UNSET)
							player.prepare()
						}
					}
				} else {
					player.clearMediaItems()
				}
			}
		}
		
		serviceScope.launch {
			playerRepository.isPlaying().distinctUntilChanged().collectLatest { shouldPlay ->
				if (shouldPlay && !player.isPlaying) {
					player.play()
				} else if (!shouldPlay && player.isPlaying) {
					player.pause()
				}
			}
		}
	}
	
	// CAMBIO: Ahora retornamos MediaLibrarySession? compatible con la clase padre
	override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
		return mediaSession
	}
	
	override fun onTaskRemoved(rootIntent: Intent?) {
		if (!player.playWhenReady || player.mediaItemCount == 0) {
			stopSelf()
		}
	}
	
	override fun onDestroy() {
		mediaSession.run {
			player.release()
			release()
		}
		serviceScope.cancel()
		super.onDestroy()
	}
}