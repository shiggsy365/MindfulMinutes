package com.mindfulminutes.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalAppTheme = staticCompositionLocalOf<AppTheme> { ThemeForest }

@Composable
fun MindfulMinutesTheme(
    appTheme: AppTheme = ThemeForest,
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = appTheme.accent,
        onPrimary = appTheme.background,
        primaryContainer = appTheme.accentDim,
        secondary = appTheme.accent,
        onSecondary = appTheme.background,
        background = appTheme.background,
        onBackground = appTheme.textPrimary,
        surface = appTheme.surface,
        onSurface = appTheme.textPrimary,
        surfaceVariant = appTheme.cardBg,
        onSurfaceVariant = appTheme.textSecondary,
        outline = appTheme.cardBorder
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = appTheme.background.toArgb()
            window.navigationBarColor = appTheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    CompositionLocalProvider(LocalAppTheme provides appTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MindfulTypography,
            content = content
        )
    }
}
