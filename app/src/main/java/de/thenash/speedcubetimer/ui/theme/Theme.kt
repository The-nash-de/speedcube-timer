package de.thenash.speedcubetimer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB8C7FF),
    onPrimary = Color(0xFF101114),

    background = Color(0xFF0B0B0C),
    onBackground = Color(0xFFF2F2F2),

    surface = Color(0xFF171719),
    onSurface = Color(0xFFF2F2F2),

    surfaceVariant = Color(0xFF222225),
    onSurfaceVariant = Color(0xFFD0D0D0)
)

@Composable
fun SpeedcubeTimerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}