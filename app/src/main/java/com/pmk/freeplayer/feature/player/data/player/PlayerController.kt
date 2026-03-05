package com.pmk.freeplayer.feature.player.data.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.pmk.freeplayer.core.domain.model.enums.RepeatMode
import com.pmk.freeplayer.feature.player.domain.model.PlaybackProgress
import com.pmk.freeplayer.feature.player.domain.model.PlayerState
import com.pmk.freeplayer.feature.player.domain.model.QueueItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerController @Inject constructor(
	@ApplicationContext private val context: Context,
	val queueManager: QueueManager,
	private val audioFocusHandler: AudioFocusHandler,
) {
	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
	
	private val _state = MutableStateFlow<PlayerState>(PlayerState.Idle)
	val state: StateFlow<PlayerState> = _state.asStateFlow()
	
	private var progressJob: Job? = null
	private var trackStartMs: Long = 0L
	private var listenedMs:   Long = 0L
	
	private val exoPlayer: ExoPlayer by lazy {
		ExoPlayer.Builder(context).build().apply {
			addListener(playerListener)
		}
	}
	
	// ── Public controls ───────────────────────────────────────────
	
	fun loadQueue(items: List<QueueItem>, startIndex: Int) {
		queueManager.set(items, startIndex)
		val focusResult = audioFocusHandler.request(
			onGain = { exoPlayer.play() },
			onLoss = { loss ->
				when (loss) {
					AudioFocusHandler.FocusLoss.PERMANENT         -> pause()
					AudioFocusHandler.FocusLoss.TRANSIENT         -> pause()
					AudioFocusHandler.FocusLoss.TRANSIENT_CAN_DUCK -> exoPlayer.volume = 0.3f
				}
			},
		)
		if (focusResult == AudioFocusHandler.FocusResult.DENIED) return
		
		val item = queueManager.currentItem ?: return
		exoPlayer.setMediaItem(MediaItem.fromUri(item.filePath))
		exoPlayer.prepare()
		exoPlayer.play()
		trackStartMs = System.currentTimeMillis()
		listenedMs = 0L
	}
	
	fun play()  { exoPlayer.play() }
	fun pause() { exoPlayer.pause() }
	
	fun seekTo(positionMs: Long) {
		exoPlayer.seekTo(positionMs)
		listenedMs += System.currentTimeMillis() - trackStartMs
		trackStartMs = System.currentTimeMillis()
	}
	
	fun skipToNext() {
		emitTrackEnded(wasSkipped = true)
		if (queueManager.advanceToNext()) loadCurrent()
		else { _state.value = PlayerState.Idle; stopProgress() }
	}
	
	fun skipToPrevious() {
		if (exoPlayer.currentPosition > 3_000L) {
			exoPlayer.seekTo(0)
			return
		}
		emitTrackEnded(wasSkipped = true)
		if (queueManager.backToPrevious()) loadCurrent()
	}
	
	fun skipToIndex(index: Int) {
		emitTrackEnded(wasSkipped = true)
		queueManager.jumpTo(index)
		loadCurrent()
	}
	
	fun setRepeatMode(mode: RepeatMode) {
		exoPlayer.repeatMode = when (mode) {
			RepeatMode.OFF -> Player.REPEAT_MODE_OFF
			RepeatMode.ONE -> Player.REPEAT_MODE_ONE
			RepeatMode.ALL -> Player.REPEAT_MODE_ALL
		}
		emitCurrentState()
	}
	
	fun setShuffleEnabled(enabled: Boolean) {
		queueManager.setShuffle(enabled)
		emitCurrentState()
	}
	
	fun setPlaybackSpeed(speed: Float) { exoPlayer.setPlaybackSpeed(speed) }
	
	fun setEqualizerEnabled(enabled: Boolean) { /* Configured in PlayerService via AudioEffect */ }
	fun setBassBoost(level: Int)              { /* Delegated to AudioEffect in PlayerService  */ }
	fun setVirtualizer(level: Int)            { /* Delegated to AudioEffect in PlayerService  */ }
	
	fun release() {
		audioFocusHandler.abandon()
		stopProgress()
		exoPlayer.release()
		_state.value = PlayerState.Idle
	}
	
	// ── Private helpers ───────────────────────────────────────────
	
	private fun loadCurrent() {
		val item = queueManager.currentItem ?: return
		exoPlayer.setMediaItem(MediaItem.fromUri(item.filePath))
		exoPlayer.prepare()
		exoPlayer.play()
		trackStartMs = System.currentTimeMillis()
		listenedMs   = 0L
	}
	
	private fun startProgressUpdates() {
		stopProgress()
		progressJob = scope.launch {
			while (isActive) {
				emitCurrentState()
				delay(500L)
			}
		}
	}
	
	private fun stopProgress() { progressJob?.cancel(); progressJob = null }
	
	private fun emitCurrentState() {
		val item = queueManager.currentItem ?: return
		val progress = PlaybackProgress(
			positionMs = exoPlayer.currentPosition,
			durationMs = exoPlayer.duration.coerceAtLeast(0L),
		)
		_state.value = if (exoPlayer.isPlaying) {
			PlayerState.Playing(
				currentItem    = item,
				progress       = progress,
				queue          = queueManager.queue,
				currentIndex   = queueManager.currentIndex,
				shuffleEnabled = queueManager.shuffleEnabled,
				repeatMode     = exoPlayer.repeatMode.toDomain(),
			)
		} else {
			PlayerState.Paused(
				currentItem    = item,
				progress       = progress,
				queue          = queueManager.queue,
				currentIndex   = queueManager.currentIndex,
				shuffleEnabled = queueManager.shuffleEnabled,
				repeatMode     = exoPlayer.repeatMode.toDomain(),
			)
		}
	}
	
	private fun emitTrackEnded(wasSkipped: Boolean) {
		val item = queueManager.currentItem ?: return
		listenedMs += System.currentTimeMillis() - trackStartMs
		_state.value = PlayerState.TrackEnded(
			item       = item,
			listenedMs = listenedMs,
			wasSkipped = wasSkipped,
		)
	}
	
	private val playerListener = object : Player.Listener {
		override fun onIsPlayingChanged(isPlaying: Boolean) {
			if (isPlaying) startProgressUpdates() else stopProgress()
			emitCurrentState()
		}
		
		override fun onPlaybackStateChanged(playbackState: Int) {
			if (playbackState == Player.STATE_ENDED) {
				emitTrackEnded(wasSkipped = false)
				if (queueManager.advanceToNext()) loadCurrent()
				else { _state.value = PlayerState.Idle; stopProgress() }
			}
		}
		
		override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
			_state.value = PlayerState.Error(
				code    = error.errorCodeName,
				message = error.message ?: "Playback error",
			)
		}
	}
	
	private fun Int.toDomain(): RepeatMode = when (this) {
		Player.REPEAT_MODE_ONE -> RepeatMode.ONE
		Player.REPEAT_MODE_ALL -> RepeatMode.ALL
		else                   -> RepeatMode.OFF
	}
}