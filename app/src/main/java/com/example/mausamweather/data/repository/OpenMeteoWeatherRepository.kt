package com.example.mausamweather.data.repository

import com.example.mausamweather.data.local.dao.WeatherDao
import com.example.mausamweather.data.mapper.WeatherEntityMapper.toDomain
import com.example.mausamweather.data.mapper.WeatherEntityMapper.toEntity
import com.example.mausamweather.data.remote.OpenMeteoRemoteDataSource
import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.ForecastDay
import com.example.mausamweather.domain.model.HourlyForecast
import com.example.mausamweather.domain.model.Weather
import com.example.mausamweather.domain.repository.WeatherRepository

/**
 * Demonstrates Polymorphism & Abstraction:
 * Real repository implementation combining live Open-Meteo network requests
 * with Room Database offline caching fallback.
 */
class OpenMeteoWeatherRepository(
    private val remoteDataSource: OpenMeteoRemoteDataSource = OpenMeteoRemoteDataSource(),
    private val weatherDao: WeatherDao? = null
) : WeatherRepository {

    override suspend fun searchCity(query: String): Result<List<City>> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("City name cannot be empty."))
        }

        val networkResult = remoteDataSource.searchCity(trimmed)
        if (networkResult.isSuccess) {
            return networkResult
        }

        // Offline fallback: Search local Room cache for matching cities
        if (weatherDao != null) {
            val cachedEntities = weatherDao.getAllCachedWeather()
            val offlineMatches = cachedEntities
                .filter { it.cityName.contains(trimmed, ignoreCase = true) || it.country.contains(trimmed, ignoreCase = true) }
                .map { entity ->
                    City(
                        id = entity.cityId,
                        name = entity.cityName,
                        latitude = entity.latitude,
                        longitude = entity.longitude,
                        country = entity.country,
                        admin1 = entity.admin1
                    )
                }
            if (offlineMatches.isNotEmpty()) {
                return Result.success(offlineMatches)
            }
        }

        return networkResult
    }

    override suspend fun getWeather(city: City): Result<Weather> {
        val networkResult = remoteDataSource.getWeatherForecast(city)

        if (networkResult.isSuccess) {
            val liveWeather = networkResult.getOrThrow()
            // Save fresh data to local Room cache
            weatherDao?.insertWeather(liveWeather.toEntity())
            return Result.success(liveWeather)
        }

        // Offline fallback: Query local Room cache by cityId or cityName
        if (weatherDao != null) {
            val cachedEntity = weatherDao.getWeatherByCityId(city.id)
                ?: weatherDao.getWeatherByCityName(city.name)

            if (cachedEntity != null) {
                return Result.success(cachedEntity.toDomain())
            }
        }

        val originalException = networkResult.exceptionOrNull()
            ?: Exception("Unable to fetch live weather and no cached data available for ${city.name}.")

        return Result.failure(originalException)
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
