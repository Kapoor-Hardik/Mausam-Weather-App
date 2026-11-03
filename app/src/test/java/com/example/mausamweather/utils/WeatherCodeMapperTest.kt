package com.example.mausamweather.utils

import com.example.mausamweather.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherCodeMapperTest {

    @Test
    fun mapCodeToCondition_returnsCorrectConditionForStandardCodes() {
        assertEquals(WeatherCondition.ClearSky, WeatherCodeMapper.mapCodeToCondition(0))
        assertEquals(WeatherCondition.MainlyClear, WeatherCodeMapper.mapCodeToCondition(1))
        assertEquals(WeatherCondition.PartlyCloudy, WeatherCodeMapper.mapCodeToCondition(2))
        assertEquals(WeatherCondition.Overcast, WeatherCodeMapper.mapCodeToCondition(3))
        assertEquals(WeatherCondition.Fog, WeatherCodeMapper.mapCodeToCondition(45))
        assertEquals(WeatherCondition.DepositingRimeFog, WeatherCodeMapper.mapCodeToCondition(48))
        assertEquals(WeatherCondition.LightDrizzle, WeatherCodeMapper.mapCodeToCondition(51))
        assertEquals(WeatherCondition.SlightRain, WeatherCodeMapper.mapCodeToCondition(61))
        assertEquals(WeatherCondition.SlightSnow, WeatherCodeMapper.mapCodeToCondition(71))
        assertEquals(WeatherCondition.RainShowers, WeatherCodeMapper.mapCodeToCondition(80))
        assertEquals(WeatherCondition.Thunderstorm, WeatherCodeMapper.mapCodeToCondition(95))
    }

    @Test
    fun mapCodeToCondition_returnsUnknownForUnrecognizedCodes() {
        val condition = WeatherCodeMapper.mapCodeToCondition(999)
        assertTrue(condition is WeatherCondition.Unknown)
        assertEquals(999, (condition as WeatherCondition.Unknown).rawCode)
    }
}
