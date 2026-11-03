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
import com.example.mausamweather.domain.model.City
import com.example.mausamweather.domain.model.TemperatureUnit
import com.example.mausamweather.domain.model.UserPreferences
import com.example.mausamweather.domain.repository.UserPreferencesRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mausam_preferences")

class PreferencesManager(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    private val gson by lazy { Gson() }

    private object PreferencesKeys {
        val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
        val APP_THEME = stringPreferencesKey("app_theme")
        val LAST_SELECTED_CITY_ID = longPreferencesKey("last_selected_city_id")
        val RECENT_CITIES_JSON = stringPreferencesKey("recent_cities_json")
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
            } catch (_: Exception) {
                TemperatureUnit.CELSIUS
            }

            val theme = try {
                AppTheme.valueOf(themeStr)
            } catch (_: Exception) {
                AppTheme.SYSTEM
            }

            UserPreferences(
                temperatureUnit = unit,
                theme = theme,
                lastSelectedCityId = cityId
            )
        }

    override val recentSearchesFlow: Flow<List<City>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val json = preferences[PreferencesKeys.RECENT_CITIES_JSON]
            if (json.isNullOrEmpty()) {
                emptyList()
            } else {
                try {
                    val type = object : TypeToken<List<City>>() {}.type
                    gson.fromJson<List<City>>(json, type) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }
            }
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

    override suspend fun addRecentCity(city: City) {
        dataStore.edit { preferences ->
            val json = preferences[PreferencesKeys.RECENT_CITIES_JSON]
            val existingList: MutableList<City> = if (json.isNullOrEmpty()) {
                mutableListOf()
            } else {
                try {
                    val type = object : TypeToken<List<City>>() {}.type
                    gson.fromJson<List<City>>(json, type)?.toMutableList() ?: mutableListOf()
                } catch (_: Exception) {
                    mutableListOf()
                }
            }

            // Remove existing duplicate by ID or Name + Country match
            existingList.removeAll { 
                it.id == city.id || (it.name.equals(city.name, ignoreCase = true) && it.country.equals(city.country, ignoreCase = true))
            }

            // Prepend new city at index 0 (most recent first)
            existingList.add(0, city)

            // Keep max 5 items
            val updatedList = existingList.take(5)

            preferences[PreferencesKeys.RECENT_CITIES_JSON] = gson.toJson(updatedList)
        }
    }

    override suspend fun clearRecentCities() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.RECENT_CITIES_JSON)
        }
    }
}
