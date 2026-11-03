package com.example.mausam.presentation.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mausam.domain.model.CurrentWeather
import com.example.mausam.domain.model.EventItem
import com.example.mausam.domain.repository.EventRepository
import com.example.mausam.domain.repository.WeatherRepository
import com.example.mausam.domain.usecase.ComfortIndexCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class EventsUiState(
    val events: List<EventItem> = emptyList(),
    val showAddEventDialog: Boolean = false,
    val newName: String = "",
    val newDate: String = "",
    val newTime: String = "",
    val newLocation: String = ""
)

class EventsViewModel(
    private val eventRepo: EventRepository,
    private val weatherRepo: WeatherRepository,
    private val comfortCalculator: ComfortIndexCalculator = ComfortIndexCalculator()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            eventRepo.getEvents().collectLatest { list ->
                _uiState.value = _uiState.value.copy(events = list)
            }
        }
    }

    fun toggleAddDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(
            showAddEventDialog = show,
            newName = "",
            newDate = "",
            newTime = "",
            newLocation = ""
        )
    }

    fun onNameChanged(v: String) { _uiState.value = _uiState.value.copy(newName = v) }
    fun onDateChanged(v: String) { _uiState.value = _uiState.value.copy(newDate = v) }
    fun onTimeChanged(v: String) { _uiState.value = _uiState.value.copy(newTime = v) }
    fun onLocationChanged(v: String) { _uiState.value = _uiState.value.copy(newLocation = v) }

    fun addEvent() {
        val name = _uiState.value.newName.ifEmpty { "Outdoor Event" }
        val date = _uiState.value.newDate.ifEmpty { "Saturday, Nov 4" }
        val time = _uiState.value.newTime.ifEmpty { "04:00 PM" }
        val location = _uiState.value.newLocation.ifEmpty { "City Park" }

        viewModelScope.launch {
            val weather = weatherRepo.getCurrentWeather(location).getOrDefault(
                CurrentWeather(
                    locationName = location, region = "", country = "",
                    temperatureC = 25.0, feelsLikeC = 26.0, conditionText = "Clear", conditionIcon = "☀️",
                    highTempC = 28.0, lowTempC = 20.0, humidity = 50, windKph = 10.0, windDirection = "N",
                    pressureMb = 1013.0, uvIndex = 5.0, visibilityKm = 10.0, sunrise = "06:00 AM", sunset = "06:00 PM",
                    daylightDuration = "12h", lastUpdated = "Now"
                )
            )

            val comfort = comfortCalculator.calculateComfortIndex(weather, 15)

            val event = EventItem(
                id = "",
                name = name,
                date = date,
                time = time,
                location = location,
                expectedTempC = weather.temperatureC,
                rainProbability = 15,
                windKph = weather.windKph,
                comfortIndexRating = comfort.rating,
                comfortExplanation = comfort.explanation
            )

            eventRepo.addEvent(event)
            toggleAddDialog(false)
        }
    }

    fun removeEvent(id: String) {
        viewModelScope.launch {
            eventRepo.removeEvent(id)
        }
    }
}
