package com.example.mausam.presentation.alerts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mausam.domain.model.AlertSeverity
import com.example.mausam.ui.theme.AlertInfoColor
import com.example.mausam.ui.theme.AlertSevereColor
import com.example.mausam.ui.theme.AlertWarningColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    viewModel: AlertsViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Weather & Environmental Alerts") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Active Weather & Safety Warnings",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (state.alerts.isEmpty()) {
                Text(
                    text = "No active severe weather or environmental alerts for your area.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.alerts) { alert ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when (alert.severity) {
                                    AlertSeverity.SEVERE -> AlertSevereColor.copy(alpha = 0.15f)
                                    AlertSeverity.WARNING -> AlertWarningColor.copy(alpha = 0.15f)
                                    AlertSeverity.INFO -> AlertInfoColor.copy(alpha = 0.15f)
                                }
                            )
                        ) {
                            Row(modifier = Modifier.padding(16.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = alert.severity.name,
                                    tint = when (alert.severity) {
                                        AlertSeverity.SEVERE -> AlertSevereColor
                                        AlertSeverity.WARNING -> AlertWarningColor
                                        AlertSeverity.INFO -> AlertInfoColor
                                    },
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = alert.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = when (alert.severity) {
                                                AlertSeverity.SEVERE -> AlertSevereColor
                                                AlertSeverity.WARNING -> AlertWarningColor
                                                AlertSeverity.INFO -> AlertInfoColor
                                            }
                                        ) {
                                            Text(
                                                text = alert.severity.name,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = alert.description,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Effective: ${alert.effectiveTime}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
