package com.example.mausamweather.domain.model

/**
 * Domain model representing a geographic location returned from Geocoding API.
 */
data class City(
    val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val admin1: String? = null
) {
    val displayLocation: String
        get() = if (!admin1.isNullOrBlank()) {
            "$name, $admin1, $country"
        } else {
            "$name, $country"
        }
}
