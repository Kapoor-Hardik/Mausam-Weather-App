package com.example.mausamweather.data.repository

import com.example.mausamweather.data.remote.OpenMeteoRemoteDataSource
import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.ForecastDay
import com.example.mausamweather.domain.model.HourlyForecast
import com.example.mausamweather.domain.model.Weather
import com.example.mausamweather.domain.repository.WeatherRepository

/**
 * Demonstrates Polymorphism & Abstraction:
 * Real network implementation of WeatherRepository querying Open-Meteo services.
 */
class OpenMeteoWeatherRepository(
    private val remoteDataSource: OpenMeteoRemoteDataSource = OpenMeteoRemoteDataSource()
) : WeatherRepository {

    override suspend fun searchCity(query: String): Result<List<City>> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("City name cannot be empty."))
        }
        return remoteDataSource.searchCity(trimmed)
    }

    override suspend fun getWeather(city: City): Result<Weather> {
        return remoteDataSource.getWeatherForecast(city)
    }

    override suspend fun getWeatherByCoordinates(
        latitude: Double,
        longitude: Double,
        cityName: String
    ): Result<Weather> {
        val city = City(
            id = (latitude.hashCode() * 31L + longitude.hashCode()),
            name = cityName,
            latitude = latitude,
            longitude = longitude,
            country = "Custom Location"
        )
        return getWeather(city)
    }

    override suspend fun getHourlyForecast(city: City): Result<List<HourlyForecast>> {
        return getWeather(city).map { weather ->
            weather.hourlyForecast
        }
    }

    override suspend fun getDailyForecast(city: City): Result<List<ForecastDay>> {
        return getWeather(city).map { weather ->
            weather.dailyForecast
        }
    }
}
