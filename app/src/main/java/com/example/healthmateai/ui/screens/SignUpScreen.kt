package com.example.healthmateai.ui.screens

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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.healthmateai.ui.model.AuthValidationContent
import com.example.healthmateai.ui.model.SignUpContent
import com.example.healthmateai.ui.theme.AccentBlue
import com.example.healthmateai.ui.theme.AccentCyan
import com.example.healthmateai.ui.theme.BgDark
import com.example.healthmateai.ui.theme.SurfaceCard
import com.example.healthmateai.ui.theme.SurfaceCardAlt
import com.example.healthmateai.ui.theme.SurfaceOutline
import com.example.healthmateai.ui.theme.TextLight
import com.example.healthmateai.ui.theme.TextPrimary
import com.example.healthmateai.ui.theme.TextSecondary

@Composable
fun SignUpScreen(
    content: SignUpContent,
    sharedContent: AuthSharedContent,
    validationContent: AuthValidationContent,
    onSignUpClick: (name: String, email: String, password: String) -> Unit,
    onLoginClick: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }
    var startAnimations by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { startAnimations = true }

    val headerAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
        label = "signup_header_alpha"
    )
    val headerOffset by animateDpAsState(
        targetValue = if (startAnimations) 0.dp else 12.dp,
        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
        label = "signup_header_offset"
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
                        value = name,
                        onValueChange = { name = it },
                        label = content.nameLabel,
                        icon = Icons.Default.Person,
                        keyboardType = KeyboardType.Text
                    )

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

                    AuthTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = content.confirmLabel,
                        icon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        showPassword = showConfirmPassword,
                        onShowPasswordToggle = { showConfirmPassword = !showConfirmPassword }
                    )

                    if (password.isNotBlank() && confirmPassword.isNotBlank()) {
                        val matches = password == confirmPassword
                        Text(
                            text = if (matches) validationContent.passwordMatch else validationContent.passwordMismatch,
                            fontSize = 12.sp,
                            color = if (matches) AccentCyan else Color(0xFFFF8C8C)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AccentBlue,
                                uncheckedColor = SurfaceOutline
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = content.termsText,
                            fontSize = 11.sp,
                            color = TextLight
                        )
                    }

                    LiftButton(
                        text = content.ctaLabel,
                        enabled = name.isNotBlank() && email.isNotBlank() &&
                            password.isNotBlank() && password == confirmPassword && termsAccepted && !isLoading,
                        isLoading = isLoading,
                        onClick = { onSignUpClick(name, email, password) }
                    )

                    DividerRow(text = content.dividerLabel)

                    AuthFooterLink(
                        prompt = content.loginPrompt,
                        link = content.loginLink,
                        onClick = onLoginClick
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
            radius = w * 0.4f,
            center = Offset(w * 0.15f, h * 0.15f)
        )
        drawCircle(
            color = AccentCyan.copy(alpha = 0.06f),
            radius = w * 0.7f,
            center = Offset(w * 0.85f, h * 0.1f)
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
        label = "signup_button_lift"
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
        else -> Icons.Default.Person
    }
}
