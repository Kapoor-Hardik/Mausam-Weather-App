package com.example.mausam.domain.usecase

import com.example.mausam.domain.model.ActivityRecommendation
import com.example.mausam.domain.model.CurrentWeather

class ActivityRecommendationEngine {

    fun calculateRunningRecommendation(weather: CurrentWeather): ActivityRecommendation {
        var score = 5
        val reasons = mutableListOf<String>()

        // Temperature considerations
        when {
            weather.temperatureC > 33.0 -> {
                score -= 3
                reasons.add("High heat conditions")
            }
            weather.temperatureC > 28.0 -> {
                score -= 1
                reasons.add("Warm temperature")
            }
            weather.temperatureC < 4.0 -> {
                score -= 2
                reasons.add("Chilly temperature")
            }
            weather.temperatureC in 12.0..22.0 -> {
                reasons.add("Optimal running temperature")
            }
        }

        // Humidity
        if (weather.humidity > 80 && weather.temperatureC > 25.0) {
            score -= 1
            reasons.add("High humidity may cause overheating")
        }

        // Wind
        if (weather.windKph > 35.0) {
            score -= 2
            reasons.add("Strong wind gusts")
        } else if (weather.windKph > 22.0) {
            score -= 1
            reasons.add("Breezy conditions")
        }

        // UV Index
        if (weather.uvIndex >= 8.0) {
            score -= 1
            reasons.add("Very high UV - avoid midday run")
        }

        // Condition text
        val cond = weather.conditionText.lowercase()
        if (cond.contains("rain") || cond.contains("storm") || cond.contains("shower")) {
            score -= 2
            reasons.add("Wet ground and precipitation")
        }

        val clampedScore = score.coerceIn(1, 5)
        val ratingText = when (clampedScore) {
            5 -> "Ideal conditions"
            4 -> "Good conditions"
            3 -> "Moderate conditions"
            2 -> "Challenging conditions"
            else -> "Poor conditions"
        }

        val bestWindow = if (weather.temperatureC > 26.0) {
            "6:00 AM – 8:00 AM or after sunset"
        } else {
            "7:00 AM – 10:30 AM or 4:30 PM – 6:30 PM"
        }

        val advice = reasons.joinToString(" • ").ifEmpty { "Conditions are pleasant for outdoor fitness." }

        return ActivityRecommendation(
            activityName = "Running & Outdoor Fitness",
            ratingStars = clampedScore,
            ratingText = ratingText,
            bestTimeWindow = bestWindow,
            advice = advice,
            isFavorable = clampedScore >= 3
        )
    }

    fun calculateCyclingRecommendation(weather: CurrentWeather): ActivityRecommendation {
        var score = 5
        val reasons = mutableListOf<String>()

        if (weather.windKph > 30.0) {
            score -= 2
            reasons.add("Strong crosswinds")
        }
        if (weather.visibilityKm < 4.0) {
            score -= 2
            reasons.add("Reduced road visibility")
        }
        if (weather.conditionText.contains("Rain", ignoreCase = true)) {
            score -= 3
            reasons.add("Slick roads")
        }

        val clampedScore = score.coerceIn(1, 5)
        return ActivityRecommendation(
            activityName = "Cycling",
            ratingStars = clampedScore,
            ratingText = if (clampedScore >= 4) "Great ride conditions" else "Caution on roads",
            bestTimeWindow = "Morning daylight hours",
            advice = reasons.joinToString(" • ").ifEmpty { "Smooth roads and clear breeze expected." },
            isFavorable = clampedScore >= 3
        )
    }
}
