package com.example.mausam.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mausam.domain.model.AppThemeMode
import com.example.mausam.domain.model.SpeedUnit
import com.example.mausam.domain.model.TemperatureUnit
import com.example.mausam.domain.model.UserInterest
import com.example.mausam.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "mausam_user_prefs")

class PreferencesManager(private val context: Context) {

    private object Keys {
        val SELECTED_INTERESTS = stringSetPreferencesKey("selected_interests")
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
        val SPEED_UNIT = stringPreferencesKey("speed_unit")
        val APP_THEME = stringPreferencesKey("app_theme")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val SEVERE_ONLY = booleanPreferencesKey("severe_only")
        val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
        val SCHOOL_MORNING = stringPreferencesKey("school_morning")
        val SCHOOL_AFTERNOON = stringPreferencesKey("school_afternoon")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        val interestsSet = prefs[Keys.SELECTED_INTERESTS] ?: setOf(
            UserInterest.HEALTH.name,
            UserInterest.FITNESS.name,
            UserInterest.TRAVEL.name
        )

        val interests = interestsSet.mapNotNull { name ->
            try { UserInterest.valueOf(name) } catch (_: Exception) { null }
        }.toSet()

        val tempUnit = try {
            TemperatureUnit.valueOf(prefs[Keys.TEMP_UNIT] ?: TemperatureUnit.CELSIUS.name)
        } catch (_: Exception) { TemperatureUnit.CELSIUS }

        val speedUnit = try {
            SpeedUnit.valueOf(prefs[Keys.SPEED_UNIT] ?: SpeedUnit.KILOMETERS_PER_HOUR.name)
        } catch (_: Exception) { SpeedUnit.KILOMETERS_PER_HOUR }

        val appTheme = try {
            AppThemeMode.valueOf(prefs[Keys.APP_THEME] ?: AppThemeMode.SYSTEM.name)
        } catch (_: Exception) { AppThemeMode.SYSTEM }

        UserPreferences(
            selectedInterests = interests,
            temperatureUnit = tempUnit,
            speedUnit = speedUnit,
            appTheme = appTheme,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
            severeWeatherAlertsOnly = prefs[Keys.SEVERE_ONLY] ?: false,
            isOnboarded = prefs[Keys.IS_ONBOARDED] ?: false,
            schoolMorningHour = prefs[Keys.SCHOOL_MORNING] ?: "07:30",
            schoolAfternoonHour = prefs[Keys.SCHOOL_AFTERNOON] ?: "14:45"
        )
    }

    suspend fun updateInterests(interests: Set<UserInterest>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SELECTED_INTERESTS] = interests.map { it.name }.toSet()
        }
    }

    suspend fun updateUnits(tempUnit: TemperatureUnit, speedUnit: SpeedUnit) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TEMP_UNIT] = tempUnit.name
            prefs[Keys.SPEED_UNIT] = speedUnit.name
        }
    }

    suspend fun updateTheme(theme: AppThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.APP_THEME] = theme.name
        }
    }

    suspend fun updateNotifications(enabled: Boolean, severeOnly: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.NOTIFICATIONS_ENABLED] = enabled
            prefs[Keys.SEVERE_ONLY] = severeOnly
        }
    }

    suspend fun setOnboarded(onboarded: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_ONBOARDED] = onboarded
        }
    }

    suspend fun updateCommuteHours(morning: String, afternoon: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SCHOOL_MORNING] = morning
            prefs[Keys.SCHOOL_AFTERNOON] = afternoon
        }
    }
}
