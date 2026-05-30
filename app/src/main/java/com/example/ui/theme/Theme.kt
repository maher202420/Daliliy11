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

private val SilverMetallicScheme = darkColorScheme(
    primary = Color(0xFF607D8B),       // Steel metallic gray
    secondary = Color(0xFF78909C),     // Light slate metallic gray
    background = Color(0xFFECEFF1),    // Light silver/white canvas
    surface = Color(0xFFCFD8DC),       // Soft silver brushed surface
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF263238),  // Dark slate text
    onSurface = Color(0xFF37474F)      // Dark surface text
)

private val BeigeCreamScheme = darkColorScheme(
    primary = Color(0xFF8D6E63),       // Warm luxury brown
    secondary = Color(0xFFC7B198),     // Rich beige cream accent
    background = Color(0xFFFDFBF7),    // Crisp warm rice white canvas
    surface = Color(0xFFF4EFE6),       // Soft beige milk tea surface
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF3E2723),  // Espresso coffee text
    onSurface = Color(0xFF4E342E)      // Warm chocolate text
)

@Composable
fun MyApplicationTheme(
    themeChoice: String = "red_black",
    content: @Composable () -> Unit
) {
    val activeColorScheme = when (themeChoice) {
        "red_black" -> DarkRedBlackScheme
        "slate_silver" -> DarkModernSlateScheme
        "emerald_green" -> DarkEmeraldScheme
        "royal_indigo" -> DarkRoyalIndigoScheme
        "silver_metallic" -> SilverMetallicScheme
        "beige_cream" -> BeigeCreamScheme
        else -> DarkRedBlackScheme
    }

    MaterialTheme(
        colorScheme = activeColorScheme,
        typography = Typography,
        content = content
    )
}
