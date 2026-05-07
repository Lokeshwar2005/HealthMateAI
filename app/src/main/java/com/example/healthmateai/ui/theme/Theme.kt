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

// Primary Colors
val PrimaryOrange = Color(0xFFF1B36A)
val PrimaryTeal = Color(0xFF39D0C8)
val PrimaryDarkBlue = Color(0xFF4C6FFF)

// Accent Colors
val AccentCyan = Color(0xFF3DD6D0)
val AccentBlue = Color(0xFF5B7CFF)
val AccentGreen = Color(0xFF2BD4A4)
val AccentViolet = Color(0xFF6C7EFF)

// Text Colors
val TextPrimary = Color(0xFFE7EAF2)
val TextSecondary = Color(0xFF9AA4B5)
val TextLight = Color(0xFF6F7787)

// Background Colors
val BgDark = Color(0xFF0E1220)
val BgDarkAlt = Color(0xFF12182A)
val BgWhite = Color(0xFFFFFFFF)
val BgLightGray = Color(0xFFF9FAFB)

// Surface Colors
val SurfaceCard = Color(0xFF1A2135)
val SurfaceCardAlt = Color(0xFF202845)
val SurfaceOutline = Color(0xFF2A334A)
val SurfaceGlass = Color(0x662A334A)

// Status Colors
val ErrorRed = Color(0xFFEF4444)
val SuccessGreen = Color(0xFF10B981)

val GradientStart = Color(0xFF10C9B5)
val GradientEnd = Color(0xFF355DFF)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    secondary = PrimaryDarkBlue,
    tertiary = PrimaryOrange,
    background = BgLightGray,
    surface = BgWhite,
    error = ErrorRed,
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTeal,
    secondary = PrimaryDarkBlue,
    tertiary = PrimaryOrange,
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

