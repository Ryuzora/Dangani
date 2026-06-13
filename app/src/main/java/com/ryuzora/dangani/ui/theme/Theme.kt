package com.ryuzora.dangani.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import android.app.Activity
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = DanganiBlue,
    onPrimary = Color.White,
    primaryContainer = DanganiLightBlue,
    onPrimaryContainer = DanganiBlue,
    secondary = TaskPointsGreen,
    onSecondary = Color.White,
    secondaryContainer = TaskPointsLightGreen,
    onSecondaryContainer = TaskPointsGreen,
    tertiary = VerificationBadgeBg,
    onTertiary = VerificationBadgeText,
    background = BackgroundGray,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed,
    onErrorContainer = Color.White,
    outline = DividerColor,
    outlineVariant = DividerColor,
)

private val DarkColorScheme = darkColorScheme(
    primary = DanganiBlue,
    onPrimary = Color.White,
    primaryContainer = DanganiLightBlueDark,
    onPrimaryContainer = Color.White,
    secondary = TaskPointsGreenDark,
    onSecondary = Color.White,
    secondaryContainer = TaskPointsLightGreenDark,
    onSecondaryContainer = TaskPointsGreenDark,
    tertiary = VerificationBadgeBgDark,
    onTertiary = VerificationBadgeTextDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = CardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = TextSecondaryDark,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed,
    onErrorContainer = Color.White,
    outline = DividerDark,
    outlineVariant = DividerDark,
)

@Composable
fun DanganiTheme(
    darkTheme: Boolean = false,
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
        typography = Typography,
        content = content
    )
}

