package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Original Colors Mapping
val RedBlackScheme = darkColorScheme(
    primary = Color(ColorKt.getRedPrimary()),
    primaryContainer = Color(ColorKt.getRedPrimaryContainer()),
    background = Color(ColorKt.getRedBackground()),
    surface = Color(ColorKt.getRedSurface()),
    onPrimary = Color(ColorKt.getRedOnPrimary()),
    onBackground = Color(ColorKt.getBrightWhite()),
    onSurface = Color(ColorKt.getRedOnSurface())
)

val RoyalIndigoScheme = darkColorScheme(
    primary = Color(ColorKt.getIndigoPrimary()),
    primaryContainer = Color(ColorKt.getIndigoPrimaryContainer()),
    background = Color(ColorKt.getIndigoBackground()),
    surface = Color(ColorKt.getIndigoSurface()),
    onPrimary = Color(ColorKt.getIndigoOnPrimary()),
    onBackground = Color(ColorKt.getBrightWhite()),
    onSurface = Color(ColorKt.getIndigoOnSurface())
)

val EmeraldGreenScheme = darkColorScheme(
    primary = Color(ColorKt.getEmeraldPrimary()),
    primaryContainer = Color(ColorKt.getEmeraldPrimaryContainer()),
    background = Color(ColorKt.getEmeraldBackground()),
    surface = Color(ColorKt.getEmeraldSurface()),
    onPrimary = Color(ColorKt.getEmeraldOnPrimary()),
    onBackground = Color(ColorKt.getBrightWhite()),
    onSurface = Color(ColorKt.getEmeraldOnSurface())
)

val SlateSilverScheme = darkColorScheme(
    primary = Color(ColorKt.getSlatePrimary()),
    primaryContainer = Color(ColorKt.getSlatePrimaryContainer()),
    background = Color(ColorKt.getSlateBackground()),
    surface = Color(ColorKt.getSlateSurface()),
    onPrimary = Color(ColorKt.getSlateOnPrimary()),
    onBackground = Color(ColorKt.getBrightWhite()),
    onSurface = Color(ColorKt.getSlateOnSurface())
)

val OceanTealScheme = darkColorScheme(
    primary = Color(ColorKt.getTealPrimary()),
    primaryContainer = Color(ColorKt.getTealPrimaryContainer()),
    background = Color(ColorKt.getTealBackground()),
    surface = Color(ColorKt.getTealSurface()),
    onPrimary = Color(ColorKt.getTealOnPrimary()),
    onBackground = Color(ColorKt.getBrightWhite()),
    onSurface = Color(ColorKt.getTealOnSurface())
)

val BeigeCreamScheme = lightColorScheme(
    primary = Color(ColorKt.getBeigePrimary()),
    primaryContainer = Color(ColorKt.getBeigePrimaryContainer()),
    background = Color(ColorKt.getBeigeBackground()),
    surface = Color(ColorKt.getBeigeSurface()),
    onPrimary = Color(ColorKt.getBeigeOnPrimary()),
    onBackground = Color(ColorKt.getBeigeOnSurface()),
    onSurface = Color(ColorKt.getBeigeOnSurface())
)

// Luxuries Aesthetic Themes (NEW!)
val RoyalGoldScheme = darkColorScheme(
    primary = Color(0xFFFFD700),          // Radiant Gold Accent
    primaryContainer = Color(0xFF3A3100), // Dark Gold container
    background = Color(0xFF121212),       // Carbon space black
    surface = Color(0xFF1E1E1E),          // Dark charcoal surface
    onPrimary = Color(0xFF121212),        // Contrast black text
    onBackground = Color(0xFFFFFFFF),     // Pristine White text
    onSurface = Color(0xFFE5E5E5)         // Safe off-white text
)

val ForestSageScheme = darkColorScheme(
    primary = Color(0xFF90B384),          // Sage Velvet Green
    primaryContainer = Color(0xFF1F2E21), // Deep Forest Moss container
    background = Color(0xFF0F1511),       // Calm Wilderness Black
    surface = Color(0xFF1A221C),          // Dark Sage card
    onPrimary = Color(0xFF0F1511),        // Moss text on green
    onBackground = Color(0xFFFFFFFF),     // Bright White text
    onSurface = Color(0xFFE0E6E1)         // Clean greenish-gray
)

val CosmicLavenderScheme = darkColorScheme(
    primary = Color(0xFFDFD2FF),          // Lavish Cosmic Lavender aura
    primaryContainer = Color(0xFF2C194E), // Deep Violet Nebula container
    background = Color(0xFF120B24),       // Galaxy darkest purple
    surface = Color(0xFF1C1337),          // Nebula dark card
    onPrimary = Color(0xFF120B24),        // Deep Contrast purple
    onBackground = Color(0xFFFFFFFF),     // Pure white
    onSurface = Color(0xFFE8E5F0)         // Lavender white
)

@Composable
fun MyApplicationTheme(themeChoice: String, content: @Composable () -> Unit) {
    val colors = when (themeChoice.lowercase()) {
        "red", "redblack" -> RedBlackScheme
        "indigo", "royalindigo" -> RoyalIndigoScheme
        "emerald", "emeraldgreen" -> EmeraldGreenScheme
        "slate", "slatesilver" -> SlateSilverScheme
        "teal", "oceanteal" -> OceanTealScheme
        "beige", "beigecream" -> BeigeCreamScheme
        "gold", "royalgold" -> RoyalGoldScheme
        "sage", "forestsage" -> ForestSageScheme
        "lavender", "cosmiclavender" -> CosmicLavenderScheme
        else -> SlateSilverScheme // Elegant Default fallback
    }

    MaterialTheme(
        colorScheme = colors,
        typography = TypeKt.getTypography(),
        content = content
    )
}
