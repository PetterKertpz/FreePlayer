package com.pmk.freeplayer.feature.metadata.di

import android.content.Context
import com.pmk.freeplayer.feature.metadata.data.remote.api.GeniusApiService
import com.pmk.freeplayer.feature.metadata.data.remote.store.MetadataConfigStore
import com.pmk.freeplayer.feature.metadata.data.repository.LyricsRepositoryImpl
import com.pmk.freeplayer.feature.metadata.data.repository.MetadataRepositoryImpl
import com.pmk.freeplayer.feature.metadata.domain.repository.LyricsRepository
import com.pmk.freeplayer.feature.metadata.domain.repository.MetadataRepository
import com.pmk.freeplayer.feature.settings.data.datastore.SettingsDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

// feature/metadata/di/MetadataModule.kt

@Module
@InstallIn(SingletonComponent::class)
abstract class MetadataModule {
	
	@Binds
	@Singleton
	abstract fun bindMetadataRepository(impl: MetadataRepositoryImpl): MetadataRepository
	
	@Binds @Singleton
	abstract fun bindLyricsRepository(impl: LyricsRepositoryImpl): LyricsRepository
	
	companion object {
		
		@Provides @Singleton
		fun provideGeniusApiService(
			@GeniusOkHttpClient okHttpClient: OkHttpClient,
		): GeniusApiService = Retrofit.Builder()
			.baseUrl("https://api.genius.com/")
			.client(okHttpClient)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
			.create(GeniusApiService::class.java)
		
		@Provides @Singleton
		fun provideMetadataConfigStore(
			@ApplicationContext context: Context,
			settingsDataStore: SettingsDataStore,
		): MetadataConfigStore = MetadataConfigStore(context, settingsDataStore)
		
		@Provides
		@Singleton @GeniusOkHttpClient
		fun provideGeniusOkHttpClient(
			metadataConfigStore: MetadataConfigStore,
		): OkHttpClient = OkHttpClient.Builder()
			.addInterceptor { chain ->
				val token = runBlocking { metadataConfigStore.getCurrent().geniusAccessToken }
				val request = if (!token.isNullOrBlank()) {
					chain.request().newBuilder()
						.addHeader("Authorization", "Bearer $token")
						.build()
				} else chain.request()
				chain.proceed(request)
			}
			.connectTimeout(15, TimeUnit.SECONDS)
			.readTimeout(15, TimeUnit.SECONDS)
			.build()
	}
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeniusOkHttpClient