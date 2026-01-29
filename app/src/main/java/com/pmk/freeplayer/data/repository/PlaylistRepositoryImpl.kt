package com.pmk.freeplayer.data.repository

import com.pmk.freeplayer.data.local.dao.PlaylistDao
import com.pmk.freeplayer.data.local.dao.SongDao
import com.pmk.freeplayer.data.local.entity.relation.PlaylistSongJoin
import com.pmk.freeplayer.data.mapper.createUserPlaylistEntity
import com.pmk.freeplayer.data.mapper.toDomain
import com.pmk.freeplayer.data.mapper.updateCover
import com.pmk.freeplayer.domain.model.Playlist
import com.pmk.freeplayer.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepositoryImpl @Inject constructor(
	private val playlistDao: PlaylistDao,
	private val songDao: SongDao
) : PlaylistRepository {
	
	// ───────────────────────────────────────────────────────────────
	// CRUD operations
	// ───────────────────────────────────────────────────────────────
	
	override fun getAllPlaylists(): Flow<List<Playlist>> {
		return playlistDao.getAllPlaylists().map { it.toDomain() }
	}
	
	override fun getPlaylistById(id: Long): Flow<Playlist?> {
		return playlistDao.getPlaylistById(id).map { it?.toDomain() }
	}
	
	override suspend fun createPlaylist(name: String, description: String?): Long {
		return playlistDao.createPlaylistIfNotExists(name, description)
	}
	
	override suspend fun updatePlaylist(id: Long, name: String, description: String?) {
		val current = playlistDao.getPlaylistByIdSync(id) ?: return
		playlistDao.updateBasicInfo(
			id = id,
			name = name,
			description = description,
			coverPath = current.coverPath,
			timestamp = System.currentTimeMillis()
		)
	}
	
	override suspend fun deletePlaylist(id: Long) {
		playlistDao.deleteById(id)
	}
	
	override suspend fun duplicatePlaylist(id: Long, newName: String): Long {
		val newId = playlistDao.insert(createUserPlaylistEntity(name = newName))
		val originalSongIds = playlistDao.getSongIds(id)
		addSongsToPlaylist(newId, originalSongIds)
		return newId
	}
	
	// ───────────────────────────────────────────────────────────────
	// Song management in playlist
	// ───────────────────────────────────────────────────────────────
	
	override suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
		if (playlistDao.exists(playlistId, songId)) return
		
		val maxOrder = playlistDao.getMaxSortOrder(playlistId) ?: -1
		val join = PlaylistSongJoin(
			playlistId = playlistId,
			songId = songId,
			sortOrder = maxOrder + 1
		)
		playlistDao.insert(join)
		updatePlaylistStats(playlistId)
	}
	
	override suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) {
		if (songIds.isEmpty()) return
		
		var currentOrder = (playlistDao.getMaxSortOrder(playlistId) ?: -1) + 1
		
		songIds.forEach { songId ->
			if (!playlistDao.exists(playlistId, songId)) {
				playlistDao.insert(
					PlaylistSongJoin(
						playlistId = playlistId,
						songId = songId,
						sortOrder = currentOrder++
					)
				)
			}
		}
		updatePlaylistStats(playlistId)
	}
	
	override suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
		playlistDao.delete(playlistId, songId)
		updatePlaylistStats(playlistId)
	}
	
	override suspend fun moveSongInPlaylist(playlistId: Long, fromPosition: Int, toPosition: Int) {
		val items = playlistDao.getJoinData(playlistId).toMutableList()
		
		if (fromPosition !in items.indices || toPosition !in items.indices) return
		if (fromPosition == toPosition) return
		
		val item = items.removeAt(fromPosition)
		items.add(toPosition, item)
		
		// Actualizar solo los items que cambiaron de posición
		items.forEachIndexed { index, data ->
			if (index != data.sort_order) {
				playlistDao.updateOrder(playlistId, data.song_id, index)
			}
		}
	}
	
	override suspend fun existsSongInPlaylist(playlistId: Long, songId: Long): Boolean {
		return playlistDao.exists(playlistId, songId)
	}
	
	// ───────────────────────────────────────────────────────────────
	// Playlist cover
	// ───────────────────────────────────────────────────────────────
	
	override suspend fun updatePlaylistCover(id: Long, uri: String?) {
		val playlist = playlistDao.getPlaylistByIdSync(id) ?: return
		val updated = playlist.updateCover(uri)
		playlistDao.update(updated)
	}
	
	// ───────────────────────────────────────────────────────────────
	// Search
	// ───────────────────────────────────────────────────────────────
	
	override fun searchPlaylists(query: String): Flow<List<Playlist>> {
		return playlistDao.searchPlaylists(query).map { it.toDomain() }
	}
	
	// ───────────────────────────────────────────────────────────────
	// Helpers internos
	// ───────────────────────────────────────────────────────────────
	
	/**
	 * Recalcula y actualiza conteo de canciones y duración total.
	 * Se llama después de cada inserción/borrado.
	 */
	private suspend fun updatePlaylistStats(playlistId: Long) {
		val songIds = playlistDao.getSongIds(playlistId)
		val songCount = songIds.size
		
		// Calcular duración total de forma eficiente
		val totalDuration = if (songIds.isNotEmpty()) {
			songDao.getTotalDurationForIds(songIds)
		} else {
			0L
		}
		
		val playlist = playlistDao.getPlaylistByIdSync(playlistId) ?: return
		playlistDao.update(
			playlist.copy(
				songCount = songCount,
				totalDurationMs = totalDuration,
				updatedAt = System.currentTimeMillis()
			)
		)
	}
}