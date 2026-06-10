package org.inovaapp.project.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val MindGreen = Color(0xFF4CAF82)
val MindGreenDark = Color(0xFF2E7D57)
val MindBackground = Color(0xFFF0F7F4)
val MindOnBackground = Color(0xFF1A2E25)
val MindError = Color(0xFFE57373)

private val LightColors = lightColorScheme(
    primary = MindGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB8E8D0),
    background = MindBackground,
    surface = Color.White,
    onBackground = MindOnBackground,
    onSurface = MindOnBackground,
    error = MindError
)

@Composable
fun MindGuardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}