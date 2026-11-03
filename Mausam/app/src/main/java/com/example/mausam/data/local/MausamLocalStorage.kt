package com.example.mausam.data.local

import com.example.mausam.domain.model.EventItem
import com.example.mausam.domain.model.SavedDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class MausamLocalStorage {

    private val _savedDestinations = MutableStateFlow<List<SavedDestination>>(
        listOf(
            SavedDestination(
                id = "dest-1",
                cityName = "London",
                country = "United Kingdom",
                temperatureC = 18.0,
                condition = "Light Rain expected tomorrow",
                conditionIcon = "🌧️",
                alertText = "Rain expected during morning commute",
                packingTips = listOf("☔ Carry a raincoat or compact umbrella", "🧥 Light layer jacket")
            ),
            SavedDestination(
                id = "dest-2",
                cityName = "Tokyo",
                country = "Japan",
                temperatureC = 22.0,
                condition = "Clear & Pleasant",
                conditionIcon = "☀️",
                alertText = null,
                packingTips = listOf("🕶️ Sunglasses & sunscreen", "👟 Comfortable walking shoes")
            ),
            SavedDestination(
                id = "dest-3",
                cityName = "Sydney",
                country = "Australia",
                temperatureC = 26.0,
                condition = "Sunny Coastal Breeze",
                conditionIcon = "🌤️",
                alertText = "High UV Index Warning",
                packingTips = listOf("🏖️ Swimwear & beach towel", "🕶️ Broad spectrum sunscreen")
            )
        )
    )

    private val _events = MutableStateFlow<List<EventItem>>(
        listOf(
            EventItem(
                id = "evt-1",
                name = "Garden Party & Barbecue",
                date = "Saturday, Oct 28",
                time = "05:00 PM",
                location = "Community Park",
                expectedTempC = 25.0,
                rainProbability = 10,
                windKph = 8.0,
                comfortIndexRating = "Ideal",
                comfortExplanation = "Superb outdoor weather. Mild temperature, low wind, and low rain probability."
            ),
            EventItem(
                id = "evt-2",
                name = "School Sports Day",
                date = "Tuesday, Oct 31",
                time = "09:00 AM",
                location = "St. Jude Grounds",
                expectedTempC = 29.0,
                rainProbability = 20,
                windKph = 12.0,
                comfortIndexRating = "Comfortable",
                comfortExplanation = "Good weather. Ensure water stations and shaded seating for kids."
            )
        )
    )

    fun getSavedDestinations(): Flow<List<SavedDestination>> = _savedDestinations.asStateFlow()

    fun addDestination(cityName: String, country: String) {
        val newDest = SavedDestination(
            id = UUID.randomUUID().toString(),
            cityName = cityName,
            country = country.ifEmpty { "Location" },
            temperatureC = 24.0,
            condition = "Partly Cloudy",
            conditionIcon = "🌤️",
            alertText = null,
            packingTips = listOf("🎒 Standard travel essentials")
        )
        _savedDestinations.value = _savedDestinations.value + newDest
    }

    fun removeDestination(id: String) {
        _savedDestinations.value = _savedDestinations.value.filterNot { it.id == id }
    }

    fun getEvents(): Flow<List<EventItem>> = _events.asStateFlow()

    fun addEvent(event: EventItem) {
        _events.value = _events.value + event.copy(id = UUID.randomUUID().toString())
    }

    fun removeEvent(id: String) {
        _events.value = _events.value.filterNot { it.id == id }
    }
}
