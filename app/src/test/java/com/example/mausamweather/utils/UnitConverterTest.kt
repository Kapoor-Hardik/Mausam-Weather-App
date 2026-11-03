package com.example.mausamweather.utils

import com.example.mausamweather.domain.model.TemperatureUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class UnitConverterTest {

    @Test
    fun celsiusToFahrenheit_convertsAccurately() {
        assertEquals(32.0, UnitConverter.celsiusToFahrenheit(0.0), 0.01)
        assertEquals(212.0, UnitConverter.celsiusToFahrenheit(100.0), 0.01)
        assertEquals(77.0, UnitConverter.celsiusToFahrenheit(25.0), 0.01)
        assertEquals(-40.0, UnitConverter.celsiusToFahrenheit(-40.0), 0.01)
    }

    @Test
    fun formatTemperature_formatsCelsiusCorrectly() {
        val result = UnitConverter.formatTemperature(24.6, TemperatureUnit.CELSIUS)
        assertEquals("25°C", result)
    }

    @Test
    fun formatTemperature_formatsFahrenheitCorrectly() {
        val result = UnitConverter.formatTemperature(25.0, TemperatureUnit.FAHRENHEIT)
        assertEquals("77°F", result)
    }

    @Test
    fun formatWindSpeed_formatsKmPerHour() {
        val result = UnitConverter.formatWindSpeed(15.4)
        assertEquals("15 km/h", result)
    }

    @Test
    fun formatHumidity_formatsPercentage() {
        val result = UnitConverter.formatHumidity(65)
        assertEquals("65%", result)
    }
}
