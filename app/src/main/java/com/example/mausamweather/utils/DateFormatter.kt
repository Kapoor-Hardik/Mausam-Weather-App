package com.example.mausamweather.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    fun formatIsoDateToDayOfWeek(isoDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(isoDate) ?: return isoDate
            val outputFormat = SimpleDateFormat("EEE", Locale.getDefault())
            outputFormat.format(date)
        } catch (_: Exception) {
            isoDate
        }
    }

    fun formatTimestampToReadable(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        return format.format(date)
    }
}
