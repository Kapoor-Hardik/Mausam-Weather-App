package com.example.mausam.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mausam.domain.model.UserInterest
import com.example.mausam.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val selectedInterests: Set<UserInterest> = setOf(UserInterest.HEALTH, UserInterest.FITNESS, UserInterest.TRAVEL),
    val isCompleted: Boolean = false
)

class OnboardingViewModel(
    private val preferencesRepo: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun toggleInterest(interest: UserInterest) {
        val current = _uiState.value.selectedInterests
        val updated = if (interest in current) {
            if (current.size > 1) current - interest else current // keep at least 1
        } else {
            current + interest
        }
        _uiState.value = _uiState.value.copy(selectedInterests = updated)
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesRepo.updateInterests(_uiState.value.selectedInterests)
            preferencesRepo.setOnboarded(true)
            _uiState.value = _uiState.value.copy(isCompleted = true)
        }
    }
}
