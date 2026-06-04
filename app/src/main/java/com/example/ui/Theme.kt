package com.example.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkGray = Color(0xFF121214)
val SurfaceGray = Color(0xFF1E1E22)

// Silver Themes
val SilverPrimary = Color(0xFFCFD2EB)
val SilverSecondary = Color(0xFF708090)
val SilverBackground = Color(0xFF10131A)

// Gold Themes
val GoldPrimary = Color(0xFFD4AF37)
val GoldSecondary = Color(0xFFDEB887)
val GoldBackground = Color(0xFF141210)

// Emerald Themes
val EmeraldPrimary = Color(0xFF42B37A)
val EmeraldSecondary = Color(0xFF2E6B47)
val EmeraldBackground = Color(0xFF0F1511)

@Composable
fun DaliliTheme(
    themeChoice: String,
    customPrimaryStr: String = "",
    customSecondaryStr: String = "",
    content: @Composable () -> Unit
) {
    val primaryColor = when (themeChoice) {
        "silver" -> SilverPrimary
        "emerald" -> EmeraldPrimary
        "custom" -> {
            try { Color(android.graphics.Color.parseColor(customPrimaryStr)) }
            catch (e: Exception) { GoldPrimary }
        }
        else -> GoldPrimary // gold is default
    }

    val secondaryColor = when (themeChoice) {
        "silver" -> SilverSecondary
        "emerald" -> EmeraldSecondary
        "custom" -> {
            try { Color(android.graphics.Color.parseColor(customSecondaryStr)) }
            catch (e: Exception) { GoldSecondary }
        }
        else -> GoldSecondary
    }

    val backgroundColor = when (themeChoice) {
        "silver" -> SilverBackground
        "emerald" -> EmeraldBackground
        "custom" -> DarkGray
        else -> GoldBackground
    }

    val colorScheme = darkColorScheme(
        primary = primaryColor,
        secondary = secondaryColor,
        background = backgroundColor,
        surface = SurfaceGray,
        onPrimary = Color.Black,
        onSecondary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
