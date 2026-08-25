package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LocalIsHighContrast = staticCompositionLocalOf { false }

private val DarkColorScheme =
  darkColorScheme(
    primary = FarmGreenPrimary,
    secondary = FarmGreenSecondary,
    tertiary = FarmGreenContainer,
    background = Color(0xFF121411),
    surface = Color(0xFF1E211D),
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = FarmGreenPrimary,
    secondary = FarmGreenSecondary,
    tertiary = FarmGreenContainer,
    primaryContainer = FarmGreenLight,
    onPrimaryContainer = FarmGreenHeader,
    background = FarmBackground,
    surface = FarmSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = FarmTextDark,
    onBackground = FarmTextDark,
    onSurface = FarmTextDark,
    outline = FarmBorder
  )

private val HighContrastLightColorScheme =
  lightColorScheme(
    primary = Color(0xFF1B5E20), // Deep pure Forest Green
    secondary = Color(0xFF004D40),
    tertiary = Color(0xFF2E7D32),
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF000000),
    background = Color(0xFFFFFFFF), // Pure white
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF000000),
    onBackground = Color(0xFF000000), // Pure Black text
    onSurface = Color(0xFF000000),    // Pure Black text
    outline = Color(0xFF000000)       // Strong black border
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  isHighContrast: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    isHighContrast -> HighContrastLightColorScheme
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  val typography = if (isHighContrast) HighContrastTypography else Typography

  CompositionLocalProvider(LocalIsHighContrast provides isHighContrast) {
    MaterialTheme(colorScheme = colorScheme, typography = typography, content = content)
  }
}

