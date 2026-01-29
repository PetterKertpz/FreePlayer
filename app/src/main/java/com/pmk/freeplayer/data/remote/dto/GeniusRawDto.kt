package com.pmk.freeplayer.data.remote.dto

import com.google.gson.annotations.SerializedName

// Estructura contenedora (Envelope) requerida por la API
data class GeniusRawSearchResponse(
	@SerializedName("meta") val meta: GeniusMetaRaw,
	@SerializedName("response") val response: GeniusResponseDataRaw?
)

data class GeniusMetaRaw(
	@SerializedName("status") val status: Int
)

data class GeniusResponseDataRaw(
	@SerializedName("hits") val hits: List<GeniusHitRaw>
)

data class GeniusHitRaw(
	@SerializedName("result") val result: GeniusSongRaw
)

// Los datos crudos de la canción según la API
data class GeniusSongRaw(
	@SerializedName("id") val id: Long,
	@SerializedName("title") val title: String,
	@SerializedName("url") val url: String, // URL para el scraping futuro
	@SerializedName("header_image_thumbnail_url") val coverUrl: String?,
	@SerializedName("release_date_for_display") val releaseDate: String?,
	@SerializedName("primary_artist") val primaryArtist: GeniusArtistRaw
)

data class GeniusArtistRaw(
	@SerializedName("name") val name: String
)