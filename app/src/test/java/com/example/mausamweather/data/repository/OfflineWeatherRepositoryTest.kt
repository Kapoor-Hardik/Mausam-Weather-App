package com.example.mausamweather.data.repository

import com.example.mausamweather.data.local.dao.WeatherDao
import com.example.mausamweather.data.local.entity.WeatherEntity
import com.example.mausamweather.data.remote.GeocodingApiService
import com.example.mausamweather.data.remote.OpenMeteoRemoteDataSource
import com.example.mausamweather.data.remote.WeatherApiService
import com.example.mausamweather.data.remote.dto.CurrentWeatherDto
import com.example.mausamweather.data.remote.dto.DailyWeatherDto
import com.example.mausamweather.data.remote.dto.GeocodingResponseDto
import com.example.mausamweather.data.remote.dto.WeatherResponseDto
import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.WeatherCondition
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class OfflineWeatherRepositoryTest {

    private lateinit var fakeGeocodingService: FakeOfflineGeocodingService
    private lateinit var fakeWeatherService: FakeOfflineWeatherService
    private lateinit var fakeDao: FakeWeatherDao
    private lateinit var repository: OpenMeteoWeatherRepository

    @Before
    fun setUp() {
        fakeGeocodingService = FakeOfflineGeocodingService()
        fakeWeatherService = FakeOfflineWeatherService()
        fakeDao = FakeWeatherDao()
        val remoteDataSource = OpenMeteoRemoteDataSource(fakeGeocodingService, fakeWeatherService)
        repository = OpenMeteoWeatherRepository(remoteDataSource, fakeDao)
    }

    @Test
    fun getWeather_savesToRoomDatabaseWhenOnlineRequestSucceeds() = runTest {
        val city = City(1, "Rome", 41.9028, 12.4964, "Italy")
        fakeWeatherService.response = Response.success(
            WeatherResponseDto(
                current = CurrentWeatherDto(temperature2m = 20.0, relativeHumidity2m = 55, weatherCode = 0, windSpeed10m = 9.0),
                daily = DailyWeatherDto(time = listOf("2026-03-30"), weatherCode = listOf(0), temperature2mMax = listOf(22.0), temperature2mMin = listOf(14.0))
            )
        )

        val result = repository.getWeather(city)

        assertTrue(result.isSuccess)
        val weather = result.getOrThrow()
        assertFalse(weather.isFromCache)

        val savedInDao = fakeDao.getWeatherByCityId(1)
        assertNotNull(savedInDao)
        assertEquals("Rome", savedInDao?.cityName)
        assertEquals(20.0, savedInDao?.currentTempCelsius ?: 0.0, 0.01)
    }

    @Test
    fun getWeather_returnsCachedWeatherWhenOnlineRequestFailsAndCacheExists() = runTest {
        val city = City(2, "Madrid", 40.4168, -3.7038, "Spain")

        fakeDao.insertWeather(
            WeatherEntity(
                cityId = 2,
                cityName = "Madrid",
                latitude = 40.4168,
                longitude = -3.7038,
                country = "Spain",
                admin1 = "Madrid",
                currentTempCelsius = 18.5,
                relativeHumidity = 50,
                windSpeedKmh = 11.0,
                weatherCode = 1,
                dailyForecastJson = "[]",
                hourlyForecastJson = "[]",
                cachedTimestamp = 1700000000000L
            )
        )

        fakeWeatherService.response = Response.error(503, "Service Unavailable".toResponseBody(null))

        val result = repository.getWeather(city)

        assertTrue(result.isSuccess)
        val weather = result.getOrThrow()
        assertTrue(weather.isFromCache)
        assertEquals("Madrid", weather.city.name)
        assertEquals(18.5, weather.currentTempCelsius, 0.01)
        assertEquals(WeatherCondition.MainlyClear, weather.condition)
        assertEquals(1700000000000L, weather.lastUpdatedTimestamp)
    }

    @Test
    fun getWeather_returnsErrorWhenBothOnlineAndCacheLookupFail() = runTest {
        val city = City(99, "Unknown City", 0.0, 0.0, "Unknown")
        fakeWeatherService.response = Response.error(404, "Not Found".toResponseBody(null))

        val result = repository.getWeather(city)

        assertTrue(result.isFailure)
    }
}

private class FakeOfflineGeocodingService : GeocodingApiService {
    var response: Response<GeocodingResponseDto> = Response.success(GeocodingResponseDto())
    override suspend fun searchCity(cityName: String, count: Int): Response<GeocodingResponseDto> = response
}

private class FakeOfflineWeatherService : WeatherApiService {
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

private class FakeWeatherDao : WeatherDao {
    private val cache = mutableMapOf<Long, WeatherEntity>()

    override suspend fun insertWeather(weatherEntity: WeatherEntity): Long {
        cache[weatherEntity.cityId] = weatherEntity
        return weatherEntity.cityId
    }

    override suspend fun getWeatherByCityId(cityId: Long): WeatherEntity? {
        return cache[cityId]
    }

    override suspend fun getWeatherByCityName(cityName: String): WeatherEntity? {
        return cache.values.firstOrNull { it.cityName.equals(cityName, ignoreCase = true) }
    }

    override suspend fun getAllCachedWeather(): List<WeatherEntity> {
        return cache.values.toList()
    }

    override suspend fun getRecentCachedCities(): List<WeatherEntity> {
        return cache.values.sortedByDescending { it.cachedTimestamp }.take(5)
    }

    override suspend fun deleteWeatherByCityId(cityId: Long): Int {
        return if (cache.remove(cityId) != null) 1 else 0
    }

    override suspend fun clearAllCache(): Int {
        val count = cache.size
        cache.clear()
        return count
    }
}
