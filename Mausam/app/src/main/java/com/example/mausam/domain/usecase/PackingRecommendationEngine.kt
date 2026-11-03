package com.example.mausam.domain.usecase

import com.example.mausam.domain.model.CurrentWeather
import com.example.mausam.domain.model.DailyForecast

class PackingRecommendationEngine {

    fun generatePackingTips(currentWeather: CurrentWeather, forecasts: List<DailyForecast> = emptyList()): List<String> {
        val tips = mutableListOf<String>()

        val willRain = currentWeather.conditionText.contains("rain", ignoreCase = true) ||
                forecasts.any { it.rainProbability >= 40 || it.conditionText.contains("rain", ignoreCase = true) }

        val hasCold = currentWeather.lowTempC <= 14.0 || forecasts.any { it.minTempC <= 14.0 }
        val hasFreezing = currentWeather.lowTempC <= 3.0 || forecasts.any { it.minTempC <= 3.0 }
        val hasHeat = currentWeather.highTempC >= 28.0 || forecasts.any { it.maxTempC >= 28.0 }
        val hasHighUV = currentWeather.uvIndex >= 6.0

        if (willRain) {
            tips.add("☔ Waterproof jacket or umbrella (Rain forecast)")
            tips.add("👞 Water-resistant footwear")
        }

        if (hasFreezing) {
            tips.add("🧤 Thermal layers, gloves & beanie (Near-freezing lows)")
        } else if (hasCold) {
            tips.add("🧥 Warm hoodie or light jacket for evenings")
        }

        if (hasHeat) {
            tips.add("👕 Breathable cotton or linen clothing")
            tips.add("💧 Refillable water bottle for hydration")
        }

        if (hasHighUV) {
            tips.add("🕶️ UV sunglasses & broad-spectrum sunscreen (SPF 50+)")
            tips.add("🧢 Sun hat / cap for midday sun protection")
        }

        if (tips.isEmpty()) {
            tips.add("🎒 Standard comfortable travel attire")
            tips.add("🧥 Light outer layer for variable conditions")
        }

        return tips.distinct()
    }
}
