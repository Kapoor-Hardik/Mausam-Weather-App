package com.example.mausam.domain.model

data class ActivityRecommendation(
    val activityName: String,
    val ratingStars: Int, // 1 to 5
    val ratingText: String, // "Good conditions", "Ideal", "Poor conditions"
    val bestTimeWindow: String,
    val advice: String,
    val isFavorable: Boolean
)

data class ComfortIndex(
    val score: Int, // 0 - 100
    val rating: String, // Ideal, Comfortable, Caution, Uncomfortable, Dangerous
    val explanation: String,
    val factors: List<String>
)

data class PersonalizedDashboard(
    val prioritizedSections: List<UserInterest>,
    val activityRecommendation: ActivityRecommendation,
    val comfortIndex: ComfortIndex,
    val travelPackingTips: List<String>,
    val highPriorityAlerts: List<WeatherAlert>
)
