// en: app/src/main/java/com/example/freeplayerm/data/local/AppDatabase.kt
package com.pmk.freeplayer.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pmk.freeplayer.data.local.dao.AlbumDao
import com.pmk.freeplayer.data.local.dao.ArtistDao
import com.pmk.freeplayer.data.local.dao.EnrichmentResultDao
import com.pmk.freeplayer.data.local.dao.GenreDao
import com.pmk.freeplayer.data.local.dao.LyricsDao
import com.pmk.freeplayer.data.local.dao.PlaybackHistoryDao
import com.pmk.freeplayer.data.local.dao.PlaylistDao
import com.pmk.freeplayer.data.local.dao.QueueDao
import com.pmk.freeplayer.data.local.dao.ScannerDao
import com.pmk.freeplayer.data.local.dao.SongDao
import com.pmk.freeplayer.data.local.dao.UserDao
import com.pmk.freeplayer.data.local.entity.AlbumEntity
import com.pmk.freeplayer.data.local.entity.ArtistEntity
import com.pmk.freeplayer.data.local.entity.CleaningResultEntity
import com.pmk.freeplayer.data.local.entity.EnrichmentResultEntity
import com.pmk.freeplayer.data.local.entity.GenreEntity
import com.pmk.freeplayer.data.local.entity.LyricsEntity
import com.pmk.freeplayer.data.local.entity.PlaybackHistoryEntity
import com.pmk.freeplayer.data.local.entity.PlaylistEntity
import com.pmk.freeplayer.data.local.entity.QueueEntity
import com.pmk.freeplayer.data.local.entity.ScanResultEntity
import com.pmk.freeplayer.data.local.entity.SongEntity
import com.pmk.freeplayer.data.local.entity.UserEntity
import com.pmk.freeplayer.data.local.entity.relation.PlaylistSongJoin

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
