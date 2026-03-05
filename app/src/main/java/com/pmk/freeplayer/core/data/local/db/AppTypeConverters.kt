package com.pmk.freeplayer.core.data.local.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pmk.freeplayer.feature.albums.domain.model.AlbumType
import com.pmk.freeplayer.feature.artists.domain.model.ArtistType
import com.pmk.freeplayer.feature.metadata.data.local.entity.LyricsSource
import com.pmk.freeplayer.feature.songs.domain.model.AudioQuality
import com.pmk.freeplayer.feature.songs.domain.model.MetadataStatus
import com.pmk.freeplayer.feature.songs.domain.model.SourceType
import com.pmk.freeplayer.feature.songs.domain.model.VersionType
import com.pmk.freeplayer.feature.statistics.domain.model.EntityType
import com.pmk.freeplayer.feature.statistics.domain.model.PlaySource

// core/data/local/db/AppTypeConverters.kt

class AppTypeConverters {
	
	private val gson = Gson()
	
	// ── List<String> — featuringArtists en SongEntity ─────────────
	
	@TypeConverter
	fun fromStringList(value: List<String>?): String? =
		value?.let { gson.toJson(it) }
	
	@TypeConverter
	fun toStringList(value: String?): List<String>? =
		value?.let { gson.fromJson(it, object : TypeToken<List<String>>() {}.type) }
	
	// ── Map<String, String> — externalIds en SongEntity ──────────
	
	@TypeConverter
	fun fromStringMap(value: Map<String, String>?): String? =
		value?.let { gson.toJson(it) }
	
	@TypeConverter
	fun toStringMap(value: String?): Map<String, String>? =
		value?.let { gson.fromJson(it, object : TypeToken<Map<String, String>>() {}.type) }
	
	// ── AudioQuality ──────────────────────────────────────────────
	
	@TypeConverter
	fun fromAudioQuality(value: AudioQuality): String = value.name
	
	@TypeConverter
	fun toAudioQuality(value: String): AudioQuality =
		runCatching { AudioQuality.valueOf(value) }.getOrDefault(AudioQuality.UNKNOWN)
	
	// ── VersionType ───────────────────────────────────────────────
	
	@TypeConverter
	fun fromVersionType(value: VersionType): String = value.name
	
	@TypeConverter
	fun toVersionType(value: String): VersionType =
		runCatching { VersionType.valueOf(value) }.getOrDefault(VersionType.ORIGINAL)
	
	// ── SourceType ────────────────────────────────────────────────
	
	@TypeConverter
	fun fromSourceType(value: SourceType): String = value.name
	
	@TypeConverter
	fun toSourceType(value: String): SourceType =
		runCatching { SourceType.valueOf(value) }.getOrDefault(SourceType.LOCAL)
	
	// ── MetadataStatus (sealed interface) ─────────────────────────
	// Formato: "RAW" | "CLEAN" | "ENRICHED" | "NOT_FOUND" |
	//          "SKIPPED"  | "FAILED_1" | "FAILED_2" | "FAILED_3"
	
	@TypeConverter
	fun fromMetadataStatus(value: MetadataStatus): String =
		with(MetadataStatus.Companion) { value.storageKey() }
	
	@TypeConverter
	fun toMetadataStatus(value: String): MetadataStatus =
		MetadataStatus.from(value)
	
	// ── AlbumType ─────────────────────────────────────────────────
	
	@TypeConverter
	fun fromAlbumType(value: AlbumType): String = value.name
	
	@TypeConverter
	fun toAlbumType(value: String): AlbumType =
		runCatching { AlbumType.valueOf(value) }.getOrDefault(AlbumType.ALBUM)
	
	// ── ArtistType ────────────────────────────────────────────────
	
	@TypeConverter
	fun fromArtistType(value: ArtistType): String = value.name
	
	@TypeConverter
	fun toArtistType(value: String): ArtistType =
		ArtistType.from(value)
	
	// ── EntityType — stats_aggregates PK ─────────────────────────
	
	@TypeConverter
	fun fromEntityType(value: EntityType): String = value.name
	
	@TypeConverter
	fun toEntityType(value: String): EntityType =
		runCatching { EntityType.valueOf(value) }.getOrThrow()
	
	// ── PlaySource — play_events ──────────────────────────────────
	
	@TypeConverter
	fun fromPlaySource(value: PlaySource): String = value.name
	
	@TypeConverter
	fun toPlaySource(value: String): PlaySource =
		runCatching { PlaySource.valueOf(value) }.getOrDefault(PlaySource.LIBRARY)
	
	// ── LyricsSource — lyrics table ───────────────────────────────
	
	@TypeConverter
	fun fromLyricsSource(value: LyricsSource): String = value.name
	
	@TypeConverter
	fun toLyricsSource(value: String): LyricsSource =
		runCatching { LyricsSource.valueOf(value) }.getOrDefault(LyricsSource.GENIUS_SCRAPE)
}