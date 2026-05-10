package com.example.healthmateai.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Modern header card with icon and title
 */
@Composable
fun ModernHeader(
    title: String,
    subtitle: String,
    iconBackgroundColor: Color,
    icon: ImageVector,
    iconTint: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(top = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = iconBackgroundColor,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF003D66)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}

/**
 * Success message card
 */
@Composable
fun SuccessMessageCard(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFECFDF5),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = Color(0xFF10B981),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                fontSize = 12.sp,
                color = Color(0xFF059669),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Error message card
 */
@Composable
fun ErrorMessageCard(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFFFEBEE),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = message,
            fontSize = 12.sp,
            color = Color(0xFFD32F2F),
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Primary action button
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFA500),
            disabledContainerColor = Color(0xFFFFD9A3)
        ),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                text = text,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Secondary action button
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.5.dp,
            color = Color(0xFFE5E7EB)
        ),
        enabled = enabled
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color(0xFF1F2937),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Divider with text in the middle
 */
@Composable
fun DividerWithText(text: String = "OR") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = Color(0xFFE5E7EB)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF9CA3AF),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = Color(0xFFE5E7EB)
        )
    }
}

/**
 * Auth navigation link
 */
@Composable
fun AuthNavigationLink(
    firstText: String,
    linkText: String,
    onLinkClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = firstText,
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )
        Text(
            text = linkText,
            fontSize = 13.sp,
            color = Color(0xFF17A2B8),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 4.dp)
                .clickable { onLinkClick() }
        )
    }
}

/**
 * Password validation indicator
 */
@Composable
fun PasswordValidationIndicator(
    password: String,
    confirmPassword: String
) {
    if (password.isNotEmpty() && password != confirmPassword) {
        Text(
            text = "✗ Passwords do not match",
            fontSize = 12.sp,
            color = Color(0xFFD32F2F),
            fontWeight = FontWeight.Medium
        )
    } else if (password.isNotEmpty() && password == confirmPassword && password.length >= 6) {
        Text(
            text = "✓ Passwords match",
            fontSize = 12.sp,
            color = Color(0xFF10B981),
            fontWeight = FontWeight.Medium
        )
    }
}

// ========================== PREMIUM HEADER & BUTTON COMPONENTS ==========================

/**
 * Premium futuristic header card with cyan glow and gradient background
 * Perfect for Disease Prediction hero section
 */
@Composable
fun PremiumHeaderCard(
    title: String,
    subtitle: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0F1E3C),  // Deep navy
                            Color(0xFF1A3A52)   // Deep blue
                        ),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(0f, 500f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF3DD6D0).copy(alpha = 0.4f),
                            Color(0xFF58E5FF).copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 18.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFF5FAFF),
                        letterSpacing = 0.3.sp
                    )
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFB1BFDF),
                        letterSpacing = 0.1.sp
                    )
                }

                icon?.let { vectorIcon ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF27C5C0).copy(alpha = 0.15f),
                                        Color(0xFF54E4FF).copy(alpha = 0.1f)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF3DD6D0).copy(alpha = 0.3f),
                                        Color(0xFF58E5FF).copy(alpha = 0.15f)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = vectorIcon,
                            contentDescription = title,
                            tint = Color(0xFF3DD6D0),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Premium glowing button - Clean single-layer design
 * Deep teal to cyan gradient with soft outer glow
 * Minimal, futuristic healthcare dashboard style
 * No nested containers, no layered effects
 */
@Composable
fun PremiumGlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState().value
    
    val scale = animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "glowButtonScale"
    )

    Box(
        modifier = modifier
            .height(52.dp)
            .shadow(
                elevation = if (enabled) 20.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                clip = true
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1BA8A4),  // Deep teal
                        Color(0xFF3DD6D0)   // Medium cyan
                    ),
                    start = Offset(0f, 26f),
                    end = Offset(500f, 26f)
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
                Text(
                    text = "Loading...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    letterSpacing = 0.2.sp
                )
            }
        } else {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                letterSpacing = 0.3.sp
            )
        }
    }
}

/**
 * Premium back button - Clean single-layer design with cyan outline
 * Pairs perfectly with PremiumGlowButton
 * Same height and alignment, no nested backgrounds
 */
@Composable
fun PremiumBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState().value
    
    val scale = animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "backButtonScale"
    )

    Box(
        modifier = modifier
            .height(52.dp)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                clip = true
            )
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF3DD6D0).copy(alpha = 0.6f),
                        Color(0xFF54E4FF).copy(alpha = 0.4f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = Color(0xFF1A2F47).copy(alpha = 0.6f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Back",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFAFC8E0),
            letterSpacing = 0.3.sp
        )
    }
}

// ========================== SPACING SYSTEM ==========================

/**
 * Consistent spacing values for the entire app
 * Use these to maintain visual harmony
 */
object AppSpacing {
    val xs = 4.dp      // Minimal spacing
    val sm = 8.dp      // Small spacing
    val md = 12.dp     // Medium spacing
    val lg = 16.dp     // Large spacing (default content padding)
    val xl = 20.dp     // Extra large spacing
    val xxl = 24.dp    // Double extra large
    val xxxl = 32.dp   // Triple extra large
}

/**
 * Consistent padding values for cards and containers
 */
object AppPadding {
    val containerSmall = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    val containerMedium = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    val containerLarge = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
    val screenHorizontal = 16.dp  // Horizontal padding for screen edges
    val screenVertical = 12.dp    // Vertical padding for screen content
}
