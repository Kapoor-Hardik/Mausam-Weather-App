package com.example.mausamweather.domain.repository

import com.example.mausamweather.domain.model.AppTheme
import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.TemperatureUnit
import com.example.mausamweather.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Demonstrates Abstraction: Domain repository contract for managing user preferences and search history.
 */
interface UserPreferencesRepository {
    val userPreferencesFlow: Flow<UserPreferences>
    val recentSearchesFlow: Flow<List<City>>

    suspend fun updateTemperatureUnit(unit: TemperatureUnit)
    suspend fun updateAppTheme(theme: AppTheme)
    suspend fun updateLastSelectedCityId(cityId: Long)
    suspend fun addRecentCity(city: City)
    suspend fun clearRecentCities()
}
