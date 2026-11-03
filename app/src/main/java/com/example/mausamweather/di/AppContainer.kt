package com.example.mausamweather.di

import android.content.Context
import com.example.mausamweather.data.local.PreferencesManager
import com.example.mausamweather.data.local.WeatherDatabase
import com.example.mausamweather.data.local.dataStore
import com.example.mausamweather.data.remote.OpenMeteoRemoteDataSource
import com.example.mausamweather.data.repository.MockWeatherRepository
import com.example.mausamweather.data.repository.OpenMeteoWeatherRepository
import com.example.mausamweather.domain.repository.UserPreferencesRepository
import com.example.mausamweather.domain.repository.WeatherRepository

/**
 * Service Locator container for manual Dependency Injection.
 * Provides central access to repositories, data sources, and database instances.
 */
class AppContainer(
    private val context: Context,
    useMockRepository: Boolean = false
) {
    private val database: WeatherDatabase by lazy {
        WeatherDatabase.getDatabase(context)
    }

    private val remoteDataSource by lazy {
        OpenMeteoRemoteDataSource()
    }

    val weatherRepository: WeatherRepository by lazy {
        if (useMockRepository) {
            MockWeatherRepository()
        } else {
            OpenMeteoWeatherRepository(
                remoteDataSource = remoteDataSource,
                weatherDao = database.weatherDao()
            )
        }
    }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        PreferencesManager(context.dataStore)
    }
}
