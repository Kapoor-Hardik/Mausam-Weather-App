package com.example.mausamweather.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.TemperatureUnit
import com.example.mausamweather.domain.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.InitialEmpty)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchState = MutableStateFlow(SearchUiState())
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

    private val _temperatureUnit = MutableStateFlow(TemperatureUnit.CELSIUS)
    val temperatureUnit: StateFlow<TemperatureUnit> = _temperatureUnit.asStateFlow()

    private var currentSelectedCity: City? = null
    private var searchJob: Job? = null
    private var weatherJob: Job? = null

    init {
        loadInitialDefaultCity()
    }

    private fun loadInitialDefaultCity() {
        val defaultCity = City(
            id = 1,
            name = "London",
            latitude = 51.5074,
            longitude = -0.1278,
            country = "United Kingdom",
            admin1 = "England"
        )
        fetchWeatherForCity(defaultCity)
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchState.update { it.copy(query = newQuery, errorMessage = null) }
        searchJob?.cancel()

        val trimmed = newQuery.trim()
        if (trimmed.isEmpty()) {
            _searchState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            _searchState.update { it.copy(isSearching = true) }
            val result = weatherRepository.searchCity(trimmed)
            if (result.isSuccess) {
                val cities = result.getOrDefault(emptyList())
                _searchState.update {
                    it.copy(
                        searchResults = cities,
                        isSearching = false,
                        errorMessage = if (cities.isEmpty()) "No cities found for '$trimmed'." else null
                    )
                }
            } else {
                _searchState.update {
                    it.copy(
                        searchResults = emptyList(),
                        isSearching = false,
                        errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to search city."
                    )
                }
            }
        }
    }

    fun onCitySelected(city: City) {
        _searchState.update {
            it.copy(
                query = "",
                searchResults = emptyList(),
                isSearchActive = false,
                errorMessage = null
            )
        }
        fetchWeatherForCity(city)
    }

    fun fetchWeatherForCity(city: City) {
        currentSelectedCity = city
        weatherJob?.cancel()
        weatherJob = viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val result = weatherRepository.getWeather(city)

            if (result.isSuccess) {
                val weather = result.getOrThrow()
                _uiState.value = HomeUiState.Success(
                    weather = weather,
                    temperatureUnit = _temperatureUnit.value
                )
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage
                    ?: "Unable to fetch weather. Please check your internet connection."
                _uiState.value = HomeUiState.Error(message = errorMsg)
            }
        }
    }

    fun onRefresh() {
        val city = currentSelectedCity ?: return
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.value = currentState.copy(isRefreshing = true)
        }
        viewModelScope.launch {
            val result = weatherRepository.getWeather(city)
            if (result.isSuccess) {
                val weather = result.getOrThrow()
                _uiState.value = HomeUiState.Success(
                    weather = weather,
                    temperatureUnit = _temperatureUnit.value,
                    isRefreshing = false
                )
            } else {
                if (currentState is HomeUiState.Success) {
                    _uiState.value = currentState.copy(isRefreshing = false)
                } else {
                    val errorMsg = result.exceptionOrNull()?.localizedMessage
                        ?: "Failed to refresh weather data."
                    _uiState.value = HomeUiState.Error(message = errorMsg)
                }
            }
        }
    }

    fun onToggleTemperatureUnit() {
        val newUnit = if (_temperatureUnit.value == TemperatureUnit.CELSIUS) {
            TemperatureUnit.FAHRENHEIT
        } else {
            TemperatureUnit.CELSIUS
        }
        _temperatureUnit.value = newUnit

        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.value = currentState.copy(temperatureUnit = newUnit)
        }
    }

    fun onSearchActiveChanged(active: Boolean) {
        _searchState.update { it.copy(isSearchActive = active) }
    }
}
