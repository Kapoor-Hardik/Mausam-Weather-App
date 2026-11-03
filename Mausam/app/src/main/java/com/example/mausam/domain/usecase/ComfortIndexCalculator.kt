package com.example.mausam.domain.usecase

import com.example.mausam.domain.model.ComfortIndex
import com.example.mausam.domain.model.CurrentWeather
import kotlin.math.abs

class ComfortIndexCalculator {

    fun calculateComfortIndex(weather: CurrentWeather, rainProb: Int = 0): ComfortIndex {
        var score = 100
        val factors = mutableListOf<String>()

        // 1. Temperature deviation from ideal comfort (around 22°C)
        val tempDiff = abs(weather.temperatureC - 22.0)
        when {
            tempDiff > 15 -> {
                score -= 35
                factors.add(if (weather.temperatureC > 22) "Extreme Heat (${weather.temperatureC.toInt()}°C)" else "Extreme Cold (${weather.temperatureC.toInt()}°C)")
            }
            tempDiff > 8 -> {
                score -= 20
                factors.add(if (weather.temperatureC > 22) "Warm weather (${weather.temperatureC.toInt()}°C)" else "Cool weather (${weather.temperatureC.toInt()}°C)")
            }
            tempDiff > 4 -> {
                score -= 8
                factors.add("Slight temperature variance")
            }
            else -> {
                factors.add("Pleasant temperature near 22°C")
            }
        }

        // 2. Humidity impact (ideal 40-60%)
        when {
            weather.humidity > 80 -> {
                score -= 20
                factors.add("Oppressive humidity (${weather.humidity}%)")
            }
            weather.humidity > 65 -> {
                score -= 10
                factors.add("Noticeable humidity (${weather.humidity}%)")
            }
            weather.humidity < 25 -> {
                score -= 10
                factors.add("Very dry air (${weather.humidity}%)")
            }
        }

        // 3. Wind impact
        when {
            weather.windKph > 35 -> {
                score -= 20
                factors.add("Gusty winds (${weather.windKph.toInt()} km/h)")
            }
            weather.windKph > 20 -> {
                score -= 8
                factors.add("Breezy conditions (${weather.windKph.toInt()} km/h)")
            }
        }

        // 4. Rain risk
        if (rainProb >= 60 || weather.conditionText.contains("rain", ignoreCase = true)) {
            score -= 25
            factors.add("High chance of precipitation ($rainProb%)")
        } else if (rainProb >= 30) {
            score -= 12
            factors.add("Possible showers ($rainProb%)")
        }

        val clampedScore = score.coerceIn(0, 100)
        val (rating, explanation) = when {
            clampedScore >= 80 -> Pair("Ideal", "Superb outdoor comfort. Conditions are pleasant with optimal temperature.")
            clampedScore >= 65 -> Pair("Comfortable", "Pleasant weather with minor deviations in temperature or breeze.")
            clampedScore >= 45 -> Pair("Moderate", "Acceptable for outdoor events, but consider shaded areas or light outerwear.")
            clampedScore >= 25 -> Pair("Caution", "Unfavorable factors like heat, humidity, or rain risk may impact comfort.")
            else -> Pair("Challenging", "Harsh outdoor conditions. Indoor alternative recommended for events.")
        }

        return ComfortIndex(
            score = clampedScore,
            rating = rating,
            explanation = explanation,
            factors = factors
        )
    }
}
