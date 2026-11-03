package com.example.mausam.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.mausam.domain.model.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlueTertiary,
    secondary = SkyBluePrimary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = WeatherLightSurface,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = SkyBluePrimary,
    secondary = SkyBlueSecondary,
    tertiary = SkyBlueTertiary,
    background = WeatherLightBackground,
    surface = WeatherLightSurface,
    onPrimary = WeatherLightSurface,
    onBackground = WeatherLightOnSurface,
    onSurface = WeatherLightOnSurface
)

@Composable
fun MausamTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
