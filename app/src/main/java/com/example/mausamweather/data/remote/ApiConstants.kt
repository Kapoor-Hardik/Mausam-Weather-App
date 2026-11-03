package com.example.mausamweather.data.remote

object ApiConstants {
    const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/"
    const val WEATHER_BASE_URL = "https://api.open-meteo.com/"

    const val DEFAULT_SEARCH_COUNT = 5
    const val DEFAULT_FORECAST_DAYS = 5
    const val TIMEZONE_AUTO = "auto"
    const val CURRENT_WEATHER_VARIABLES = "temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,is_day"
    const val DAILY_WEATHER_VARIABLES = "weather_code,temperature_2m_max,temperature_2m_min"
    const val HOURLY_WEATHER_VARIABLES = "temperature_2m,relative_humidity_2m,weather_code"

    const val NETWORK_TIMEOUT_SECONDS = 15L
}
