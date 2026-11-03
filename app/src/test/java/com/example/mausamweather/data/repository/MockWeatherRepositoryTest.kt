package com.example.mausamweather.data.repository

import com.example.mausamweather.domain.model.City
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MockWeatherRepositoryTest {

    private lateinit var mockRepository: MockWeatherRepository

    @Before
    fun setUp() {
        mockRepository = MockWeatherRepository()
    }

    @Test
    fun searchCity_returnsMatchingMockCities() = runTest {
        val result = mockRepository.searchCity("London")
        assertTrue(result.isSuccess)
        val cities = result.getOrThrow()
        assertEquals(1, cities.size)
        assertEquals("London", cities[0].name)
    }

    @Test
    fun getWeather_returnsRealisticMockWeatherData() = runTest {
        val city = City(1, "Tokyo", 35.6762, 139.6503, "Japan")
        val result = mockRepository.getWeather(city)
        assertTrue(result.isSuccess)
        val weather = result.getOrThrow()
        assertEquals("Tokyo", weather.city.name)
        assertEquals(5, weather.dailyForecast.size)
    }

    @Test
    fun mockRepository_returnsErrorWhenConfigured() = runTest {
        mockRepository.shouldReturnError = true
        val result = mockRepository.searchCity("London")
        assertTrue(result.isFailure)
        assertEquals("Mock network error", result.exceptionOrNull()?.message)
    }
}
