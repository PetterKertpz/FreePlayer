package com.pmk.freeplayer.feature.player.di

import com.pmk.freeplayer.feature.player.data.repository.PlayerRepositoryImpl
import com.pmk.freeplayer.feature.player.domain.repository.PlayerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {
	
	@Binds
	@Singleton
	abstract fun bindPlayerRepository(
		impl: PlayerRepositoryImpl,
	): PlayerRepository
}