package com.example.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// 1. COSMIC SLATE THEME (🌌)
val CosmicSlateBackground = Color(0xFF0F172A) // slate 900
val CosmicSlateSurface = Color(0xFF1E293B) // slate 800
val CosmicSlatePrimary = Color(0xFF38BDF8) // sky 400
val CosmicSlateSecondary = Color(0xFF818CF8) // indigo 400
val CosmicSlateText = Color(0xFFF8FAFC) // slate 50

val CosmicSlateScheme = darkColorScheme(
    primary = CosmicSlatePrimary,
    secondary = CosmicSlateSecondary,
    background = CosmicSlateBackground,
    surface = CosmicSlateSurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = CosmicSlateText,
    onSurface = CosmicSlateText
)

// 2. CHARCOAL GOLD THEME (✨)
val CharcoalBackground = Color(0xFF121212)
val CharcoalSurface = Color(0xFF1E1E1E)
val CharcoalGoldPrimary = Color(0xFFD4AF37) // Metallic Gold
val CharcoalGoldSecondary = Color(0xFFF3E5AB) // Vanilla/soft Gold
val CharcoalText = Color(0xFFF5F5F5)

val CharcoalGoldScheme = darkColorScheme(
    primary = CharcoalGoldPrimary,
    secondary = CharcoalGoldSecondary,
    background = CharcoalBackground,
    surface = CharcoalSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = CharcoalText,
    onSurface = CharcoalText
)

// 3. ROYAL EMERALD THEME (🟢)
val RoyalEmeraldBackground = Color(0xFF082F49) // very deep petrol/teal-emerald tint
val RoyalEmeraldTrueBackground = Color(0xFF042013) // Deep dark forest
val RoyalEmeraldSurface = Color(0xFF0F3A20)
val RoyalEmeraldPrimary = Color(0xFF34D399) // Emerald Mint
val RoyalEmeraldSecondary = Color(0xFF6EE7B7)
val RoyalEmeraldText = Color(0xFFECFDF5)

val RoyalEmeraldScheme = darkColorScheme(
    primary = RoyalEmeraldPrimary,
    secondary = RoyalEmeraldSecondary,
    background = RoyalEmeraldTrueBackground,
    surface = RoyalEmeraldSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = RoyalEmeraldText,
    onSurface = RoyalEmeraldText
)

// Default Font Style configuration
val DaliliTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 26.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)

@Composable
fun DaliliTheme(
    themeName: String = "Cosmic Slate",
    customPrimaryColorHex: String? = null,
    content: @Composable () -> Unit
) {
    // Dynamically pick scheme
    val baseScheme = when (themeName) {
        "Charcoal Gold" -> CharcoalGoldScheme
        "Royal Emerald" -> RoyalEmeraldScheme
        else -> CosmicSlateScheme
    }

    // Override colors if the admin specifies a custom HEX code
    val finalScheme = if (!customPrimaryColorHex.isNullOrBlank()) {
        try {
            val parsedColor = Color(android.graphics.Color.parseColor(customPrimaryColorHex))
            baseScheme.copy(primary = parsedColor)
        } catch (_: Exception) {
            baseScheme
        }
    } else {
        baseScheme
    }

    MaterialTheme(
        colorScheme = finalScheme,
        typography = DaliliTypography,
        content = content
    )
}
