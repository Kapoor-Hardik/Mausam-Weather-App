package com.example.mausamweather.data.mapper

import com.example.mausamweather.data.remote.dto.CurrentWeatherDto
import com.example.mausamweather.data.remote.dto.DailyWeatherDto
import com.example.mausamweather.data.remote.dto.GeocodingItemDto
import com.example.mausamweather.data.remote.dto.GeocodingResponseDto
import com.example.mausamweather.data.remote.dto.HourlyWeatherDto
import com.example.mausamweather.data.remote.dto.WeatherResponseDto
import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.CurrentWeather
import com.example.mausamweather.domain.model.ForecastDay
import com.example.mausamweather.domain.model.HourlyForecast
import com.example.mausamweather.domain.model.Weather
import com.example.mausamweather.utils.DateFormatter
import com.example.mausamweather.utils.WeatherCodeMapper

/**
 * Clean mapper functions converting raw API DTOs into immutable Domain Models.
 * Provides safe fallback handling for null or missing JSON fields.
 */
object WeatherDtoMapper {

    fun mapGeocodingResponseToCities(responseDto: GeocodingResponseDto?): List<City> {
        val items = responseDto?.results ?: return emptyList()
        return items.mapNotNull { item -> mapGeocodingItemToCity(item) }
    }

    fun mapGeocodingItemToCity(itemDto: GeocodingItemDto?): City? {
        if (itemDto == null) return null
        val name = itemDto.name?.trim() ?: return null
        val lat = itemDto.latitude ?: return null
        val lon = itemDto.longitude ?: return null
        val country = itemDto.country?.trim() ?: "Unknown Country"
        val id = itemDto.id ?: System.currentTimeMillis()

        return City(
            id = id,
            name = name,
            latitude = lat,
            longitude = lon,
            country = country,
            admin1 = itemDto.admin1?.trim()
        )
    }

    fun mapCurrentWeatherDto(currentDto: CurrentWeatherDto?): CurrentWeather {
        val temp = currentDto?.temperature2m ?: 0.0
        val humidity = currentDto?.relativeHumidity2m ?: 0
        val windSpeed = currentDto?.windSpeed10m ?: 0.0
        val rawCode = currentDto?.weatherCode ?: 0
        val condition = WeatherCodeMapper.mapCodeToCondition(rawCode)
        val isDay = currentDto?.isDay != 0

        return CurrentWeather(
            temperatureCelsius = temp,
            relativeHumidity = humidity,
            windSpeedKmh = windSpeed,
            condition = condition,
            isDay = isDay
        )
    }

    fun mapDailyWeatherDtoToForecastDays(dailyDto: DailyWeatherDto?): List<ForecastDay> {
        if (dailyDto == null) return emptyList()
        val times = dailyDto.time ?: return emptyList()
        val codes = dailyDto.weatherCode ?: emptyList()
        val maxTemps = dailyDto.temperature2mMax ?: emptyList()
        val minTemps = dailyDto.temperature2mMin ?: emptyList()

        val forecastList = mutableListOf<ForecastDay>()
        val count = times.size.coerceAtMost(5)

        for (i in 0 until count) {
            val dateStr = times[i]
            val dayOfWeek = DateFormatter.formatIsoDateToDayOfWeek(dateStr)
            val code = if (i < codes.size) codes[i] else 0
            val maxTemp = if (i < maxTemps.size) maxTemps[i] else 0.0
            val minTemp = if (i < minTemps.size) minTemps[i] else 0.0
            val condition = WeatherCodeMapper.mapCodeToCondition(code)

            forecastList.add(
                ForecastDay(
                    date = dateStr,
                    dayOfWeek = dayOfWeek,
                    condition = condition,
                    maxTempCelsius = maxTemp,
                    minTempCelsius = minTemp
                )
            )
        }
        return forecastList
    }

    fun mapHourlyWeatherDtoToHourlyForecasts(hourlyDto: HourlyWeatherDto?): List<HourlyForecast> {
        if (hourlyDto == null) return emptyList()
        val times = hourlyDto.time ?: return emptyList()
        val temps = hourlyDto.temperature2m ?: emptyList()
        val humidities = hourlyDto.relativeHumidity2m ?: emptyList()
        val codes = hourlyDto.weatherCode ?: emptyList()

        val list = mutableListOf<HourlyForecast>()
        val count = times.size.coerceAtMost(24)

        for (i in 0 until count) {
            val timeIso = times[i]
            val formattedTime = if (timeIso.length >= 16) timeIso.substring(11, 16) else timeIso
            val temp = if (i < temps.size) temps[i] else 0.0
            val humidity = if (i < humidities.size) humidities[i] else 0
            val code = if (i < codes.size) codes[i] else 0

            list.add(
                HourlyForecast(
                    timeIso = timeIso,
                    formattedTime = formattedTime,
                    temperatureCelsius = temp,
                    humidity = humidity,
                    condition = WeatherCodeMapper.mapCodeToCondition(code)
                )
            )
        }
        return list
    }

    fun mapWeatherResponseDtoToWeather(
        city: City,
        responseDto: WeatherResponseDto?,
        isFromCache: Boolean = false
    ): Weather {
        val currentWeather = mapCurrentWeatherDto(responseDto?.current)
        val dailyForecasts = mapDailyWeatherDtoToForecastDays(responseDto?.daily)

        return Weather(
            city = city,
            currentTempCelsius = currentWeather.temperatureCelsius,
            relativeHumidity = currentWeather.relativeHumidity,
            windSpeedKmh = currentWeather.windSpeedKmh,
            condition = currentWeather.condition,
            dailyForecast = dailyForecasts,
            lastUpdatedTimestamp = System.currentTimeMillis(),
            isFromCache = isFromCache
        )
    }
}
