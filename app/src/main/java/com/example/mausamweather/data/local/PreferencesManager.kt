package com.example.mausamweather.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mausamweather.domain.model.AppTheme
import com.example.mausamweather.domain.model.TemperatureUnit
import com.example.mausamweather.domain.model.UserPreferences
import com.example.mausamweather.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mausam_preferences")

class PreferencesManager(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    private object PreferencesKeys {
        val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
        val APP_THEME = stringPreferencesKey("app_theme")
        val LAST_SELECTED_CITY_ID = longPreferencesKey("last_selected_city_id")
    }

    override val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val unitStr = preferences[PreferencesKeys.TEMPERATURE_UNIT] ?: TemperatureUnit.CELSIUS.name
            val themeStr = preferences[PreferencesKeys.APP_THEME] ?: AppTheme.SYSTEM.name
            val cityId = preferences[PreferencesKeys.LAST_SELECTED_CITY_ID]

            val unit = try {
                TemperatureUnit.valueOf(unitStr)
            } catch (e: Exception) {
                TemperatureUnit.CELSIUS
            }

            val theme = try {
                AppTheme.valueOf(themeStr)
            } catch (e: Exception) {
                AppTheme.SYSTEM
            }

            UserPreferences(
                temperatureUnit = unit,
                theme = theme,
                lastSelectedCityId = cityId
            )
        }

    override suspend fun updateTemperatureUnit(unit: TemperatureUnit) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TEMPERATURE_UNIT] = unit.name
        }
    }

    override suspend fun updateAppTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = theme.name
        }
    }

    override suspend fun updateLastSelectedCityId(cityId: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SELECTED_CITY_ID] = cityId
        }
    }
}
