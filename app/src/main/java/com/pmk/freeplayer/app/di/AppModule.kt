package com.pmk.freeplayer.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// app/di/AppModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
	
	@Provides
	@Singleton
	fun provideAndStartAppInitializer(
		initializer: AppStartupInitializer,
	): AppStartupInitializer {
		initializer.initialize()
		return initializer
	}
}