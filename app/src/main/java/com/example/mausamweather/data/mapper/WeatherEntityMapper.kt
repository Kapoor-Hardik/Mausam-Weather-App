package com.example.mausamweather.data.mapper

import com.example.mausamweather.data.local.entity.WeatherEntity
import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.ForecastDay
import com.example.mausamweather.domain.model.HourlyForecast
import com.example.mausamweather.domain.model.Weather
import com.example.mausamweather.utils.WeatherCodeMapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private data class CachedForecastDayDto(
    val date: String,
    val dayOfWeek: String,
    val weatherCode: Int,
    val maxTempCelsius: Double,
    val minTempCelsius: Double
)

private data class CachedHourlyForecastDto(
    val timeIso: String,
    val formattedTime: String,
    val weatherCode: Int,
    val temperatureCelsius: Double,
    val humidity: Int
)

object WeatherEntityMapper {

    private val gson by lazy { Gson() }

    fun Weather.toEntity(): WeatherEntity {
        val dailyDtos = this.dailyForecast.map {
            CachedForecastDayDto(
                date = it.date,
                dayOfWeek = it.dayOfWeek,
                weatherCode = it.condition.code,
                maxTempCelsius = it.maxTempCelsius,
                minTempCelsius = it.minTempCelsius
            )
        }

        val hourlyDtos = this.hourlyForecast.map {
            CachedHourlyForecastDto(
                timeIso = it.timeIso,
                formattedTime = it.formattedTime,
                weatherCode = it.condition.code,
                temperatureCelsius = it.temperatureCelsius,
                humidity = it.humidity
            )
        }

        return WeatherEntity(
            cityId = this.city.id,
            cityName = this.city.name,
            latitude = this.city.latitude,
            longitude = this.city.longitude,
            country = this.city.country,
            admin1 = this.city.admin1,
            currentTempCelsius = this.currentTempCelsius,
            relativeHumidity = this.relativeHumidity,
            windSpeedKmh = this.windSpeedKmh,
            weatherCode = this.condition.code,
            dailyForecastJson = gson.toJson(dailyDtos),
            hourlyForecastJson = gson.toJson(hourlyDtos),
            cachedTimestamp = this.lastUpdatedTimestamp
        )
    }

    fun WeatherEntity.toDomain(): Weather {
        val dailyType = object : TypeToken<List<CachedForecastDayDto>>() {}.type
        val hourlyType = object : TypeToken<List<CachedHourlyForecastDto>>() {}.type

        val dailyDtos: List<CachedForecastDayDto> = try {
            gson.fromJson(this.dailyForecastJson, dailyType) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        val hourlyDtos: List<CachedHourlyForecastDto> = try {
            gson.fromJson(this.hourlyForecastJson, hourlyType) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        val dailyForecasts = dailyDtos.map {
            ForecastDay(
                date = it.date,
                dayOfWeek = it.dayOfWeek,
                condition = WeatherCodeMapper.mapCodeToCondition(it.weatherCode),
                maxTempCelsius = it.maxTempCelsius,
                minTempCelsius = it.minTempCelsius
            )
        }

        val hourlyForecasts = hourlyDtos.map {
            HourlyForecast(
                timeIso = it.timeIso,
                formattedTime = it.formattedTime,
                temperatureCelsius = it.temperatureCelsius,
                humidity = it.humidity,
                condition = WeatherCodeMapper.mapCodeToCondition(it.weatherCode)
            )
        }

        val city = City(
            id = this.cityId,
            name = this.cityName,
            latitude = this.latitude,
            longitude = this.longitude,
            country = this.country,
            admin1 = this.admin1
        )

        return Weather(
            city = city,
            currentTempCelsius = this.currentTempCelsius,
            relativeHumidity = this.relativeHumidity,
            windSpeedKmh = this.windSpeedKmh,
            condition = WeatherCodeMapper.mapCodeToCondition(this.weatherCode),
            dailyForecast = dailyForecasts,
            hourlyForecast = hourlyForecasts,
            lastUpdatedTimestamp = this.cachedTimestamp,
            isFromCache = true
        )
    }
}
