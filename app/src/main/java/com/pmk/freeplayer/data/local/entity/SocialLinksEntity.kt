package com.pmk.freeplayer.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "artist_social_links", // Inglés plural
	foreignKeys = [
		ForeignKey(
			entity = ArtistEntity::class,
			parentColumns = ["artist_id"], // Debe coincidir con ArtistEntity
			childColumns = ["artist_id"],
			onDelete = ForeignKey.CASCADE, // Si borras al artista, se borran sus links
			onUpdate = ForeignKey.CASCADE
		)
	],
	indices = [
		Index(value = ["artist_id"]),
		// Evita duplicados: Un artista no puede tener dos links de "INSTAGRAM"
		Index(value = ["artist_id", "platform"], unique = true)
	]
)
data class ArtistSocialLinkEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "link_id") val linkId: Long = 0, // Long estándar
	
	@ColumnInfo(name = "artist_id") val artistId: Long,
	
	// Guardamos el String (ej: "INSTAGRAM", "TWITTER")
	@ColumnInfo(name = "platform") val platform: String,
	
	@ColumnInfo(name = "username") val username: String, // Texto visible: "@eminem"
	@ColumnInfo(name = "url") val url: String,           // Link real: "https://..."
	
	@ColumnInfo(name = "is_verified") val isVerified: Boolean = false,
	@ColumnInfo(name = "follower_count") val followerCount: Int? = null,
	
	@ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)