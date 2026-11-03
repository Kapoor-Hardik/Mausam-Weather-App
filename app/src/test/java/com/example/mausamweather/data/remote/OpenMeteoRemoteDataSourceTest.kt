package com.example.mausamweather.data.remote

import com.example.mausamweather.data.remote.dto.CurrentWeatherDto
import com.example.mausamweather.data.remote.dto.DailyWeatherDto
import com.example.mausamweather.data.remote.dto.GeocodingItemDto
import com.example.mausamweather.data.remote.dto.GeocodingResponseDto
import com.example.mausamweather.data.remote.dto.WeatherResponseDto
import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.WeatherCondition
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class OpenMeteoRemoteDataSourceTest {

    private lateinit var mockGeocodingService: FakeGeocodingApiService
    private lateinit var mockWeatherService: FakeWeatherApiService
    private lateinit var remoteDataSource: OpenMeteoRemoteDataSource

    @Before
    fun setUp() {
        mockGeocodingService = FakeGeocodingApiService()
        mockWeatherService = FakeWeatherApiService()
        remoteDataSource = OpenMeteoRemoteDataSource(
            geocodingApiService = mockGeocodingService,
            weatherApiService = mockWeatherService
        )
    }

    @Test
    fun searchCity_returnsFailureForEmptyOrBlankQuery() = runTest {
        val result = remoteDataSource.searchCity("   ")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun searchCity_returnsSuccessWithMappedCitiesWhenSuccessful() = runTest {
        mockGeocodingService.responseToReturn = Response.success(
            GeocodingResponseDto(
                results = listOf(
                    GeocodingItemDto(1, "Tokyo", 35.6762, 139.6503, "Japan", "Tokyo")
                )
            )
        )

        val result = remoteDataSource.searchCity("Tokyo")
        assertTrue(result.isSuccess)
        val cities = result.getOrThrow()
        assertEquals(1, cities.size)
        assertEquals("Tokyo", cities[0].name)
        assertEquals("Japan", cities[0].country)
    }

    @Test
    fun searchCity_handlesHttpErrorGracefully() = runTest {
        mockGeocodingService.responseToReturn = Response.error(
            500,
            "Internal Server Error".toResponseBody(null)
        )

        val result = remoteDataSource.searchCity("Tokyo")
        assertTrue(result.isFailure)
    }

    @Test
    fun getWeatherForecast_returnsMappedWeatherWhenSuccessful() = runTest {
        val city = City(1, "Berlin", 52.52, 13.405, "Germany", "Berlin")
        mockWeatherService.responseToReturn = Response.success(
            WeatherResponseDto(
                latitude = 52.52,
                longitude = 13.405,
                current = CurrentWeatherDto(
                    temperature2m = 18.4,
                    relativeHumidity2m = 55,
                    weatherCode = 1,
                    windSpeed10m = 10.0
                ),
                daily = DailyWeatherDto(
                    time = listOf("2026-03-30"),
                    weatherCode = listOf(1),
                    temperature2mMax = listOf(20.0),
                    temperature2mMin = listOf(12.0)
                )
            )
        )

        val result = remoteDataSource.getWeatherForecast(city)
        assertTrue(result.isSuccess)

        val weather = result.getOrThrow()
        assertEquals("Berlin", weather.city.name)
        assertEquals(18.4, weather.currentTempCelsius, 0.01)
        assertEquals(WeatherCondition.MainlyClear, weather.condition)
    }
}

private class FakeGeocodingApiService : GeocodingApiService {
    var responseToReturn: Response<GeocodingResponseDto> = Response.success(GeocodingResponseDto())

    override suspend fun searchCity(cityName: String, count: Int): Response<GeocodingResponseDto> {
        return responseToReturn
    }
}

private class FakeWeatherApiService : WeatherApiService {
    var responseToReturn: Response<WeatherResponseDto> = Response.success(WeatherResponseDto())

    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        current: String,
        daily: String,
        hourly: String?,
        forecastDays: Int,
        timezone: String
    ): Response<WeatherResponseDto> {
        return responseToReturn
    }
}
