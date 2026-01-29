package com.pmk.freeplayer.data.repository

import com.pmk.freeplayer.data.local.dao.LyricsDao
import com.pmk.freeplayer.data.mapper.createNotFoundRecord
import com.pmk.freeplayer.data.mapper.createOnlineLyrics
import com.pmk.freeplayer.data.mapper.createSearchingPlaceholder
import com.pmk.freeplayer.data.mapper.toDomain
import com.pmk.freeplayer.data.mapper.toEntity
import com.pmk.freeplayer.data.mapper.toSourceString
import com.pmk.freeplayer.domain.model.Lyrics
import com.pmk.freeplayer.domain.model.Song
import com.pmk.freeplayer.domain.model.enums.LyricsStatus
import com.pmk.freeplayer.domain.repository.LyricsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class LyricsRepositoryImpl @Inject constructor(
	private val lyricsDao: LyricsDao
) : LyricsRepository {
	
	// ═══════════════════════════════════════════════════════════════
	// GET & MANAGE LYRICS
	// ═══════════════════════════════════════════════════════════════
	
	override fun getLyrics(songId: Long): Flow<Lyrics?> {
		return lyricsDao.getLyricsBySongId(songId).map { it?.toDomain() }
	}
	
	override suspend fun saveLyrics(lyrics: Lyrics) {
		val existing = lyricsDao.getLyricsBySongIdSync(lyrics.songId)
		val entity = if (existing != null) {
			lyrics.toEntity(preserveDateAdded = existing.dateAdded)
		} else {
			lyrics.toEntity()
		}
		lyricsDao.insert(entity)
	}
	
	override suspend fun deleteLyrics(songId: Long) {
		lyricsDao.deleteBySongId(songId)
	}
	
	override suspend fun hasLyrics(songId: Long): Boolean {
		return lyricsDao.hasLyrics(songId)
	}
	
	// ═══════════════════════════════════════════════════════════════
	// SEARCH LYRICS
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun searchOnline(title: String, artist: String): Lyrics? {
		// TODO: Implementar con RemoteDataSource (Genius, etc.) cuando esté disponible
		return null
	}
	
	override suspend fun searchLocalLrcFile(songPath: String): Lyrics? {
		val lrcPath = songPath.substringBeforeLast(".") + ".lrc"
		val lrcFile = File(lrcPath)
		
		if (!lrcFile.exists() || !lrcFile.canRead()) {
			return null
		}
		
		val lrcContent = runCatching { lrcFile.readText() }.getOrNull()
		return if (!lrcContent.isNullOrBlank()) {
			Lyrics(
				id = 0,
				songId = 0, // Caller must set this
				plainText = lrcContent,
				syncedText = lrcContent,
				sourceUrl = lrcPath,
				language = null,
				status = LyricsStatus.FOUND_LOCAL
			)
		} else {
			null
		}
	}
	
	// ═══════════════════════════════════════════════════════════════
	// LYRICS STATUS
	// ═══════════════════════════════════════════════════════════════
	
	override suspend fun updateStatus(songId: Long, status: LyricsStatus) {
		val existing = lyricsDao.getLyricsBySongIdSync(songId)
		
		if (existing != null) {
			// Usa la función del mapper (importada)
			val updated = existing.copy(source = status.toSourceString())
			lyricsDao.update(updated)
		} else {
			val placeholder = when (status) {
				LyricsStatus.SEARCHING -> createSearchingPlaceholder(songId)
				LyricsStatus.NOT_FOUND -> createNotFoundRecord(songId)
				else -> return
			}
			lyricsDao.insert(placeholder)
		}
	}
	
	override suspend fun markAsFound(songId: Long, lyrics: String, sourceUrl: String?) {
		val entity = createOnlineLyrics(
			songId = songId,
			plainText = lyrics,
			sourceUrl = sourceUrl ?: "",
			sourceName = "ONLINE"
		)
		lyricsDao.insert(entity)
	}
	
	override suspend fun markAsNotFound(songId: Long) {
		val entity = createNotFoundRecord(songId)
		lyricsDao.insert(entity)
	}
	
	// ═══════════════════════════════════════════════════════════════
	// BATCH QUERIES
	// ═══════════════════════════════════════════════════════════════
	
	override fun getSongsPendingLyricsSearch(limit: Int): Flow<List<Song>> {
		// TODO: Implementar con SongDao cuando esté disponible
		// Debe retornar canciones donde lyrics.status = NOT_SEARCHED
		throw NotImplementedError("Requiere SongDao y query JOIN")
	}
	
	override suspend fun countByStatus(status: LyricsStatus): Int {
		// TODO: Implementar query específico en LyricsDao
		throw NotImplementedError("Requiere query COUNT WHERE source = ?")
	}
}