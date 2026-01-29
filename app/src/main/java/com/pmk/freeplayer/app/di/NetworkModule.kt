package com.pmk.freeplayer.app.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pmk.freeplayer.BuildConfig
import com.pmk.freeplayer.data.remote.api.GeniusService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeniusRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
	
	private const val GENIUS_BASE_URL = "https://api.genius.com/"
	
	@Provides
	@Singleton
	fun provideGson(): Gson {
		return GsonBuilder()
			.setLenient()
			.create()
	}
	
	@Provides
	@Singleton
	fun provideLoggingInterceptor(): HttpLoggingInterceptor {
		return HttpLoggingInterceptor().apply {
			level = if (BuildConfig.DEBUG) {
				HttpLoggingInterceptor.Level.BODY
			} else {
				HttpLoggingInterceptor.Level.NONE
			}
		}
	}
	
	@Provides
	@Singleton
	fun provideGeniusAuthInterceptor(): Interceptor {
		return Interceptor { chain ->
			val request = chain.request().newBuilder()
				.addHeader("Authorization", "Bearer ${BuildConfig.GENIUS_TOKEN}")
				.build()
			chain.proceed(request)
		}
	}
	
	@Provides
	@Singleton
	fun provideOkHttpClient(
		authInterceptor: Interceptor,
		loggingInterceptor: HttpLoggingInterceptor
	): OkHttpClient {
		return OkHttpClient.Builder()
			.addInterceptor(authInterceptor)
			.addInterceptor(loggingInterceptor)
			.connectTimeout(30, TimeUnit.SECONDS)
			.readTimeout(30, TimeUnit.SECONDS)
			.writeTimeout(30, TimeUnit.SECONDS)
			.build()
	}
	
	@Provides
	@Singleton
	@GeniusRetrofit
	fun provideGeniusRetrofit(
		okHttpClient: OkHttpClient,
		gson: Gson
	): Retrofit {
		return Retrofit.Builder()
			.baseUrl(GENIUS_BASE_URL)
			.client(okHttpClient)
			.addConverterFactory(GsonConverterFactory.create(gson))
			.build()
	}
	
	@Provides
	@Singleton
	fun provideGeniusService(
		@GeniusRetrofit retrofit: Retrofit
	): GeniusService {
		return retrofit.create(GeniusService::class.java)
	}
}