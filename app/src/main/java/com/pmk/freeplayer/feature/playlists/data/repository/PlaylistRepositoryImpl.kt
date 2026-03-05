package com.pmk.freeplayer.feature.playlists.data.repository

import com.pmk.freeplayer.feature.playlists.data.local.dao.PlaylistDao
import com.pmk.freeplayer.feature.playlists.data.local.relation.PlaylistSongJoin
import com.pmk.freeplayer.feature.playlists.data.mapper.createUserPlaylistEntity
import com.pmk.freeplayer.feature.playlists.data.mapper.toDomain
import com.pmk.freeplayer.feature.playlists.data.mapper.toEntity
import com.pmk.freeplayer.feature.playlists.domain.model.Playlist
import com.pmk.freeplayer.feature.playlists.domain.repository.PlaylistRepository
import com.pmk.freeplayer.feature.playlists.domain.repository.PlaylistWithSongs
import com.pmk.freeplayer.feature.songs.data.local.dao.SongDao
import com.pmk.freeplayer.feature.songs.data.mapper.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class PlaylistRepositoryImpl @Inject constructor(
	private val playlistDao: PlaylistDao,
	private val songDao: SongDao,
	@Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : PlaylistRepository {
	
	// ── Queries ───────────────────────────────────────────────────
	
	override fun getPlaylists(query: String?): Flow<List<Playlist>> =
		if (!query.isNullOrBlank()) playlistDao.search(query).map { it.toDomain() }
		else playlistDao.getAll().map { it.toDomain() }
	
	override fun getUserPlaylists(): Flow<List<Playlist>> =
		playlistDao.getUserPlaylists().map { it.toDomain() }
	
	override fun getSystemPlaylists(): Flow<List<Playlist>> =
		playlistDao.getSystemPlaylists().map { it.toDomain() }
	
	override fun getPlaylistById(id: Long): Flow<Playlist?> =
		playlistDao.getByIdFlow(id).map { it?.toDomain() }
	
	override fun getPlaylistWithSongs(playlistId: Long): Flow<PlaylistWithSongs?> =
		playlistDao.getSongIdsFlow(playlistId)
			.flatMapLatest { songIds ->
				combine(
					playlistDao.getByIdFlow(playlistId),
					songDao.getByIds(songIds),
				) { playlistEntity, songEntities ->
					playlistEntity?.let {
						PlaylistWithSongs(
							playlist = it.toDomain(),
							songs    = songEntities.toDomain(),
						)
					}
				}
			}
	
	override suspend fun existsSongInPlaylist(playlistId: Long, songId: Long): Boolean =
		playlistDao.joinExists(playlistId, songId)
	
	// ── Writes ────────────────────────────────────────────────────
	
	override suspend fun createPlaylist(name: String, description: String?): Long =
		withContext(ioDispatcher) {
			val now = System.currentTimeMillis()
			playlistDao.getOrCreate(name, now).also {
				// If just created, description needs to be set
				playlistDao.getById(it)?.let { entity ->
					if (entity.description != description) {
						playlistDao.update(entity.copy(description = description, updatedAt = now))
					}
				}
			}
		}
	
	override suspend fun updatePlaylist(id: Long, name: String, description: String?) =
		withContext(ioDispatcher) {
			val original = requireNotNull(playlistDao.getById(id)) { "Playlist not found: id=$id" }
			val domain = original.toDomain().copy(name = name, description = description)
			playlistDao.update(domain.toEntity(original, System.currentTimeMillis()))
		}
	
	override suspend fun updatePlaylistCover(id: Long, coverUri: String?) =
		withContext(ioDispatcher) {
			val original = requireNotNull(playlistDao.getById(id)) { "Playlist not found: id=$id" }
			playlistDao.update(original.copy(coverPath = coverUri, updatedAt = System.currentTimeMillis()))
		}
	
	override suspend fun togglePin(playlistId: Long) = withContext(ioDispatcher) {
		playlistDao.togglePinned(playlistId, System.currentTimeMillis())
	}
	
	override suspend fun deletePlaylist(id: Long) = withContext(ioDispatcher) {
		playlistDao.deleteById(id)
	}
	
	override suspend fun duplicatePlaylist(id: Long, newName: String): Long =
		withContext(ioDispatcher) {
			val now = System.currentTimeMillis()
			val newId = playlistDao.insert(createUserPlaylistEntity(newName, now = now))
			val songIds = playlistDao.getSongIds(id)
			if (songIds.isNotEmpty()) {
				insertJoinsAtEnd(newId, songIds, now)
				playlistDao.refreshStats(newId, now)
			}
			newId
		}
	
	// ── Song membership ───────────────────────────────────────────
	
	override suspend fun addSongToPlaylist(playlistId: Long, songId: Long) =
		withContext(ioDispatcher) {
			if (playlistDao.joinExists(playlistId, songId)) return@withContext
			val now = System.currentTimeMillis()
			val nextOrder = (playlistDao.getMaxSortOrder(playlistId) ?: -1) + 1
			playlistDao.insertJoin(PlaylistSongJoin(playlistId, songId, nextOrder, now))
			playlistDao.refreshStats(playlistId, now)
		}
	
	override suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) =
		withContext(ioDispatcher) {
			if (songIds.isEmpty()) return@withContext
			val now = System.currentTimeMillis()
			insertJoinsAtEnd(playlistId, songIds, now)
			playlistDao.refreshStats(playlistId, now)
		}
	
	override suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) =
		withContext(ioDispatcher) {
			playlistDao.deleteJoin(playlistId, songId)
			playlistDao.refreshStats(playlistId, System.currentTimeMillis())
		}
	
	override suspend fun moveSongInPlaylist(playlistId: Long, fromPosition: Int, toPosition: Int) =
		withContext(ioDispatcher) {
			if (fromPosition == toPosition) return@withContext
			val rows = playlistDao.getJoinOrder(playlistId).toMutableList()
			if (fromPosition !in rows.indices || toPosition !in rows.indices) return@withContext
			
			val moved = rows.removeAt(fromPosition)
			rows.add(toPosition, moved)
			
			// Only update rows whose sort_order actually changed
			rows.forEachIndexed { index, row ->
				if (row.sortOrder != index) {
					playlistDao.updateJoinOrder(playlistId, row.songId, index)
				}
			}
		}
	
	// ── Private helpers ───────────────────────────────────────────
	
	/**
	 * Bulk-inserts joins starting after the current max sort_order.
	 * Skips duplicates. Single atomic batch insert.
	 */
	private suspend fun insertJoinsAtEnd(playlistId: Long, songIds: List<Long>, now: Long) {
		var nextOrder = (playlistDao.getMaxSortOrder(playlistId) ?: -1) + 1
		val existingIds = playlistDao.getSongIds(playlistId).toHashSet()
		val joins = songIds
			.filterNot { it in existingIds }
			.map { songId -> PlaylistSongJoin(playlistId, songId, nextOrder++, now) }
		if (joins.isNotEmpty()) playlistDao.insertJoins(joins)
	}
}