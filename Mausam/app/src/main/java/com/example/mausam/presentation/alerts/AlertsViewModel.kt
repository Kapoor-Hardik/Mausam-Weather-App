package com.example.mausam.presentation.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mausam.domain.model.AlertSeverity
import com.example.mausam.domain.model.WeatherAlert
import com.example.mausam.domain.repository.AlertRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AlertsUiState(
    val alerts: List<WeatherAlert> = emptyList(),
    val selectedSeverityFilter: AlertSeverity? = null,
    val isLoading: Boolean = false
)

class AlertsViewModel(
    private val alertRepo: AlertRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertsUiState())
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    init {
        loadAlerts("Faridabad")
    }

    fun loadAlerts(location: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val res = alertRepo.getActiveAlerts(location)
            _uiState.value = _uiState.value.copy(
                alerts = res.getOrDefault(emptyList()),
                isLoading = false
            )
        }
    }

    fun filterBySeverity(severity: AlertSeverity?) {
        _uiState.value = _uiState.value.copy(selectedSeverityFilter = severity)
    }
}
