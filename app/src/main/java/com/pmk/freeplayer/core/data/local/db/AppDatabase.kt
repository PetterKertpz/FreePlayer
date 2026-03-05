package com.pmk.freeplayer.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pmk.freeplayer.feature.albums.data.local.dao.AlbumDao
import com.pmk.freeplayer.feature.albums.data.local.entity.AlbumEntity
import com.pmk.freeplayer.feature.artists.data.local.dao.ArtistDao
import com.pmk.freeplayer.feature.artists.data.local.entity.ArtistEntity
import com.pmk.freeplayer.feature.auth.data.local.dao.UserDao
import com.pmk.freeplayer.feature.auth.data.local.entity.UserEntity
import com.pmk.freeplayer.feature.genres.data.local.dao.GenreDao
import com.pmk.freeplayer.feature.genres.data.local.entity.GenreEntity
import com.pmk.freeplayer.feature.playlists.data.local.dao.PlaylistDao
import com.pmk.freeplayer.feature.playlists.data.local.entity.PlaylistEntity
import com.pmk.freeplayer.feature.playlists.data.local.relation.PlaylistSongJoin
import com.pmk.freeplayer.feature.songs.data.local.dao.SongDao
import com.pmk.freeplayer.feature.songs.data.local.entity.SongEntity
import com.pmk.freeplayer.feature.statistics.data.local.dao.StatisticsDao
import com.pmk.freeplayer.feature.statistics.data.local.entity.PlayEventEntity
import com.pmk.freeplayer.feature.statistics.data.local.entity.StatsAggregateEntity

@Database(
	entities = [
		// ── Auth ──────────────────────────────────────────────────
		UserEntity::class,
		
		// ── Library submodules ────────────────────────────────────
		SongEntity::class,
		ArtistEntity::class,
		AlbumEntity::class,
		GenreEntity::class,
		
		// ── Playlists ─────────────────────────────────────────────
		PlaylistEntity::class,
		PlaylistSongJoin::class,
		
		// ── Statistics ────────────────────────────────────────────
		PlayEventEntity::class,
		StatsAggregateEntity::class,
		
		// ── Scanner / Metadata added here when rebuilt ────────────
		// ScanResultEntity::class,
		// LyricsEntity::class,
		// EnrichmentResultEntity::class,
	],
	version = 1,
	exportSchema = true, // required for migration audit trail
)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
	
	// ── Auth ──────────────────────────────────────────────────────
	abstract fun userDao(): UserDao
	
	// ── Library submodules ────────────────────────────────────────
	abstract fun songDao(): SongDao
	abstract fun artistDao(): ArtistDao
	abstract fun albumDao(): AlbumDao
	abstract fun genreDao(): GenreDao
	
	// ── Playlists ─────────────────────────────────────────────────
	abstract fun playlistDao(): PlaylistDao
	
	// ── Statistics ────────────────────────────────────────────────
	abstract fun statisticsDao(): StatisticsDao
	
	companion object {
		const val DATABASE_NAME = "freeplayer_db"
	}
}