package com.example.mausam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.mausam.data.local.MausamLocalStorage
import com.example.mausam.data.local.PreferencesManager
import com.example.mausam.data.remote.MockWeatherDataSource
import com.example.mausam.data.repository.*
import com.example.mausam.domain.model.UserPreferences
import com.example.mausam.presentation.alerts.AlertsViewModel
import com.example.mausam.presentation.events.EventsViewModel
import com.example.mausam.presentation.home.HomeViewModel
import com.example.mausam.presentation.locations.SavedLocationsViewModel
import com.example.mausam.presentation.onboarding.OnboardingViewModel
import com.example.mausam.presentation.preferences.PreferencesViewModel
import com.example.mausam.presentation.profile.ProfileViewModel
import com.example.mausam.presentation.search.SearchViewModel
import com.example.mausam.ui.navigation.MausamAppNavHost
import com.example.mausam.ui.theme.MausamTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Core Data Sources & Preferences
        val weatherDataSource = MockWeatherDataSource()
        val preferencesManager = PreferencesManager(applicationContext)
        val localStorage = MausamLocalStorage()

        // 2. Repositories
        val weatherRepo = WeatherRepositoryImpl(weatherDataSource)
        val airQualityRepo = AirQualityRepositoryImpl(weatherDataSource)
        val marineRepo = MarineRepositoryImpl(weatherDataSource)
        val locationRepo = LocationRepositoryImpl()
        val savedLocationRepo = SavedLocationRepositoryImpl(localStorage)
        val alertRepo = AlertRepositoryImpl(weatherDataSource)
        val eventRepo = EventRepositoryImpl(localStorage)
        val preferencesRepo = PreferencesRepositoryImpl(preferencesManager)
        val featureConditionsRepo = FeatureConditionsRepositoryImpl()

        // 3. Screen-Level ViewModels
        val homeViewModel = HomeViewModel(
            weatherRepo = weatherRepo,
            airQualityRepo = airQualityRepo,
            marineRepo = marineRepo,
            alertRepo = alertRepo,
            featureConditionsRepo = featureConditionsRepo,
            preferencesRepo = preferencesRepo
        )
        val onboardingViewModel = OnboardingViewModel(preferencesRepo)
        val searchViewModel = SearchViewModel(locationRepo)
        val savedLocationsViewModel = SavedLocationsViewModel(savedLocationRepo)
        val alertsViewModel = AlertsViewModel(alertRepo)
        val eventsViewModel = EventsViewModel(eventRepo, weatherRepo)
        val preferencesViewModel = PreferencesViewModel(preferencesRepo)
        val profileViewModel = ProfileViewModel(preferencesRepo)

        setContent {
            val userPrefs by preferencesRepo.getUserPreferences().collectAsState(
                initial = UserPreferences()
            )

            MausamTheme(themeMode = userPrefs.appTheme) {
                MausamAppNavHost(
                    homeViewModel = homeViewModel,
                    onboardingViewModel = onboardingViewModel,
                    searchViewModel = searchViewModel,
                    savedLocationsViewModel = savedLocationsViewModel,
                    alertsViewModel = alertsViewModel,
                    eventsViewModel = eventsViewModel,
                    preferencesViewModel = preferencesViewModel,
                    profileViewModel = profileViewModel
                )
            }
        }
    }
}
