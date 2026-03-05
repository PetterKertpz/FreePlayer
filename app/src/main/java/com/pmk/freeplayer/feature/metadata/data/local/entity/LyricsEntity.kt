package com.pmk.freeplayer.feature.metadata.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "lyrics",
	indices = [Index(value = ["song_id"], unique = true)]
)
data class LyricsEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "lyrics_id")     val lyricsId: Long = 0,
	
	@ColumnInfo(name = "song_id")       val songId: Long,
	@ColumnInfo(name = "plain_text")    val plainText: String,
	@ColumnInfo(name = "language")      val language: String? = null,
	@ColumnInfo(name = "source")        val source: LyricsSource = LyricsSource.GENIUS_SCRAPE,
	@ColumnInfo(name = "fetched_at")    val fetchedAt: Long,
	@ColumnInfo(name = "last_updated")  val lastUpdated: Long,
)

enum class LyricsSource { GENIUS_SCRAPE, GENIUS_API }