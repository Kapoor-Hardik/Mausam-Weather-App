package com.example.mausam.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mausam.domain.model.UserInterest
import com.example.mausam.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSavedLocations: () -> Unit,
    onNavigateToEvents: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopHeader(
                locationName = state.locationName,
                onSearchClick = onNavigateToSearch,
                onAlertsClick = onNavigateToAlerts,
                onProfileClick = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Unable to fetch weather data",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.error!!,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.refresh() }) {
                        Text("Retry")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // 1. High Priority Weather Alerts Banner
                    item {
                        ActiveAlertsBanner(
                            alerts = state.alerts,
                            onSeeAll = onNavigateToAlerts
                        )
                    }

                    // 2. Main Current Weather Card
                    item {
                        state.currentWeather?.let { weather ->
                            CurrentWeatherCard(weather = weather)
                        }
                    }

                    // 3. Hourly Forecast Row
                    item {
                        HourlyForecastSection(hourly = state.hourlyForecast)
                    }

                    // 4. Personalization Engine - DYNAMICALLY PRIORITIZED SECTIONS
                    val sections = state.dashboard?.prioritizedSections ?: listOf(
                        UserInterest.HEALTH,
                        UserInterest.FITNESS,
                        UserInterest.TRAVEL
                    )

                    sections.forEach { interest ->
                        item {
                            when (interest) {
                                UserInterest.HEALTH -> HealthSectionCard(
                                    airQuality = state.airQuality,
                                    pollenData = state.pollenData
                                )
                                UserInterest.FITNESS -> {
                                    state.dashboard?.activityRecommendation?.let { rec ->
                                        state.currentWeather?.let { weather ->
                                            FitnessSectionCard(recommendation = rec, weather = weather)
                                        }
                                    }
                                }
                                UserInterest.BEACH -> BeachSectionCard(marine = state.marineConditions)
                                UserInterest.TRAVEL -> TravelPackingCard(
                                    tips = state.dashboard?.travelPackingTips ?: emptyList(),
                                    onSavedClick = onNavigateToSavedLocations
                                )
                                UserInterest.FAMILY -> SchoolCommuteCard(commute = state.schoolCommute)
                                UserInterest.AGRICULTURE -> AgricultureCard(agri = state.agriculture)
                                UserInterest.COMMUTING -> CommuterCard(commute = state.commute)
                                UserInterest.EVENTS -> {
                                    state.dashboard?.comfortIndex?.let { comfort ->
                                        EventComfortCard(comfort = comfort, onPlannerClick = onNavigateToEvents)
                                    }
                                }
                            }
                        }
                    }

                    // 5. 7-Day Forecast Section
                    item {
                        DailyForecastSection(daily = state.dailyForecast)
                    }
                }
            }
        }
    }
}
