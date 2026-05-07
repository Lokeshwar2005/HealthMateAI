package com.example.healthmateai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
