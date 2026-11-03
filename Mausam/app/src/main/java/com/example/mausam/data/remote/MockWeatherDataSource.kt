package com.example.mausam.data.remote

import com.example.mausam.domain.model.AirQuality
import com.example.mausam.domain.model.AlertSeverity
import com.example.mausam.domain.model.CurrentWeather
import com.example.mausam.domain.model.DailyForecast
import com.example.mausam.domain.model.HourlyForecast
import com.example.mausam.domain.model.MarineConditions
import com.example.mausam.domain.model.PollenData
import com.example.mausam.domain.model.WeatherAlert

class MockWeatherDataSource : WeatherDataSource {

    override suspend fun fetchCurrentWeather(location: String): CurrentWeather {
        val cleanLoc = location.ifEmpty { "Faridabad" }
        return CurrentWeather(
            locationName = cleanLoc,
            region = "Haryana",
            country = "India",
            temperatureC = 28.0,
            feelsLikeC = 30.0,
            conditionText = "Partly Cloudy",
            conditionIcon = "🌤️",
            highTempC = 32.0,
            lowTempC = 22.0,
            humidity = 62,
            windKph = 14.0,
            windDirection = "NW",
            pressureMb = 1012.0,
            uvIndex = 7.5,
            visibilityKm = 6.0,
            sunrise = "06:05 AM",
            sunset = "06:45 PM",
            daylightDuration = "12h 40m",
            lastUpdated = "10:30 AM Today",
            isStale = false
        )
    }

    override suspend fun fetchHourlyForecast(location: String): List<HourlyForecast> {
        return listOf(
            HourlyForecast("NOW", 28.0, "Partly Cloudy", "🌤️", 10),
            HourlyForecast("12 PM", 30.0, "Sunny", "☀️", 5),
            HourlyForecast("1 PM", 32.0, "Sunny", "☀️", 0),
            HourlyForecast("2 PM", 31.0, "Hot & Sunny", "☀️", 10),
            HourlyForecast("3 PM", 30.0, "Scattered Clouds", "⛅", 20),
            HourlyForecast("4 PM", 29.0, "Scattered Clouds", "⛅", 25),
            HourlyForecast("5 PM", 27.0, "Light Rain Shower", "🌦️", 65),
            HourlyForecast("6 PM", 25.0, "Light Rain", "🌧️", 70),
            HourlyForecast("7 PM", 24.0, "Cloudy", "☁️", 30),
            HourlyForecast("8 PM", 23.0, "Clear Night", "🌙", 10)
        )
    }

    override suspend fun fetchDailyForecast(location: String): List<DailyForecast> {
        return listOf(
            DailyForecast("Today", "Oct 24", 32.0, 22.0, "Partly Cloudy", "🌤️", 20),
            DailyForecast("Thu", "Oct 25", 30.0, 21.0, "Scattered Showers", "🌦️", 60),
            DailyForecast("Fri", "Oct 26", 28.0, 20.0, "Moderate Rain", "🌧️", 80),
            DailyForecast("Sat", "Oct 27", 29.0, 19.0, "Mostly Clear", "🌤️", 15),
            DailyForecast("Sun", "Oct 28", 31.0, 21.0, "Sunny", "☀️", 0),
            DailyForecast("Mon", "Oct 29", 32.0, 22.0, "Sunny", "☀️", 5),
            DailyForecast("Tue", "Oct 30", 33.0, 23.0, "Hot", "🌤️", 10)
        )
    }

    override suspend fun fetchAirQuality(location: String): AirQuality {
        return AirQuality(
            aqi = 112,
            status = "Unhealthy for Sensitive Groups",
            pm25 = 39.5,
            pm10 = 85.0,
            o3 = 45.2,
            no2 = 28.1,
            healthAdvice = "Sensitive individuals should wear an N95 mask outdoors and limit prolonged physical exertion."
        )
    }

    override suspend fun fetchPollenData(location: String): PollenData {
        return PollenData(
            treePollen = "Moderate",
            grassPollen = "High",
            weedPollen = "Low",
            summary = "Grass pollen counts are elevated today. High risk for outdoor allergies during afternoon."
        )
    }

    override suspend fun fetchMarineConditions(location: String): MarineConditions {
        return MarineConditions(
            waveHeightMeters = 1.4,
            wavePeriodSeconds = 8,
            waterTempC = 26.5,
            nextHighTide = "02:40 PM",
            nextLowTide = "08:35 PM",
            surfCondition = "Good for Surfing",
            suitabilityRating = 4
        )
    }

    override suspend fun fetchWeatherAlerts(location: String): List<WeatherAlert> {
        return listOf(
            WeatherAlert(
                id = "alt-1",
                title = "Rain Shower Alert",
                severity = AlertSeverity.WARNING,
                description = "70% probability of localized rain showers between 5:00 PM and 7:00 PM.",
                effectiveTime = "5:00 PM – 7:00 PM",
                category = "Rain"
            ),
            WeatherAlert(
                id = "alt-2",
                title = "High UV Index Warning",
                severity = AlertSeverity.WARNING,
                description = "UV index peaks at 8.0 between 11:30 AM and 2:30 PM. Wear sunscreen.",
                effectiveTime = "11:30 AM – 2:30 PM",
                category = "UV"
            ),
            WeatherAlert(
                id = "alt-3",
                title = "Air Quality Advisory",
                severity = AlertSeverity.INFO,
                description = "AQI elevated at 112 due to airborne dust and particulate matter.",
                effectiveTime = "All Day",
                category = "Air Quality"
            )
        )
    }
}
