package com.example.mausam.domain.usecase

import com.example.mausam.domain.model.CurrentWeather
import com.example.mausam.domain.model.UserInterest
import com.example.mausam.domain.model.UserPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EngineUnitTests {

    private val activityEngine = ActivityRecommendationEngine()
    private val packingEngine = PackingRecommendationEngine()
    private val comfortCalculator = ComfortIndexCalculator()
    private val personalizationEngine = PersonalizationEngine(activityEngine, packingEngine, comfortCalculator)

    private val sampleWeather = CurrentWeather(
        locationName = "Faridabad", region = "Haryana", country = "India",
        temperatureC = 20.0, feelsLikeC = 20.0, conditionText = "Sunny", conditionIcon = "☀️",
        highTempC = 25.0, lowTempC = 15.0, humidity = 50, windKph = 10.0, windDirection = "N",
        pressureMb = 1013.0, uvIndex = 4.0, visibilityKm = 10.0, sunrise = "06:00 AM", sunset = "06:00 PM",
        daylightDuration = "12h", lastUpdated = "Now"
    )

    @Test
    fun testRunningRecommendationForOptimalConditions() {
        val rec = activityEngine.calculateRunningRecommendation(sampleWeather)
        assertEquals(5, rec.ratingStars)
        assertTrue(rec.isFavorable)
    }

    @Test
    fun testComfortIndexIdealTemperature() {
        val comfort = comfortCalculator.calculateComfortIndex(sampleWeather)
        assertTrue(comfort.score >= 80)
        assertEquals("Ideal", comfort.rating)
    }

    @Test
    fun testPackingTipsForRain() {
        val rainyWeather = sampleWeather.copy(conditionText = "Moderate Rain")
        val tips = packingEngine.generatePackingTips(rainyWeather)
        assertTrue(tips.any { it.contains("raincoat", ignoreCase = true) || it.contains("umbrella", ignoreCase = true) })
    }

    @Test
    fun testPersonalizationSectionReordering() {
        val prefs = UserPreferences(
            selectedInterests = setOf(UserInterest.HEALTH, UserInterest.EVENTS)
        )
        val sections = personalizationEngine.prioritizeSections(prefs)
        assertEquals(2, sections.size)
        assertEquals(UserInterest.HEALTH, sections[0])
        assertEquals(UserInterest.EVENTS, sections[1])
        assertFalse(sections.contains(UserInterest.BEACH))
    }
}
