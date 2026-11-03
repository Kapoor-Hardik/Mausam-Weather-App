package com.example.mausamweather.domain.model

data class HourlyForecast(
    val timeIso: String,
    val formattedTime: String,
    val temperatureCelsius: Double,
    val humidity: Int,
    val condition: WeatherCondition
)
