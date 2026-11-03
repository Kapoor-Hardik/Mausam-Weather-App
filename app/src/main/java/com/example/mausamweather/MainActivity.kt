package com.example.mausamweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mausamweather.presentation.home.HomeScreen
import com.example.mausamweather.presentation.home.HomeViewModel
import com.example.mausamweather.presentation.home.HomeViewModelFactory
import com.example.mausamweather.ui.theme.MausamWeatherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appContainer = (application as MausamApplication).container
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(appContainer.weatherRepository)
            )

            MausamWeatherTheme {
                HomeScreen(viewModel = homeViewModel)
            }
        }
    }
}
