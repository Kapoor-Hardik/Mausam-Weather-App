package com.example.mausamweather.presentation.settings

import com.example.mausamweather.domain.model.AppTheme
import com.example.mausamweather.domain.model.TemperatureUnit

data class SettingsUiState(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val theme: AppTheme = AppTheme.SYSTEM,
    val isSaving: Boolean = false
)
