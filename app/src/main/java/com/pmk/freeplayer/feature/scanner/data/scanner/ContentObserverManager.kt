package com.pmk.freeplayer.feature.scanner.data.scanner

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentObserverManager @Inject constructor(
	@ApplicationContext private val context: Context,
) {
	
	/**
	 * Emits [Unit] whenever MediaStore.Audio changes are detected.
	 * Debounced 3s to avoid firing on every file during a bulk copy.
	 * Consumer decides whether to trigger a Smart scan.
	 */
	@OptIn(FlowPreview::class)
	val audioChanges: Flow<Unit> = callbackFlow {
		val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
			override fun onChange(selfChange: Boolean) {
				trySend(Unit)
			}
		}
		
		context.contentResolver.registerContentObserver(
			MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
			/* notifyForDescendants = */ true,
			observer,
		)
		context.contentResolver.registerContentObserver(
			MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
			/* notifyForDescendants = */ true,
			observer,
		)
		
		awaitClose {
			context.contentResolver.unregisterContentObserver(observer)
		}
	}.debounce(3_000L)
}