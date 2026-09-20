package com.communityconnect.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Green600,
    onPrimary = Color.White,
    primaryContainer = Green100,
    onPrimaryContainer = Green900,
    secondary = Orange600,
    onSecondary = Color.White,
    secondaryContainer = Orange100,
    onSecondaryContainer = Orange900,
    tertiary = Blue600,
    onTertiary = Color.White,
    tertiaryContainer = Blue100,
    onTertiaryContainer = Blue900,
    error = Red600,
    onError = Color.White,
    errorContainer = Red100,
    onErrorContainer = Red900,
    background = Neutral50,
    onBackground = Neutral900,
    surface = Color.White,
    onSurface = Neutral900,
    surfaceVariant = Neutral100,
    onSurfaceVariant = Neutral600,
    outline = Neutral300,
    outlineVariant = Neutral200,
    shadow = Color.Black,
    scrim = Color.Black,
    inverseSurface = Neutral900,
    inverseOnSurface = Neutral50,
    inversePrimary = Green400,
    surfaceTint = Green600
)

private val DarkColorScheme = darkColorScheme(
    primary = Green400,
    onPrimary = Green950,
    primaryContainer = Green800,
    onPrimaryContainer = Green100,
    secondary = Orange400,
    onSecondary = Orange950,
    secondaryContainer = Orange800,
    onSecondaryContainer = Orange100,
    tertiary = Blue400,
    onTertiary = Blue950,
    tertiaryContainer = Blue800,
    onTertiaryContainer = Blue100,
    error = Red400,
    onError = Red50,
    errorContainer = Red900,
    onErrorContainer = Red50,
    background = Neutral950,
    onBackground = Neutral50,
    surface = Neutral900,
    onSurface = Neutral50,
    surfaceVariant = Neutral800,
    onSurfaceVariant = Neutral400,
    outline = Neutral700,
    outlineVariant = Neutral800,
    shadow = Color.Black,
    scrim = Color.Black,
    inverseSurface = Neutral50,
    inverseOnSurface = Neutral900,
    inversePrimary = Green600,
    surfaceTint = Green400
)

@Composable
fun Theme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}