package com.example.mausamweather.data.local

import com.example.mausamweather.domain.model.City
import com.example.mausamweather.presentation.home.FakeUserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecentSearchesTest {

    private lateinit var fakeRepository: FakeUserPreferencesRepository

    @Before
    fun setUp() {
        fakeRepository = FakeUserPreferencesRepository()
    }

    @Test
    fun addRecentCity_prependsNewCityToFront() = runTest {
        val city1 = City(1, "London", 51.5, -0.1, "UK")
        val city2 = City(2, "Mumbai", 19.0, 72.8, "India")

        fakeRepository.addRecentCity(city1)
        fakeRepository.addRecentCity(city2)

        val recent = fakeRepository.recentCitiesFlow.value
        assertEquals(2, recent.size)
        assertEquals("Mumbai", recent[0].name)
        assertEquals("London", recent[1].name)
    }

    @Test
    fun addRecentCity_removesDuplicatesAndMovesToFront() = runTest {
        val city1 = City(1, "London", 51.5, -0.1, "UK")
        val city2 = City(2, "Mumbai", 19.0, 72.8, "India")
        val city1Duplicate = City(1, "London", 51.5, -0.1, "UK")

        fakeRepository.addRecentCity(city1)
        fakeRepository.addRecentCity(city2)
        fakeRepository.addRecentCity(city1Duplicate)

        val recent = fakeRepository.recentCitiesFlow.value
        assertEquals(2, recent.size)
        assertEquals("London", recent[0].name)
        assertEquals("Mumbai", recent[1].name)
    }

    @Test
    fun addRecentCity_enforcesMaxLimitOfFiveCities() = runTest {
        for (i in 1..7) {
            fakeRepository.addRecentCity(City(i.toLong(), "City$i", 0.0, 0.0, "Country"))
        }

        val recent = fakeRepository.recentCitiesFlow.value
        assertEquals(5, recent.size)
        assertEquals("City7", recent[0].name)
        assertEquals("City3", recent[4].name)
    }

    @Test
    fun clearRecentCities_emptiesHistory() = runTest {
        fakeRepository.addRecentCity(City(1, "Tokyo", 35.6, 139.6, "Japan"))
        fakeRepository.clearRecentCities()

        val recent = fakeRepository.recentCitiesFlow.value
        assertTrue(recent.isEmpty())
    }
}
