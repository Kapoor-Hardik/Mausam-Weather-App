package com.example.mausam.presentation.locations

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedLocationsScreen(
    viewModel: SavedLocationsViewModel,
    onSelectDestination: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Saved Destinations") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.toggleAddDialog(true) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Destination")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.destinations.isEmpty()) {
                Text(
                    text = "No saved destinations yet.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.destinations) { dest ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectDestination(dest.cityName) },
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
                                            text = dest.cityName,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = dest.country,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = dest.conditionIcon, fontSize = 28.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${dest.temperatureC.toInt()}°C",
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                        )
                                        IconButton(onClick = { viewModel.removeDestination(dest.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                                        }
                                    }
                                }

                                dest.alertText?.let { alert ->
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "⚠️ $alert",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                if (dest.packingTips.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "🎒 Packing Tip: ${dest.packingTips.first()}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (state.showAddDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.toggleAddDialog(false) },
                    title = { Text("Add Destination") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = state.newCityInput,
                                onValueChange = { viewModel.onCityInputChanged(it) },
                                label = { Text("City Name") },
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = state.newCountryInput,
                                onValueChange = { viewModel.onCountryInputChanged(it) },
                                label = { Text("Country / Region") },
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = { viewModel.addDestination() }) {
                            Text("Save")
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
