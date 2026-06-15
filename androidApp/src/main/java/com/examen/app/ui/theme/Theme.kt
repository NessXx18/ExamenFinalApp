package com.examen.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta de colores enfocada en la salud mental (tonos verdes y claros)
val MintGreen = Color(0xFF98FF98)
val LightSage = Color(0xFFE2E8D5)
val ForestGreen = Color(0xFF2E8B57)
val SoftTeal = Color(0xFF5F9EA0)
val CalmWhite = Color(0xFFF5F8F5)
val DarkCharcoal = Color(0xFF2F4F4F)

private val DarkColorScheme = darkColorScheme(
    primary = MintGreen,
    secondary = SoftTeal,
    tertiary = LightSage,
    background = DarkCharcoal,
    surface = DarkCharcoal,
    onPrimary = DarkCharcoal,
    onSecondary = CalmWhite,
    onTertiary = DarkCharcoal,
    onBackground = CalmWhite,
    onSurface = CalmWhite
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    secondary = SoftTeal,
    tertiary = MintGreen,
    background = CalmWhite,
    surface = CalmWhite,
    onPrimary = CalmWhite,
    onSecondary = CalmWhite,
    onTertiary = DarkCharcoal,
    onBackground = DarkCharcoal,
    onSurface = DarkCharcoal
)

@Composable
fun MentalHealthAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
