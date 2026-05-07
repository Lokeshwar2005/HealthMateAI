package com.example.healthmateai.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.healthmateai.ui.theme.BgDark
import com.example.healthmateai.ui.theme.HealthMateAITheme

private enum class ProfileDialog {
    MedicalId,
    Recommendations,
    LogoutConfirm
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    userName: String,
    userEmail: String,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var activeDialog by remember { mutableStateOf<ProfileDialog?>(null) }
    var isLoggingOut by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFFF4F8FF),
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Your personal health workspace",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF9BAED7)
                )
            }

            IconButton(onClick = {
                isLoggingOut = false
                activeDialog = ProfileDialog.LogoutConfirm
            }) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout",
                    tint = Color(0xFFFF6B6B)
                )
            }
        }

        ProfileHero(userName = userName, userEmail = userEmail)

        SectionTitle(
            title = "Health Utilities",
            subtitle = "Fast access to essential profile tools"
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 2,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UtilityCard(
                title = "Nearby Hospitals",
                subtitle = "Open hospital search in Maps",
                icon = Icons.Default.LocalHospital,
                modifier = Modifier.weight(1f),
                onClick = {
                    val uri = Uri.parse("geo:0,0?q=hospitals near me")
                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (_: ActivityNotFoundException) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                }
            )
            UtilityCard(
                title = "Medical ID",
                subtitle = "Personal health info",
                icon = Icons.Default.MedicalInformation,
                modifier = Modifier.weight(1f),
                onClick = { activeDialog = ProfileDialog.MedicalId }
            )
            UtilityCard(
                title = "AI Recommendations",
                subtitle = "Personalized guidance",
                icon = Icons.Default.Recommend,
                modifier = Modifier.weight(1f),
                onClick = { activeDialog = ProfileDialog.Recommendations }
            )
        }

        SectionTitle(
            title = "Emergency",
            subtitle = "Quick dial actions when you need them"
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            EmergencyActionCard(
                title = "Ambulance",
                number = "108",
                icon = Icons.Default.Favorite,
                modifier = Modifier.weight(1f),
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))
                    context.startActivity(intent)
                }
            )
            EmergencyActionCard(
                title = "Police",
                number = "100",
                icon = Icons.Default.Shield,
                modifier = Modifier.weight(1f),
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:100"))
                    context.startActivity(intent)
                }
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
    }

    when (activeDialog) {
        ProfileDialog.MedicalId -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Medical ID") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Name: $userName")
                        Text("Email: $userEmail")
                        Text("Blood type: Not set")
                        Text("Allergies: Not set")
                        Text("Emergency contact: Add in settings")
                    }
                },
                confirmButton = {
                    Button(onClick = { activeDialog = null }) { Text("Close") }
                }
            )
        }

        ProfileDialog.Recommendations -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("AI Recommendations") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("• Stay hydrated and keep a routine checkup schedule.")
                        Text("• Review prediction trends before making decisions.")
                        Text("• Open the prediction tab for a fresh risk analysis.")
                    }
                },
                confirmButton = {
                    Button(onClick = { activeDialog = null }) { Text("Great") }
                }
            )
        }

        ProfileDialog.LogoutConfirm -> {
            AlertDialog(
                onDismissRequest = {
                    if (!isLoggingOut) {
                        activeDialog = null
                    }
                },
                title = { Text("Sign out of HealthMateAI?") },
                text = { Text("You will need to log in again to access your dashboard.") },
                confirmButton = {
                    Button(
                        onClick = {
                            if (isLoggingOut) return@Button
                            isLoggingOut = true
                            onLogout()
                            activeDialog = null
                        },
                        enabled = !isLoggingOut,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
                    ) {
                        Text(if (isLoggingOut) "Logging out..." else "Logout")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { activeDialog = null },
                        enabled = !isLoggingOut
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        null -> Unit
    }
}

@Composable
private fun ProfileHero(userName: String, userEmail: String) {
    val shimmerTransition = rememberInfiniteTransition(label = "profileHero")
    val shimmerAlpha by shimmerTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(animation = tween(1800), repeatMode = RepeatMode.Reverse),
        label = "profileHeroAlpha"
    )
    val pulse by shimmerTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(animation = tween(1800), repeatMode = RepeatMode.Reverse),
        label = "avatarPulse"
    )

    val cardShape = RoundedCornerShape(18.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16254A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Surface(
                    modifier = Modifier
                        .size(76.dp)
                        .graphicsLayer {
                            scaleX = pulse
                            scaleY = pulse
                        },
                    shape = CircleShape,
                    color = Color(0xFF2BB9FF)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(3.dp),
                        shape = CircleShape,
                        color = Color(0xFF0F1830)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFF6FAFF)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFFF4F8FF),
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFC9D7F4)
                    )
                }
            }

            Divider(color = Color.White.copy(alpha = 0.08f))

            Text(
                text = "Your AI-backed health workspace is ready.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFE1EAFB).copy(alpha = shimmerAlpha),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFFF4F8FF),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF9BAED7)
        )
    }
}

@Composable
private fun UtilityCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(120),
        label = "utilityScale"
    )

    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151F3B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF59E6FF),
                modifier = Modifier.size(24.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFF4F8FF),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFB5C4E4)
                )
            }
        }
    }
}

@Composable
private fun EmergencyActionCard(
    title: String,
    number: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(120),
        label = "emergencyScale"
    )

    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151F3B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF59E6FF),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFFF4F8FF),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = number,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF59E6FF),
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfilePreview() {
    HealthMateAITheme {
        ProfileScreen(
            userName = "Dhanush",
            userEmail = "dhanush@healthmate.ai",
            onLogout = {}
        )
    }
}
