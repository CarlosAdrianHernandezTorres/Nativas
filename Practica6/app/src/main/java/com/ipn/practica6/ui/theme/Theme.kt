package com.ipn.practica6.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorSchemeGuinda = darkColorScheme(
    primary = GuindaLight,
    secondary = GuindaPrimary,
    background = Color.Black,
    onPrimary = Color.White,
)

private val LightColorSchemeGuinda = lightColorScheme(
    primary = GuindaPrimary,
    secondary = GuindaDark,
    background = Color.White,
    onPrimary = Color.White,
)

private val DarkColorSchemeAzul = darkColorScheme(
    primary = AzulLight,
    secondary = AzulPrimary,
    background = Color.Black,
    onPrimary = Color.White,
)

private val LightColorSchemeAzul = lightColorScheme(
    primary = AzulPrimary,
    secondary = AzulDark,
    background = Color.White,
    onPrimary = Color.White,
)

enum class AppTheme {
    GUINDA, AZUL
}

val CurrentTheme = AppTheme.GUINDA

@Composable
fun Practica3Theme(
    theme: AppTheme,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        AppTheme.GUINDA -> if (darkTheme) DarkColorSchemeGuinda else LightColorSchemeGuinda
        AppTheme.AZUL -> if (darkTheme) DarkColorSchemeAzul else LightColorSchemeAzul
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}