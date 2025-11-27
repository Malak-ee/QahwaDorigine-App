package com.example.qahwadorigine.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 🎨 Palette de couleurs Cozy Coffee
private val WarmCream = Color(0xFFFFF8E7)
private val SoftBeige = Color(0xFFF5E6D3)
private val CoffeeBean = Color(0xFF6F4E37)
private val RichMocha = Color(0xFF4A3228)
private val CaramelBrown = Color(0xFF8B6F47)
private val LightLatte = Color(0xFFE8D5C4)
private val DeepEspresso = Color(0xFF3E2723)
private val CreamyFoam = Color(0xFFF6F6F6)
private val GoldenAccent = Color(0xFFD4A574)
private val WarmCinnamon = Color(0xFFA0826D)

// Light Theme - Cozy & Warm
private val LightColorScheme = lightColorScheme(
    primary = CoffeeBean,
    onPrimary = Color.White,
    primaryContainer = SoftBeige,
    onPrimaryContainer = RichMocha,

    secondary = CaramelBrown,
    onSecondary = Color.White,
    secondaryContainer = LightLatte,
    onSecondaryContainer = DeepEspresso,

    tertiary = GoldenAccent,
    onTertiary = Color.White,
    tertiaryContainer = WarmCream,
    onTertiaryContainer = RichMocha,

    background = CreamyFoam,
    onBackground = DeepEspresso,

    surface = Color.White,
    onSurface = DeepEspresso,
    surfaceVariant = SoftBeige,
    onSurfaceVariant = WarmCinnamon,

    outline = Color(0xFFBFA88E),
    outlineVariant = LightLatte,

    error = Color(0xFF8B4513),
    onError = Color.White,
    errorContainer = Color(0xFFFFE4D6),
    onErrorContainer = Color(0xFF5D2F0D),
)

// Dark Theme - Cozy Night Café
private val DarkColorScheme = darkColorScheme(
    primary = GoldenAccent,
    onPrimary = DeepEspresso,
    primaryContainer = RichMocha,
    onPrimaryContainer = LightLatte,

    secondary = WarmCinnamon,
    onSecondary = DeepEspresso,
    secondaryContainer = Color(0xFF5D4037),
    onSecondaryContainer = SoftBeige,

    tertiary = LightLatte,
    onTertiary = DeepEspresso,
    tertiaryContainer = Color(0xFF4E342E),
    onTertiaryContainer = WarmCream,

    background = Color(0xFF1C1410),
    onBackground = WarmCream,

    surface = Color(0xFF2C221A),
    onSurface = WarmCream,
    surfaceVariant = Color(0xFF3E2F26),
    onSurfaceVariant = LightLatte,

    outline = Color(0xFF8B7355),
    outlineVariant = Color(0xFF5D4937),

    error = Color(0xFFFFB4A9),
    onError = Color(0xFF680003),
)

@Composable
fun QahwaDOrigineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}