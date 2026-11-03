package com.example.mausamweather.data.remote

import com.example.mausamweather.data.remote.dto.WeatherResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getWeatherForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = ApiConstants.CURRENT_WEATHER_VARIABLES,
        @Query("daily") daily: String = ApiConstants.DAILY_WEATHER_VARIABLES,
        @Query("hourly") hourly: String? = ApiConstants.HOURLY_WEATHER_VARIABLES,
        @Query("forecast_days") forecastDays: Int = ApiConstants.DEFAULT_FORECAST_DAYS,
        @Query("timezone") timezone: String = ApiConstants.TIMEZONE_AUTO
    ): Response<WeatherResponseDto>
}
