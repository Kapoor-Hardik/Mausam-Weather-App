package com.example.mausam.presentation.preferences

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mausam.domain.model.AppThemeMode
import com.example.mausam.domain.model.SpeedUnit
import com.example.mausam.domain.model.TemperatureUnit
import com.example.mausam.domain.model.UserInterest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    viewModel: PreferencesViewModel
) {
    val prefs by viewModel.userPreferences.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("App Preferences & Customization") }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Personalized Interests",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Enable or disable interest cards on your homepage dashboard:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(UserInterest.values()) { interest ->
                val isSelected = interest in prefs.selectedInterests
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = interest.emoji, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = interest.displayName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = interest.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = isSelected,
                            onCheckedChange = { viewModel.toggleInterest(interest) }
                        )
                    }
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Measurement Units",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Temperature Unit", style = MaterialTheme.typography.bodyMedium)
                    Row {
                        FilterChip(
                            selected = prefs.temperatureUnit == TemperatureUnit.CELSIUS,
                            onClick = { viewModel.updateTemperatureUnit(TemperatureUnit.CELSIUS) },
                            label = { Text("°C") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = prefs.temperatureUnit == TemperatureUnit.FAHRENHEIT,
                            onClick = { viewModel.updateTemperatureUnit(TemperatureUnit.FAHRENHEIT) },
                            label = { Text("°F") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Wind Speed Unit", style = MaterialTheme.typography.bodyMedium)
                    Row {
                        FilterChip(
                            selected = prefs.speedUnit == SpeedUnit.KILOMETERS_PER_HOUR,
                            onClick = { viewModel.updateSpeedUnit(SpeedUnit.KILOMETERS_PER_HOUR) },
                            label = { Text("km/h") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = prefs.speedUnit == SpeedUnit.MILES_PER_HOUR,
                            onClick = { viewModel.updateSpeedUnit(SpeedUnit.MILES_PER_HOUR) },
                            label = { Text("mph") }
                        )
                    }
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Theme & Appearance",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = prefs.appTheme == AppThemeMode.SYSTEM,
                        onClick = { viewModel.updateTheme(AppThemeMode.SYSTEM) },
                        label = { Text("System Default") }
                    )
                    FilterChip(
                        selected = prefs.appTheme == AppThemeMode.LIGHT,
                        onClick = { viewModel.updateTheme(AppThemeMode.LIGHT) },
                        label = { Text("Light") }
                    )
                    FilterChip(
                        selected = prefs.appTheme == AppThemeMode.DARK,
                        onClick = { viewModel.updateTheme(AppThemeMode.DARK) },
                        label = { Text("Dark") }
                    )
                }
            }
        }
    }
}
