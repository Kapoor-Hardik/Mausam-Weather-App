package com.example.mausam.presentation.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mausam.domain.model.AppThemeMode
import com.example.mausam.domain.model.SpeedUnit
import com.example.mausam.domain.model.TemperatureUnit
import com.example.mausam.domain.model.UserInterest
import com.example.mausam.domain.model.UserPreferences
import com.example.mausam.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PreferencesViewModel(
    private val preferencesRepo: PreferencesRepository
) : ViewModel() {

    private val _userPreferences = MutableStateFlow(UserPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepo.getUserPreferences().collectLatest { prefs ->
                _userPreferences.value = prefs
            }
        }
    }

    fun toggleInterest(interest: UserInterest) {
        val current = _userPreferences.value.selectedInterests
        val updated = if (interest in current) {
            if (current.size > 1) current - interest else current
        } else {
            current + interest
        }
        viewModelScope.launch {
            preferencesRepo.updateInterests(updated)
        }
    }

    fun updateTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch {
            preferencesRepo.updateUnits(unit, _userPreferences.value.speedUnit)
        }
    }

    fun updateSpeedUnit(unit: SpeedUnit) {
        viewModelScope.launch {
            preferencesRepo.updateUnits(_userPreferences.value.temperatureUnit, unit)
        }
    }

    fun updateTheme(theme: AppThemeMode) {
        viewModelScope.launch {
            preferencesRepo.updateTheme(theme)
        }
    }

    fun updateNotifications(enabled: Boolean, severeOnly: Boolean) {
        viewModelScope.launch {
            preferencesRepo.updateNotifications(enabled, severeOnly)
        }
    }

    fun updateCommuteHours(morning: String, afternoon: String) {
        viewModelScope.launch {
            preferencesRepo.updateCommuteHours(morning, afternoon)
        }
    }
}
