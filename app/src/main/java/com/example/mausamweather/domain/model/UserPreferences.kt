package com.example.mausamweather.domain.model

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

data class UserPreferences(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val theme: AppTheme = AppTheme.SYSTEM,
    val lastSelectedCityId: Long? = null
)
