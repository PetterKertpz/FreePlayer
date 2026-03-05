package com.pmk.freeplayer.feature.statistics.di

import com.pmk.freeplayer.feature.statistics.data.observer.PlaybackEventObserver
import com.pmk.freeplayer.feature.statistics.data.repository.StatisticsRepositoryImpl
import com.pmk.freeplayer.feature.statistics.domain.repository.StatisticsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StatisticsModule {
	
	@Binds
	@Singleton
	abstract fun bindStatisticsRepository(
		impl: StatisticsRepositoryImpl,
	): StatisticsRepository
	
	companion object {
		
		/**
		 * Eagerly starts the observer at app launch.
		 * The [observer] parameter forces Hilt to instantiate and inject it,
		 * then [start()] wires it to the PlayerRepository StateFlow.
		 */
		@Provides
		@Singleton
		fun provideAndStartPlaybackObserver(
			observer: PlaybackEventObserver,
		): PlaybackEventObserver {
			observer.start()
			return observer
		}
	}
}