package com.example.mausamweather.data.remote

import com.example.mausamweather.data.mapper.WeatherDtoMapper
import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.Weather
import java.io.IOException
import retrofit2.HttpException

/**
 * Encapsulates Open-Meteo Retrofit APIs.
 * Handles network failures, HTTP status errors, timeouts, and maps response DTOs to Domain Models.
 */
class OpenMeteoRemoteDataSource(
    private val geocodingApiService: GeocodingApiService = NetworkClient.geocodingApiService,
    private val weatherApiService: WeatherApiService = NetworkClient.weatherApiService
) {

    suspend fun searchCity(cityName: String): Result<List<City>> {
        val trimmedQuery = cityName.trim()
        if (trimmedQuery.isEmpty()) {
            return Result.failure(IllegalArgumentException("City search query cannot be empty."))
        }

        return try {
            val response = geocodingApiService.searchCity(trimmedQuery)
            if (response.isSuccessful) {
                val dto = response.body()
                val cities = WeatherDtoMapper.mapGeocodingResponseToCities(dto)
                Result.success(cities)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            Result.failure(IOException("Network connection error. Please check your internet connection.", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWeatherForecast(city: City): Result<Weather> {
        return try {
            val response = weatherApiService.getWeatherForecast(
                latitude = city.latitude,
                longitude = city.longitude
            )
            if (response.isSuccessful) {
                val dto = response.body()
                val weather = WeatherDtoMapper.mapWeatherResponseDtoToWeather(city, dto, isFromCache = false)
                Result.success(weather)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            Result.failure(IOException("Network connection error. Please check your internet connection.", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
