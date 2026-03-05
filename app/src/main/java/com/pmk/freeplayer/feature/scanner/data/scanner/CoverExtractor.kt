package com.pmk.freeplayer.feature.scanner.data.scanner

import android.content.Context
import android.media.MediaMetadataRetriever
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject

class CoverExtractor @Inject constructor(
	@ApplicationContext private val context: Context,
) {
	
	private val coversDir: File by lazy {
		File(context.filesDir, "covers").also { it.mkdirs() }
	}
	
	/**
	 * Extracts the embedded cover art from [filePath] and saves it to the
	 * app's private covers directory.
	 *
	 * Returns the saved file path or null if no embedded art exists.
	 * Skips extraction if a cached file already exists for this path.
	 */
	fun extract(filePath: String): String? {
		val cacheKey  = filePath.md5()
		val cacheFile = File(coversDir, "$cacheKey.jpg")
		if (cacheFile.exists()) return cacheFile.absolutePath
		
		val retriever = MediaMetadataRetriever()
		return try {
			retriever.setDataSource(filePath)
			val bytes = retriever.embeddedPicture ?: return null
			cacheFile.writeBytes(bytes)
			cacheFile.absolutePath
		} catch (_: Exception) {
			null
		} finally {
			retriever.release()
		}
	}
	
	/**
	 * Extracts the sample rate for files where MediaStore doesn't provide it.
	 * Called selectively — not for every file during a full scan.
	 */
	fun extractSampleRate(filePath: String): Int? {
		val retriever = MediaMetadataRetriever()
		return try {
			retriever.setDataSource(filePath)
			retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)
				?.toIntOrNull()
		} catch (_: Exception) {
			null
		} finally {
			retriever.release()
		}
	}
	
	private fun String.md5(): String {
		val digest = MessageDigest.getInstance("MD5")
		return digest.digest(toByteArray())
			.joinToString("") { "%02x".format(it) }
	}
}