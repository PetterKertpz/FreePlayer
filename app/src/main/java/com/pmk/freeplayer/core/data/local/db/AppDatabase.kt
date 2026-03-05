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
import com.pmk.freeplayer.feature.metadata.data.local.dao.LyricsDao
import com.pmk.freeplayer.feature.metadata.data.local.entity.LyricsEntity
import com.pmk.freeplayer.feature.playlists.data.local.dao.PlaylistDao
import com.pmk.freeplayer.feature.playlists.data.local.entity.PlaylistEntity
import com.pmk.freeplayer.feature.playlists.data.local.relation.PlaylistSongJoin
import com.pmk.freeplayer.feature.songs.data.local.dao.SongDao
import com.pmk.freeplayer.feature.songs.data.local.entity.SongEntity
import com.pmk.freeplayer.feature.statistics.data.local.dao.StatisticsDao
import com.pmk.freeplayer.feature.statistics.data.local.entity.PlayEventEntity
import com.pmk.freeplayer.feature.statistics.data.local.entity.StatsAggregateEntity

// core/data/local/db/AppDatabase.kt — versión completa

@Database(
	entities = [
		UserEntity::class,
		SongEntity::class,
		ArtistEntity::class,
		AlbumEntity::class,
		GenreEntity::class,
		PlaylistEntity::class,
		PlaylistSongJoin::class,
		PlayEventEntity::class,
		StatsAggregateEntity::class,
		LyricsEntity::class,         // ← agregado (feature/metadata)
	],
	version = 2,                     // ← bump por LyricsEntity
	exportSchema = true,
)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
	
	abstract fun userDao(): UserDao
	abstract fun songDao(): SongDao
	abstract fun artistDao(): ArtistDao
	abstract fun albumDao(): AlbumDao
	abstract fun genreDao(): GenreDao
	abstract fun playlistDao(): PlaylistDao
	abstract fun statisticsDao(): StatisticsDao
	abstract fun lyricsDao(): LyricsDao      // ← agregado
	
	companion object {
		const val DATABASE_NAME = "freeplayer_db"
	}
}