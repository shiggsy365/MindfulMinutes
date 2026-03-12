package com.mindfulminutes.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * AppTheme defines a complete color palette for the app.
 * The Zen page always uses its own full-screen background images.
 * All other pages use the selected AppTheme for colors and artwork.
 */
data class AppTheme(
    val id: String,
    val name: String,
    val icon: String,
    val background: Color,
    val surface: Color,
    val surfaceLight: Color,
    val accent: Color,
    val accentDim: Color,
    val accentBorder: Color,
    val accentBg: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textMuted: Color,
    val cardBg: Color,
    val cardBorder: Color,
    val cardBgHover: Color,
    val inhaleColor: Color,
    val exhaleColor: Color,
    val holdColor: Color,
    val artifactStyle: ArtifactStyle
)

enum class ArtifactStyle {
    RIPPLES,      // Ocean, Water
    LEAVES,       // Forest
    WAVES,        // Beach
    PETALS,       // Lavender
    GRAINS,       // Desert
    SNOWFLAKES,   // Arctic
    EMBERS        // Ember
}

val ThemeForest = AppTheme(
    id = "forest",
    name = "Forest",
    icon = "🌲",
    background = Color(0xFF080C0A),
    surface = Color(0xFF0E1410),
    surfaceLight = Color(0xFF151D18),
    accent = Color(0xFF8DA88D),
    accentDim = Color(0x338DA88D),
    accentBorder = Color(0x4D8DA88D),
    accentBg = Color(0x1A8DA88D),
    textPrimary = Color(0xFFE8F0E8),
    textSecondary = Color(0xFFB5BEB5),
    textTertiary = Color(0xFF7E867E),
    textMuted = Color(0xFF4A504A),
    cardBg = Color(0x1AFFFFFF),
    cardBorder = Color(0x1FFFFFFF),
    cardBgHover = Color(0x33FFFFFF),
    inhaleColor = Color(0xFF7DA08D),
    exhaleColor = Color(0xFF7D8DA0),
    holdColor = Color(0xFFA0957D),
    artifactStyle = ArtifactStyle.LEAVES
)

val ThemeOcean = AppTheme(
    id = "ocean",
    name = "Ocean",
    icon = "🌊",
    background = Color(0xFF050D15),
    surface = Color(0xFF0A1520),
    surfaceLight = Color(0xFF0F1E2A),
    accent = Color(0xFF5BB8D4),
    accentDim = Color(0x335BB8D4),
    accentBorder = Color(0x4D5BB8D4),
    accentBg = Color(0x1A5BB8D4),
    textPrimary = Color(0xFFDFF0F5),
    textSecondary = Color(0xFFA8C5D0),
    textTertiary = Color(0xFF6A90A0),
    textMuted = Color(0xFF3A5A68),
    cardBg = Color(0x1A1E6A7A),
    cardBorder = Color(0x1F5BB8D4),
    cardBgHover = Color(0x2A5BB8D4),
    inhaleColor = Color(0xFF5BB8D4),
    exhaleColor = Color(0xFF5B7DD4),
    holdColor = Color(0xFF5BD4B8),
    artifactStyle = ArtifactStyle.RIPPLES
)

val ThemeSunset = AppTheme(
    id = "sunset",
    name = "Sunset",
    icon = "🌅",
    background = Color(0xFF120808),
    surface = Color(0xFF1C1009),
    surfaceLight = Color(0xFF231510),
    accent = Color(0xFFE8913A),
    accentDim = Color(0x33E8913A),
    accentBorder = Color(0x4DE8913A),
    accentBg = Color(0x1AE8913A),
    textPrimary = Color(0xFFF5EAE0),
    textSecondary = Color(0xFFD4B89A),
    textTertiary = Color(0xFFA07850),
    textMuted = Color(0xFF6A4A30),
    cardBg = Color(0x1A7A3A10),
    cardBorder = Color(0x1FE8913A),
    cardBgHover = Color(0x2AE8913A),
    inhaleColor = Color(0xFFE8913A),
    exhaleColor = Color(0xFFE85A3A),
    holdColor = Color(0xFFE8C13A),
    artifactStyle = ArtifactStyle.WAVES
)

val ThemeLavender = AppTheme(
    id = "lavender",
    name = "Lavender",
    icon = "💜",
    background = Color(0xFF0C080F),
    surface = Color(0xFF130E19),
    surfaceLight = Color(0xFF1A1322),
    accent = Color(0xFFB08ED4),
    accentDim = Color(0x33B08ED4),
    accentBorder = Color(0x4DB08ED4),
    accentBg = Color(0x1AB08ED4),
    textPrimary = Color(0xFFEDE8F5),
    textSecondary = Color(0xFFB8A8D0),
    textTertiary = Color(0xFF8870A0),
    textMuted = Color(0xFF5A4A70),
    cardBg = Color(0x1A4A2A7A),
    cardBorder = Color(0x1FB08ED4),
    cardBgHover = Color(0x2AB08ED4),
    inhaleColor = Color(0xFFB08ED4),
    exhaleColor = Color(0xFF8E9ED4),
    holdColor = Color(0xFFD48EB0),
    artifactStyle = ArtifactStyle.PETALS
)

val ThemeDesert = AppTheme(
    id = "desert",
    name = "Desert",
    icon = "🏜️",
    background = Color(0xFF100D08),
    surface = Color(0xFF1A150D),
    surfaceLight = Color(0xFF221C12),
    accent = Color(0xFFC4956A),
    accentDim = Color(0x33C4956A),
    accentBorder = Color(0x4DC4956A),
    accentBg = Color(0x1AC4956A),
    textPrimary = Color(0xFFF5EED5),
    textSecondary = Color(0xFFD4BFA0),
    textTertiary = Color(0xFFA08060),
    textMuted = Color(0xFF6A5040),
    cardBg = Color(0x1A7A5020),
    cardBorder = Color(0x1FC4956A),
    cardBgHover = Color(0x2AC4956A),
    inhaleColor = Color(0xFFC4956A),
    exhaleColor = Color(0xFFC4A86A),
    holdColor = Color(0xFFA08050),
    artifactStyle = ArtifactStyle.GRAINS
)

val ThemeArctic = AppTheme(
    id = "arctic",
    name = "Arctic",
    icon = "❄️",
    background = Color(0xFF080C10),
    surface = Color(0xFF0D1318),
    surfaceLight = Color(0xFF121A20),
    accent = Color(0xFF90C8E8),
    accentDim = Color(0x3390C8E8),
    accentBorder = Color(0x4D90C8E8),
    accentBg = Color(0x1A90C8E8),
    textPrimary = Color(0xFFECF4F8),
    textSecondary = Color(0xFFB8D0DC),
    textTertiary = Color(0xFF7899A8),
    textMuted = Color(0xFF486070),
    cardBg = Color(0x1A204858),
    cardBorder = Color(0x1F90C8E8),
    cardBgHover = Color(0x2A90C8E8),
    inhaleColor = Color(0xFF90C8E8),
    exhaleColor = Color(0xFF908EC8),
    holdColor = Color(0xFF90E8E8),
    artifactStyle = ArtifactStyle.SNOWFLAKES
)

val ThemeEmber = AppTheme(
    id = "ember",
    name = "Ember",
    icon = "🔥",
    background = Color(0xFF0D0806),
    surface = Color(0xFF160E0A),
    surfaceLight = Color(0xFF1E1208),
    accent = Color(0xFFD44A2A),
    accentDim = Color(0x33D44A2A),
    accentBorder = Color(0x4DD44A2A),
    accentBg = Color(0x1AD44A2A),
    textPrimary = Color(0xFFF5E8E0),
    textSecondary = Color(0xFFD4A898),
    textTertiary = Color(0xFFA06850),
    textMuted = Color(0xFF6A4030),
    cardBg = Color(0x1A5A1A0A),
    cardBorder = Color(0x1FD44A2A),
    cardBgHover = Color(0x2AD44A2A),
    inhaleColor = Color(0xFFD44A2A),
    exhaleColor = Color(0xFFD4802A),
    holdColor = Color(0xFFA42A10),
    artifactStyle = ArtifactStyle.EMBERS
)

val ALL_THEMES = listOf(
    ThemeForest, ThemeOcean, ThemeSunset, ThemeLavender,
    ThemeDesert, ThemeArctic, ThemeEmber
)
