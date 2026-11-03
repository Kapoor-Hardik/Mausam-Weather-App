package com.example.mausamweather.domain.repository

import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.ForecastDay
import com.example.mausamweather.domain.model.HourlyForecast
import com.example.mausamweather.domain.model.Weather

/**
 * Demonstrates Abstraction: Domain repository contract hiding underlying data sources (API, Room DB, or Mock).
 */
interface WeatherRepository {
    suspend fun searchCity(query: String): Result<List<City>>
    suspend fun getWeather(city: City): Result<Weather>
    suspend fun getWeatherByCoordinates(latitude: Double, longitude: Double, cityName: String): Result<Weather>
    suspend fun getHourlyForecast(city: City): Result<List<HourlyForecast>>
    suspend fun getDailyForecast(city: City): Result<List<ForecastDay>>
}
