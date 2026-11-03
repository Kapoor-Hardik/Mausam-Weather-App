package com.example.mausamweather.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mausamweather.di.AppContainer
import com.example.mausamweather.presentation.home.HomeScreen
import com.example.mausamweather.presentation.home.HomeViewModel
import com.example.mausamweather.presentation.home.HomeViewModelFactory
import com.example.mausamweather.presentation.settings.SettingsScreen
import com.example.mausamweather.presentation.settings.SettingsViewModel
import com.example.mausamweather.presentation.settings.SettingsViewModelFactory

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
}

@Composable
fun MausamNavGraph(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(
                    weatherRepository = appContainer.weatherRepository,
                    userPreferencesRepository = appContainer.userPreferencesRepository
                )
            )
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(route = Screen.Settings.route) {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(
                    userPreferencesRepository = appContainer.userPreferencesRepository
                )
            )
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
