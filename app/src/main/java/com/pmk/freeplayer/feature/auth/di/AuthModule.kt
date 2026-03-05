package com.pmk.freeplayer.feature.auth.di

import com.google.firebase.auth.FirebaseAuth
import com.pmk.freeplayer.core.domain.session.SessionProvider
import com.pmk.freeplayer.feature.auth.data.remote.OAuthManager
import com.pmk.freeplayer.feature.auth.data.remote.OAuthManagerImpl
import com.pmk.freeplayer.feature.auth.data.repository.UserRepositoryImpl
import com.pmk.freeplayer.feature.auth.data.security.PasswordHasher
import com.pmk.freeplayer.feature.auth.data.security.PasswordHasherImpl
import com.pmk.freeplayer.feature.auth.data.session.SessionManager
import com.pmk.freeplayer.feature.auth.data.session.SessionManagerImpl
import com.pmk.freeplayer.feature.auth.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
	
	// ═══════════════════════════════════════════════════════════════
	// BINDINGS — Interface → Implementación
	// ═══════════════════════════════════════════════════════════════
	
	@Binds
	@Singleton
	abstract fun bindUserRepository(
		impl: UserRepositoryImpl,
	): UserRepository
	
	@Binds
	@Singleton
	abstract fun bindOAuthManager(
		impl: OAuthManagerImpl,
	): OAuthManager
	
	@Binds
	@Singleton
	abstract fun bindPasswordHasher(
		impl: PasswordHasherImpl,
	): PasswordHasher
	
	@Binds
	@Singleton
	abstract fun bindSessionManager(
		impl: SessionManagerImpl,
	): SessionManager
	
	@Binds
	@Singleton
	abstract fun bindSessionProvider(
		impl: SessionManagerImpl,
	): SessionProvider
	
	// ═══════════════════════════════════════════════════════════════
	// PROVIDERS — Objetos que requieren construcción manual
	// ═══════════════════════════════════════════════════════════════
	
	companion object {
		
		/**
		 * FirebaseAuth es un singleton del SDK.
		 * Se provee aquí para que Hilt pueda inyectarlo en [OAuthManagerImpl].
		 */
		@Provides
		@Singleton
		fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
	}
}