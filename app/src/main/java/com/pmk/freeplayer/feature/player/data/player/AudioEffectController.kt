package com.pmk.freeplayer.feature.player.data.player

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import javax.inject.Inject
import javax.inject.Singleton

// feature/player/data/player/AudioEffectController.kt

@Singleton
class AudioEffectController @Inject constructor() {
	
	private var equalizer: Equalizer? = null
	private var bassBoost: BassBoost? = null
	private var virtualizer: Virtualizer? = null
	
	private var equalizerEnabled = false
	private var pendingBassLevel = 0
	private var pendingVirtualizerLevel = 0
	
	// Llamado desde PlayerController cuando ExoPlayer está listo (audioSessionId disponible)
	fun attach(audioSessionId: Int) {
		release()
		runCatching {
			equalizer   = Equalizer(0, audioSessionId).apply { enabled = equalizerEnabled }
			bassBoost   = BassBoost(0, audioSessionId).apply {
				enabled = pendingBassLevel > 0
				setStrength(pendingBassLevel.toShort().coerceIn(0, 1000))
			}
			virtualizer = Virtualizer(0, audioSessionId).apply {
				enabled = pendingVirtualizerLevel > 0
				setStrength(pendingVirtualizerLevel.toShort().coerceIn(0, 1000))
			}
		}
	}
	
	fun setEqualizerEnabled(enabled: Boolean) {
		equalizerEnabled = enabled
		equalizer?.enabled = enabled
	}
	
	fun setBassBoost(level: Int) {
		pendingBassLevel = level.coerceIn(0, 1000)
		bassBoost?.apply {
			enabled = pendingBassLevel > 0
			setStrength(pendingBassLevel.toShort())
		}
	}
	
	fun setVirtualizer(level: Int) {
		pendingVirtualizerLevel = level.coerceIn(0, 1000)
		virtualizer?.apply {
			enabled = pendingVirtualizerLevel > 0
			setStrength(pendingVirtualizerLevel.toShort())
		}
	}
	
	fun setBandLevel(band: Short, levelMilliBel: Short) {
		equalizer?.setBandLevel(band, levelMilliBel)
	}
	
	fun getBandLevels(): List<Pair<Short, Short>> {
		val eq = equalizer ?: return emptyList()
		return (0 until (eq.numberOfBands.toInt())).map { band ->
			band.toShort() to eq.getBandLevel(band.toShort())
		}
	}
	
	fun release() {
		runCatching { equalizer?.release() }
		runCatching { bassBoost?.release() }
		runCatching { virtualizer?.release() }
		equalizer   = null
		bassBoost   = null
		virtualizer = null
	}
}