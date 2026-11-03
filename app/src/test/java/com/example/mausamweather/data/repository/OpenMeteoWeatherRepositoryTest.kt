package com.example.mausamweather.data.repository

import com.example.mausamweather.data.remote.GeocodingApiService
import com.example.mausamweather.data.remote.OpenMeteoRemoteDataSource
import com.example.mausamweather.data.remote.WeatherApiService
import com.example.mausamweather.data.remote.dto.CurrentWeatherDto
import com.example.mausamweather.data.remote.dto.DailyWeatherDto
import com.example.mausamweather.data.remote.dto.GeocodingItemDto
import com.example.mausamweather.data.remote.dto.GeocodingResponseDto
import com.example.mausamweather.data.remote.dto.WeatherResponseDto
import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.WeatherCondition
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class OpenMeteoWeatherRepositoryTest {

    private lateinit var fakeGeocodingService: FakeGeocodingService
    private lateinit var fakeWeatherService: FakeWeatherService
    private lateinit var repository: OpenMeteoWeatherRepository

    @Before
    fun setUp() {
        fakeGeocodingService = FakeGeocodingService()
        fakeWeatherService = FakeWeatherService()
        val remoteDataSource = OpenMeteoRemoteDataSource(fakeGeocodingService, fakeWeatherService)
        repository = OpenMeteoWeatherRepository(remoteDataSource)
    }

    @Test
    fun searchCity_rejectsBlankInput() = runTest {
        val result = repository.searchCity("")
        assertTrue(result.isFailure)
    }

    @Test
    fun searchCity_returnsMatchingCitiesOnSuccess() = runTest {
        fakeGeocodingService.response = Response.success(
            GeocodingResponseDto(
                results = listOf(
                    GeocodingItemDto(1, "Paris", 48.8566, 2.3522, "France", "Ile-de-France")
                )
            )
        )

        val result = repository.searchCity("Paris")
        assertTrue(result.isSuccess)
        val list = result.getOrThrow()
        assertEquals(1, list.size)
        assertEquals("Paris", list[0].name)
    }

    @Test
    fun getWeather_returnsWeatherOnSuccess() = runTest {
        val city = City(1, "Paris", 48.8566, 2.3522, "France")
        fakeWeatherService.response = Response.success(
            WeatherResponseDto(
                current = CurrentWeatherDto(temperature2m = 15.0, relativeHumidity2m = 50, weatherCode = 0, windSpeed10m = 8.0),
                daily = DailyWeatherDto(time = listOf("2026-03-30"), weatherCode = listOf(0), temperature2mMax = listOf(16.0), temperature2mMin = listOf(10.0))
            )
        )

        val result = repository.getWeather(city)
        assertTrue(result.isSuccess)
        val weather = result.getOrThrow()
        assertEquals(15.0, weather.currentTempCelsius, 0.01)
        assertEquals(WeatherCondition.ClearSky, weather.condition)
    }
}

private class FakeGeocodingService : GeocodingApiService {
    var response: Response<GeocodingResponseDto> = Response.success(GeocodingResponseDto())
    override suspend fun searchCity(cityName: String, count: Int): Response<GeocodingResponseDto> = response
}

private class FakeWeatherService : WeatherApiService {
    var response: Response<WeatherResponseDto> = Response.success(WeatherResponseDto())
    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        current: String,
        daily: String,
        hourly: String?,
        forecastDays: Int,
        timezone: String
    ): Response<WeatherResponseDto> = response
}
