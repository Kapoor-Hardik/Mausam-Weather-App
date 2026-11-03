package com.example.mausamweather.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mausamweather.domain.model.ForecastDay
import com.example.mausamweather.domain.model.TemperatureUnit
import com.example.mausamweather.utils.UnitConverter

@Composable
fun ForecastSection(
    dailyForecast: List<ForecastDay>,
    temperatureUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "5-Day Forecast",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        dailyForecast.take(5).forEach { forecastDay ->
            ForecastItemRow(
                forecastDay = forecastDay,
                temperatureUnit = temperatureUnit
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ForecastItemRow(
    forecastDay: ForecastDay,
    temperatureUnit: TemperatureUnit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.width(80.dp)) {
                Text(
                    text = forecastDay.dayOfWeek,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = forecastDay.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = forecastDay.condition.emojiIcon,
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = forecastDay.condition.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(120.dp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = UnitConverter.formatTemperature(forecastDay.maxTempCelsius, temperatureUnit),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = UnitConverter.formatTemperature(forecastDay.minTempCelsius, temperatureUnit),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
