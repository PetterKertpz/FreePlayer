package com.pmk.freeplayer.feature.settings.di

import com.pmk.freeplayer.feature.settings.data.repository.SettingsRepositoryImpl
import com.pmk.freeplayer.feature.settings.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {
	
	@Binds
	@Singleton
	abstract fun bindSettingsRepository(
		impl: SettingsRepositoryImpl,
	): SettingsRepository
}