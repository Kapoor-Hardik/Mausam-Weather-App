package com.example.mausam.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mausam.domain.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val selectedLocation: String? = null,
    val gpsError: String? = null
)

class SearchViewModel(
    private val locationRepo: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.length >= 2) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val results = locationRepo.searchLocations(query).getOrDefault(emptyList())
                _uiState.value = _uiState.value.copy(searchResults = results, isLoading = false)
            }
        } else {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
        }
    }

    fun useCurrentGpsLocation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, gpsError = null)
            val res = locationRepo.getCurrentGpsLocation()
            if (res.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedLocation = res.getOrNull()
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    gpsError = "Unable to acquire current location. Please check location permissions."
                )
            }
        }
    }

    fun selectLocation(location: String) {
        _uiState.value = _uiState.value.copy(selectedLocation = location)
    }
}
