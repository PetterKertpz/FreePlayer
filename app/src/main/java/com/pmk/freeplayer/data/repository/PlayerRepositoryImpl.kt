package com.pmk.freeplayer.data.repository

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.pmk.freeplayer.core.service.MusicService
import com.pmk.freeplayer.core.service.mapper.toMediaItem
import com.pmk.freeplayer.core.service.mapper.toMediaItems
import com.pmk.freeplayer.data.local.dao.PlaybackHistoryDao
import com.pmk.freeplayer.data.local.dao.QueueDao
import com.pmk.freeplayer.data.local.datastore.PlayerPreferences
import com.pmk.freeplayer.data.mapper.createPlaybackHistoryEntity
import com.pmk.freeplayer.data.mapper.toDomain
import com.pmk.freeplayer.data.mapper.toQueueEntities
import com.pmk.freeplayer.data.mapper.toQueueEntity
import com.pmk.freeplayer.domain.model.PlaybackHistory
import com.pmk.freeplayer.domain.model.Queue
import com.pmk.freeplayer.domain.model.SleepTimer
import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.TrackDuration
import com.pmk.freeplayer.domain.model.enums.AudioOutput
import com.pmk.freeplayer.domain.model.enums.PlaybackSource
import com.pmk.freeplayer.domain.model.enums.RepeatMode
import com.pmk.freeplayer.domain.model.state.PlaybackState
import com.pmk.freeplayer.domain.repository.PlayerRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepositoryImpl @Inject constructor(
	@ApplicationContext private val context: Context,
	private val historyDao: PlaybackHistoryDao,
	private val queueDao: QueueDao,
	private val preferences: PlayerPreferences
) : PlayerRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// MEDIA CONTROLLER (Connection to MusicService)
	// ═══════════════════════════════════════════════════════════════
	
	private var player: Player? = null
	private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
	
	// Local States (Source of Truth for UI rapid updates)
	private val _isPlaying = MutableStateFlow(false)
	private val _currentPosition = MutableStateFlow(TrackDuration.ZERO)
	private val _sleepTimer = MutableStateFlow<SleepTimer?>(null)
	
	private var positionTrackerJob: Job? = null
	
	init {
		val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
		val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
		
		controllerFuture.addListener({
			try {
				player = controllerFuture.get()
				setupPlayerListener()
				// Sincronizar estado inicial
				_isPlaying.value = player?.isPlaying == true
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}, MoreExecutors.directExecutor())
	}
	
	private fun setupPlayerListener() {
		player?.addListener(object : Player.Listener {
			override fun onIsPlayingChanged(isPlaying: Boolean) {
				_isPlaying.value = isPlaying
				if (isPlaying) startPositionTracker() else stopPositionTracker()
			}
			
			override fun onPlaybackStateChanged(playbackState: Int) {
				if (playbackState == Player.STATE_ENDED) {
					repoScope.launch { goToNext() }
				}
			}
			
			// Si el servicio cambia de canción por su cuenta (ej: autoplay), actualizamos la UI
			override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
				mediaItem?.mediaId?.toLongOrNull()?.let { songId ->
					repoScope.launch {
						// Actualizar índice visual sin recargar toda la cola
						val queue = getCurrentQueue().first()
						val index = queue.canciones.indexOfFirst { it.id == songId }
						if (index != -1) updateCurrentIndex(index)
					}
				}
			}
		})
	}
	
	// ═══════════════════════════════════════════════════════════════
	// PLAYBACK STATE
	// ═══════════════════════════════════════════════════════════════
	
	override fun getCurrentSong(): Flow<Song?> {
		return combine(queueDao.getQueue(), preferences.playerState) { queueItems, state ->
			queueItems.find { it.song.songId == state.lastSongId }?.toDomain()
		}
	}
	
	override fun getCurrentPosition(): Flow<TrackDuration> = _currentPosition.asStateFlow()
	
	override fun isPlaying(): Flow<Boolean> = _isPlaying.asStateFlow()
	
	override fun getPlaybackState(): Flow<PlaybackState> {
		// Al reducir los argumentos a 4, Kotlin ya puede inferir los tipos correctamente
		return combine(
			getCurrentSong(),
			_isPlaying,
			_currentPosition,
			preferences.playerState // Este objeto ya contiene repeat, shuffle y speed
		) { song, isPlaying, position, prefs ->
			
			// Mapeamos el modo de repetición aquí mismo para ahorrar flujos
			val mappedRepeatMode = when (prefs.repeatMode) {
				1 -> RepeatMode.ONE
				2 -> RepeatMode.ALL
				else -> RepeatMode.OFF
			}
			
			PlaybackState(
				currentSong = song,
				isPlaying = isPlaying,
				currentPosition = position,
				totalDuration = song?.duration ?: TrackDuration.ZERO,
				repeatMode = mappedRepeatMode,
				shuffleEnabled = prefs.isShuffleEnabled,
				playbackSpeed = prefs.speed
			)
		}
	}
	
	// ═══════════════════════════════════════════════════════════════
	// INTERNAL HELPERS (Position Tracking)
	// ═══════════════════════════════════════════════════════════════
	
	private fun startPositionTracker() {
		positionTrackerJob?.cancel()
		positionTrackerJob = repoScope.launch {
			while (isActive) {
				val currentMs = player?.currentPosition ?: 0L
				_currentPosition.value = TrackDuration(currentMs)
				delay(1000L) // Actualizar UI cada segundo
			}
		}
	}
	
	private fun stopPositionTracker() {
		positionTrackerJob?.cancel()
		// Asegurar que guardamos la posición exacta al pausar
		val finalMs = player?.currentPosition ?: 0L
		_currentPosition.value = TrackDuration(finalMs)
	}
	
	// ═══════════════════════════════════════════════════════════════
	// PLAYBACK CONFIGURATION
	// ═══════════════════════════════════════════════════════════════
	
	override fun getRepeatMode(): Flow<RepeatMode> {
		return preferences.playerState.map { state ->
			when (state.repeatMode) {
				1 -> RepeatMode.ONE
				2 -> RepeatMode.ALL
				else -> RepeatMode.OFF
			}
		}
	}
	
	override suspend fun setRepeatMode(mode: RepeatMode) {
		val modeInt = when (mode) {
			RepeatMode.ONE -> 1
			RepeatMode.ALL -> 2
			RepeatMode.OFF -> 0
		}
		preferences.setRepeatMode(modeInt)
		
		// Sincronizar Player
		player?.repeatMode = when(mode) {
			RepeatMode.ONE -> Player.REPEAT_MODE_ONE
			RepeatMode.ALL -> Player.REPEAT_MODE_ALL
			RepeatMode.OFF -> Player.REPEAT_MODE_OFF
		}
	}
	
	override fun isShuffleEnabled(): Flow<Boolean> {
		return preferences.playerState.map { it.isShuffleEnabled }
	}
	
	override suspend fun setShuffleEnabled(enabled: Boolean) {
		preferences.toggleShuffle(enabled)
		player?.shuffleModeEnabled = enabled
	}
	
	// ═══════════════════════════════════════════════════════════════
	// QUEUE MANAGEMENT
	// ═══════════════════════════════════════════════════════════════
	
	override fun getCurrentQueue(): Flow<Queue> {
		return combine(queueDao.getQueue(), preferences.playerState) { queueItems, state ->
			queueItems.toDomain(state.lastSongId)
		}
	}
	
	override fun getCurrentIndex(): Flow<Int> {
		return getCurrentQueue().map { it.indiceActual }
	}
	
	override suspend fun setQueue(songs: List<Song>, initialIndex: Int) {
		// 1. Persistencia (Single Source of Truth)
		val initialSong = songs.getOrNull(initialIndex)
		queueDao.replaceQueue(songs.toQueueEntities())
		
		if (initialSong != null) {
			updateCurrentIndex(initialIndex)
			saveSessionState(
				songId = initialSong.id,
				positionMs = 0,
				queueIds = songs.map { it.id },
				currentIndex = initialIndex
			)
			
			// 2. Actualizar Servicio Inmediatamente
			val mediaItems = songs.toMediaItems()
			player?.setMediaItems(mediaItems, initialIndex, C.TIME_UNSET)
			player?.prepare()
			player?.play()
		}
	}
	
	override suspend fun updateCurrentIndex(newIndex: Int) {
		val queue = getCurrentQueue().first()
		val song = queue.canciones.getOrNull(newIndex)
		if (song != null) {
			preferences.saveLastSong(song.id, 0)
		}
	}
	
	override suspend fun updateQueue(songs: List<Song>, currentIndex: Int?) {
		queueDao.replaceQueue(songs.toQueueEntities())
		if (currentIndex != null) updateCurrentIndex(currentIndex)
		
		// El servicio observará el DAO, pero forzamos update si el player ya está listo
		if (player != null && songs.isNotEmpty()) {
			val mediaItems = songs.toMediaItems()
			// Lógica simple: reemplazar todo para asegurar consistencia
			// Nota: En producción, usar diffing es mejor para no cortar el audio
			player?.setMediaItems(mediaItems)
		}
	}
	
	override suspend fun addToQueue(songs: List<Song>) {
		val queue = getCurrentQueue().first()
		val startOrder = queue.cantidadTotal
		val newEntities = songs.mapIndexed { index, song ->
			song.toQueueEntity(startOrder + index)
		}
		queueDao.insertAll(newEntities)
		
		// Añadir al Player sin detener reproducción
		player?.addMediaItems(songs.toMediaItems())
	}
	
	override suspend fun addToEnd(song: Song) {
		queueDao.addToEnd(song.id)
		player?.addMediaItem(song.toMediaItem())
	}
	
	override suspend fun addNext(song: Song) {
		val currentIndex = getCurrentIndex().first()
		queueDao.playNext(song.id, currentIndex)
		
		// Insertar en player justo después del actual
		val playerIndex = player?.currentMediaItemIndex ?: 0
		player?.addMediaItem(playerIndex + 1, song.toMediaItem())
	}
	
	override suspend fun removeFromQueue(index: Int) {
		val queueItems = queueDao.getQueueSync()
		val itemToRemove = queueItems.getOrNull(index) ?: return
		
		queueDao.deleteById(itemToRemove.queueItem.id)
		player?.removeMediaItem(index)
	}
	
	override suspend fun moveInQueue(from: Int, to: Int) {
		val queueItems = queueDao.getQueueSync()
		val itemFrom = queueItems.getOrNull(from) ?: return
		queueDao.updateOrder(itemFrom.queueItem.id, to)
		
		player?.moveMediaItem(from, to)
	}
	
	override suspend fun clearQueue() {
		queueDao.clearQueue()
		player?.clearMediaItems()
	}
	
	// ═══════════════════════════════════════════════════════════════
	// QUEUE NAVIGATION (Control del Player)
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun goToNext(): Song? {
		val queue = getCurrentQueue().first()
		if (!queue.tieneSiguiente) return null
		
		player?.seekToNext()
		player?.play() // Asegurar play si estaba pausado
		
		val nextIndex = queue.indiceActual + 1
		val nextSong = queue.canciones[nextIndex]
		updateCurrentIndex(nextIndex)
		return nextSong
	}
	
	override suspend fun goToPrevious(): Song? {
		val queue = getCurrentQueue().first()
		
		// Lógica estándar: Si pasaron >3 seg, reiniciar canción
		if ((player?.currentPosition ?: 0) > 3000) {
			player?.seekTo(0)
			return queue.songActual
		}
		
		if (!queue.tieneAnterior) {
			player?.seekTo(0)
			return queue.songActual
		}
		
		player?.seekToPrevious()
		player?.play()
		
		val prevIndex = queue.indiceActual - 1
		val prevSong = queue.canciones[prevIndex]
		updateCurrentIndex(prevIndex)
		return prevSong
	}
	
	override suspend fun goToIndex(index: Int): Song? {
		val queue = getCurrentQueue().first()
		val song = queue.canciones.getOrNull(index) ?: return null
		
		player?.seekTo(index, C.TIME_UNSET)
		player?.play()
		
		updateCurrentIndex(index)
		return song
	}
	
	// ═══════════════════════════════════════════════════════════════
	// SHUFFLE
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun shuffleQueue() {
		setShuffleEnabled(true)
	}
	
	override suspend fun restoreOriginalOrder() {
		setShuffleEnabled(false)
	}
	
	override fun hasOriginalOrder(): Flow<Boolean> {
		return isShuffleEnabled().map { !it }
	}
	
	// ═══════════════════════════════════════════════════════════════
	// SESSION PERSISTENCE
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun saveSessionState(
		songId: Long?,
		positionMs: Long,
		queueIds: List<Long>,
		currentIndex: Int
	) {
		if (songId != null) {
			preferences.saveLastSong(songId, positionMs)
		}
	}
	
	override fun getLastSongId(): Flow<Long?> = preferences.playerState.map {
		if (it.lastSongId == -1L) null else it.lastSongId
	}
	
	override fun getLastPosition(): Flow<Long> = preferences.playerState.map { it.lastPositionMs }
	
	override fun getLastIndex(): Flow<Int> = getCurrentIndex()
	
	override fun getLastQueueIds(): Flow<List<Long>> {
		return queueDao.getQueue().map { list -> list.map { it.song.songId } }
	}
	
	// ═══════════════════════════════════════════════════════════════
	// PLAYBACK HISTORY
	// ═══════════════════════════════════════════════════════════════
	
	override fun getHistory(limit: Int): Flow<List<PlaybackHistory>> {
		return historyDao.getRecentHistory(userId = 1L, limit = limit)
			.map { list -> list.toDomain() }
	}
	
	override fun getHistoryByDateRange(startDate: Long, endDate: Long): Flow<List<PlaybackHistory>> {
		return historyDao.getHistoryByUserId(userId = 1L)
			.map { list ->
				list.filter { it.timestamp in startDate..endDate }
					.toDomain()
			}
	}
	
	override suspend fun recordPlayback(songId: Long, listenedDuration: Long, completed: Boolean) {
		val currentSong = getCurrentSong().firstOrNull()
		val totalDuration = currentSong?.duration?.millis ?: 0L
		
		val entity = createPlaybackHistoryEntity(
			songId = songId,
			playedDurationMs = listenedDuration,
			totalDurationMs = totalDuration,
			source = PlaybackSource.LIBRARY,
			outputType = AudioOutput.SPEAKER,
			playbackMode = "NORMAL"
		)
		historyDao.insert(entity)
	}
	
	override suspend fun clearHistory() {
		historyDao.deleteByUserId(userId = 1L)
	}
	
	override suspend fun deleteHistoryEntry(id: Long) {
		historyDao.deleteById(id)
	}
	
	override suspend fun getTotalListeningTime(): Long {
		return historyDao.getTotalListeningTime(userId = 1L) ?: 0L
	}
	
	override suspend fun getSongsPlayedToday(): Int {
		return 0
	}
	
	// ═══════════════════════════════════════════════════════════════
	// SLEEP TIMER
	// ═══════════════════════════════════════════════════════════════
	
	override fun getSleepTimerState(): Flow<SleepTimer?> = _sleepTimer.asStateFlow()
	
	override suspend fun setSleepTimer(timer: SleepTimer) {
		_sleepTimer.value = timer
		// Aquí conectarías con lógica real de Timer si fuera necesario
	}
	
	override suspend fun cancelSleepTimer() {
		_sleepTimer.value = null
	}
	
	override suspend fun extendSleepTimer(extraMinutes: Int) {
		val current = _sleepTimer.value
		if (current != null) {
			_sleepTimer.value = current.copy(
				minutosRestantes = current.minutosRestantes + extraMinutes
			)
		}
	}
}