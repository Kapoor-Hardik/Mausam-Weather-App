package com.example.mausam.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mausam.presentation.alerts.AlertsScreen
import com.example.mausam.presentation.alerts.AlertsViewModel
import com.example.mausam.presentation.events.EventsScreen
import com.example.mausam.presentation.events.EventsViewModel
import com.example.mausam.presentation.home.HomeScreen
import com.example.mausam.presentation.home.HomeViewModel
import com.example.mausam.presentation.locations.SavedLocationsScreen
import com.example.mausam.presentation.locations.SavedLocationsViewModel
import com.example.mausam.presentation.onboarding.OnboardingScreen
import com.example.mausam.presentation.onboarding.OnboardingViewModel
import com.example.mausam.presentation.preferences.PreferencesScreen
import com.example.mausam.presentation.preferences.PreferencesViewModel
import com.example.mausam.presentation.profile.ProfileScreen
import com.example.mausam.presentation.profile.ProfileViewModel
import com.example.mausam.presentation.search.SearchScreen
import com.example.mausam.presentation.search.SearchViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Onboarding : Screen("onboarding", "Onboarding")
    object Home : Screen("home", "Home", Icons.Default.Home)
    object SavedLocations : Screen("saved_locations", "Locations", Icons.Default.Place)
    object Alerts : Screen("alerts", "Alerts", Icons.Default.Warning)
    object Events : Screen("events", "Events", Icons.Default.Event)
    object Preferences : Screen("preferences", "Settings", Icons.Default.Settings)
    object Search : Screen("search", "Search")
    object Profile : Screen("profile", "Profile")
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.SavedLocations,
    Screen.Alerts,
    Screen.Events,
    Screen.Preferences
)

@Composable
fun MausamAppNavHost(
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel,
    onboardingViewModel: OnboardingViewModel,
    searchViewModel: SearchViewModel,
    savedLocationsViewModel: SavedLocationsViewModel,
    alertsViewModel: AlertsViewModel,
    eventsViewModel: EventsViewModel,
    preferencesViewModel: PreferencesViewModel,
    profileViewModel: ProfileViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val prefsState by preferencesViewModel.userPreferences.collectAsState()
    val startDestination = if (prefsState.isOnboarded) Screen.Home.route else Screen.Onboarding.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    viewModel = onboardingViewModel,
                    onOnboardingFinished = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToAlerts = { navController.navigate(Screen.Alerts.route) },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToSavedLocations = { navController.navigate(Screen.SavedLocations.route) },
                    onNavigateToEvents = { navController.navigate(Screen.Events.route) }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = searchViewModel,
                    onLocationSelected = { location ->
                        homeViewModel.loadWeatherData(location)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.SavedLocations.route) {
                SavedLocationsScreen(
                    viewModel = savedLocationsViewModel,
                    onSelectDestination = { city ->
                        homeViewModel.loadWeatherData(city)
                        navController.navigate(Screen.Home.route)
                    }
                )
            }

            composable(Screen.Alerts.route) {
                AlertsScreen(viewModel = alertsViewModel)
            }

            composable(Screen.Events.route) {
                EventsScreen(viewModel = eventsViewModel)
            }

            composable(Screen.Preferences.route) {
                PreferencesScreen(viewModel = preferencesViewModel)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onNavigateToPreferences = { navController.navigate(Screen.Preferences.route) },
                    onResetOnboarding = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
