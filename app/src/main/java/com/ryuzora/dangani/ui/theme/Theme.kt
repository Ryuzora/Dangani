package com.ryuzora.dangani.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DanganiColorScheme = lightColorScheme(
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

@Composable
fun DanganiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DanganiColorScheme,
        typography = Typography,
        content = content
    )
}