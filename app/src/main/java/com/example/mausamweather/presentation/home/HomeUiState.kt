package com.example.mausamweather.presentation.home

import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.TemperatureUnit
import com.example.mausamweather.domain.model.Weather

/**
 * Sealed interface representing all possible UI states for the MAUSAM Home Screen.
 */
sealed interface HomeUiState {
    object InitialEmpty : HomeUiState
    object Loading : HomeUiState
    data class Success(
        val weather: Weather,
        val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
        val isRefreshing: Boolean = false
    ) : HomeUiState
    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : HomeUiState
}

data class SearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<City> = emptyList(),
    val recentCities: List<City> = emptyList(),
    val errorMessage: String? = null,
    val isSearchActive: Boolean = false
)
