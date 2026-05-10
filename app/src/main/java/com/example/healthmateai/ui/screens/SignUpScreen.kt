package com.example.healthmateai.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthmateai.ui.model.AuthSharedContent
import com.example.healthmateai.ui.model.AuthValidationContent
import com.example.healthmateai.ui.model.SignUpContent
import com.example.healthmateai.ui.theme.*

@Composable
fun SignUpScreen(
    content: SignUpContent, sharedContent: AuthSharedContent, validationContent: AuthValidationContent,
    onSignUpClick: (name: String, email: String, password: String) -> Unit,
    onLoginClick: () -> Unit, isLoading: Boolean = false, errorMessage: String? = null
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

    val headerAlpha by animateFloatAsState(if (startAnimations) 1f else 0f, tween(600, easing = FastOutSlowInEasing), label = "sha")
    val headerOffset by animateDpAsState(if (startAnimations) 0.dp else 20.dp, tween(600, easing = FastOutSlowInEasing), label = "sho")
    val cardAlpha by animateFloatAsState(if (startAnimations) 1f else 0f, tween(700, delayMillis = 200), label = "sca")

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF060E1A), Color(0xFF0E1B32), Color(0xFF0A1425)))).statusBarsPadding()) {
        CyberBackground()

        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(24.dp))

            // Branding
            Column(Modifier.fillMaxWidth().alpha(headerAlpha).offset(y = headerOffset), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(shape = RoundedCornerShape(18.dp), color = AccentCyan.copy(alpha = 0.1f), border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.3f))) {
                    Icon(Icons.Default.PersonAdd, null, tint = AccentCyan, modifier = Modifier.padding(12.dp).size(28.dp))
                }
                Spacer(Modifier.height(4.dp))
                Text(sharedContent.appTitle.ifBlank { "HealthMate AI" }, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF4F8FF))
                Text("Create your health account", fontSize = 13.sp, color = AccentCyan, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(20.dp))

            Card(Modifier.fillMaxWidth().alpha(cardAlpha), shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111D35).copy(alpha = 0.85f)),
                border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.12f))
            ) {
                Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(content.title.ifBlank { "Create Account" }, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(content.subtitle.ifBlank { "Join the AI health revolution" }, fontSize = 13.sp, color = TextSecondary)
                    }

                    if (!errorMessage.isNullOrBlank()) ErrorBanner(errorMessage)

                    PremiumTextField(name, { name = it }, content.nameLabel.ifBlank { "Full Name" }, Icons.Default.Person, KeyboardType.Text)
                    PremiumTextField(email, { email = it }, content.emailLabel.ifBlank { "Email" }, Icons.Default.Email, KeyboardType.Email)
                    PremiumTextField(password, { password = it }, content.passwordLabel.ifBlank { "Password" }, Icons.Default.Lock, KeyboardType.Password, true, showPassword) { showPassword = !showPassword }
                    PremiumTextField(confirmPassword, { confirmPassword = it }, content.confirmLabel.ifBlank { "Confirm Password" }, Icons.Default.Lock, KeyboardType.Password, true, showConfirmPassword) { showConfirmPassword = !showConfirmPassword }

                    if (password.isNotBlank() && confirmPassword.isNotBlank()) {
                        val matches = password == confirmPassword
                        Surface(shape = RoundedCornerShape(8.dp), color = if (matches) Color(0xFF1D3B2A) else Color(0xFF3B1D24)) {
                            Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(if (matches) Icons.Default.CheckCircle else Icons.Default.Cancel, null, tint = if (matches) SuccessGreen else ErrorRed, modifier = Modifier.size(16.dp))
                                Text(if (matches) validationContent.passwordMatch.ifBlank { "Passwords match" } else validationContent.passwordMismatch.ifBlank { "Passwords don't match" },
                                    fontSize = 12.sp, color = if (matches) SuccessGreen else ErrorRed)
                            }
                        }
                    }

                    // Terms checkbox
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(termsAccepted, { termsAccepted = it }, colors = CheckboxDefaults.colors(checkedColor = AccentCyan, uncheckedColor = SurfaceOutline, checkmarkColor = BgDark))
                        Spacer(Modifier.width(4.dp))
                        Text(content.termsText.ifBlank { "I agree to the Terms of Service & Privacy Policy" }, fontSize = 12.sp, color = TextLight)
                    }

                    GlowButton(content.ctaLabel.ifBlank { "Create Account" },
                        name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && password == confirmPassword && termsAccepted && !isLoading, isLoading
                    ) { onSignUpClick(name, email, password) }

                    DividerRow(content.dividerLabel.ifBlank { "or" })
                    AuthFooterLink(content.loginPrompt.ifBlank { "Already have an account?" }, content.loginLink.ifBlank { "Sign In" }, onLoginClick)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
