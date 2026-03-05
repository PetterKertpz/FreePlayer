package com.pmk.freeplayer.feature.player.di

import com.pmk.freeplayer.app.di.IoDispatcher
import com.pmk.freeplayer.feature.metadata.domain.usecase.ProcessSongOnPlaybackUseCase
import com.pmk.freeplayer.feature.player.data.observer.MetadataPlaybackObserver
import com.pmk.freeplayer.feature.player.data.player.AudioEffectController
import com.pmk.freeplayer.feature.player.data.repository.PlayerRepositoryImpl
import com.pmk.freeplayer.feature.player.domain.repository.PlayerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {
	
	@Binds
	@Singleton
	abstract fun bindPlayerRepository(
		impl: PlayerRepositoryImpl,
	): PlayerRepository
	
	companion object {
		
		@Provides
		@Singleton
		fun provideAudioEffectController(): AudioEffectController = AudioEffectController()
		
		@Provides @Singleton
		fun provideMetadataPlaybackObserver(
			playerRepository: PlayerRepository,
			processSongOnPlayback: ProcessSongOnPlaybackUseCase,
			@IoDispatcher ioDispatcher: CoroutineDispatcher,
		): MetadataPlaybackObserver = MetadataPlaybackObserver(
			playerRepository, processSongOnPlayback, ioDispatcher,
		)
	}
	
}