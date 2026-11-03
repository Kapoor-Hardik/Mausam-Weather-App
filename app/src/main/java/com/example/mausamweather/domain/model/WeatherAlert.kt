package com.example.mausamweather.domain.model

enum class AlertSeverity {
    INFO, WARNING, SEVERE
}

data class WeatherAlert(
    val id: String,
    val title: String,
    val description: String,
    val severity: AlertSeverity,
    val issuedTime: String
)
