package com.example.mausamweather.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherResponseDto(
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null,
    @SerializedName("timezone") val timezone: String? = null,
    @SerializedName("current") val current: CurrentWeatherDto? = null,
    @SerializedName("daily") val daily: DailyWeatherDto? = null,
    @SerializedName("hourly") val hourly: HourlyWeatherDto? = null
)

data class CurrentWeatherDto(
    @SerializedName("temperature_2m") val temperature2m: Double? = null,
    @SerializedName("relative_humidity_2m") val relativeHumidity2m: Int? = null,
    @SerializedName("weather_code") val weatherCode: Int? = null,
    @SerializedName("wind_speed_10m") val windSpeed10m: Double? = null,
    @SerializedName("is_day") val isDay: Int? = null
)

data class DailyWeatherDto(
    @SerializedName("time") val time: List<String>? = null,
    @SerializedName("weather_code") val weatherCode: List<Int>? = null,
    @SerializedName("temperature_2m_max") val temperature2mMax: List<Double>? = null,
    @SerializedName("temperature_2m_min") val temperature2mMin: List<Double>? = null
)

data class HourlyWeatherDto(
    @SerializedName("time") val time: List<String>? = null,
    @SerializedName("temperature_2m") val temperature2m: List<Double>? = null,
    @SerializedName("relative_humidity_2m") val relativeHumidity2m: List<Int>? = null,
    @SerializedName("weather_code") val weatherCode: List<Int>? = null
)
