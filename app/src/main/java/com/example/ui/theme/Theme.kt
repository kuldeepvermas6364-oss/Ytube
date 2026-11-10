package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = YtRed,
  onPrimary = Color.White,
  primaryContainer = YtRedDark,
  onPrimaryContainer = Color.White,
  secondary = YtBlue,
  onSecondary = Color.Black,
  background = YtBlack,
  onBackground = YtTextPrimary,
  surface = YtDarkSurface,
  onSurface = YtTextPrimary,
  surfaceVariant = YtDarkCard,
  onSurfaceVariant = YtTextSecondary,
  outline = YtDarkBorder
)

private val LightColorScheme = lightColorScheme(
  primary = YtRed,
  onPrimary = Color.White,
  primaryContainer = YtRedDark,
  onPrimaryContainer = Color.White,
  secondary = YtBlue,
  onSecondary = Color.White,
  background = YtLightBackground,
  onBackground = YtLightTextPrimary,
  surface = YtLightSurface,
  onSurface = YtLightTextPrimary,
  surfaceVariant = YtLightCard,
  onSurfaceVariant = YtLightTextSecondary,
  outline = Color(0xFFE0E0E0)
)

@Composable
fun YouTubeTheme(
  darkTheme: Boolean = true, // Default to iconic YouTube Dark Mode
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
