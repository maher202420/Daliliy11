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
    val primaryColor = parseColorSafe(settings.primaryColorHex, Color(0xFF6200EE))
    val secondaryColor = parseColorSafe(settings.secondaryColorHex, Color(0xFF03DAC5))

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = primaryColor,
            secondary = secondaryColor,
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            secondary = secondaryColor,
            background = Color(0xFFF9F9F9),
            surface = Color.White,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onBackground = Color(0xFF121212),
            onSurface = Color(0xFF121212)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
