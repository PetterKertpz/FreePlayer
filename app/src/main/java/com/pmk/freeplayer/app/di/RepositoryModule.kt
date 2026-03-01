package com.pmk.freeplayer.app.di

import com.pmk.freeplayer.data.repository.ArtistRepositoryImpl
import com.pmk.freeplayer.data.repository.EnrichmentRepositoryImpl
import com.pmk.freeplayer.data.repository.LibraryRepositoryImpl
import com.pmk.freeplayer.data.repository.LyricsRepositoryImpl
import com.pmk.freeplayer.data.repository.PlayerRepositoryImpl
import com.pmk.freeplayer.data.repository.PlaylistRepositoryImpl
import com.pmk.freeplayer.data.repository.ScannerRepositoryImpl
import com.pmk.freeplayer.data.repository.SettingsRepositoryImpl
import com.pmk.freeplayer.data.repository.UserRepositoryImpl
import com.pmk.freeplayer.domain.repository.ArtistRepository
import com.pmk.freeplayer.domain.repository.LibraryRepository
import com.pmk.freeplayer.feature.player.domain.repository.PlayerRepository
import com.pmk.freeplayer.feature.playlists.domain.repository.PlaylistRepository
import com.pmk.freeplayer.core.domain.repository.SettingsRepository
import com.pmk.freeplayer.feature.auth.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
	
	@Binds
	@Singleton
	abstract fun bindScannerRepository(
		impl: ScannerRepositoryImpl
	): ScannerRepository
	
	@Binds
	@Singleton
	abstract fun bindLibraryRepository(
		impl: LibraryRepositoryImpl
	): LibraryRepository
	
	@Binds
	@Singleton
	abstract fun bindPlayerRepository(
		impl: PlayerRepositoryImpl
	): PlayerRepository
	
	@Binds
	@Singleton
	abstract fun bindPlaylistRepository(
		impl: PlaylistRepositoryImpl
	): PlaylistRepository
	
	@Binds
	@Singleton
	abstract fun bindArtistRepository(
		impl: ArtistRepositoryImpl
	): ArtistRepository
	
	@Binds
	@Singleton
	abstract fun bindLyricsRepository(
		impl: LyricsRepositoryImpl
	): LyricsRepository
	
	@Binds
	@Singleton
	abstract fun bindEnrichmentRepository(
		impl: EnrichmentRepositoryImpl
	): EnrichmentRepository
	
	@Binds
	@Singleton
	abstract fun bindSettingsRepository(
		impl: SettingsRepositoryImpl
	): SettingsRepository
	
	@Binds
	@Singleton
	abstract fun bindUserRepository(
		impl: UserRepositoryImpl
	): UserRepository
}