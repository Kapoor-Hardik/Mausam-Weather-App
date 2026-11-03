package com.example.mausam.domain.model

enum class UserInterest(val displayName: String, val emoji: String, val description: String) {
    HEALTH("Health", "🌿", "AQI, Pollen, UV index, allergies & asthma management"),
    FITNESS("Outdoor Fitness", "🏃", "Running suitability, sunrise/sunset, wind & heat index"),
    BEACH("Beach & Surfing", "🏄", "Tides, wave heights, water temp & coastal winds"),
    TRAVEL("Travel", "✈️", "Saved destinations, transit alerts & packing suggestions"),
    FAMILY("Family & School", "👨‍👩‍👧", "Morning commute, school runs & rain/storm safety"),
    AGRICULTURE("Agriculture & Garden", "🌱", "Soil moisture, rainfall predictions & frost alerts"),
    COMMUTING("Commuting & Traffic", "🚗", "Road visibility, fog alerts & transit conditions"),
    EVENTS("Event Planning", "🎉", "Extended forecast, comfort index & rain risk")
}

data class CurrentWeather(
    val locationName: String,
    val region: String,
    val country: String,
    val temperatureC: Double,
    val feelsLikeC: Double,
    val conditionText: String,
    val conditionIcon: String,
    val highTempC: Double,
    val lowTempC: Double,
    val humidity: Int,
    val windKph: Double,
    val windDirection: String,
    val pressureMb: Double,
    val uvIndex: Double,
    val visibilityKm: Double,
    val sunrise: String,
    val sunset: String,
    val daylightDuration: String,
    val lastUpdated: String,
    val isStale: Boolean = false
)

data class HourlyForecast(
    val time: String,
    val tempC: Double,
    val conditionText: String,
    val conditionIcon: String,
    val rainProbability: Int
)

data class DailyForecast(
    val dayName: String,
    val date: String,
    val maxTempC: Double,
    val minTempC: Double,
    val conditionText: String,
    val conditionIcon: String,
    val rainProbability: Int
)

data class AirQuality(
    val aqi: Int,
    val status: String,
    val pm25: Double,
    val pm10: Double,
    val o3: Double,
    val no2: Double,
    val healthAdvice: String
)

data class PollenData(
    val treePollen: String, // Low, Moderate, High
    val grassPollen: String,
    val weedPollen: String,
    val summary: String
)

data class MarineConditions(
    val waveHeightMeters: Double,
    val wavePeriodSeconds: Int,
    val waterTempC: Double,
    val nextHighTide: String,
    val nextLowTide: String,
    val surfCondition: String, // Excellent, Good, Fair, Choppy
    val suitabilityRating: Int // 1-5
)

enum class AlertSeverity {
    INFO, WARNING, SEVERE
}

data class WeatherAlert(
    val id: String,
    val title: String,
    val severity: AlertSeverity,
    val description: String,
    val effectiveTime: String,
    val category: String
)

data class SavedDestination(
    val id: String,
    val cityName: String,
    val country: String,
    val temperatureC: Double,
    val condition: String,
    val conditionIcon: String,
    val alertText: String? = null,
    val packingTips: List<String> = emptyList()
)

data class SchoolCommuteConditions(
    val morningTime: String = "7:30 AM",
    val afternoonTime: String = "2:45 PM",
    val morningTempC: Double,
    val morningRainProb: Int,
    val morningVisibilityKm: Double,
    val advisory: String
)

data class AgricultureConditions(
    val soilMoisturePercent: Int,
    val rainfallNext24hMm: Double,
    val frostRisk: String, // None, Low, Moderate, High
    val seasonalAdvice: String,
    val isSimulated: Boolean = true
)

data class CommuteConditions(
    val trafficCondition: String, // Smooth, Moderate, Heavy, Congested
    val visibilityKm: Double,
    val fogRisk: String, // Clear, Mist, Dense Fog
    val advisory: String,
    val isEstimated: Boolean = true
)

data class EventItem(
    val id: String,
    val name: String,
    val date: String,
    val time: String,
    val location: String,
    val expectedTempC: Double,
    val rainProbability: Int,
    val windKph: Double,
    val comfortIndexRating: String, // Ideal, Comfortable, Caution, Poor
    val comfortExplanation: String
)

enum class TemperatureUnit {
    CELSIUS, FAHRENHEIT
}

enum class SpeedUnit {
    KILOMETERS_PER_HOUR, MILES_PER_HOUR
}

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK
}

data class UserPreferences(
    val selectedInterests: Set<UserInterest> = setOf(
        UserInterest.HEALTH,
        UserInterest.FITNESS,
        UserInterest.TRAVEL
    ),
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val speedUnit: SpeedUnit = SpeedUnit.KILOMETERS_PER_HOUR,
    val appTheme: AppThemeMode = AppThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val severeWeatherAlertsOnly: Boolean = false,
    val isOnboarded: Boolean = true,
    val schoolMorningHour: String = "07:30",
    val schoolAfternoonHour: String = "14:45"
)
