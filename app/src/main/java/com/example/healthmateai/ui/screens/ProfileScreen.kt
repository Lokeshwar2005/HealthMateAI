package com.example.healthmateai.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthmateai.ai.HealthPredictionSnapshot
import com.example.healthmateai.ai.recommendations.RecommendationsViewModel
import com.example.healthmateai.ui.theme.*

private enum class ProfileDialog { MedicalId, Recommendations, LogoutConfirm }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    userName: String,
    userEmail: String,
    onLogout: () -> Unit,
    healthSnapshot: HealthPredictionSnapshot = HealthPredictionSnapshot()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var activeDialog by remember { mutableStateOf<ProfileDialog?>(null) }
    var isLoggingOut by remember { mutableStateOf(false) }

    // Medical ID state - persisted via SharedPreferences
    val prefs = remember { context.getSharedPreferences("medical_id", Context.MODE_PRIVATE) }
    var emergencyContact by remember { mutableStateOf(prefs.getString("emergency_contact", "") ?: "") }
    var isEditingMedicalId by remember { mutableStateOf(false) }

    // Recommendations VM
    val recommendationsVm: RecommendationsViewModel = viewModel()
    val recState by recommendationsVm.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(BgDark).verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Profile", style = MaterialTheme.typography.headlineMedium, color = Color(0xFFF4F8FF), fontWeight = FontWeight.ExtraBold)
                Text("Your personal health workspace", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF9BAED7))
            }
            IconButton(onClick = { isLoggingOut = false; activeDialog = ProfileDialog.LogoutConfirm }) {
                Icon(Icons.AutoMirrored.Filled.Logout, "Logout", tint = Color(0xFFFF6B6B))
            }
        }

        ProfileHero(userName, userEmail)

        SectionTitle("Health Utilities", "Fast access to essential profile tools")

        FlowRow(Modifier.fillMaxWidth(), maxItemsInEachRow = 2, horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            UtilityCard("Nearby Hospitals", "Open hospital search", Icons.Default.LocalHospital, Modifier.weight(1f)) {
                val uri = Uri.parse("geo:0,0?q=hospitals near me")
                val intent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.google.android.apps.maps") }
                try { context.startActivity(intent) } catch (_: ActivityNotFoundException) { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
            }
            UtilityCard("Medical ID", "Emergency info", Icons.Default.MedicalInformation, Modifier.weight(1f)) { activeDialog = ProfileDialog.MedicalId }
            UtilityCard("AI Recommendations", "Personalized guidance", Icons.Default.Recommend, Modifier.weight(1f)) {
                activeDialog = ProfileDialog.Recommendations
                if (recState.sections.isEmpty() && !recState.isLoading) recommendationsVm.loadRecommendations(healthSnapshot)
            }
        }

        SectionTitle("Emergency", "Quick dial actions when you need them")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            EmergencyActionCard("Ambulance", "108", Icons.Default.Favorite, Modifier.weight(1f)) { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))) }
            EmergencyActionCard("Police", "100", Icons.Default.Shield, Modifier.weight(1f)) { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:100"))) }
        }
        Spacer(Modifier.height(6.dp))
    }

    when (activeDialog) {
        ProfileDialog.MedicalId -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null; isEditingMedicalId = false },
                containerColor = Color(0xFF141E36), titleContentColor = TextPrimary,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(shape = RoundedCornerShape(10.dp), color = AccentCyan.copy(alpha = 0.15f)) {
                            Icon(Icons.Default.MedicalInformation, null, tint = AccentCyan, modifier = Modifier.padding(6.dp))
                        }
                        Text("Medical ID", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        MedicalIdRow(icon = Icons.Default.Person, label = "Name", value = userName)
                        HorizontalDivider(color = SurfaceOutline.copy(alpha = 0.4f))
                        MedicalIdRow(icon = Icons.Default.Email, label = "Email", value = userEmail)
                        HorizontalDivider(color = SurfaceOutline.copy(alpha = 0.4f))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, null, tint = AccentCyan, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Emergency Contact", color = TextSecondary, fontSize = 12.sp)
                                if (isEditingMedicalId) {
                                    OutlinedTextField(
                                        value = emergencyContact, onValueChange = { emergencyContact = it },
                                        placeholder = { Text("Enter phone number") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp), singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AccentCyan, unfocusedBorderColor = SurfaceOutline,
                                            focusedContainerColor = SurfaceCardAlt, unfocusedContainerColor = SurfaceCardAlt,
                                            cursorColor = AccentCyan, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                                        )
                                    )
                                } else {
                                    Text(emergencyContact.ifBlank { "Not set" }, color = TextPrimary, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    if (isEditingMedicalId) {
                        Button(onClick = {
                            prefs.edit().putString("emergency_contact", emergencyContact).apply()
                            isEditingMedicalId = false
                        }, colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = BgDark)) {
                            Icon(Icons.Default.Save, null, Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Save")
                        }
                    } else {
                        Button(onClick = { isEditingMedicalId = true }, colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)) {
                            Icon(Icons.Default.Edit, null, Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Edit")
                        }
                    }
                },
                dismissButton = { OutlinedButton(onClick = { activeDialog = null; isEditingMedicalId = false }) { Text("Close", color = TextSecondary) } }
            )
        }

        ProfileDialog.Recommendations -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                containerColor = Color(0xFF0F1A30), titleContentColor = TextPrimary,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(shape = RoundedCornerShape(10.dp), color = AccentGreen.copy(alpha = 0.15f)) {
                            Icon(Icons.Default.Recommend, null, tint = AccentGreen, modifier = Modifier.padding(6.dp))
                        }
                        Text("AI Recommendations", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState())) {
                        when {
                            recState.isLoading -> {
                                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        CircularProgressIndicator(color = AccentCyan, modifier = Modifier.size(36.dp))
                                        Text("Generating personalized recommendations...", color = TextSecondary, fontSize = 13.sp)
                                    }
                                }
                            }
                            recState.error != null -> {
                                Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF3B1D24), border = BorderStroke(1.dp, Color(0xFF5B2D34))) {
                                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(recState.error ?: "Error", color = Color(0xFFFF8C8C), fontSize = 13.sp)
                                        Button(onClick = { recommendationsVm.retry(healthSnapshot) }, colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = BgDark)) {
                                            Text("Retry")
                                        }
                                    }
                                }
                            }
                            !healthSnapshot.hasPredictionData -> {
                                Text("Run a health prediction first to get personalized AI recommendations.", color = TextSecondary, fontSize = 14.sp)
                            }
                            else -> {
                                recState.sections.forEach { section ->
                                    Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFF162340), border = BorderStroke(1.dp, SurfaceOutline.copy(alpha = 0.4f))) {
                                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("${section.emoji} ${section.title}", color = Color(0xFFE6F0FF), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                            section.items.forEach { item ->
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Text("•", color = AccentCyan)
                                                    Text(item, color = Color(0xFFB8C9EA), style = MaterialTheme.typography.bodyMedium)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = { Button(onClick = { activeDialog = null }) { Text("Done") } }
            )
        }

        ProfileDialog.LogoutConfirm -> {
            AlertDialog(
                onDismissRequest = { if (!isLoggingOut) activeDialog = null },
                containerColor = SurfaceCard, titleContentColor = TextPrimary, textContentColor = TextSecondary,
                title = { Text("Sign out of HealthMateAI?") },
                text = { Text("You will need to log in again to access your dashboard.") },
                confirmButton = {
                    Button(onClick = { if (!isLoggingOut) { isLoggingOut = true; onLogout(); activeDialog = null } },
                        enabled = !isLoggingOut, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
                    ) { Text(if (isLoggingOut) "Logging out..." else "Logout") }
                },
                dismissButton = { OutlinedButton(onClick = { activeDialog = null }, enabled = !isLoggingOut) { Text("Cancel") } }
            )
        }
        null -> Unit
    }
}

@Composable
private fun MedicalIdRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, null, tint = AccentCyan, modifier = Modifier.size(20.dp))
        Column {
            Text(label, color = TextSecondary, fontSize = 12.sp)
            Text(value, color = TextPrimary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ProfileHero(userName: String, userEmail: String) {
    val shimmerTransition = rememberInfiniteTransition(label = "profileHero")
    val shimmerAlpha by shimmerTransition.animateFloat(0.08f, 0.18f, infiniteRepeatable(tween(1800), RepeatMode.Reverse), label = "a")
    val pulse by shimmerTransition.animateFloat(1f, 1.08f, infiniteRepeatable(tween(1800), RepeatMode.Reverse), label = "b")

    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF16254A)), elevation = CardDefaults.cardElevation(8.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Surface(Modifier.size(76.dp).graphicsLayer { scaleX = pulse; scaleY = pulse }, shape = CircleShape, color = Color(0xFF2BB9FF)) {
                    Surface(Modifier.fillMaxSize().padding(3.dp), shape = CircleShape, color = Color(0xFF0F1830)) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(userName.firstOrNull()?.uppercaseChar()?.toString() ?: "U", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF6FAFF))
                        }
                    }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(userName, style = MaterialTheme.typography.headlineSmall, color = Color(0xFFF4F8FF), fontWeight = FontWeight.ExtraBold)
                    Text(userEmail, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFC9D7F4))
                }
            }
            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
            Text("Your AI-backed health workspace is ready.", style = MaterialTheme.typography.bodyLarge, color = Color(0xFFE1EAFB).copy(alpha = shimmerAlpha), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = Color(0xFFF4F8FF), fontWeight = FontWeight.Bold)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF9BAED7))
    }
}

@Composable
private fun UtilityCard(title: String, subtitle: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f, tween(120), label = "s")
    Card(onClick = onClick, interactionSource = interactionSource, modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale },
        shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF151F3B)), elevation = CardDefaults.cardElevation(8.dp)) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, title, tint = Color(0xFF59E6FF), modifier = Modifier.size(24.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = Color(0xFFF4F8FF), fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFFB5C4E4))
            }
        }
    }
}

@Composable
private fun EmergencyActionCard(title: String, number: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f, tween(120), label = "e")
    Card(onClick = onClick, interactionSource = interactionSource, modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale },
        shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF151F3B)), elevation = CardDefaults.cardElevation(8.dp)) {
        Column(Modifier.fillMaxWidth().padding(vertical = 18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, title, tint = Color(0xFF59E6FF), modifier = Modifier.size(24.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, color = Color(0xFFF4F8FF), fontWeight = FontWeight.SemiBold)
            Text(number, style = MaterialTheme.typography.titleLarge, color = Color(0xFF59E6FF), fontWeight = FontWeight.ExtraBold)
        }
    }
}
