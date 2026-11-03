package com.example.mausam.presentation.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mausam.domain.model.SavedDestination
import com.example.mausam.domain.repository.SavedLocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class SavedLocationsUiState(
    val destinations: List<SavedDestination> = emptyList(),
    val showAddDialog: Boolean = false,
    val newCityInput: String = "",
    val newCountryInput: String = ""
)

class SavedLocationsViewModel(
    private val savedLocationRepo: SavedLocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedLocationsUiState())
    val uiState: StateFlow<SavedLocationsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            savedLocationRepo.getSavedDestinations().collectLatest { dests ->
                _uiState.value = _uiState.value.copy(destinations = dests)
            }
        }
    }

    fun toggleAddDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(
            showAddDialog = show,
            newCityInput = "",
            newCountryInput = ""
        )
    }

    fun onCityInputChanged(city: String) {
        _uiState.value = _uiState.value.copy(newCityInput = city)
    }

    fun onCountryInputChanged(country: String) {
        _uiState.value = _uiState.value.copy(newCountryInput = country)
    }

    fun addDestination() {
        val city = _uiState.value.newCityInput.trim()
        val country = _uiState.value.newCountryInput.trim()
        if (city.isNotEmpty()) {
            viewModelScope.launch {
                savedLocationRepo.addDestination(city, country)
                toggleAddDialog(false)
            }
        }
    }

    fun removeDestination(id: String) {
        viewModelScope.launch {
            savedLocationRepo.removeDestination(id)
        }
    }
}
