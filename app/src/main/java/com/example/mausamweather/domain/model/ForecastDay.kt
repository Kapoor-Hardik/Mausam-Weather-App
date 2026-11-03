package com.example.mausamweather.domain.model

data class ForecastDay(
    val date: String,
    val dayOfWeek: String,
    val condition: WeatherCondition,
    val maxTempCelsius: Double,
    val minTempCelsius: Double
)
