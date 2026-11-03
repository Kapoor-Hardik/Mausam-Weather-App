package com.example.mausam.domain.repository

import com.example.mausam.domain.model.AgricultureConditions
import com.example.mausam.domain.model.AirQuality
import com.example.mausam.domain.model.AppThemeMode
import com.example.mausam.domain.model.CommuteConditions
import com.example.mausam.domain.model.CurrentWeather
import com.example.mausam.domain.model.DailyForecast
import com.example.mausam.domain.model.EventItem
import com.example.mausam.domain.model.HourlyForecast
import com.example.mausam.domain.model.MarineConditions
import com.example.mausam.domain.model.PollenData
import com.example.mausam.domain.model.SavedDestination
import com.example.mausam.domain.model.SchoolCommuteConditions
import com.example.mausam.domain.model.SpeedUnit
import com.example.mausam.domain.model.TemperatureUnit
import com.example.mausam.domain.model.UserInterest
import com.example.mausam.domain.model.UserPreferences
import com.example.mausam.domain.model.WeatherAlert
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(location: String): Result<CurrentWeather>
    suspend fun getHourlyForecast(location: String): Result<List<HourlyForecast>>
    suspend fun getDailyForecast(location: String): Result<List<DailyForecast>>
}

interface AirQualityRepository {
    suspend fun getAirQuality(location: String): Result<AirQuality>
    suspend fun getPollenData(location: String): Result<PollenData>
}

interface MarineRepository {
    suspend fun getMarineConditions(location: String): Result<MarineConditions>
}

interface LocationRepository {
    suspend fun searchLocations(query: String): Result<List<String>>
    suspend fun getCurrentGpsLocation(): Result<String>
}

interface SavedLocationRepository {
    fun getSavedDestinations(): Flow<List<SavedDestination>>
    suspend fun addDestination(cityName: String, country: String)
    suspend fun removeDestination(id: String)
}

interface AlertRepository {
    suspend fun getActiveAlerts(location: String): Result<List<WeatherAlert>>
}

interface EventRepository {
    fun getEvents(): Flow<List<EventItem>>
    suspend fun addEvent(event: EventItem)
    suspend fun removeEvent(id: String)
}

interface PreferencesRepository {
    fun getUserPreferences(): Flow<UserPreferences>
    suspend fun updateInterests(interests: Set<UserInterest>)
    suspend fun updateUnits(tempUnit: TemperatureUnit, speedUnit: SpeedUnit)
    suspend fun updateTheme(theme: AppThemeMode)
    suspend fun updateNotifications(enabled: Boolean, severeOnly: Boolean)
    suspend fun setOnboarded(onboarded: Boolean)
    suspend fun updateCommuteHours(morning: String, afternoon: String)
}

interface FeatureConditionsRepository {
    suspend fun getSchoolCommuteConditions(location: String): Result<SchoolCommuteConditions>
    suspend fun getAgricultureConditions(location: String): Result<AgricultureConditions>
    suspend fun getCommuteConditions(location: String): Result<CommuteConditions>
}
