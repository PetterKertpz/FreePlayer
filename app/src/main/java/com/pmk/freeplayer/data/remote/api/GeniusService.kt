package com.pmk.freeplayer.data.remote.api

import com.pmk.freeplayer.data.remote.dto.GeniusRawSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeniusService {
	@GET("search")
	suspend fun searchSong(
		@Query("q") query: String
	): Response<GeniusRawSearchResponse> // Usamos el wrapper Raw
}