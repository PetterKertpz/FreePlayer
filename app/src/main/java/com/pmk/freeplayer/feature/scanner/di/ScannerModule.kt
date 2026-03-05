package com.pmk.freeplayer.feature.scanner.di

import com.pmk.freeplayer.feature.scanner.data.repository.ScannerRepositoryImpl
import com.pmk.freeplayer.feature.scanner.domain.repository.ScannerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ScannerModule {
	
	@Binds
	@Singleton
	abstract fun bindScannerRepository(
		impl: ScannerRepositoryImpl,
	): ScannerRepository
}