package com.example.mausamweather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherEntity(
    @PrimaryKey
    val cityId: Long,
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val admin1: String?,
    val currentTempCelsius: Double,
    val relativeHumidity: Int,
    val windSpeedKmh: Double,
    val weatherCode: Int,
    val dailyForecastJson: String,
    val hourlyForecastJson: String,
    val cachedTimestamp: Long
)
