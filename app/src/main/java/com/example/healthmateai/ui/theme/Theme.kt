package com.example.healthmateai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

// ========================== PRIMARY COLORS ==========================
val PrimaryOrange = Color(0xFFF1B36A)
val PrimaryTeal = Color(0xFF39D0C8)
val PrimaryDarkBlue = Color(0xFF4C6FFF)

// ========================== ACCENT COLORS - PREMIUM CYBER HEALTH ==========================
val AccentCyan = Color(0xFF3DD6D0)           // Soft cyan for glows
val AccentCyanBright = Color(0xFF54E4FF)     // Bright cyan for accents
val AccentBlue = Color(0xFF5B7CFF)           // Premium blue
val AccentGreen = Color(0xFF2BD4A4)          // Medical green
val AccentViolet = Color(0xFF6C7EFF)         // Futuristic violet
val AccentPurple = Color(0xFF7B68EE)         // Deep purple
val AccentMagenta = Color(0xFF00D9FF)        // Neon magenta

// ========================== TEXT COLORS ==========================
val TextPrimary = Color(0xFFE7EAF2)          // Light text for dark theme
val TextSecondary = Color(0xFF9AA4B5)        // Muted secondary text
val TextLight = Color(0xFF6F7787)            // Lighter secondary text
val TextDim = Color(0xFF4A5569)              // Very dim text for hints

// ========================== BACKGROUND COLORS - PREMIUM DARK ==========================
val BgDark = Color(0xFF0E1220)               // Pure dark background
val BgDarkAlt = Color(0xFF12182A)            // Slightly lighter dark
val BgDarkDeep = Color(0xFF0A0F1A)           // Even darker for depth

// ========================== SURFACE COLORS - GRADIENT COMPATIBLE ==========================
val SurfaceCard = Color(0xFF1A2135)          // Primary card surface
val SurfaceCardAlt = Color(0xFF202845)       // Alternative card surface
val SurfaceCardLight = Color(0xFF2A3552)     // Lighter card for contrast
val SurfaceOutline = Color(0xFF2A334A)       // Outline/border color
val SurfaceGlass = Color(0x662A334A)         // Semi-transparent glass effect
val SurfaceGlassLight = Color(0x99334455)    // Lighter glass for overlays

// ========================== LIGHT THEME COLORS ==========================
val BgWhite = Color(0xFFFFFFFF)
val BgLightGray = Color(0xFFF9FAFB)

// ========================== STATUS COLORS ==========================
val ErrorRed = Color(0xFFEF4444)             // Error state
val SuccessGreen = Color(0xFF10B981)         // Success state
val WarningYellow = Color(0xFFEAB308)        // Warning state

// ========================== GRADIENT COLORS ==========================
val GradientStart = Color(0xFF10C9B5)        // Gradient start cyan
val GradientEnd = Color(0xFF355DFF)          // Gradient end blue
val GradientCyanTeal = Color(0xFF27C5C0)     // Cyan-Teal for buttons
val GradientGlow = Color(0xFF54E4FF)         // Bright glow

// ========================== NEW PREMIUM PALETTE ==========================
// Cyber-health themed colors for futuristic dashboard

/** Deep navy for hero sections and dark backgrounds */
val CyberNavy = Color(0xFF0F1E3C)

/** Deep blue for gradient end points */
val CyberDeepBlue = Color(0xFF1A3A52)

/** Neon cyan for glows and accents */
val CyberCyan = Color(0xFF3DD6D0)

/** Bright cyan for active states */
val CyberCyanBright = Color(0xFF58E5FF)

/** Medical teal for health indicators */
val CyberMedicalTeal = Color(0xFF20B4A0)

/** Futuristic blue for interactive elements */
val CyberBlue = Color(0xFF4E9FED)

/** Premium purple for premium features */
val CyberPurple = Color(0xFF8B7EFF)

// Utility extension for alpha modifications
fun Color.withAlpha(alpha: Float): Color = this.copy(alpha = alpha.coerceIn(0f, 1f))

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    secondary = PrimaryDarkBlue,
    tertiary = PrimaryOrange,
    background = BgLightGray,
    surface = BgWhite,
    error = ErrorRed,
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    secondary = AccentBlue,
    tertiary = AccentGreen,
    background = BgDark,
    surface = SurfaceCard,
    surfaceVariant = SurfaceCardAlt,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceOutline,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorRed,
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.2).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp
    )
)

private val AppShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
)

@Composable
fun HealthMateAITheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}


