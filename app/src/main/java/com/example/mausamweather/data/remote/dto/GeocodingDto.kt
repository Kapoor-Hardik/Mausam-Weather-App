package com.example.mausamweather.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GeocodingResponseDto(
    @SerializedName("results") val results: List<GeocodingItemDto>? = null,
    @SerializedName("generationtime_ms") val generationTimeMs: Double? = null
)

data class GeocodingItemDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("admin1") val admin1: String? = null,
    @SerializedName("country_code") val countryCode: String? = null
)
