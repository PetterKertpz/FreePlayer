package com.pmk.freeplayer.app.di

import com.pmk.freeplayer.domain.strategy.FuzzySongMatchingStrategy
import com.pmk.freeplayer.domain.strategy.SongMatchingStrategy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
	
	@Provides
	@Singleton
	fun provideSongMatchingStrategy(): SongMatchingStrategy {
		return FuzzySongMatchingStrategy(
			titleWeight = 0.55f,
			artistWeight = 0.45f,
			albumWeight = 0.0f,
			confidenceThreshold = 0.75f
		)
	}
}