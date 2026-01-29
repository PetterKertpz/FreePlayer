package com.pmk.freeplayer.data.local.source

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.pmk.freeplayer.data.local.entity.SongEntity
import com.pmk.freeplayer.domain.model.enums.IntegrityStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceMusicDataSource @Inject constructor(
	@ApplicationContext private val context: Context
) {
	companion object {
		private const val TAG = "DeviceMusicDataSource"
		private const val MIN_DURATION_MS = 10_000L
		private const val MIN_FILE_SIZE_BYTES = 100_000L // 100KB mínimo
		
		// Extensiones válidas de audio
		private val VALID_EXTENSIONS = setOf(
			"mp3", "m4a", "aac", "flac", "wav", "ogg", "opus", "wma", "alac"
		)
		
		// Carpetas a excluir (notificaciones, ringtones, etc.)
		private val EXCLUDED_PATHS = listOf(
			"/ringtones/", "/notifications/", "/alarms/",
			"/whatsapp/", "/telegram/", "/call/", "/voice/"
		)
	}
	
	/**
	 * Escanea todas las canciones del dispositivo.
	 * @param onProgress Callback opcional para reportar progreso (0.0 - 1.0)
	 */
	suspend fun scanLocalSongs(
		onProgress: ((Float) -> Unit)? = null
	): Result<List<SongEntity>> = withContext(Dispatchers.IO) {
		runCatching {
			val songs = mutableListOf<SongEntity>()
			val seenPaths = mutableSetOf<String>()
			
			val collection = getMediaCollection()
			val projection = buildProjection()
			val selection = buildSelection()
			val selectionArgs = buildSelectionArgs()
			
			context.contentResolver.query(
				collection,
				projection,
				selection,
				selectionArgs,
				"${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"
			)?.use { cursor ->
				val totalCount = cursor.count
				if (totalCount == 0) return@runCatching emptyList()
				
				val indices = CursorIndices(cursor)
				var processed = 0
				
				while (cursor.moveToNext()) {
					yield() // Permite cancelación de coroutine
					
					val song = parseSongFromCursor(cursor, indices, seenPaths)
					song?.let { songs.add(it) }
					
					processed++
					onProgress?.invoke(processed.toFloat() / totalCount)
				}
			} ?: run {
				Log.w(TAG, "ContentResolver query returned null")
			}
			
			Log.i(TAG, "Scan complete: ${songs.size} songs found")
			songs.toList()
		}.onFailure { e ->
			Log.e(TAG, "Error scanning songs", e)
		}
	}
	
	private fun getMediaCollection(): Uri {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
		} else {
			MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
		}
	}
	
	private fun buildProjection(): Array<String> {
		val base = arrayOf(
			MediaStore.Audio.Media._ID,
			MediaStore.Audio.Media.TITLE,
			MediaStore.Audio.Media.ARTIST,
			MediaStore.Audio.Media.ALBUM,
			MediaStore.Audio.Media.ALBUM_ID,
			MediaStore.Audio.Media.DURATION,
			MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.SIZE,
			MediaStore.Audio.Media.DATE_ADDED,
			MediaStore.Audio.Media.DATE_MODIFIED,
			MediaStore.Audio.Media.TRACK,
			MediaStore.Audio.Media.YEAR,
			MediaStore.Audio.Media.MIME_TYPE
		)
		
		// BITRATE disponible desde Android Q
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			base + MediaStore.Audio.Media.BITRATE
		} else base
	}
	
	private fun buildSelection(): String {
		val conditions = mutableListOf<String>()
		
		// Solo música (no ringtones, alarmas, etc.)
		conditions.add("${MediaStore.Audio.Media.IS_MUSIC} = 1")
		
		// Duración mínima
		conditions.add("${MediaStore.Audio.Media.DURATION} >= ?")
		
		// Tamaño mínimo
		conditions.add("${MediaStore.Audio.Media.SIZE} >= ?")
		
		return conditions.joinToString(" AND ")
	}
	
	private fun buildSelectionArgs(): Array<String> {
		return arrayOf(
			MIN_DURATION_MS.toString(),
			MIN_FILE_SIZE_BYTES.toString()
		)
	}
	
	private fun parseSongFromCursor(
		cursor: android.database.Cursor,
		idx: CursorIndices,
		seenPaths: MutableSet<String>
	): SongEntity? {
		return try {
			val path = cursor.getStringOrNull(idx.path) ?: return null
			
			// Validaciones
			if (!isValidPath(path)) return null
			if (path in seenPaths) return null
			seenPaths.add(path)
			
			val id = cursor.getLong(idx.id)
			val title = cursor.getStringOrNull(idx.title)?.takeIf { it.isNotBlank() }
				?: extractTitleFromPath(path)
			val artist = cursor.getStringOrNull(idx.artist).cleanArtistName()
			val album = cursor.getStringOrNull(idx.album) ?: "Unknown Album"
			val albumId = cursor.getLongOrNull(idx.albumId)
			val duration = cursor.getLong(idx.duration)
			val fileSize = cursor.getLong(idx.size)
			val dateAdded = cursor.getLong(idx.dateAdded).toMillisIfSeconds()
			val dateModified = cursor.getLongOrNull(idx.dateModified)?.toMillisIfSeconds()
			val trackNumber = cursor.getIntOrNull(idx.track)?.normalizeTrackNumber()
			val year = cursor.getIntOrNull(idx.year)?.takeIf { it in 1900..2100 }
			val mimeType = cursor.getStringOrNull(idx.mimeType) ?: guessMimeType(path)
			val bitrate = if (idx.bitrate >= 0) cursor.getIntOrNull(idx.bitrate) else null
			
			// Generar URI del artwork
			val artworkUri = albumId?.let { buildAlbumArtUri(it) }
			
			// Extraer metadatos adicionales si es necesario
			val audioMeta = extractAudioMetadata(path, bitrate)
			
			SongEntity(
				songId = 0, // Room autogenera
				title = title,
				artistId = null, // Se resuelve en Repository con relaciones
				albumId = null,
				genreId = null,
				duration = duration,
				filePath = path,
				size = fileSize,
				mimeType = mimeType,
				dateAdded = dateAdded,
				dateModified = dateModified,
				trackNumber = trackNumber,
				year = year,
				originalTitle = title,
				originalArtist = artist,
				bitrate = audioMeta.bitrate,
				sampleRate = audioMeta.sampleRate,
				audioQuality = audioMeta.quality,
				hasCover = artworkUri != null,
				playCount = 0,
				isFavorite = false,
				metadataStatus = IntegrityStatus.CRUDO.name, // Matches your existing status
				hasLyrics = false,
				confidenceScore = 0f,
				sourceType = "SCANNER",
				// Campos que faltan en tu Entity - ajusta según tu versión real
				discNumber = 1,
			)
		} catch (e: Exception) {
			Log.w(TAG, "Error parsing song at cursor position ${cursor.position}", e)
			null
		}
	}
	
	private fun isValidPath(path: String): Boolean {
		val lowerPath = path.lowercase()
		
		// Verificar extensión
		val extension = path.substringAfterLast('.', "").lowercase()
		if (extension !in VALID_EXTENSIONS) return false
		
		// Excluir carpetas no deseadas
		if (EXCLUDED_PATHS.any { lowerPath.contains(it) }) return false
		
		// Verificar que el archivo existe (opcional, puede ser lento)
		// return File(path).exists()
		return true
	}
	
	private fun extractTitleFromPath(path: String): String {
		return File(path).nameWithoutExtension
			.replace("_", " ")
			.replace("-", " - ")
			.trim()
	}
	
	private fun buildAlbumArtUri(albumId: Long): String {
		return ContentUris.withAppendedId(
			Uri.parse("content://media/external/audio/albumart"),
			albumId
		).toString()
	}
	
	private fun extractAudioMetadata(path: String, existingBitrate: Int?): AudioMetadata {
		// Si ya tenemos bitrate del MediaStore, usarlo
		if (existingBitrate != null && existingBitrate > 0) {
			return AudioMetadata(
				bitrate = existingBitrate,
				sampleRate = null,
				quality = classifyQuality(existingBitrate, null, path)
			)
		}
		
		// Fallback: usar MediaMetadataRetriever (más lento)
		return try {
			MediaMetadataRetriever().use { retriever ->
				retriever.setDataSource(path)
				val bitrate = retriever
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
					?.toIntOrNull()
					?.let { it / 1000 } // Convertir a kbps
				val sampleRate = retriever
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)
					?.toIntOrNull()
				
				AudioMetadata(
					bitrate = bitrate,
					sampleRate = sampleRate,
					quality = classifyQuality(bitrate, sampleRate, path)
				)
			}
		} catch (e: Exception) {
			AudioMetadata(null, null, classifyQuality(null, null, path))
		}
	}
	
	private fun classifyQuality(bitrate: Int?, sampleRate: Int?, path: String): String {
		val ext = path.substringAfterLast('.').lowercase()
		
		// Formatos lossless
		if (ext in listOf("flac", "wav", "alac")) {
			return if (sampleRate != null && sampleRate > 48000) "HI-RES" else "LOSSLESS"
		}
		
		// Clasificar por bitrate
		return when {
			bitrate == null -> "UNKNOWN"
			bitrate >= 320 -> "HIGH"
			bitrate >= 192 -> "MEDIUM"
			else -> "LOW"
		}
	}
	
	private fun guessMimeType(path: String): String {
		return when (path.substringAfterLast('.').lowercase()) {
			"mp3" -> "audio/mpeg"
			"m4a", "aac" -> "audio/mp4"
			"flac" -> "audio/flac"
			"wav" -> "audio/wav"
			"ogg", "opus" -> "audio/ogg"
			"wma" -> "audio/x-ms-wma"
			else -> "audio/*"
		}
	}
	
	// ==================== EXTENSION FUNCTIONS ====================
	
	private fun android.database.Cursor.getStringOrNull(index: Int): String? {
		return if (index >= 0 && !isNull(index)) getString(index) else null
	}
	
	private fun android.database.Cursor.getLongOrNull(index: Int): Long? {
		return if (index >= 0 && !isNull(index)) getLong(index) else null
	}
	
	private fun android.database.Cursor.getIntOrNull(index: Int): Int? {
		return if (index >= 0 && !isNull(index)) getInt(index) else null
	}
	
	private fun String?.cleanArtistName(): String {
		return when {
			this == null -> "Unknown Artist"
			this == "<unknown>" -> "Unknown Artist"
			this.isBlank() -> "Unknown Artist"
			else -> this.trim()
		}
	}
	
	private fun Long.toMillisIfSeconds(): Long {
		// MediaStore devuelve segundos, convertir a milisegundos si es necesario
		return if (this < 1_000_000_000_000L) this * 1000 else this
	}
	
	private fun Int.normalizeTrackNumber(): Int {
		// A veces viene como CDNN (ej: 1002 = CD1, Track 2)
		return this % 1000
	}
	
	// ==================== DATA CLASSES ====================
	
	private data class AudioMetadata(
		val bitrate: Int?,
		val sampleRate: Int?,
		val quality: String
	)
	
	private class CursorIndices(cursor: android.database.Cursor) {
		val id = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
		val title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
		val artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
		val album = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
		val albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
		val duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
		val path = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
		val size = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
		val dateAdded = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
		val dateModified = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)
		val track = cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)
		val year = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)
		val mimeType = cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)
		val bitrate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			cursor.getColumnIndex(MediaStore.Audio.Media.BITRATE)
		} else -1
	}
}

// Extension para usar MediaMetadataRetriever con use {}
private inline fun <R> MediaMetadataRetriever.use(block: (MediaMetadataRetriever) -> R): R {
	return try {
		block(this)
	} finally {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			close()
		} else {
			release()
		}
	}
}