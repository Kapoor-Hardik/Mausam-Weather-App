package com.example.mausam.data.remote

import com.example.mausam.domain.model.AirQuality
import com.example.mausam.domain.model.CurrentWeather
import com.example.mausam.domain.model.DailyForecast
import com.example.mausam.domain.model.HourlyForecast
import com.example.mausam.domain.model.MarineConditions
import com.example.mausam.domain.model.PollenData
import com.example.mausam.domain.model.WeatherAlert

/**
 * Abstract data source allowing seamless switching between Mock data, OpenWeatherMap,
 * WeatherAPI, or other meteorological providers.
 */
interface WeatherDataSource {
    suspend fun fetchCurrentWeather(location: String): CurrentWeather
    suspend fun fetchHourlyForecast(location: String): List<HourlyForecast>
    suspend fun fetchDailyForecast(location: String): List<DailyForecast>
    suspend fun fetchAirQuality(location: String): AirQuality
    suspend fun fetchPollenData(location: String): PollenData
    suspend fun fetchMarineConditions(location: String): MarineConditions
    suspend fun fetchWeatherAlerts(location: String): List<WeatherAlert>
}
