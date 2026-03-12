package com.mindfulminutes.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// ── Static convenience accessors ──────────────────────────────────────────────
// These read the current theme from the composition local.
// They can be used in any @Composable context.

val Background: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.background

val Surface: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.surface

val SurfaceLight: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.surfaceLight

val Accent: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.accent

val AccentDim: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.accentDim

val AccentBorder: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.accentBorder

val AccentBg: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.accentBg

val TextPrimary: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.textPrimary

val TextSecondary: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.textSecondary

val TextTertiary: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.textTertiary

val TextMuted: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.textMuted

val CardBg: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.cardBg

val CardBorder: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.cardBorder

val CardBgHover: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.cardBgHover

val InhaleColor: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.inhaleColor

val ExhaleColor: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.exhaleColor

val HoldColor: Color
    @Composable @ReadOnlyComposable get() = LocalAppTheme.current.holdColor

// ── Static non-themed colors ──────────────────────────────────────────────────
val DividerColor = Color(0x14FFFFFF)
val FavColor = Color(0xFFD48D8D)
