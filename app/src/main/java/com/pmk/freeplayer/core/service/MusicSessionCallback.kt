package com.pmk.freeplayer.core.service

import androidx.media3.common.MediaItem
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.pmk.freeplayer.domain.repository.PlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

// CAMBIO: Ahora hereda de MediaLibrarySession.Callback
class MusicSessionCallback @Inject constructor(
	private val repository: PlayerRepository
) : MediaLibraryService.MediaLibrarySession.Callback {
	
	private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
	
	// ═══════════════════════════════════════════════════════════════
	// CONEXIÓN
	// ═══════════════════════════════════════════════════════════════
	
	override fun onConnect(
		session: MediaSession,
		controller: MediaSession.ControllerInfo
	): MediaSession.ConnectionResult {
		val connectionResult = super.onConnect(session, controller)
		val sessionCommands = connectionResult.availableSessionCommands
			.buildUpon()
			.build()
		return MediaSession.ConnectionResult.accept(sessionCommands, connectionResult.availablePlayerCommands)
	}
	
	// ═══════════════════════════════════════════════════════════════
	// MÉTODOS DE LIBRERÍA (Requeridos para MediaLibrarySession)
	// ═══════════════════════════════════════════════════════════════
	
	// Estos métodos son necesarios para que Android Auto no se queje,
	// aunque por ahora retornen listas vacías o raíz simple.
	
	override fun onGetLibraryRoot(
		session: MediaLibraryService.MediaLibrarySession,
		browser: MediaSession.ControllerInfo,
		params: MediaLibraryService.LibraryParams?
	): ListenableFuture<LibraryResult<MediaItem>> {
		// Retornamos un elemento raíz básico para permitir conexión
		val rootItem = MediaItem.Builder()
			.setMediaId("root")
			.setMediaMetadata(
				androidx.media3.common.MediaMetadata.Builder()
					.setIsBrowsable(true)
					.setIsPlayable(false)
					.build()
			)
			.build()
		return Futures.immediateFuture(LibraryResult.ofItem(rootItem, params))
	}
	
	override fun onGetChildren(
		session: MediaLibraryService.MediaLibrarySession,
		browser: MediaSession.ControllerInfo,
		parentId: String,
		page: Int,
		pageSize: Int,
		params: MediaLibraryService.LibraryParams?
	): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
		// Por ahora retornamos lista vacía hasta que implementes la lógica de navegación
		return Futures.immediateFuture(LibraryResult.ofItemList(ImmutableList.of(), params))
	}
	
	override fun onAddMediaItems(
		mediaSession: MediaSession,
		controller: MediaSession.ControllerInfo,
		mediaItems: MutableList<MediaItem>
	): ListenableFuture<List<MediaItem>> {
		return Futures.immediateFuture(mediaItems)
	}
}