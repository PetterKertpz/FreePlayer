package com.pmk.freeplayer.feature.player.data.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioFocusHandler @Inject constructor(
	@ApplicationContext private val context: Context,
) {
	enum class FocusResult { GRANTED, DENIED }
	enum class FocusLoss   { PERMANENT, TRANSIENT, TRANSIENT_CAN_DUCK }
	
	private val audioManager = context.getSystemService(AudioManager::class.java)
	private var onLoss: ((FocusLoss) -> Unit)? = null
	private var onGain: (() -> Unit)? = null
	
	private val focusRequest: AudioFocusRequest? by lazy {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
				.setAudioAttributes(
					AudioAttributes.Builder()
						.setUsage(AudioAttributes.USAGE_MEDIA)
						.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
						.build()
				)
				.setAcceptsDelayedFocusGain(true)
				.setOnAudioFocusChangeListener(::onAudioFocusChange)
				.build()
		} else null
	}
	
	fun request(
		onGain: () -> Unit,
		onLoss: (FocusLoss) -> Unit,
	): FocusResult {
		this.onGain = onGain
		this.onLoss = onLoss
		
		val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			audioManager.requestAudioFocus(focusRequest!!)
		} else {
			@Suppress("DEPRECATION")
			audioManager.requestAudioFocus(
				::onAudioFocusChange,
				AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN,
			)
		}
		return if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) FocusResult.GRANTED
		else FocusResult.DENIED
	}
	
	fun abandon() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
		} else {
			@Suppress("DEPRECATION")
			audioManager.abandonAudioFocus(::onAudioFocusChange)
		}
		onGain = null
		onLoss = null
	}
	
	private fun onAudioFocusChange(focusChange: Int) {
		when (focusChange) {
			AudioManager.AUDIOFOCUS_GAIN                    -> onGain?.invoke()
			AudioManager.AUDIOFOCUS_LOSS                    -> onLoss?.invoke(FocusLoss.PERMANENT)
			AudioManager.AUDIOFOCUS_LOSS_TRANSIENT          -> onLoss?.invoke(FocusLoss.TRANSIENT)
			AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> onLoss?.invoke(FocusLoss.TRANSIENT_CAN_DUCK)
		}
	}
}