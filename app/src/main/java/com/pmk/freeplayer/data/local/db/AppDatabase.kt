// en: app/src/main/java/com/example/freeplayerm/data/local/AppDatabase.kt
package com.pmk.freeplayer.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pmk.freeplayer.data.local.dao.AlbumDao
import com.pmk.freeplayer.data.local.dao.ArtistDao
import com.pmk.freeplayer.data.local.dao.GenreDao
import com.pmk.freeplayer.data.local.dao.PlaylistDao
import com.pmk.freeplayer.data.local.dao.QueueDao
import com.pmk.freeplayer.data.local.dao.SongDao
import com.pmk.freeplayer.data.local.dao.UserDao
import com.pmk.freeplayer.data.local.entity.AlbumEntity
import com.pmk.freeplayer.data.local.entity.ArtistEntity
import com.pmk.freeplayer.data.local.entity.ArtistSocialLinkEntity
import com.pmk.freeplayer.data.local.entity.GenreEntity
import com.pmk.freeplayer.data.local.entity.LyricsEntity
import com.pmk.freeplayer.data.local.entity.PlaybackHistoryEntity
import com.pmk.freeplayer.data.local.entity.PlaylistEntity
import com.pmk.freeplayer.data.local.entity.QueueEntity
import com.pmk.freeplayer.data.local.entity.SongEntity
import com.pmk.freeplayer.data.local.entity.UserEntity
import com.pmk.freeplayer.data.local.entity.relation.PlaylistSongEntity
import com.pmk.freeplayer.data.local.entity.relation.SongArtistEntity

@Database(
   entities =
      [
         // ==================== ENTIDADES BASE ====================
         UserEntity::class,
         SongEntity::class,
         AlbumEntity::class,
         ArtistEntity::class,
         GenreEntity::class,

         // ==================== ORGANIZACIÓN ====================
         PlaylistEntity::class,
         QueueEntity::class, // La versión ligera que hicimos
         PlaybackHistoryEntity::class, // Analytics

         // ==================== DETALLES ====================
         LyricsEntity::class,
         ArtistSocialLinkEntity::class,

         // ==================== TABLAS INTERMEDIAS (RELACIONES N:M) ====================
         // Estas son vitales para Room
         PlaylistSongEntity::class, // Canciones dentro de Playlist
         SongArtistEntity::class, // Feat. Artists (Queen & Bowie)
      ],
   version = 1, // Reiniciamos a 1 porque cambiamos todo el esquema
   exportSchema = false,
)
@TypeConverters(TypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

   // ==================== DAOs ====================

   abstract fun userDao(): UserDao

   abstract fun songDao(): SongDao

   abstract fun albumDao(): AlbumDao

   abstract fun artistDao(): ArtistDao

   abstract fun genreDao(): GenreDao

   abstract fun playlistDao(): PlaylistDao

   abstract fun queueDao(): QueueDao // Antes PlaybackQueueDao

   abstract fun historyDao(): ArtistDao // (Debes crear HistoryDao, usé ArtistDao de placeholder)

   // Nota: ArtistSocialLink y Lyrics se suelen acceder a través de
   // ArtistDao y SongDao respectivamente, o puedes crear sus propios DAOs si prefieres.

   companion object {
      const val DATABASE_NAME = "freeplayer_db"
   }
}
