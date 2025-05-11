package com.example.lupath.ui.theme

import android.app.Activity
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
import com.example.lupath.ui.theme.DarkColorScheme

private val DarkColorScheme = darkColorScheme(
    primary = GreenLight,
    secondary = GreenDark,
    tertiary = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = GreenDark,
    secondary = GreenLight,
    tertiary = Color.Green,


    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),

)

private val CustomColorScheme = lightColorScheme(
    primary = GreenLight,
    onPrimary = OnGreen,
    surface = GreenLight,
    onSurface = OnGreen,
    background = GreenLight,
    onBackground = OnGreen,
    secondary = GreenDark
)

@Composable
fun LuPathTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            darkColorScheme()
        }

        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}