package com.example.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

fun parseColorHex(hexStr: String, fallback: Color): Color {
    return try {
        if (hexStr.startsWith("#")) {
            Color(android.graphics.Color.parseColor(hexStr))
        } else {
            Color(android.graphics.Color.parseColor("#$hexStr"))
        }
    } catch (e: Exception) {
        fallback
    }
}

@Composable
fun DaliliTheme(
    themeChoice: String = "dark",
    customPrimaryStr: String = "#1A237E",
    customSecondaryStr: String = "#FFD700",
    content: @Composable () -> Unit
) {
    val primaryColor = parseColorHex(customPrimaryStr, Color(0xFF1A1A40))
    val secondaryColor = parseColorHex(customSecondaryStr, Color(0xFFEAA611))

    val colorScheme = when (themeChoice) {
        "light" -> {
            lightColorScheme(
                primary = primaryColor,
                secondary = secondaryColor,
                background = Color(0xFFF8F9FA),
                surface = Color(0xFFFFFFFF),
                onPrimary = Color.White,
                onSecondary = Color.Black,
                onBackground = Color(0xFF212529),
                onSurface = Color(0xFF212529),
                outline = Color(0xFFDEE2E6)
            )
        }
        "cosmic" -> {
            // High contrast cosmic slate theme
            darkColorScheme(
                primary = primaryColor,
                secondary = secondaryColor,
                background = Color(0xFF020715),
                surface = Color(0xFF0B1430),
                onPrimary = Color.White,
                onSecondary = Color.Black,
                onBackground = Color(0xFFE2E8F0),
                onSurface = Color(0xFFF8FAFC),
                outline = Color(0xFF1E293B)
            )
        }
        else -> { // Default "dark" theme
            darkColorScheme(
                primary = primaryColor,
                secondary = secondaryColor,
                background = Color(0xFF121212),
                surface = Color(0xFF1E1E1E),
                onPrimary = Color.White,
                onSecondary = Color.Black,
                onBackground = Color(0xFFE0E0E0),
                onSurface = Color(0xFFFFFFFF),
                outline = Color(0xFF2C2C2C)
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
