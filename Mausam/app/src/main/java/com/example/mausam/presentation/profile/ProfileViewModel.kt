package com.example.mausam.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mausam.domain.model.UserPreferences
import com.example.mausam.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val preferencesRepo: PreferencesRepository
) : ViewModel() {

    private val _preferences = MutableStateFlow(UserPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepo.getUserPreferences().collectLatest { prefs ->
                _preferences.value = prefs
            }
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            preferencesRepo.setOnboarded(false)
        }
    }
}
