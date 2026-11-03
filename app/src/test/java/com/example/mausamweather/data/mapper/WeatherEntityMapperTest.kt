package com.example.mausamweather.data.mapper

import com.example.mausamweather.data.mapper.WeatherEntityMapper.toDomain
import com.example.mausamweather.data.mapper.WeatherEntityMapper.toEntity
import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.ForecastDay
import com.example.mausamweather.domain.model.HourlyForecast
import com.example.mausamweather.domain.model.Weather
import com.example.mausamweather.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherEntityMapperTest {

    @Test
    fun weather_toEntityAndToDomain_preservesAllDataAndFlagsAsCache() {
        val city = City(100, "San Francisco", 37.7749, -122.4194, "United States", "California")
        val daily = listOf(
            ForecastDay("2026-03-30", "Mon", WeatherCondition.ClearSky, 18.0, 10.0)
        )
        val hourly = listOf(
            HourlyForecast("2026-03-30T10:00", "10:00", 15.0, 70, WeatherCondition.ClearSky)
        )
        val timestamp = 1700000000000L

        val originalWeather = Weather(
            city = city,
            currentTempCelsius = 16.5,
            relativeHumidity = 65,
            windSpeedKmh = 10.5,
            condition = WeatherCondition.ClearSky,
            dailyForecast = daily,
            hourlyForecast = hourly,
            lastUpdatedTimestamp = timestamp,
            isFromCache = false
        )

        val entity = originalWeather.toEntity()

        assertEquals(100L, entity.cityId)
        assertEquals("San Francisco", entity.cityName)
        assertEquals(16.5, entity.currentTempCelsius, 0.01)
        assertEquals(timestamp, entity.cachedTimestamp)

        val domainWeather = entity.toDomain()

        assertEquals("San Francisco", domainWeather.city.name)
        assertEquals("United States", domainWeather.city.country)
        assertEquals("California", domainWeather.city.admin1)
        assertEquals(16.5, domainWeather.currentTempCelsius, 0.01)
        assertEquals(WeatherCondition.ClearSky, domainWeather.condition)
        assertEquals(1, domainWeather.dailyForecast.size)
        assertEquals(1, domainWeather.hourlyForecast.size)
        assertEquals(timestamp, domainWeather.lastUpdatedTimestamp)
        assertTrue(domainWeather.isFromCache)
    }
}
