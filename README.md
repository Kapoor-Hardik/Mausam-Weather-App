# MAUSAM — Native Android Weather Application

MAUSAM (मौसम) is a modern, high-performance native Android weather application built using Kotlin, Jetpack Compose, Material 3, and Clean Architecture with MVVM. The application provides real-time weather information, city search geocoding, 5-day daily forecasts, offline Room database caching, and persistent user preferences (Celsius/Fahrenheit unit toggle, Light/Dark/System theme, and recent search history).

---

## Key Features

- **City Search & Geocoding**: Real-time city search powered by the Open-Meteo Geocoding API with multi-city matching and region/country details.
- **Current Weather Dashboard**: Displays temperature, weather condition emoji & description, high/low range, relative humidity, and wind speed in km/h.
- **5-Day Weather Forecast**: Detailed 5-day daily forecast list with min/max temperatures, day names, and weather conditions.
- **Offline Room Caching**: Automatically caches fresh weather observations in local Room Database (`mausam_weather_db`). Falls back seamlessly to offline cache with a clear *"Offline — Showing cached weather as of [time]"* warning banner during network failures.
- **Persistent Preferences (Jetpack DataStore)**:
  - **Temperature Unit**: Celsius (°C) and Fahrenheit (°F) with immediate app-wide calculation updates.
  - **Theme Selection**: System Default, Light Theme, and Dark Theme.
  - **Search History**: Persists up to 5 recently selected cities, prevents duplicates, moves re-searched cities to top, and offers a 1-tap "Clear" history option.
- **Robust Error Handling**: Handles empty input, invalid locations, offline state, HTTP errors, and timeouts gracefully with retry capabilities.
- **Comprehensive Unit Testing**: 40 unit tests covering Weather Code Mappers, Unit Converters, DTO Mappers, Room Entity Mappers, Open-Meteo Remote Data Source, Repositories, ViewModels, and Recent Search History logic.

---

## Screens and App Functionality

1. **Home Screen (`HomeScreen.kt`)**:
   - **Top App Bar**: App title, manual refresh button, and Settings navigation button.
   - **City Search Bar**: Interactive search field with live geocoding results drop-down.
   - **Recent Searches**: Horizontal chip row displaying up to 5 recent cities with 1-tap reload and clear history option.
   - **Offline Banner**: Warning banner displayed whenever weather data is served from local Room cache.
   - **Current Weather Hero Card**: Main weather dashboard card displaying location, weather emoji, temperature, condition, high/low, and °C/°F toggle chip.
   - **Metrics Card**: Grid tiles displaying relative humidity (%) and wind speed (km/h).
   - **5-Day Forecast Section**: Card list showing date, day of week, weather condition, and temperature range.
2. **Settings Screen (`SettingsScreen.kt`)**:
   - **Temperature Unit**: Radio buttons to select Celsius or Fahrenheit.
   - **App Theme**: Radio buttons to select System Default, Light, or Dark theme.

---

## Technology Stack and Dependency Versions

- **Language**: Kotlin `2.2.10`
- **Min SDK**: `24` (Android 7.0 Nougat) | **Target SDK**: `35` | **Compile SDK**: `37`
- **Build System**: Android Gradle Plugin `9.4.1`, KSP `2.2.10-2.0.2`
- **UI Framework**: Jetpack Compose (BOM `2026.02.01`, Material 3, Navigation Compose `2.8.8`, Material Icons Extended)
- **Asynchronous Execution**: Kotlin Coroutines `1.10.1`, `StateFlow`
- **Networking**: Retrofit `2.11.0`, Gson Converter `2.11.0`, OkHttp Logging Interceptor `4.12.0`
- **Database & Persistence**: Room Database `2.8.5`, DataStore Preferences `1.1.3`
- **Testing**: JUnit `4.13.2`, Kotlinx Coroutines Test `1.10.1`

---

## Architecture & Package Structure

MAUSAM follows Clean Architecture guidelines divided into `data`, `domain`, `presentation`, `navigation`, `utils`, and `di`:

```
com.example.mausamweather/
├── data/
│   ├── local/
│   │   ├── dao/WeatherDao.kt
│   │   ├── entity/WeatherEntity.kt
│   │   ├── PreferencesManager.kt
│   │   └── WeatherDatabase.kt
│   ├── mapper/
│   │   ├── WeatherDtoMapper.kt
│   │   └── WeatherEntityMapper.kt
│   ├── remote/
│   │   ├── dto/
│   │   │   ├── GeocodingDto.kt
│   │   │   └── WeatherDto.kt
│   │   ├── ApiConstants.kt
│   │   ├── GeocodingApiService.kt
│   │   ├── NetworkClient.kt
│   │   ├── OpenMeteoRemoteDataSource.kt
│   │   └── WeatherApiService.kt
│   └── repository/
│       ├── MockWeatherRepository.kt
│       └── OpenMeteoWeatherRepository.kt
├── domain/
│   ├── model/
│   │   ├── City.kt
│   │   ├── CurrentWeather.kt
│   │   ├── ForecastDay.kt
│   │   ├── HourlyForecast.kt
│   │   ├── TemperatureUnit.kt
│   │   ├── UserPreferences.kt
│   │   ├── Weather.kt
│   │   ├── WeatherAlert.kt
│   │   └── WeatherCondition.kt
│   └── repository/
│       ├── UserPreferencesRepository.kt
│       └── WeatherRepository.kt
├── presentation/
│   ├── components/
│   │   ├── CurrentWeatherHeroCard.kt
│   │   ├── ForecastSection.kt
│   │   ├── OfflineWarningBanner.kt
│   │   ├── RecentSearchesSection.kt
│   │   ├── SearchBarAndDialog.kt
│   │   └── WeatherMetricsCard.kt
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   ├── HomeUiState.kt
│   │   ├── HomeViewModel.kt
│   │   └── HomeViewModelFactory.kt
│   └── settings/
│       ├── SettingsScreen.kt
│       ├── SettingsUiState.kt
│       ├── SettingsViewModel.kt
│       └── SettingsViewModelFactory.kt
├── ui/
│   ├── navigation/MausamNavGraph.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── utils/
│   ├── DateFormatter.kt
│   ├── UnitConverter.kt
│   └── WeatherCodeMapper.kt
├── di/AppContainer.kt
├── MausamApplication.kt
└── MainActivity.kt
```

---

## APIs Used

1. **Open-Meteo Geocoding API**:
   - Endpoint: `GET https://geocoding-api.open-meteo.com/v1/search`
   - Parameters: `name={cityName}&count=5`
2. **Open-Meteo Weather Forecast API**:
   - Endpoint: `GET https://api.open-meteo.com/v1/forecast`
   - Parameters: `latitude={lat}&longitude={lon}&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,is_day&daily=weather_code,temperature_2m_max,temperature_2m_min&forecast_days=5&timezone=auto`

---

## Local Database and Offline Caching

- **Room Database (`WeatherDatabase`)**: Database name `mausam_weather_db`, table `weather_cache`.
- **Cache Persistence**: Upon receiving a successful network response, live weather data is converted to `WeatherEntity` and saved to Room.
- **Offline Fallback**: When offline or during HTTP network errors, `OpenMeteoWeatherRepository` automatically queries local Room cache by city ID or city name, returning the cached `Weather` object with `isFromCache = true` and `lastUpdatedTimestamp = cachedTimestamp`.

---

## Object-Oriented Programming (OOP) Concepts Implemented

1. **Encapsulation**:
   - ViewModel state is held in private `MutableStateFlow` properties and exposed to Composables as read-only `StateFlow`:
     - File: `presentation/home/HomeViewModel.kt`
     - Example:
       ```kotlin
       private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.InitialEmpty)
       val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
       ```
   - Database operations and DataStore internals are encapsulated inside `PreferencesManager` and `OpenMeteoRemoteDataSource`.

2. **Abstraction**:
   - Repository interfaces hide networking (Retrofit), database (Room), and preferences (DataStore) implementation details from the domain and UI layers:
     - File: `domain/repository/WeatherRepository.kt`
     - File: `domain/repository/UserPreferencesRepository.kt`
     - Example:
       ```kotlin
       interface WeatherRepository {
           suspend fun searchCity(query: String): Result<List<City>>
           suspend fun getWeather(city: City): Result<Weather>
       }
       ```

3. **Inheritance**:
   - Sealed class hierarchies model domain conditions and screen state variations:
     - File: `domain/model/WeatherCondition.kt` (`sealed class WeatherCondition` with subclasses `ClearSky`, `Fog`, `SlightRain`, `Thunderstorm`, `Unknown`)
     - File: `presentation/home/HomeUiState.kt` (`sealed interface HomeUiState` with states `InitialEmpty`, `Loading`, `Success`, `Error`)

4. **Polymorphism**:
   - `WeatherRepository` is implemented by two distinct classes:
     - Live Implementation: `data/repository/OpenMeteoWeatherRepository.kt`
     - Mock Implementation: `data/repository/MockWeatherRepository.kt`
   - Either implementation can be injected via `AppContainer` into ViewModels without changing any UI or presentation logic:
     - File: `di/AppContainer.kt`

---

## Setup and Run Instructions

1. Open **Android Studio** (2024.1+ recommended).
2. Select **Open** and choose directory: `the cloned MAUSAM repository folder on your computer`.
3. Wait for Gradle Sync to complete.
4. Select `app` run configuration and an Android Emulator or Physical Device (API 24+).
5. Click **Run** (`Shift + F10`).

---

## How to Run Unit Tests

Execute the following command in the Android Studio Terminal or Command Prompt:

```bash
./gradlew app:testDebugUnitTest
```

**Unit Test Results**: **40 Passed, 0 Skipped, 0 Failed**.

---

## Assumptions, Limitations, and Known Issues

- **Assumptions**: Open-Meteo API is freely accessible without an API key.
- **Limitations**: Hourly forecast endpoint parsing is supported in domain models and DTOs, but the primary UI focuses on current weather and 5-day daily forecasts.
- **Known Issues**: None. All compilation checks and 40 unit tests pass with zero errors.

---

## Future Improvements

- Air Quality Index (AQI) integration.
- Interactive weather radar maps using Google Maps SDK.
- Weather alerts and severe weather push notifications.
- Customizable Android home screen widgets.

---

## API & Data Attribution

- Weather forecast data provided by [Open-Meteo](https://open-meteo.com/) under non-commercial attribution license.
- Geocoding search provided by [Open-Meteo Geocoding API](https://geocoding-api.open-meteo.com/).
