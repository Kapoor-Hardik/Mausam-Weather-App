package com.example.mausamweather.domain.repository

import com.example.mausamweather.domain.model.AppTheme
import com.example.mausamweather.domain.model.TemperatureUnit
import com.example.mausamweather.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Demonstrates Abstraction: Domain repository contract for managing user preferences.
 */
interface UserPreferencesRepository {
    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun updateTemperatureUnit(unit: TemperatureUnit)
    suspend fun updateAppTheme(theme: AppTheme)
    suspend fun updateLastSelectedCityId(cityId: Long)
}
