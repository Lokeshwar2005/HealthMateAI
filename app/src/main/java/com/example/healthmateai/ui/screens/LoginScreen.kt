package com.example.healthmateai.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthmateai.ui.model.AuthSharedContent
import com.example.healthmateai.ui.model.LoginContent
import com.example.healthmateai.ui.theme.*

@Composable
fun LoginScreen(
    content: LoginContent, sharedContent: AuthSharedContent,
    onLoginClick: (email: String, password: String) -> Unit,
    onSignUpClick: () -> Unit, onGoogleSignInClick: () -> Unit = {},
    isLoading: Boolean = false, errorMessage: String? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var startAnimations by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { startAnimations = true }

    val headerAlpha by animateFloatAsState(if (startAnimations) 1f else 0f, tween(600, easing = FastOutSlowInEasing), label = "ha")
    val headerOffset by animateDpAsState(if (startAnimations) 0.dp else 20.dp, tween(600, easing = FastOutSlowInEasing), label = "ho")
    val cardAlpha by animateFloatAsState(if (startAnimations) 1f else 0f, tween(700, delayMillis = 200), label = "ca")

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF060E1A), Color(0xFF0E1B32), Color(0xFF0A1425)))).statusBarsPadding()) {
        CyberBackground()

        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(40.dp))

            // Branding
            Column(Modifier.fillMaxWidth().alpha(headerAlpha).offset(y = headerOffset), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(shape = RoundedCornerShape(18.dp), color = AccentCyan.copy(alpha = 0.1f), border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.3f))) {
                    Icon(Icons.Default.Favorite, null, tint = AccentCyan, modifier = Modifier.padding(14.dp).size(32.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text(sharedContent.appTitle.ifBlank { "HealthMate AI" }, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF4F8FF))
                Text(sharedContent.appSubtitle.ifBlank { "Your AI Health Companion" }, fontSize = 14.sp, color = AccentCyan, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(32.dp))

            // Card
            Card(Modifier.fillMaxWidth().alpha(cardAlpha), shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111D35).copy(alpha = 0.85f)),
                border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.12f))
            ) {
                Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(content.title.ifBlank { "Welcome Back" }, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(content.subtitle.ifBlank { "Sign in to continue" }, fontSize = 13.sp, color = TextSecondary)
                    }

                    if (!errorMessage.isNullOrBlank()) ErrorBanner(errorMessage)

                    PremiumTextField(email, { email = it }, content.emailLabel.ifBlank { "Email" }, Icons.Default.Email, KeyboardType.Email)
                    PremiumTextField(password, { password = it }, content.passwordLabel.ifBlank { "Password" }, Icons.Default.Lock, KeyboardType.Password, true, showPassword) { showPassword = !showPassword }

                    Text(content.forgotLabel.ifBlank { "Forgot password?" }, color = AccentCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.End).clickable { })

                    GlowButton(content.ctaLabel.ifBlank { "Sign In" }, email.isNotBlank() && password.isNotBlank() && !isLoading, isLoading) { onLoginClick(email, password) }

                    DividerRow(content.dividerLabel.ifBlank { "or continue with" })

                    OutlinedButton(onClick = onGoogleSignInClick, Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.3f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                    ) {
                        Icon(Icons.Default.Language, null, tint = AccentCyan, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(content.googleLabel.ifBlank { "Sign in with Google" }, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }

                    AuthFooterLink(content.signupPrompt.ifBlank { "Don't have an account?" }, content.signupLink.ifBlank { "Sign Up" }, onSignUpClick)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(content.termsText.ifBlank { "By continuing you agree to our Terms & Privacy Policy" }, fontSize = 11.sp, color = TextLight, textAlign = TextAlign.Center)
        }
    }
}

// ─── Shared Auth Composables ───

@Composable
internal fun CyberBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val offset1 by infiniteTransition.animateFloat(0f, 1f, infiniteRepeatable(tween(8000), RepeatMode.Reverse), label = "o1")
    val offset2 by infiniteTransition.animateFloat(1f, 0f, infiniteRepeatable(tween(6000), RepeatMode.Reverse), label = "o2")

    Canvas(Modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        drawCircle(AccentCyan.copy(alpha = 0.06f), radius = w * 0.5f, center = Offset(w * 0.2f + w * 0.1f * offset1, h * 0.15f))
        drawCircle(AccentBlue.copy(alpha = 0.05f), radius = w * 0.7f, center = Offset(w * 0.8f, h * 0.25f - h * 0.05f * offset2))
        drawCircle(AccentPurple.copy(alpha = 0.04f), radius = w * 0.4f, center = Offset(w * 0.5f, h * 0.85f + h * 0.03f * offset1))
    }
}

@Composable
internal fun ErrorBanner(message: String) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color(0xFF3B1D24), border = BorderStroke(1.dp, Color(0xFF5B2D34))) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFFF8C8C), modifier = Modifier.size(18.dp))
            Text(message, color = Color(0xFFFF8C8C), fontSize = 12.sp)
        }
    }
}

@Composable
internal fun PremiumTextField(
    value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector,
    keyboardType: KeyboardType, isPassword: Boolean = false, showPassword: Boolean = false, onShowPasswordToggle: () -> Unit = {}
) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth(),
        leadingIcon = { Icon(icon, label, tint = AccentCyan) },
        trailingIcon = if (isPassword) {{ IconButton(onClick = onShowPasswordToggle) { Icon(if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, label, tint = TextLight) } }} else null,
        visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentCyan, unfocusedBorderColor = SurfaceOutline.copy(alpha = 0.6f),
            focusedContainerColor = Color(0xFF0D1828), unfocusedContainerColor = Color(0xFF0D1828),
            cursorColor = AccentCyan, focusedLabelColor = AccentCyan, unfocusedLabelColor = TextLight,
            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
        ),
        singleLine = true, shape = RoundedCornerShape(14.dp)
    )
}

@Composable
internal fun GlowButton(text: String, enabled: Boolean, isLoading: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val lift by animateDpAsState(if (pressed) 0.dp else 6.dp, tween(120), label = "l")

    Button(onClick = onClick, Modifier.fillMaxWidth().height(54.dp), enabled = enabled, interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = SurfaceOutline),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = lift, pressedElevation = 0.dp), shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(Modifier.fillMaxSize().background(
            if (enabled) Brush.horizontalGradient(listOf(Color(0xFF00BCD4), Color(0xFF3F7FFF), Color(0xFF6C5CE7))) else Brush.horizontalGradient(listOf(SurfaceOutline, SurfaceOutline)),
            RoundedCornerShape(14.dp)
        ), contentAlignment = Alignment.Center) {
            if (isLoading) CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
            else Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
internal fun DividerRow(text: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(Modifier.weight(1f), color = SurfaceOutline.copy(alpha = 0.5f))
        Text(text, color = TextLight, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 12.dp))
        HorizontalDivider(Modifier.weight(1f), color = SurfaceOutline.copy(alpha = 0.5f))
    }
}

@Composable
internal fun AuthFooterLink(prompt: String, link: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(prompt, color = TextSecondary, fontSize = 13.sp)
        Spacer(Modifier.width(6.dp))
        Text(link, color = AccentCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onClick() })
    }
}

internal fun authIconFor(name: String): ImageVector = when (name.lowercase()) {
    "lock" -> Icons.Default.Lock; "user" -> Icons.Default.Person; else -> Icons.Default.Lock
}
