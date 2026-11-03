package com.example.mausamweather.data.repository

import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.ForecastDay
import com.example.mausamweather.domain.model.HourlyForecast
import com.example.mausamweather.domain.model.Weather
import com.example.mausamweather.domain.model.WeatherCondition
import com.example.mausamweather.domain.repository.WeatherRepository

/**
 * Demonstrates Polymorphism:
 * Mock implementation of WeatherRepository supplying realistic sample data for testing and previews.
 */
class MockWeatherRepository(
    var shouldReturnError: Boolean = false,
    var errorMessage: String = "Mock network error"
) : WeatherRepository {

    private val mockCities = listOf(
        City(1, "London", 51.5074, -0.1278, "United Kingdom", "England"),
        City(2, "Mumbai", 19.0760, 72.8777, "India", "Maharashtra"),
        City(3, "Tokyo", 35.6762, 139.6503, "Japan", "Tokyo"),
        City(4, "New York", 40.7128, -74.0060, "United States", "New York")
    )

    override suspend fun searchCity(query: String): Result<List<City>> {
        if (shouldReturnError) return Result.failure(Exception(errorMessage))
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return Result.failure(IllegalArgumentException("City search query cannot be empty."))

        val matches = mockCities.filter {
            it.name.contains(trimmed, ignoreCase = true) || it.country.contains(trimmed, ignoreCase = true)
        }
        return Result.success(matches)
    }

    override suspend fun getWeather(city: City): Result<Weather> {
        if (shouldReturnError) return Result.failure(Exception(errorMessage))

        val mockDaily = listOf(
            ForecastDay("2026-03-30", "Mon", WeatherCondition.ClearSky, 24.0, 15.0),
            ForecastDay("2026-03-31", "Tue", WeatherCondition.PartlyCloudy, 22.0, 14.0),
            ForecastDay("2026-04-01", "Wed", WeatherCondition.SlightRain, 19.0, 12.0),
            ForecastDay("2026-04-02", "Thu", WeatherCondition.Thunderstorm, 18.0, 11.0),
            ForecastDay("2026-04-03", "Fri", WeatherCondition.MainlyClear, 21.0, 13.0)
        )

        val mockHourly = listOf(
            HourlyForecast("2026-03-30T10:00", "10:00", 21.0, 62, WeatherCondition.ClearSky),
            HourlyForecast("2026-03-30T11:00", "11:00", 22.5, 60, WeatherCondition.ClearSky),
            HourlyForecast("2026-03-30T12:00", "12:00", 24.0, 58, WeatherCondition.MainlyClear)
        )

        val weather = Weather(
            city = city,
            currentTempCelsius = 22.5,
            relativeHumidity = 60,
            windSpeedKmh = 12.0,
            condition = WeatherCondition.ClearSky,
            dailyForecast = mockDaily,
            hourlyForecast = mockHourly,
            isFromCache = false
        )
        return Result.success(weather)
    }

    override suspend fun getWeatherByCoordinates(
        latitude: Double,
        longitude: Double,
        cityName: String
    ): Result<Weather> {
        val city = City(0, cityName, latitude, longitude, "Mock Country")
        return getWeather(city)
    }

    override suspend fun getHourlyForecast(city: City): Result<List<HourlyForecast>> {
        return getWeather(city).map { it.hourlyForecast }
    }

    override suspend fun getDailyForecast(city: City): Result<List<ForecastDay>> {
        return getWeather(city).map { it.dailyForecast }
    }
}
