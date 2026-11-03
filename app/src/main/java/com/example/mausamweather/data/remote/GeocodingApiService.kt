package com.example.mausamweather.data.remote

import com.example.mausamweather.data.remote.dto.GeocodingResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiService {
    @GET("v1/search")
    suspend fun searchCity(
        @Query("name") cityName: String,
        @Query("count") count: Int = ApiConstants.DEFAULT_SEARCH_COUNT
    ): Response<GeocodingResponseDto>
}
