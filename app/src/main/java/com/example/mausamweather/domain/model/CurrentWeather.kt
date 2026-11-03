package com.example.mausamweather.domain.model

data class CurrentWeather(
    val temperatureCelsius: Double,
    val relativeHumidity: Int,
    val windSpeedKmh: Double,
    val condition: WeatherCondition,
    val isDay: Boolean = true
)
