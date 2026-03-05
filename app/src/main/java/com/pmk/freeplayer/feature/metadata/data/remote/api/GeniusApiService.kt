package com.pmk.freeplayer.feature.metadata.data.remote.api

import GeniusArtistDto
import GeniusMeta
import GeniusSearchResponse
import GeniusSongDetailResponse
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// feature/metadata/data/remote/api/GeniusApiService.kt

interface GeniusApiService {
	
	@GET("search")
	suspend fun search(
		@Query("q") query: String,
		@Query("per_page") perPage: Int = 5,
	): GeniusSearchResponse
	
	@GET("songs/{id}")
	suspend fun getSong(
		@Path("id") songId: Int,
		@Query("text_format") textFormat: String = "plain",
	): GeniusSongDetailResponse
	
	@GET("artists/{id}")
	suspend fun getArtist(
		@Path("id") artistId: Int,
		@Query("text_format") textFormat: String = "plain",
	): GeniusArtistDetailResponse
}

data class GeniusArtistDetailResponse(
	@SerializedName("meta")     val meta: GeniusMeta,
	@SerializedName("response") val response: GeniusArtistBody,
)
data class GeniusArtistBody(@SerializedName("artist") val artist: GeniusArtistDto)