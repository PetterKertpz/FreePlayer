// en: app/src/main/java/com/example/freeplayerm/data/local/AppDatabase.kt
package com.pmk.freeplayer.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pmk.freeplayer.feature.albums.data.local.dao.AlbumDao
import com.pmk.freeplayer.feature.artists.data.local.dao.ArtistDao
import com.pmk.freeplayer.feature.genres.data.local.dao.GenreDao
import com.pmk.freeplayer.feature.statistics.data.local.dao.PlaybackHistoryDao
import com.pmk.freeplayer.feature.playlists.data.local.dao.PlaylistDao
import com.pmk.freeplayer.feature.player.data.local.dao.QueueDao
import com.pmk.freeplayer.feature.songs.data.local.dao.SongDao
import com.pmk.freeplayer.feature.auth.data.local.dao.UserDao
import com.pmk.freeplayer.feature.albums.data.local.entity.AlbumEntity
import com.pmk.freeplayer.feature.artists.data.local.entity.ArtistEntity
import com.pmk.freeplayer.feature.genres.data.local.entity.GenreEntity
import com.pmk.freeplayer.feature.statistics.data.local.entity.PlaybackHistoryEntity
import com.pmk.freeplayer.feature.playlists.data.local.entity.PlaylistEntity
import com.pmk.freeplayer.feature.player.data.local.entity.QueueEntity
import com.pmk.freeplayer.feature.songs.data.local.entity.SongEntity
import com.pmk.freeplayer.feature.auth.data.local.entity.UserEntity
import com.pmk.freeplayer.feature.playlists.data.local.relation.PlaylistSongJoin

@Database(
   entities =
      [
         // ==================== ENTIDADES BASE ====================
         UserEntity::class,
         SongEntity::class,
         AlbumEntity::class,
         ArtistEntity::class,
         GenreEntity::class,
         ScanResultEntity::class,
         CleaningResultEntity::class,
         EnrichmentResultEntity::class,
         // ==================== ORGANIZACIÓN ====================
         PlaylistEntity::class,
         QueueEntity::class, // La versión ligera que hicimos
         PlaybackHistoryEntity::class, // Analytics

         // ==================== DETALLES ====================
         LyricsEntity::class,

         // ==================== TABLAS INTERMEDIAS (RELACIONES N:M) ====================
         // Estas son vitales para Room
         PlaylistSongJoin::class,
      ],
   version = 1, // Reiniciamos a 1 porque cambiamos todo el esquema
   exportSchema = false,
)
@TypeConverters(Converts::class)
abstract class AppDatabase : RoomDatabase() {

   // ==================== DAOs ====================

   abstract fun userDao(): UserDao

   abstract fun songDao(): SongDao

   abstract fun albumDao(): AlbumDao

   abstract fun artistDao(): ArtistDao

   abstract fun genreDao(): GenreDao

   abstract fun playlistDao(): PlaylistDao
	
	abstract fun lyricsDao(): LyricsDao
	
	abstract fun enrichmentResultDao(): EnrichmentResultDao

	abstract fun playbackHistoryDao(): PlaybackHistoryDao

   abstract fun scannerDao(): ScannerDao

   abstract fun queueDao(): QueueDao // Antes PlaybackQueueDao

   
   companion object {
      const val DATABASE_NAME = "freeplayer_db"
   }
}
