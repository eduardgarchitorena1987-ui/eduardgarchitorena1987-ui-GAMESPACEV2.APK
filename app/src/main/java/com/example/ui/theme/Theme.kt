package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.model.GamingTheme

@Composable
fun MyApplicationTheme(
    gamingTheme: GamingTheme = GamingTheme.CYBER_NEON,
    content: @Composable () -> Unit
) {
    val accent = Color(gamingTheme.accentHex)
    val secondary = Color(gamingTheme.secondaryHex)

    val colorScheme = darkColorScheme(
        primary = accent,
        onPrimary = Color.Black,
        primaryContainer = accent.copy(alpha = 0.2f),
        onPrimaryContainer = accent,
        secondary = secondary,
        onSecondary = Color.Black,
        secondaryContainer = secondary.copy(alpha = 0.2f),
        onSecondaryContainer = secondary,
        background = CosmicDark,
        onBackground = TextPrimary,
        surface = SpaceDark,
        onSurface = TextPrimary,
        surfaceVariant = GlassSurfaceElevated,
        onSurfaceVariant = TextSecondary,
        outline = accent.copy(alpha = 0.35f)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
