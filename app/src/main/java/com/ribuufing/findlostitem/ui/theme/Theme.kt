package com.ribuufing.findlostitem.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.os.Build

// Light theme colors
private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD5B3),
    onPrimaryContainer = Color.Black,

    secondary = BlueAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC8DDF5),
    onSecondaryContainer = Color.Black,

    background = LightBackground,
    onBackground = Color.Black,
    surface = Color(0xFFF8F8F8),
    onSurface = Color.Black,

    error = Color(0xFFB00020),
    onError = Color.White
)

// Dark theme colors
private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFB15A21),
    onPrimaryContainer = Color.White,

    secondary = BlueAccent,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF2A3E54),
    onSecondaryContainer = Color.White,

    background = DarkBackground,
    onBackground = Color.White,
    surface = Color(0xFF1A1A1A),
    onSurface = Color.White,

    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun FindLostItemTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = true,
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

