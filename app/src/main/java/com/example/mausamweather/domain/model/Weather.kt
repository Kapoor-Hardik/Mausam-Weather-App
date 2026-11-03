package com.example.mausamweather.domain.model

data class Weather(
    val city: City,
    val currentTempCelsius: Double,
    val relativeHumidity: Int,
    val windSpeedKmh: Double,
    val condition: WeatherCondition,
    val dailyForecast: List<ForecastDay>,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis(),
    val isFromCache: Boolean = false
)
