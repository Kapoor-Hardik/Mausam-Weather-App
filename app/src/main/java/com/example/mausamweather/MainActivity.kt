package com.example.mausamweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.mausamweather.domain.model.AppTheme
import com.example.mausamweather.ui.navigation.MausamNavGraph
import com.example.mausamweather.ui.theme.MausamWeatherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appContainer = (application as MausamApplication).container
            
            // Collect theme preference directly at root
            val userPreferences by appContainer.userPreferencesRepository.userPreferencesFlow.collectAsState(
                initial = null
            )
            
            val isDarkTheme = when (userPreferences?.theme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                else -> isSystemInDarkTheme() // SYSTEM or null
            }

            MausamWeatherTheme(darkTheme = isDarkTheme) {
                MausamNavGraph(appContainer = appContainer)
            }
        }
    }
}
