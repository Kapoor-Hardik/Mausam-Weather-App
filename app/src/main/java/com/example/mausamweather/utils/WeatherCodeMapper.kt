package com.example.mausamweather.utils

import com.example.mausamweather.domain.model.WeatherCondition

/**
 * Dedicated mapper for Open-Meteo weather codes.
 * Implements strict mapping with safe unknown fallback.
 */
object WeatherCodeMapper {
    fun mapCodeToCondition(code: Int): WeatherCondition {
        return when (code) {
            0 -> WeatherCondition.ClearSky
            1 -> WeatherCondition.MainlyClear
            2 -> WeatherCondition.PartlyCloudy
            3 -> WeatherCondition.Overcast
            45 -> WeatherCondition.Fog
            48 -> WeatherCondition.DepositingRimeFog
            51 -> WeatherCondition.LightDrizzle
            53 -> WeatherCondition.ModerateDrizzle
            55 -> WeatherCondition.DenseDrizzle
            61 -> WeatherCondition.SlightRain
            63 -> WeatherCondition.ModerateRain
            65 -> WeatherCondition.HeavyRain
            71 -> WeatherCondition.SlightSnow
            73 -> WeatherCondition.ModerateSnow
            75 -> WeatherCondition.HeavySnow
            80 -> WeatherCondition.RainShowers
            81 -> WeatherCondition.ModerateRainShowers
            82 -> WeatherCondition.ViolentRainShowers
            95 -> WeatherCondition.Thunderstorm
            96 -> WeatherCondition.ThunderstormHail
            99 -> WeatherCondition.HeavyThunderstormHail
            else -> WeatherCondition.Unknown(code)
        }
    }
}
