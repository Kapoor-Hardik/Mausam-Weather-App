package com.example.mausamweather.presentation.settings

import com.example.mausamweather.domain.model.AppTheme
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakePreferencesRepository: FakeUserPreferencesRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePreferencesRepository = FakeUserPreferencesRepository()
        viewModel = SettingsViewModel(fakePreferencesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsPreferencesSuccessfully() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(TemperatureUnit.CELSIUS, state.temperatureUnit)
        assertEquals(AppTheme.SYSTEM, state.theme)
    }

    @Test
    fun onTemperatureUnitChanged_updatesRepositoryAndState() = runTest {
        viewModel.onTemperatureUnitChanged(TemperatureUnit.FAHRENHEIT)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(TemperatureUnit.FAHRENHEIT, state.temperatureUnit)
        assertEquals(TemperatureUnit.FAHRENHEIT, fakePreferencesRepository.preferencesFlow.value.temperatureUnit)
    }

    @Test
    fun onThemeChanged_updatesRepositoryAndState() = runTest {
        viewModel.onThemeChanged(AppTheme.DARK)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(AppTheme.DARK, state.theme)
        assertEquals(AppTheme.DARK, fakePreferencesRepository.preferencesFlow.value.theme)
    }
}

class FakeUserPreferencesRepository : UserPreferencesRepository {
    val preferencesFlow = MutableStateFlow(UserPreferences())

    override val userPreferencesFlow: Flow<UserPreferences> = preferencesFlow

    override suspend fun updateTemperatureUnit(unit: TemperatureUnit) {
        preferencesFlow.update { it.copy(temperatureUnit = unit) }
    }

    override suspend fun updateAppTheme(theme: AppTheme) {
        preferencesFlow.update { it.copy(theme = theme) }
    }

    override suspend fun updateLastSelectedCityId(cityId: Long) {
        preferencesFlow.update { it.copy(lastSelectedCityId = cityId) }
    }
}
