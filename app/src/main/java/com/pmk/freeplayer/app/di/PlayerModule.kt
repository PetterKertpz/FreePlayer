package com.pmk.freeplayer.app.di

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import com.pmk.freeplayer.core.service.MusicService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// ════════════════════════════════════════════════════════════════════════════
// SINGLETON SCOPE - App-level dependencies
// ════════════════════════════════════════════════════════════════════════════

@Module
@InstallIn(SingletonComponent::class)
object PlayerAppModule {
	
	/**
	 * SessionToken para conectar UI con MusicService
	 * @Singleton porque el service es único en el proceso
	 */
	@Provides
	@Singleton
	fun provideSessionToken(
		@ApplicationContext context: Context
	): SessionToken {
		return SessionToken(
			context,
			ComponentName(context, MusicService::class.java)
		)
	}
}

// ════════════════════════════════════════════════════════════════════════════
// SERVICE SCOPE - Lifecycle tied to MusicService
// ════════════════════════════════════════════════════════════════════════════

@Module
@InstallIn(ServiceComponent::class)
object PlayerServiceModule {
	
	/**
	 * AudioAttributes para configuración de audio del player
	 */
	@Provides
	@ServiceScoped
	fun provideAudioAttributes(): AudioAttributes {
		return AudioAttributes.Builder()
			.setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
			.setUsage(C.USAGE_MEDIA)
			.build()
	}
	
	/**
	 * ExoPlayer engine - núcleo del reproductor
	 * @ServiceScoped: muere con el servicio, sobrevive rotaciones
	 */
	@Provides
	@ServiceScoped
	fun provideExoPlayer(
		@ApplicationContext context: Context,
		audioAttributes: AudioAttributes
	): ExoPlayer {
		return ExoPlayer.Builder(context)
			.setAudioAttributes(audioAttributes, true) // handleAudioFocus = true
			.setHandleAudioBecomingNoisy(true) // Pause on headphone disconnect
			.setWakeMode(C.WAKE_MODE_NETWORK) // Keep CPU awake during streaming
			.build()
	}
	
	/**
	 * MediaSession para MediaNotification y controles externos
	 * @ServiceScoped: 1 sesión por servicio
	 */
	@Provides
	@ServiceScoped
	fun provideMediaSession(
		@ApplicationContext context: Context,
		player: ExoPlayer
	): MediaSession {
		return MediaSession.Builder(context, player)
			.setId("FreePlayerSession")
			.build()
	}
	
	/**
	 * Player abstraction - permite testear sin ExoPlayer real
	 */
	@Provides
	@ServiceScoped
	fun providePlayer(exoPlayer: ExoPlayer): Player = exoPlayer
}