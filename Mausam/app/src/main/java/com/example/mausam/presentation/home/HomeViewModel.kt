package com.example.mausam.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mausam.domain.model.AgricultureConditions
import com.example.mausam.domain.model.AirQuality
import com.example.mausam.domain.model.CommuteConditions
import com.example.mausam.domain.model.CurrentWeather
import com.example.mausam.domain.model.DailyForecast
import com.example.mausam.domain.model.HourlyForecast
import com.example.mausam.domain.model.MarineConditions
import com.example.mausam.domain.model.PersonalizedDashboard
import com.example.mausam.domain.model.PollenData
import com.example.mausam.domain.model.SchoolCommuteConditions
import com.example.mausam.domain.model.UserPreferences
import com.example.mausam.domain.model.WeatherAlert
import com.example.mausam.domain.repository.AirQualityRepository
import com.example.mausam.domain.repository.AlertRepository
import com.example.mausam.domain.repository.FeatureConditionsRepository
import com.example.mausam.domain.repository.MarineRepository
import com.example.mausam.domain.repository.PreferencesRepository
import com.example.mausam.domain.repository.WeatherRepository
import com.example.mausam.domain.usecase.PersonalizationEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val locationName: String = "Faridabad",
    val currentWeather: CurrentWeather? = null,
    val hourlyForecast: List<HourlyForecast> = emptyList(),
    val dailyForecast: List<DailyForecast> = emptyList(),
    val airQuality: AirQuality? = null,
    val pollenData: PollenData? = null,
    val marineConditions: MarineConditions? = null,
    val schoolCommute: SchoolCommuteConditions? = null,
    val agriculture: AgricultureConditions? = null,
    val commute: CommuteConditions? = null,
    val alerts: List<WeatherAlert> = emptyList(),
    val userPreferences: UserPreferences = UserPreferences(),
    val dashboard: PersonalizedDashboard? = null
)

class HomeViewModel(
    private val weatherRepo: WeatherRepository,
    private val airQualityRepo: AirQualityRepository,
    private val marineRepo: MarineRepository,
    private val alertRepo: AlertRepository,
    private val featureConditionsRepo: FeatureConditionsRepository,
    private val preferencesRepo: PreferencesRepository,
    private val personalizationEngine: PersonalizationEngine = PersonalizationEngine()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepo.getUserPreferences().collectLatest { prefs ->
                _uiState.value = _uiState.value.copy(userPreferences = prefs)
                loadWeatherData(_uiState.value.locationName)
            }
        }
    }

    fun loadWeatherData(location: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, locationName = location)

            val currentRes = weatherRepo.getCurrentWeather(location)
            val hourlyRes = weatherRepo.getHourlyForecast(location)
            val dailyRes = weatherRepo.getDailyForecast(location)
            val aqiRes = airQualityRepo.getAirQuality(location)
            val pollenRes = airQualityRepo.getPollenData(location)
            val marineRes = marineRepo.getMarineConditions(location)
            val alertsRes = alertRepo.getActiveAlerts(location)
            val schoolRes = featureConditionsRepo.getSchoolCommuteConditions(location)
            val agriRes = featureConditionsRepo.getAgricultureConditions(location)
            val commuteRes = featureConditionsRepo.getCommuteConditions(location)

            if (currentRes.isSuccess) {
                val weather = currentRes.getOrNull()!!
                val hourly = hourlyRes.getOrDefault(emptyList())
                val daily = dailyRes.getOrDefault(emptyList())
                val alerts = alertsRes.getOrDefault(emptyList())
                val prefs = _uiState.value.userPreferences

                val dashboard = personalizationEngine.buildDashboard(
                    weather = weather,
                    forecasts = daily,
                    alerts = alerts,
                    preferences = prefs
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    currentWeather = weather,
                    hourlyForecast = hourly,
                    dailyForecast = daily,
                    airQuality = aqiRes.getOrNull(),
                    pollenData = pollenRes.getOrNull(),
                    marineConditions = marineRes.getOrNull(),
                    alerts = alerts,
                    schoolCommute = schoolRes.getOrNull(),
                    agriculture = agriRes.getOrNull(),
                    commute = commuteRes.getOrNull(),
                    dashboard = dashboard
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = currentRes.exceptionOrNull()?.message ?: "Failed to retrieve weather data"
                )
            }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        loadWeatherData(_uiState.value.locationName)
    }
}
