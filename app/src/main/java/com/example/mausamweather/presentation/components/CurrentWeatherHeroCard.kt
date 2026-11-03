package com.example.mausamweather.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mausamweather.domain.model.TemperatureUnit
import com.example.mausamweather.domain.model.Weather
import com.example.mausamweather.utils.UnitConverter

@Composable
fun CurrentWeatherHeroCard(
    weather: Weather,
    temperatureUnit: TemperatureUnit,
    onToggleUnit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = weather.city.name,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = weather.city.displayLocation,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                FilterChip(
                    selected = true,
                    onClick = onToggleUnit,
                    label = {
                        Text(
                            text = if (temperatureUnit == TemperatureUnit.CELSIUS) "°C" else "°F",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = weather.condition.emojiIcon,
                fontSize = 72.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = UnitConverter.formatTemperature(weather.currentTempCelsius, temperatureUnit),
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.ExtraBold)
            )

            Text(
                text = weather.condition.description,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (weather.dailyForecast.isNotEmpty()) {
                val today = weather.dailyForecast.first()
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "High: ${UnitConverter.formatTemperature(today.maxTempCelsius, temperatureUnit)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Low: ${UnitConverter.formatTemperature(today.minTempCelsius, temperatureUnit)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
