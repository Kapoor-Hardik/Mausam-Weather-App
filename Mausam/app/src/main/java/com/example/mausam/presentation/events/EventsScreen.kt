package com.example.mausam.presentation.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    viewModel: EventsViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Event Weather Planner") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.toggleAddDialog(true) }) {
                Icon(Icons.Default.Add, contentDescription = "Plan Event")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.events.isEmpty()) {
                Text(
                    text = "No upcoming events planned yet.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.events) { event ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = event.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${event.date} • ${event.time} @ ${event.location}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = { viewModel.removeEvent(event.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "🌡️ Temp: ${event.expectedTempC.toInt()}°C", style = MaterialTheme.typography.bodySmall)
                                    Text(text = "🌧️ Rain: ${event.rainProbability}%", style = MaterialTheme.typography.bodySmall)
                                    Text(text = "💨 Wind: ${event.windKph.toInt()} km/h", style = MaterialTheme.typography.bodySmall)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Comfort Index: ${event.comfortIndexRating}",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = event.comfortExplanation,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (state.showAddEventDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.toggleAddDialog(false) },
                    title = { Text("Plan Outdoor Event") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = state.newName,
                                onValueChange = { viewModel.onNameChanged(it) },
                                label = { Text("Event Name (e.g. Wedding, Picnic)") },
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = state.newDate,
                                onValueChange = { viewModel.onDateChanged(it) },
                                label = { Text("Date (e.g. Saturday, Nov 12)") },
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = state.newTime,
                                onValueChange = { viewModel.onTimeChanged(it) },
                                label = { Text("Time (e.g. 05:00 PM)") },
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = state.newLocation,
                                onValueChange = { viewModel.onLocationChanged(it) },
                                label = { Text("Location") },
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = { viewModel.addEvent() }) {
                            Text("Calculate Comfort & Plan")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.toggleAddDialog(false) }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
