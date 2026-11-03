package com.example.mausamweather.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mausamweather.domain.model.AppTheme
import com.example.mausamweather.domain.model.TemperatureUnit
import com.example.mausamweather.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collect { preferences ->
                _uiState.update {
                    it.copy(
                        temperatureUnit = preferences.temperatureUnit,
                        theme = preferences.theme
                    )
                }
            }
        }
    }

    fun onTemperatureUnitChanged(unit: TemperatureUnit) {
        viewModelScope.launch {
            userPreferencesRepository.updateTemperatureUnit(unit)
        }
    }

    fun onThemeChanged(theme: AppTheme) {
        viewModelScope.launch {
            userPreferencesRepository.updateAppTheme(theme)
        }
    }
}
