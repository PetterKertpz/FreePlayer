package com.pmk.freeplayer.feature.metadata.data.remote

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

// feature/metadata/data/remote/GeniusRateLimiter.kt

@Singleton
class GeniusRateLimiter @Inject constructor() {
	
	private val mutex = Mutex()
	private var lastRequestAt = 0L
	
	suspend fun acquire() {
		mutex.withLock {
			val now = System.currentTimeMillis()
			val elapsed = now - lastRequestAt
			val delay = (300L..800L).random()
			if (elapsed < delay) {
				delay(delay - elapsed)
			}
			lastRequestAt = System.currentTimeMillis()
		}
	}
}