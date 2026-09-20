package com.yallapark.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val YallaTightRadius = 8.dp
val YallaTightShape = RoundedCornerShape(8.dp)

val LocalThemeIsDark = compositionLocalOf { false }

val YallaParkShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(8.dp)
)

private val LightColorScheme = lightColorScheme(
    primary = EcoGreen500,
    onPrimary = Color.White,
    primaryContainer = EcoGreen100,
    onPrimaryContainer = EcoGreen700,
    secondary = TechBlue500,
    onSecondary = Color.White,
    secondaryContainer = TechBlue100,
    background = Color(0xFFF9FAFB),
    surface = Color.White,
    onBackground = Color(0xFF111827),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF4B5563),
    outline = Color(0xFFE5E7EB)
)

private val DarkColorScheme = darkColorScheme(
    primary = EcoGreen500,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A2F),
    onPrimaryContainer = EcoGreen300,
    secondary = TechBlue500,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF0D253A),
    background = Color(0xFF0B1120),
    surface = Color(0xFF111827),
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF374151)
)

@Composable
fun YallaParkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalThemeIsDark provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = YallaParkShapes,
            content = content
        )
    }
}
