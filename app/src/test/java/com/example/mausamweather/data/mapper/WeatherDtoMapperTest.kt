package com.example.mausamweather.data.mapper

import com.example.mausamweather.data.remote.dto.CurrentWeatherDto
import com.example.mausamweather.data.remote.dto.DailyWeatherDto
import com.example.mausamweather.data.remote.dto.GeocodingItemDto
import com.example.mausamweather.data.remote.dto.GeocodingResponseDto
import com.example.mausamweather.data.remote.dto.HourlyWeatherDto
import com.example.mausamweather.data.remote.dto.WeatherResponseDto
import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherDtoMapperTest {

    @Test
    fun mapGeocodingResponseToCities_handlesValidResponse() {
        val response = GeocodingResponseDto(
            results = listOf(
                GeocodingItemDto(
                    id = 1,
                    name = "London",
                    latitude = 51.5074,
                    longitude = -0.1278,
                    country = "United Kingdom",
                    admin1 = "England"
                ),
                GeocodingItemDto(
                    id = 2,
                    name = "London",
                    latitude = 42.9834,
                    longitude = -81.233,
                    country = "Canada",
                    admin1 = "Ontario"
                )
            )
        )

        val cities = WeatherDtoMapper.mapGeocodingResponseToCities(response)

        assertEquals(2, cities.size)
        assertEquals("London", cities[0].name)
        assertEquals("United Kingdom", cities[0].country)
        assertEquals("London, England, United Kingdom", cities[0].displayLocation)
        assertEquals("Canada", cities[1].country)
        assertEquals("London, Ontario, Canada", cities[1].displayLocation)
    }

    @Test
    fun mapGeocodingResponseToCities_handlesNullOrEmptyResults() {
        val nullResponse = WeatherDtoMapper.mapGeocodingResponseToCities(null)
        assertTrue(nullResponse.isEmpty())

        val emptyResponse = WeatherDtoMapper.mapGeocodingResponseToCities(GeocodingResponseDto(results = emptyList()))
        assertTrue(emptyResponse.isEmpty())
    }

    @Test
    fun mapGeocodingItemToCity_ignoresItemIfNameOrCoordsAreNull() {
        val invalidItem = GeocodingItemDto(id = 1, name = null, latitude = 51.0, longitude = -0.1)
        assertNull(WeatherDtoMapper.mapGeocodingItemToCity(invalidItem))

        val invalidCoords = GeocodingItemDto(id = 1, name = "Paris", latitude = null, longitude = null)
        assertNull(WeatherDtoMapper.mapGeocodingItemToCity(invalidCoords))
    }

    @Test
    fun mapWeatherResponseDtoToWeather_mapsCurrentAndDailyForecastCorrectly() {
        val city = City(1, "Mumbai", 19.076, 72.8777, "India", "Maharashtra")
        val response = WeatherResponseDto(
            current = CurrentWeatherDto(
                temperature2m = 32.5,
                relativeHumidity2m = 68,
                weatherCode = 0,
                windSpeed10m = 14.2
            ),
            daily = DailyWeatherDto(
                time = listOf("2026-03-30", "2026-03-31", "2026-04-01", "2026-04-02", "2026-04-03"),
                weatherCode = listOf(0, 1, 2, 61, 95),
                temperature2mMax = listOf(33.0, 34.0, 32.0, 30.0, 29.0),
                temperature2mMin = listOf(24.0, 25.0, 23.0, 22.0, 21.0)
            )
        )

        val weather = WeatherDtoMapper.mapWeatherResponseDtoToWeather(city, response)

        assertNotNull(weather)
        assertEquals("Mumbai", weather.city.name)
        assertEquals(32.5, weather.currentTempCelsius, 0.01)
        assertEquals(68, weather.relativeHumidity)
        assertEquals(14.2, weather.windSpeedKmh, 0.01)
        assertEquals(WeatherCondition.ClearSky, weather.condition)
        assertEquals(5, weather.dailyForecast.size)
        assertEquals("2026-03-30", weather.dailyForecast[0].date)
        assertEquals(WeatherCondition.ClearSky, weather.dailyForecast[0].condition)
        assertEquals(WeatherCondition.Thunderstorm, weather.dailyForecast[4].condition)
        assertFalse(weather.isFromCache)
    }

    @Test
    fun mapHourlyWeatherDtoToHourlyForecasts_mapsTimesAndConditionsCorrectly() {
        val hourlyDto = HourlyWeatherDto(
            time = listOf("2026-03-30T00:00", "2026-03-30T01:00"),
            temperature2m = listOf(22.1, 21.5),
            relativeHumidity2m = listOf(80, 82),
            weatherCode = listOf(0, 2)
        )

        val hourlyForecasts = WeatherDtoMapper.mapHourlyWeatherDtoToHourlyForecasts(hourlyDto)

        assertEquals(2, hourlyForecasts.size)
        assertEquals("00:00", hourlyForecasts[0].formattedTime)
        assertEquals(22.1, hourlyForecasts[0].temperatureCelsius, 0.01)
        assertEquals(WeatherCondition.ClearSky, hourlyForecasts[0].condition)
        assertEquals("01:00", hourlyForecasts[1].formattedTime)
        assertEquals(WeatherCondition.PartlyCloudy, hourlyForecasts[1].condition)
    }
}
