package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkRedBlackScheme = darkColorScheme(
    primary = Color(0xFFE53935),       // Elegant deep red
    secondary = Color(0xFFFFB300),     // Vibrant amber
    background = Color(0xFF111111),    // Solid black
    surface = Color(0xFF1E1E1E),       // Balanced dark gray
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val DarkModernSlateScheme = darkColorScheme(
    primary = Color(0xFF1E88E5),       // Steel blue
    secondary = Color(0xFFFB8C00),     // Dark amber
    background = Color(0xFF121212),    // Charcoal slate
    surface = Color(0xFF1C1C1E),       // Dark aluminum
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val DarkEmeraldScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),       // Vivid emerald green
    secondary = Color(0xFFFFC107),     // Soft gold
    background = Color(0xFF0F1710),    // Dark forest tint
    surface = Color(0xFF19221A),       // Deep moss gray
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val DarkRoyalIndigoScheme = darkColorScheme(
    primary = Color(0xFF5C6BC0),       // Royal indigo blue
    secondary = Color(0xFF4FC3F7),     // Neon cyan sky
    background = Color(0xFF12121C),    // Deep starry night background
    surface = Color(0xFF1C1C2C),       // Space gray surface
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun MyApplicationTheme(
    themeChoice: String = "red_black",
    content: @Composable () -> Unit
) {
    val activeColorScheme = when (themeChoice) {
        "red_black" -> DarkRedBlackScheme
        "modern_slate" -> DarkModernSlateScheme
        "emerald_oasis" -> DarkEmeraldScheme
        "indigo_sky" -> DarkRoyalIndigoScheme
        else -> DarkRedBlackScheme
    }

    MaterialTheme(
        colorScheme = activeColorScheme,
        typography = Typography,
        content = content
    )
}
