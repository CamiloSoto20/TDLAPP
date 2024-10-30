package com.example.tdlapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

val DarkColorScheme = darkColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    background = Color.Black,
    surface = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

val LightColorScheme = lightColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    background = Color.White,
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    secondary = Color(0xFF6200EE) // Morado para los botones
)

@Composable
fun TDLAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

