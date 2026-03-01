package com.pmk.freeplayer.feature.player.domain.model

data class SleepTimer(
	val isActive: Boolean,
	val remainingMinutes: Int,
	val finishOnTrackEnd: Boolean,
) {
   companion object {
      val INACTIVE = SleepTimer(false, 0, false)
   }
}