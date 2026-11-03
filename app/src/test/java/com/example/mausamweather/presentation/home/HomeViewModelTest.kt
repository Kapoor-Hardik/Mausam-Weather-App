package com.example.mausamweather.presentation.home

import com.example.mausamweather.data.repository.MockWeatherRepository
import com.example.mausamweather.domain.model.AppTheme
import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.TemperatureUnit
import com.example.mausamweather.domain.model.UserPreferences
import com.example.mausamweather.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockWeatherRepository: MockWeatherRepository
    private lateinit var fakePreferencesRepository: FakeUserPreferencesRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockWeatherRepository = MockWeatherRepository()
        fakePreferencesRepository = FakeUserPreferencesRepository()
        viewModel = HomeViewModel(mockWeatherRepository, fakePreferencesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsDefaultCityWeatherSuccessfully() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)
        val successState = state as HomeUiState.Success
        assertEquals("London", successState.weather.city.name)
        assertEquals(TemperatureUnit.CELSIUS, successState.temperatureUnit)
    }

    @Test
    fun onSearchQueryChanged_updatesSearchResultsWhenMatchesExist() = runTest {
        viewModel.onSearchQueryChanged("Mumbai")
        testDispatcher.scheduler.advanceUntilIdle()

        val searchState = viewModel.searchState.value
        assertEquals("Mumbai", searchState.query)
        assertEquals(1, searchState.searchResults.size)
        assertEquals("Mumbai", searchState.searchResults[0].name)
    }

    @Test
    fun onCitySelected_fetchesWeatherForNewCityAndAddsToRecentSearches() = runTest {
        val newCity = City(2, "Mumbai", 19.076, 72.8777, "India", "Maharashtra")
        viewModel.onCitySelected(newCity)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)
        val successState = state as HomeUiState.Success
        assertEquals("Mumbai", successState.weather.city.name)
        assertEquals("", viewModel.searchState.value.query)

        val recent = viewModel.searchState.value.recentCities
        assertEquals(1, recent.size)
        assertEquals("Mumbai", recent[0].name)
    }

    @Test
    fun onClearRecentCities_clearsRecentSearchesState() = runTest {
        val city = City(2, "Mumbai", 19.076, 72.8777, "India", "Maharashtra")
        viewModel.onCitySelected(city)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.searchState.value.recentCities.size)

        viewModel.onClearRecentCities()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.searchState.value.recentCities.isEmpty())
    }

    @Test
    fun onToggleTemperatureUnit_updatesRepositoryAndReflectsInUiState() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TemperatureUnit.CELSIUS, viewModel.temperatureUnit.value)

        viewModel.onToggleTemperatureUnit()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TemperatureUnit.FAHRENHEIT, viewModel.temperatureUnit.value)
        val state = viewModel.uiState.value as HomeUiState.Success
        assertEquals(TemperatureUnit.FAHRENHEIT, state.temperatureUnit)
    }

    @Test
    fun fetchWeather_handlesRepositoryErrorState() = runTest {
        mockWeatherRepository.shouldReturnError = true
        val city = City(99, "Error City", 0.0, 0.0, "Unknown")

        viewModel.fetchWeatherForCity(city)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Error)
        val errorState = state as HomeUiState.Error
        assertEquals("Mock network error", errorState.message)
    }
}

class FakeUserPreferencesRepository : UserPreferencesRepository {
    val preferencesFlow = MutableStateFlow(UserPreferences())
    val recentCitiesFlow = MutableStateFlow<List<City>>(emptyList())

    override val userPreferencesFlow: Flow<UserPreferences> = preferencesFlow
    override val recentSearchesFlow: Flow<List<City>> = recentCitiesFlow

    override suspend fun updateTemperatureUnit(unit: TemperatureUnit) {
        preferencesFlow.update { it.copy(temperatureUnit = unit) }
    }

    override suspend fun updateAppTheme(theme: AppTheme) {
        preferencesFlow.update { it.copy(theme = theme) }
    }

    override suspend fun updateLastSelectedCityId(cityId: Long) {
        preferencesFlow.update { it.copy(lastSelectedCityId = cityId) }
    }

    override suspend fun addRecentCity(city: City) {
        val currentList = recentCitiesFlow.value.toMutableList()
        currentList.removeAll { 
            it.id == city.id || (it.name.equals(city.name, ignoreCase = true) && it.country.equals(city.country, ignoreCase = true)) 
        }
        currentList.add(0, city)
        recentCitiesFlow.value = currentList.take(5)
    }

    override suspend fun clearRecentCities() {
        recentCitiesFlow.value = emptyList()
    }
}
