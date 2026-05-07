package com.example.healthmateai.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthmateai.ui.model.AuthSharedContent
import com.example.healthmateai.ui.model.LoginContent
import com.example.healthmateai.ui.theme.AccentBlue
import com.example.healthmateai.ui.theme.AccentCyan
import com.example.healthmateai.ui.theme.BgDark
import com.example.healthmateai.ui.theme.BgDarkAlt
import com.example.healthmateai.ui.theme.SurfaceCard
import com.example.healthmateai.ui.theme.SurfaceCardAlt
import com.example.healthmateai.ui.theme.SurfaceOutline
import com.example.healthmateai.ui.theme.TextLight
import com.example.healthmateai.ui.theme.TextPrimary
import com.example.healthmateai.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    content: LoginContent,
    sharedContent: AuthSharedContent,
    onLoginClick: (email: String, password: String) -> Unit,
    onSignUpClick: () -> Unit,
    onGoogleSignInClick: () -> Unit = {},
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var startAnimations by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { startAnimations = true }

    val headerAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
        label = "login_header_alpha"
    )
    val headerOffset by animateDpAsState(
        targetValue = if (startAnimations) 0.dp else 12.dp,
        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
        label = "login_header_offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .statusBarsPadding()
    ) {
        AuthBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(headerAlpha)
                    .offset(y = headerOffset),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sharedContent.appTitle,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LiveBadge(text = sharedContent.badgeLive)
                }
                Text(
                    text = sharedContent.appSubtitle,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(SurfaceCardAlt, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = authIconFor(content.icon),
                                contentDescription = content.title,
                                tint = AccentCyan
                            )
                        }
                        Column {
                            Text(
                                text = content.title,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = content.subtitle,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        if (content.heroBadge.isNotBlank()) {
                            LiveBadge(text = content.heroBadge)
                        }
                    }

                    if (!errorMessage.isNullOrBlank()) {
                        ErrorBanner(message = errorMessage)
                    }

                    AuthTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = content.emailLabel,
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )

                    AuthTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = content.passwordLabel,
                        icon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        showPassword = showPassword,
                        onShowPasswordToggle = { showPassword = !showPassword }
                    )

                    Text(
                        text = content.forgotLabel,
                        color = AccentCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { }
                    )

                    LiftButton(
                        text = content.ctaLabel,
                        enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
                        isLoading = isLoading,
                        onClick = { onLoginClick(email, password) }
                    )

                    DividerRow(text = content.dividerLabel)

                    OutlinedButton(
                        onClick = { onGoogleSignInClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SurfaceOutline),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        )
                    ) {
                        Text(
                            text = content.googleLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    AuthFooterLink(
                        prompt = content.signupPrompt,
                        link = content.signupLink,
                        onClick = onSignUpClick
                    )

                    Text(
                        text = content.termsText,
                        fontSize = 11.sp,
                        color = TextLight
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawCircle(
            color = AccentBlue.copy(alpha = 0.08f),
            radius = w * 0.45f,
            center = Offset(w * 0.1f, h * 0.1f)
        )
        drawCircle(
            color = AccentCyan.copy(alpha = 0.06f),
            radius = w * 0.6f,
            center = Offset(w * 0.9f, h * 0.2f)
        )
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF3B1D24), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = message,
            color = Color(0xFFFF8C8C),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onShowPasswordToggle: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = AccentCyan
            )
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onShowPasswordToggle) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = label,
                        tint = TextLight
                    )
                }
            }
        } else {
            null
        },
        visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentCyan,
            unfocusedBorderColor = SurfaceOutline,
            focusedContainerColor = SurfaceCardAlt,
            unfocusedContainerColor = SurfaceCardAlt,
            cursorColor = AccentCyan,
            focusedLabelColor = AccentCyan,
            unfocusedLabelColor = TextLight
        ),
        textStyle = LocalTextStyle.current.copy(color = TextPrimary),
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun LiftButton(
    text: String,
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val lift by animateDpAsState(
        targetValue = if (pressed) 0.dp else 4.dp,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "login_button_lift"
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = AccentBlue,
            disabledContainerColor = SurfaceOutline
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = lift, pressedElevation = 0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DividerRow(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = SurfaceOutline)
        Text(
            text = text,
            color = TextLight,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = SurfaceOutline)
    }
}

@Composable
private fun AuthFooterLink(prompt: String, link: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = prompt, color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = link,
            color = AccentCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onClick() }
        )
    }
}

@Composable
private fun LiveBadge(text: String) {
    Box(
        modifier = Modifier
            .background(AccentCyan.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text = text, color = AccentCyan, fontSize = 10.sp)
    }
}

private fun authIconFor(name: String): ImageVector {
    return when (name.lowercase()) {
        "lock" -> Icons.Default.Lock
        "user" -> Icons.Default.Person
        else -> Icons.Default.Lock
    }
}
