package com.pmk.freeplayer.app.di

import android.content.Context
import androidx.room.Room
import com.pmk.freeplayer.feature.albums.data.local.dao.AlbumDao
import com.pmk.freeplayer.feature.artists.data.local.dao.ArtistDao
import com.pmk.freeplayer.feature.genres.data.local.dao.GenreDao
import com.pmk.freeplayer.feature.statistics.data.local.dao.PlaybackHistoryDao
import com.pmk.freeplayer.feature.playlists.data.local.dao.PlaylistDao
import com.pmk.freeplayer.feature.songs.data.local.dao.SongDao
import com.pmk.freeplayer.core.data.local.db.AppDatabase
import com.pmk.freeplayer.data.local.source.DeviceMusicDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
	
	@Provides
	@Singleton
	fun provideAppDatabase(
		@ApplicationContext context: Context
	): AppDatabase {
		return Room.databaseBuilder(
			context,
			AppDatabase::class.java,
			"freeplayer_db"
		)
			.fallbackToDestructiveMigration(false)
			.build()
	}
	
	// DAOs como extension functions para evitar boilerplate
	@Provides
	fun provideSongDao(db: AppDatabase): SongDao = db.songDao()
	
	@Provides
	fun provideAlbumDao(db: AppDatabase): AlbumDao = db.albumDao()
	
	@Provides
	fun provideArtistDao(db: AppDatabase): ArtistDao = db.artistDao()
	
	@Provides
	fun providePlaylistDao(db: AppDatabase): PlaylistDao = db.playlistDao()
	
	@Provides
	fun provideQueueDao(db: AppDatabase): QueueDao = db.queueDao()
	
	@Provides
	fun provideScannerDao(db: AppDatabase): ScannerDao = db.scannerDao()
	
	@Provides
	fun provideLyricsDao(db: AppDatabase): LyricsDao = db.lyricsDao()
	
	@Provides
	fun provideGenreDao(db: AppDatabase): GenreDao = db.genreDao()
	
	@Provides
	fun providePlaybackHistoryDao(db: AppDatabase): PlaybackHistoryDao = db.playbackHistoryDao()
	
	@Provides
	fun provideEnrichmentResultDao(db: AppDatabase): EnrichmentResultDao = db.enrichmentResultDao()
}

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
	
	@Provides
	@Singleton
	fun provideDeviceMusicDataSource(
		@ApplicationContext context: Context
	): DeviceMusicDataSource {
		return DeviceMusicDataSource(context)
	}
}