package com.pmk.freeplayer.feature.scanner.data.scanner

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.pmk.freeplayer.core.domain.model.enums.AudioFormat
import com.pmk.freeplayer.feature.scanner.domain.model.ScanConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

data class RawAudioFile(
	val filePath: String,
	val title: String,
	val artistName: String,
	val albumName: String,
	val genreName: String?,
	val durationMs: Long,
	val sizeBytes: Long,
	val mimeType: String,
	val bitrate: Int?,
	val sampleRate: Int?,
	val trackNumber: Int?,
	val discNumber: Int?,
	val year: Int?,
	val dateModified: Long,
	val dateAdded: Long,
)

class MediaStoreReader @Inject constructor(
	@ApplicationContext private val context: Context,
) {
	
	private val PROJECTION = arrayOf(
		MediaStore.Audio.Media._ID,
		MediaStore.Audio.Media.DATA,
		MediaStore.Audio.Media.TITLE,
		MediaStore.Audio.Media.ARTIST,
		MediaStore.Audio.Media.ALBUM,
		MediaStore.Audio.Media.DURATION,
		MediaStore.Audio.Media.SIZE,
		MediaStore.Audio.Media.MIME_TYPE,
		MediaStore.Audio.Media.BITRATE,
		MediaStore.Audio.Media.YEAR,
		MediaStore.Audio.Media.TRACK,
		MediaStore.Audio.Media.DATE_MODIFIED,
		MediaStore.Audio.Media.DATE_ADDED,
		MediaStore.Audio.Media.DISC_NUMBER,
	)
	
	private val SUPPORTED_EXTENSIONS: Set<String> =
		AudioFormat.entries
			.filter { it != AudioFormat.UNKNOWN }
			.map { it.extension }
			.toSet()
	
	/**
	 * Queries internal + external MediaStore volumes and returns all
	 * audio files that pass the [config] filters.
	 */
	fun readAll(config: ScanConfig): List<RawAudioFile> {
		val volumes = mutableListOf(MediaStore.Audio.Media.INTERNAL_CONTENT_URI)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			MediaStore.getExternalVolumeNames(context).forEach { vol ->
				volumes.add(MediaStore.Audio.Media.getContentUri(vol))
			}
		} else {
			volumes.add(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
		}
		
		return volumes.flatMap { uri ->
			queryVolume(uri, config)
		}
	}
	
	private fun queryVolume(
		uri: android.net.Uri,
		config: ScanConfig,
	): List<RawAudioFile> {
		val results = mutableListOf<RawAudioFile>()
		context.contentResolver.query(
			uri,
			PROJECTION,
			"${MediaStore.Audio.Media.DURATION} >= ?",
			arrayOf(config.minDurationMs.toString()),
			null,
		)?.use { cursor ->
			val pathCol       = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
			val titleCol      = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
			val artistCol     = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
			val albumCol      = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
			val durationCol   = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
			val sizeCol       = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
			val mimeCol       = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
			val bitrateCol    = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BITRATE)
			val yearCol       = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
			val trackCol      = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
			val modifiedCol   = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
			val addedCol      = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
			val discCol       = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISC_NUMBER)
			
			while (cursor.moveToNext()) {
				val path = cursor.getString(pathCol) ?: continue
				
				if (isExcluded(path, config.excludedPaths)) continue
				
				val ext = path.substringAfterLast('.', "").lowercase()
				if (ext !in SUPPORTED_EXTENSIONS) continue
				
				val rawTrack = cursor.getInt(trackCol)
				// TRACK encodes disc in thousands: 1002 = disc 1 track 2
				val trackNum = if (rawTrack > 0) rawTrack % 1000 else null
				val discNum  = cursor.getString(discCol)
					?.substringBefore('/')
					?.trim()
					?.toIntOrNull()
				
				results.add(
					RawAudioFile(
						filePath     = path,
						title        = cursor.getString(titleCol)
							?.takeIf { it.isNotBlank() }
							?: path.substringAfterLast('/').substringBeforeLast('.'),
						artistName   = cursor.getString(artistCol)
							?.takeIf { it.isNotBlank() && it != "<unknown>" }
							?: "Unknown Artist",
						albumName    = cursor.getString(albumCol)
							?.takeIf { it.isNotBlank() && it != "<unknown>" }
							?: "Unknown Album",
						genreName    = null, // Genre requires separate ContentResolver query
						durationMs   = cursor.getLong(durationCol),
						sizeBytes    = cursor.getLong(sizeCol),
						mimeType     = cursor.getString(mimeCol) ?: "audio/mpeg",
						bitrate      = cursor.getInt(bitrateCol).takeIf { it > 0 },
						sampleRate   = null, // Not available via MediaStore — extracted by CoverExtractor
						trackNumber  = trackNum,
						discNumber   = discNum,
						year         = cursor.getInt(yearCol).takeIf { it > 0 },
						dateModified = cursor.getLong(modifiedCol) * 1_000L, // seconds → ms
						dateAdded    = cursor.getLong(addedCol) * 1_000L,
					)
				)
			}
		}
		return results
	}
	
	private fun isExcluded(path: String, excludedPaths: Set<String>): Boolean =
		excludedPaths.any { excluded -> path.startsWith(excluded) }
}