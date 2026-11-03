package com.example.mausam.domain.usecase

import com.example.mausam.domain.model.AlertSeverity
import com.example.mausam.domain.model.CurrentWeather
import com.example.mausam.domain.model.DailyForecast
import com.example.mausam.domain.model.PersonalizedDashboard
import com.example.mausam.domain.model.UserInterest
import com.example.mausam.domain.model.UserPreferences
import com.example.mausam.domain.model.WeatherAlert

class PersonalizationEngine(
    private val activityEngine: ActivityRecommendationEngine = ActivityRecommendationEngine(),
    private val packingEngine: PackingRecommendationEngine = PackingRecommendationEngine(),
    private val comfortCalculator: ComfortIndexCalculator = ComfortIndexCalculator()
) {

    /**
     * Determines section visibility and visual ordering on the homepage based on user's selected interests.
     */
    fun prioritizeSections(preferences: UserPreferences): List<UserInterest> {
        val selected = preferences.selectedInterests
        if (selected.isEmpty()) {
            return listOf(UserInterest.HEALTH, UserInterest.FITNESS, UserInterest.TRAVEL)
        }

        // Maintain consistent natural lifestyle grouping, but strictly prioritize what the user has selected
        val naturalOrder = listOf(
            UserInterest.HEALTH,
            UserInterest.FITNESS,
            UserInterest.BEACH,
            UserInterest.TRAVEL,
            UserInterest.FAMILY,
            UserInterest.AGRICULTURE,
            UserInterest.COMMUTING,
            UserInterest.EVENTS
        )

        // Return user selected interests matching natural order
        return naturalOrder.filter { it in selected }
    }

    /**
     * Filters alerts to highlight the ones most relevant to the user's active interests.
     */
    fun filterRelevantAlerts(alerts: List<WeatherAlert>, interests: Set<UserInterest>): List<WeatherAlert> {
        return alerts.filter { alert ->
            if (alert.severity == AlertSeverity.SEVERE) return@filter true // Always show severe

            when (alert.category.lowercase()) {
                "air quality", "uv" -> UserInterest.HEALTH in interests
                "heat", "wind" -> UserInterest.FITNESS in interests || UserInterest.BEACH in interests
                "marine", "tide" -> UserInterest.BEACH in interests
                "frost", "agriculture" -> UserInterest.AGRICULTURE in interests
                "fog", "visibility", "commute" -> UserInterest.COMMUTING in interests || UserInterest.FAMILY in interests
                "rain", "storm" -> true // Rain impacts everyone
                else -> true
            }
        }
    }

    /**
     * Builds comprehensive dashboard intelligence snapshot
     */
    fun buildDashboard(
        weather: CurrentWeather,
        forecasts: List<DailyForecast>,
        alerts: List<WeatherAlert>,
        preferences: UserPreferences
    ): PersonalizedDashboard {
        val prioritized = prioritizeSections(preferences)
        val activity = activityEngine.calculateRunningRecommendation(weather)
        val comfort = comfortCalculator.calculateComfortIndex(weather, forecasts.firstOrNull()?.rainProbability ?: 0)
        val packing = packingEngine.generatePackingTips(weather, forecasts)
        val relevantAlerts = filterRelevantAlerts(alerts, preferences.selectedInterests)

        return PersonalizedDashboard(
            prioritizedSections = prioritized,
            activityRecommendation = activity,
            comfortIndex = comfort,
            travelPackingTips = packing,
            highPriorityAlerts = relevantAlerts
        )
    }
}
