package com.example.mausamweather.domain.model

/**
 * Demonstrates Inheritance: Sealed class hierarchy for weather conditions.
 */
sealed class WeatherCondition(
    val code: Int,
    val description: String,
    val emojiIcon: String
) {
    object ClearSky : WeatherCondition(0, "Clear sky", "☀️")
    object MainlyClear : WeatherCondition(1, "Mainly clear", "🌤️")
    object PartlyCloudy : WeatherCondition(2, "Partly cloudy", "⛅")
    object Overcast : WeatherCondition(3, "Overcast", "☁️")
    object Fog : WeatherCondition(45, "Fog", "🌫️")
    object DepositingRimeFog : WeatherCondition(48, "Depositing rime fog", "🌫️")
    object LightDrizzle : WeatherCondition(51, "Light drizzle", "🌧️")
    object ModerateDrizzle : WeatherCondition(53, "Moderate drizzle", "🌧️")
    object DenseDrizzle : WeatherCondition(55, "Dense drizzle", "🌧️")
    object SlightRain : WeatherCondition(61, "Slight rain", "🌧️")
    object ModerateRain : WeatherCondition(63, "Moderate rain", "🌧️")
    object HeavyRain : WeatherCondition(65, "Heavy rain", "🌧️")
    object SlightSnow : WeatherCondition(71, "Slight snow", "❄️")
    object ModerateSnow : WeatherCondition(73, "Moderate snow", "❄️")
    object HeavySnow : WeatherCondition(75, "Heavy snow", "❄️")
    object RainShowers : WeatherCondition(80, "Slight rain showers", "🌦️")
    object ModerateRainShowers : WeatherCondition(81, "Moderate rain showers", "🌦️")
    object ViolentRainShowers : WeatherCondition(82, "Violent rain showers", "⛈️")
    object Thunderstorm : WeatherCondition(95, "Thunderstorm", "⛈️")
    object ThunderstormHail : WeatherCondition(96, "Thunderstorm with slight hail", "⛈️")
    object HeavyThunderstormHail : WeatherCondition(99, "Thunderstorm with heavy hail", "⛈️")
    data class Unknown(val rawCode: Int) : WeatherCondition(rawCode, "Unknown condition ($rawCode)", "🌡️")
}
