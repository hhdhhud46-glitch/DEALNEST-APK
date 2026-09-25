package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DealCyanPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF004D40),
    onPrimaryContainer = DealCyanLight,
    secondary = DealIndigo,
    onSecondary = Color.White,
    secondaryContainer = DealIndigoDark,
    onSecondaryContainer = DealIndigoLight,
    tertiary = DealAmber,
    onTertiary = Color.Black,
    background = BrandSlate950,
    onBackground = Color(0xFFF1F5F9),
    surface = BrandSlate900,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = BrandSlate800,
    onSurfaceVariant = BrandSlate300,
    outline = CardBorderDark,
    error = DealRose,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = DealCyanDark,
    onPrimary = Color.White,
    primaryContainer = DealCyanLight,
    onPrimaryContainer = Color(0xFF003838),
    secondary = DealIndigo,
    onSecondary = Color.White,
    secondaryContainer = DealIndigoLight,
    onSecondaryContainer = DealIndigoDark,
    tertiary = DealAmber,
    onTertiary = Color.Black,
    background = BrandSlate50,
    onBackground = BrandSlate950,
    surface = CardSurfaceLight,
    onSurface = BrandSlate900,
    surfaceVariant = BrandSlate100,
    onSurfaceVariant = BrandSlate600,
    outline = CardBorderLight,
    error = DealRose,
    onError = Color.White
)

@Composable
fun DealNestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded DealNest colors consistent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
