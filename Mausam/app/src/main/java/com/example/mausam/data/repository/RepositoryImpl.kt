package com.example.mausam.data.repository

import com.example.mausam.data.local.MausamLocalStorage
import com.example.mausam.data.local.PreferencesManager
import com.example.mausam.data.remote.WeatherDataSource
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
import com.example.mausam.domain.repository.AirQualityRepository
import com.example.mausam.domain.repository.AlertRepository
import com.example.mausam.domain.repository.EventRepository
import com.example.mausam.domain.repository.FeatureConditionsRepository
import com.example.mausam.domain.repository.LocationRepository
import com.example.mausam.domain.repository.MarineRepository
import com.example.mausam.domain.repository.PreferencesRepository
import com.example.mausam.domain.repository.SavedLocationRepository
import com.example.mausam.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(
    private val dataSource: WeatherDataSource
) : WeatherRepository {
    override suspend fun getCurrentWeather(location: String): Result<CurrentWeather> = runCatching {
        dataSource.fetchCurrentWeather(location)
    }

    override suspend fun getHourlyForecast(location: String): Result<List<HourlyForecast>> = runCatching {
        dataSource.fetchHourlyForecast(location)
    }

    override suspend fun getDailyForecast(location: String): Result<List<DailyForecast>> = runCatching {
        dataSource.fetchDailyForecast(location)
    }
}

class AirQualityRepositoryImpl(
    private val dataSource: WeatherDataSource
) : AirQualityRepository {
    override suspend fun getAirQuality(location: String): Result<AirQuality> = runCatching {
        dataSource.fetchAirQuality(location)
    }

    override suspend fun getPollenData(location: String): Result<PollenData> = runCatching {
        dataSource.fetchPollenData(location)
    }
}

class MarineRepositoryImpl(
    private val dataSource: WeatherDataSource
) : MarineRepository {
    override suspend fun getMarineConditions(location: String): Result<MarineConditions> = runCatching {
        dataSource.fetchMarineConditions(location)
    }
}

class LocationRepositoryImpl : LocationRepository {
    private val cities = listOf(
        "Faridabad, India", "Delhi, India", "Mumbai, India", "Bengaluru, India",
        "London, United Kingdom", "New York, USA", "Tokyo, Japan", "Sydney, Australia",
        "Paris, France", "Berlin, Germany", "Singapore", "Dubai, UAE", "Toronto, Canada"
    )

    override suspend fun searchLocations(query: String): Result<List<String>> = runCatching {
        if (query.isBlank()) emptyList()
        else cities.filter { it.contains(query, ignoreCase = true) }
    }

    override suspend fun getCurrentGpsLocation(): Result<String> = runCatching {
        "Faridabad, India"
    }
}

class SavedLocationRepositoryImpl(
    private val localStorage: MausamLocalStorage
) : SavedLocationRepository {
    override fun getSavedDestinations(): Flow<List<SavedDestination>> = localStorage.getSavedDestinations()

    override suspend fun addDestination(cityName: String, country: String) {
        localStorage.addDestination(cityName, country)
    }

    override suspend fun removeDestination(id: String) {
        localStorage.removeDestination(id)
    }
}

class AlertRepositoryImpl(
    private val dataSource: WeatherDataSource
) : AlertRepository {
    override suspend fun getActiveAlerts(location: String): Result<List<WeatherAlert>> = runCatching {
        dataSource.fetchWeatherAlerts(location)
    }
}

class EventRepositoryImpl(
    private val localStorage: MausamLocalStorage
) : EventRepository {
    override fun getEvents(): Flow<List<EventItem>> = localStorage.getEvents()

    override suspend fun addEvent(event: EventItem) {
        localStorage.addEvent(event)
    }

    override suspend fun removeEvent(id: String) {
        localStorage.removeEvent(id)
    }
}

class PreferencesRepositoryImpl(
    private val preferencesManager: PreferencesManager
) : PreferencesRepository {
    override fun getUserPreferences(): Flow<UserPreferences> = preferencesManager.userPreferencesFlow

    override suspend fun updateInterests(interests: Set<UserInterest>) {
        preferencesManager.updateInterests(interests)
    }

    override suspend fun updateUnits(tempUnit: TemperatureUnit, speedUnit: SpeedUnit) {
        preferencesManager.updateUnits(tempUnit, speedUnit)
    }

    override suspend fun updateTheme(theme: AppThemeMode) {
        preferencesManager.updateTheme(theme)
    }

    override suspend fun updateNotifications(enabled: Boolean, severeOnly: Boolean) {
        preferencesManager.updateNotifications(enabled, severeOnly)
    }

    override suspend fun setOnboarded(onboarded: Boolean) {
        preferencesManager.setOnboarded(onboarded)
    }

    override suspend fun updateCommuteHours(morning: String, afternoon: String) {
        preferencesManager.updateCommuteHours(morning, afternoon)
    }
}

class FeatureConditionsRepositoryImpl : FeatureConditionsRepository {
    override suspend fun getSchoolCommuteConditions(location: String): Result<SchoolCommuteConditions> = runCatching {
        SchoolCommuteConditions(
            morningTime = "7:30 AM",
            afternoonTime = "2:45 PM",
            morningTempC = 22.0,
            morningRainProb = 70,
            morningVisibilityKm = 4.5,
            advisory = "☔ 70% rain probability during morning school commute. Pack umbrellas and rain ponchos for kids."
        )
    }

    override suspend fun getAgricultureConditions(location: String): Result<AgricultureConditions> = runCatching {
        AgricultureConditions(
            soilMoisturePercent = 48,
            rainfallNext24hMm = 12.5,
            frostRisk = "Low",
            seasonalAdvice = "Good soil moisture levels. Moderate rainfall expected in next 24h; hold off synthetic fertilizer application until weather clears.",
            isSimulated = true
        )
    }

    override suspend fun getCommuteConditions(location: String): Result<CommuteConditions> = runCatching {
        CommuteConditions(
            trafficCondition = "Moderate Traffic",
            visibilityKm = 3.2,
            fogRisk = "Mist in low areas",
            advisory = "⚠️ Reduced road visibility of 3.2 km due to morning mist. Maintain safe braking distance.",
            isEstimated = true
        )
    }
}
