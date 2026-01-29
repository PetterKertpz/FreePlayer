package com.pmk.freeplayer.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
   tableName = "songs",
   foreignKeys =
      [
         ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["artist_id"],
            childColumns = ["artist_id"],
            onDelete =
               ForeignKey
                  .SET_NULL, // Si borras el artista, la canción queda "huérfana" (Unknown Artist)
         ),
         ForeignKey(
            entity = AlbumEntity::class,
            parentColumns = ["album_id"],
            childColumns = ["album_id"],
            onDelete = ForeignKey.SET_NULL,
         ),
         ForeignKey(
            entity = GenreEntity::class,
            parentColumns = ["genre_id"],
            childColumns = ["genre_id"],
            onDelete = ForeignKey.SET_NULL,
         ),
      ],
   indices =
      [
         // 🚀 CRÍTICO: Búsqueda instantánea por ruta (Evita duplicados al escanear)
         Index(value = ["file_path"], unique = true),

         // Relaciones
         Index(value = ["artist_id"]),
         Index(value = ["album_id"]),
         Index(value = ["genre_id"]),

         // SortConfig UI
         Index(value = ["title"]),
         Index(value = ["play_count"]), // "Más escuchadas"
         Index(value = ["date_added"]), // "Recién agregadas"
         Index(value = ["is_favorite"]), // Filtro rápido de favoritos

         // Calidad y Metadatos (Smart Playlists)
         Index(value = ["metadata_status"]),
         Index(value = ["has_lyrics"]),
         Index(value = ["audio_quality"]),
      ],
)
data class SongEntity(
   @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "song_id") val songId: Long = 0,

   // ==================== RELACIONES ====================
   @ColumnInfo(name = "artist_id") val artistId: Long?,
   @ColumnInfo(name = "album_id") val albumId: Long?,
   @ColumnInfo(name = "genre_id") val genreId: Long?,

   // ==================== INFORMACIÓN BÁSICA ====================
   @ColumnInfo(name = "title") val title: String,

   // Importante: Guardamos en MS (Long), no en segundos, para mayor precisión
   @ColumnInfo(name = "duration") val duration: Long,
   @ColumnInfo(name = "track_number") val trackNumber: Int? = null,
   @ColumnInfo(name = "disc_number") val discNumber: Int? = 1,
   @ColumnInfo(name = "year") val year: Int? = null,

   // ==================== AUDITORÍA (Rollback) ====================
   // Guardamos los datos originales del archivo por si el usuario
   // edita los tags mal y quiere "Restaurar original".
   @ColumnInfo(name = "original_title") val originalTitle: String? = null,
   @ColumnInfo(name = "original_artist") val originalArtist: String? = null,

   // ==================== METADATOS AVANZADOS (Rich UI) ====================
   // "REMIX", "LIVE", "ACOUSTIC", "COVER", "RADIO_EDIT"
   @ColumnInfo(name = "version_type") val versionType: String? = null,

   // "MANUAL", "SCANNER", "DOWNLOAD"
   @ColumnInfo(name = "source_type") val sourceType: String = "SCANNER",

   // ==================== ARCHIVO FÍSICO ====================
   @ColumnInfo(name = "file_path") val filePath: String,
   @ColumnInfo(name = "file_size") val size: Long, // Bytes
   @ColumnInfo(name = "file_hash")
   val fileHash: String? = null, // Útil para detectar duplicados con distinto nombre
   @ColumnInfo(name = "mime_type") val mimeType: String, // "enums/mp3", "enums/flac"

   // ==================== CALIDAD DE AUDIO (Audiófilo) ====================
   @ColumnInfo(name = "bitrate") val bitrate: Int? = null, // kbps
   @ColumnInfo(name = "sample_rate") val sampleRate: Int? = null, // Hz
   // "LOW", "MEDIUM", "HIGH", "LOSSLESS", "HI-RES"
   @ColumnInfo(name = "audio_quality") val audioQuality: String? = null,

   // ==================== INTEGRACIÓN GENIUS (Scraping) ====================
   @ColumnInfo(name = "genius_id") val geniusId: String? = null,
   @ColumnInfo(name = "genius_url") val geniusUrl: String? = null,
   @ColumnInfo(name = "full_title_genius") val geniusTitle: String? = null,
   @ColumnInfo(name = "is_hot_on_genius") val isHot: Boolean = false,
   @ColumnInfo(name = "genius_pageviews") val pageviews: Int? = null,

   // JSON String para IDs externos: {"spotify": "123", "youtube": "abc"}
   @ColumnInfo(name = "external_ids_json") val externalIdsJson: String? = null,

   // ==================== ESTADÍSTICAS & ESTADO ====================
   @ColumnInfo(name = "play_count") val playCount: Int = 0,
   @ColumnInfo(name = "last_played") val lastPlayed: Long? = null,
   @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false, // ✅ Integrado aquí
   @ColumnInfo(name = "rating") val rating: Float = 0f, // 0.0 - 5.0
   @ColumnInfo(name = "date_added") val dateAdded: Long = System.currentTimeMillis(),
   @ColumnInfo(name = "date_modified") val dateModified: Long? = null, // Fecha del archivo

   // ==================== FLAGS DE SISTEMA ====================
   @ColumnInfo(name = "has_lyrics") val hasLyrics: Boolean = false,
   @ColumnInfo(name = "has_cover") val hasCover: Boolean = false,
   @ColumnInfo(name = "metadata_status") val metadataStatus: String = "CRUDO",
   @ColumnInfo(name = "confidence_score") val confidenceScore: Float = 0f,
   
   @ColumnInfo(name = "fecha_limpieza") val fechaLimpieza: Long? = null,
   @ColumnInfo(name = "fecha_enriquecimiento") val fechaEnriquecimiento: Long? = null,
)
