package com.pmk.freeplayer.feature.scanner.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.pmk.freeplayer.core.domain.model.state.MediaProcessingState
import com.pmk.freeplayer.feature.scanner.domain.model.ScanMode
import com.pmk.freeplayer.feature.scanner.domain.repository.ScannerRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScannerService : LifecycleService() {
	
	@Inject lateinit var scannerRepository: ScannerRepository
	
	override fun onCreate() {
		super.onCreate()
		createNotificationChannel()
		startForeground(NOTIFICATION_ID, buildNotification("Scanning…"))
		observeState()
	}
	
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		super.onStartCommand(intent, flags, startId)
		val mode = intent?.getStringExtra(EXTRA_MODE)
			?.let { runCatching { ScanMode::class.sealedSubclasses.first { c -> c.simpleName == it }.objectInstance }.getOrNull() }
			?: ScanMode.Manual
		
		lifecycleScope.launch {
			scannerRepository.scan(mode)
			stopSelf()
		}
		return START_NOT_STICKY
	}
	
	private fun observeState() {
		scannerRepository.scanState.onEach { state ->
			val text = when (state) {
				is MediaProcessingState.Scanning -> "Scanning… ${state.filesProcessed} files"
				is MediaProcessingState.Saving   -> "Saving ${state.itemsUpdated} items"
				is MediaProcessingState.Completed -> "Scan complete"
				is MediaProcessingState.Failed    -> "Scan failed"
				else -> return@onEach
			}
			updateNotification(text)
		}.launchIn(lifecycleScope)
	}
	
	private fun createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				CHANNEL_ID, "Library Scanner", NotificationManager.IMPORTANCE_LOW,
			)
			getSystemService(NotificationManager::class.java)
				.createNotificationChannel(channel)
		}
	}
	
	private fun buildNotification(text: String): Notification =
		NotificationCompat.Builder(this, CHANNEL_ID)
			.setContentTitle("FreePlayer")
			.setContentText(text)
			.setSmallIcon(android.R.drawable.ic_media_play)
			.setOngoing(true)
			.setSilent(true)
			.build()
	
	private fun updateNotification(text: String) {
		val manager = getSystemService(NotificationManager::class.java)
		manager.notify(NOTIFICATION_ID, buildNotification(text))
	}
	
	companion object {
		const val CHANNEL_ID      = "scanner_channel"
		const val NOTIFICATION_ID = 1001
		const val EXTRA_MODE      = "scan_mode"
	}
}