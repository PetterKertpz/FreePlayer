package com.pmk.freeplayer.feature.playlists.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pmk.freeplayer.feature.playlists.domain.model.SystemPlaylistType

@Entity(
	tableName = "playlists",
	indices = [
		Index(value = ["name"]),
		Index(value = ["is_system"]),
		Index(value = ["is_pinned"]),
		Index(value = ["system_type"]),  // fast lookup for getSystemByType
	]
)
data class PlaylistEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "playlist_id")       val playlistId: Long              = 0,
	
	@ColumnInfo(name = "name")              val name: String,
	@ColumnInfo(name = "description")       val description: String?          = null,
	@ColumnInfo(name = "cover_path")        val coverPath: String?            = null,
	@ColumnInfo(name = "hex_color")         val hexColor: String?             = null,
	
	// ── Type ──────────────────────────────────────────────────────
	@ColumnInfo(name = "is_system")         val isSystem: Boolean             = false,
	@ColumnInfo(name = "system_type")       val systemType: SystemPlaylistType? = null, // FIX: enum
	@ColumnInfo(name = "is_pinned")         val isPinned: Boolean             = false,
	
	// ── Structural cache ──────────────────────────────────────────
	// FIX: play_count removed — delegated to feature/statistics
	@ColumnInfo(name = "song_count")        val songCount: Int                = 0,
	@ColumnInfo(name = "total_duration_ms") val totalDurationMs: Long         = 0L,
	
	// ── Timestamps (FIX: no System.currentTimeMillis() defaults) ──
	@ColumnInfo(name = "created_at")        val createdAt: Long,
	@ColumnInfo(name = "updated_at")        val updatedAt: Long,
)