package com.example.mausamweather.utils

import com.example.mausamweather.domain.model.TemperatureUnit
import kotlin.math.roundToInt

object UnitConverter {
    fun celsiusToFahrenheit(celsius: Double): Double {
        return (celsius * 9 / 5) + 32
    }

    fun formatTemperature(celsius: Double, unit: TemperatureUnit): String {
        val temp = when (unit) {
            TemperatureUnit.CELSIUS -> celsius
            TemperatureUnit.FAHRENHEIT -> celsiusToFahrenheit(celsius)
        }
        return "${temp.roundToInt()}${unit.symbol}"
    }

    fun formatWindSpeed(kmh: Double): String {
        return "${kmh.roundToInt()} km/h"
    }

    fun formatHumidity(humidityPercent: Int): String {
        return "$humidityPercent%"
    }
}
