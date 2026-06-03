package com.example.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.AppSettings

fun parseColorSafe(hex: String, defaultColor: Color): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        defaultColor
    }
}

@Composable
fun DaliliTheme(
    settings: AppSettings,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Resolve dynamic colors based on current selected theme preset
    val primaryColor: Color
    val secondaryColor: Color
    val backgroundColor: Color
    val surfaceColor: Color

    when (settings.themePreset) {
        "cosmic_slate" -> {
            primaryColor = Color(0xFFB0BEC5)       // Silver
            secondaryColor = Color(0xFF78909C)     // Slate
            backgroundColor = Color(0xFF121824)    // Dark Cozy Slate
            surfaceColor = Color(0xFF1B2330)
        }
        "charcoal_gold" -> {
            primaryColor = Color(0xFFFFD700)       // High-end Gold
            secondaryColor = Color(0xFFD4AF37)     // Matte Gold
            backgroundColor = Color(0xFF121212)    // Luxury Charcoal
            surfaceColor = Color(0xFF1E1E1E)
        }
        "royal_emerald" -> {
            primaryColor = Color(0xFF00C853)       // Emerald Green
            secondaryColor = Color(0xFF81C784)     // Grassy soft
            backgroundColor = Color(0xFF0D2C1E)    // Royal Dark Emerald
            surfaceColor = Color(0xFF123D2A)
        }
        else -> {
            // "custom"
            primaryColor = parseColorSafe(settings.primaryColorHex, Color(0xFF6200EE))
            secondaryColor = parseColorSafe(settings.secondaryColorHex, Color(0xFF03DAC5))
            backgroundColor = parseColorSafe(settings.backgroundColorHex, Color(0xFF121212))
            surfaceColor = Color(0xFF1E1E1E)
        }
    }

    // Resolve app-wide text color based on preset or custom hex selection
    val appTextColor = when (settings.textColorPreset) {
        "bright_white" -> Color(0xFFFFFFFF)
        "light_gold" -> Color(0xFFFFECB3)
        "vibrant_silver" -> Color(0xFFE0E0E0)
        else -> parseColorSafe(settings.textColorHex, Color.White)
    }

    val colorScheme = darkColorScheme(
        primary = primaryColor,
        secondary = secondaryColor,
        background = backgroundColor,
        surface = surfaceColor,
        onPrimary = backgroundColor,
        onSecondary = Color.Black,
        onBackground = appTextColor,
        onSurface = appTextColor
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
