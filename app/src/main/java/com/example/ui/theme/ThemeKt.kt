package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val RedBlackScheme = darkColorScheme(
    primary = RedPrimary,
    primaryContainer = RedPrimaryContainer,
    background = RedBackground,
    surface = RedSurface,
    onPrimary = RedOnPrimary,
    onBackground = BrightWhite,
    onSurface = RedOnSurface
)

val RoyalIndigoScheme = darkColorScheme(
    primary = IndigoPrimary,
    primaryContainer = IndigoPrimaryContainer,
    background = IndigoBackground,
    surface = IndigoSurface,
    onPrimary = IndigoOnPrimary,
    onBackground = BrightWhite,
    onSurface = IndigoOnSurface
)

val EmeraldGreenScheme = darkColorScheme(
    primary = EmeraldPrimary,
    primaryContainer = EmeraldPrimaryContainer,
    background = EmeraldBackground,
    surface = EmeraldSurface,
    onPrimary = EmeraldOnPrimary,
    onBackground = BrightWhite,
    onSurface = EmeraldOnSurface
)

val SlateSilverScheme = darkColorScheme(
    primary = SlatePrimary,
    primaryContainer = SlatePrimaryContainer,
    background = SlateBackground,
    surface = SlateSurface,
    onPrimary = SlateOnPrimary,
    onBackground = BrightWhite,
    onSurface = SlateOnSurface
)

val OceanTealScheme = darkColorScheme(
    primary = TealPrimary,
    primaryContainer = TealPrimaryContainer,
    background = TealBackground,
    surface = TealSurface,
    onPrimary = TealOnPrimary,
    onBackground = BrightWhite,
    onSurface = TealOnSurface
)

val BeigeCreamScheme = lightColorScheme(
    primary = BeigePrimary,
    primaryContainer = BeigePrimaryContainer,
    background = BeigeBackground,
    surface = BeigeSurface,
    onPrimary = BeigeOnPrimary,
    onBackground = BeigeOnSurface,
    onSurface = BeigeOnSurface
)

val RoyalGoldScheme = darkColorScheme(
    primary = BrightWhite,  // Radiating text contrast
    primaryContainer = BrightWhite.copy(alpha = 0.1f),
    background = PureBlack,
    surface = PureBlack,
    onPrimary = PureBlack,
    onBackground = BrightWhite,
    onSurface = BrightWhite,
    secondary = BrightWhite.copy(alpha = 0.8f)
)

val ForestSageScheme = darkColorScheme(
    primary = BrightWhite,
    primaryContainer = BrightWhite.copy(alpha = 0.1f),
    background = PureBlack,
    surface = PureBlack,
    onPrimary = PureBlack,
    onBackground = BrightWhite,
    onSurface = BrightWhite,
    secondary = BrightWhite.copy(alpha = 0.8f)
)

val CosmicLavenderScheme = darkColorScheme(
    primary = BrightWhite,
    primaryContainer = BrightWhite.copy(alpha = 0.1f),
    background = PureBlack,
    surface = PureBlack,
    onPrimary = PureBlack,
    onBackground = BrightWhite,
    onSurface = BrightWhite,
    secondary = BrightWhite.copy(alpha = 0.8f)
)

@Composable
fun MyApplicationTheme(themeChoice: String, content: @Composable () -> Unit) {
    val colors = when (themeChoice.lowercase()) {
        "red", "redblack", "red_black" -> RedBlackScheme
        "indigo", "royalindigo" -> RoyalIndigoScheme
        "emerald", "emeraldgreen" -> EmeraldGreenScheme
        "slate", "slatesilver" -> SlateSilverScheme
        "teal", "oceanteal" -> OceanTealScheme
        "beige", "beigecream" -> BeigeCreamScheme
        "gold", "royalgold" -> RoyalGoldScheme
        "sage", "forestsage" -> ForestSageScheme
        "lavender", "cosmiclavender" -> CosmicLavenderScheme
        else -> SlateSilverScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
