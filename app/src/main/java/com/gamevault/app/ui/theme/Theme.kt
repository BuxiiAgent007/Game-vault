package com.gamevault.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameVaultColors = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = Color(0xFF1A1030),
    primaryContainer = PurpleDark,
    onPrimaryContainer = Color(0xFFEDE7FF),

    secondary = TealAccent,
    onSecondary = Color(0xFF062B26),
    secondaryContainer = Color(0xFF163E3A),
    onSecondaryContainer = TealAccent,

    tertiary = RatingGold,
    onTertiary = Color(0xFF3D2B00),

    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = Color(0xFF4A4660),
    outlineVariant = Color(0xFF332F47),

    error = ErrorRed,
    onError = Color.White
)

@Composable
fun GameVaultTheme(
    darkTheme: Boolean = true,  // kept for API compat; app is always dark
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GameVaultColors,
        typography = Typography,
        content = content
    )
}