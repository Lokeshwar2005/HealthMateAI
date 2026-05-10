package com.example.healthmateai.ui.screens

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.healthmateai.ui.theme.BgDark
import com.example.healthmateai.ui.theme.HealthMateAITheme
import com.example.healthmateai.ui.theme.AccentCyan
import com.example.healthmateai.ui.theme.SurfaceOutline
import java.util.Calendar

enum class QuickAction { DietPlanner, MedicineReminder, Chatbot }

private data class RiskSummary(
    val title: String,
    val value: Float,
    val riskLevel: String?,
    val hasPrediction: Boolean
)

private data class ActionItem(
    val title: String,
    val subtitle: String,
    val cta: String,
    val icon: ImageVector,
    val action: QuickAction
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    userName: String,
    uiState: com.example.healthmateai.ui.viewmodel.PredictionUiState,
    onQuickAction: (QuickAction) -> Unit
) {
    val listState = rememberLazyListState()
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good Morning"
            hour < 18 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    val cards = listOf(
        RiskSummary(
            title = "Diabetes Risk", 
            value = (uiState.diabetesRiskPercent ?: 0) / 100f, 
            riskLevel = uiState.diabetesRiskLevel,
            hasPrediction = uiState.diabetesRiskPercent != null
        ),
        RiskSummary(
            title = "Heart Risk", 
            value = (uiState.heartRiskPercent ?: 0) / 100f, 
            riskLevel = uiState.heartRiskLevel,
            hasPrediction = uiState.heartRiskPercent != null
        )
    )

    val chatbotAction = ActionItem(
        title = "AI Chatbot",
        subtitle = "Talk with HealthMate AI for symptom, diet, and prediction guidance.",
        cta = "Ask AI",
        icon = Icons.Default.SmartToy,
        action = QuickAction.Chatbot
    )
    val plannerActions = listOf(
        ActionItem(
            title = "AI Diet Planner",
            subtitle = "Generate adaptive meal plans from your current risk profile.",
            cta = "Create Plan",
            icon = Icons.Default.AutoGraph,
            action = QuickAction.DietPlanner
        ),
        ActionItem(
            title = "Medicine Reminder",
            subtitle = "Schedule dosage alerts with recurring notification logic.",
            cta = "Add Reminder",
            icon = Icons.Default.Medication,
            action = QuickAction.MedicineReminder
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp)
    ) {
        item {
            HeroHeader(greeting = greeting, userName = userName)
        }
        item {
            SectionHeader(title = "Health Summary", subtitle = "Live risk trends from your latest inputs")
        }
        items(cards) { item ->
            RiskCard(title = item.title, progress = item.value, riskLevel = item.riskLevel, hasPrediction = item.hasPrediction)
        }
        item {
            SectionHeader(title = "Quick Actions", subtitle = "Shortcuts to your AI health workspace")
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PrimaryQuickActionCard(
                    item = chatbotAction,
                    onTap = { onQuickAction(chatbotAction.action) }
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 2,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    plannerActions.forEach { action ->
                        SecondaryQuickActionCard(
                            item = action,
                            modifier = Modifier.weight(1f),
                            onTap = { onQuickAction(action.action) }
                        )
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(10.dp)) }
    }
}

@Composable
private fun HeroHeader(greeting: String, userName: String) {
    val floatTransition = rememberInfiniteTransition(label = "heroFloat")
    val floatingOffset by floatTransition.animateFloat(
        initialValue = -3f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(animation = tween(2200), repeatMode = RepeatMode.Reverse),
        label = "floatingOffset"
    )
    val shimmerAlpha by floatTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(animation = tween(1800), repeatMode = RepeatMode.Reverse),
        label = "heroShimmer"
    )

    val cardShape = RoundedCornerShape(20.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16254A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    greeting,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFB7C5EA),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFFF4F8FF),
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Your AI health workspace is ready",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFD0DCF6).copy(alpha = shimmerAlpha)
                )
            }
            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .graphicsLayer { translationY = floatingOffset },
                shape = RoundedCornerShape(18.dp),
                color = Color(0x2A45E2FF),
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, Color(0x664CE2FF))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Health pulse",
                        tint = Color(0xFF59F0FF),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RiskCard(title: String, progress: Float, riskLevel: String?, hasPrediction: Boolean) {
    val targetValue = remember { mutableStateOf(0f) }
    LaunchedEffect(progress) { targetValue.value = progress.coerceIn(0f, 1f) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (hasPrediction) targetValue.value else 0f,
        animationSpec = tween(durationMillis = 1200),
        label = "riskRing"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = tween(120),
        label = "riskCardScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0x6645E2FF), Color(0x1A45E2FF), Color(0x6627C4BF))
                )
            )
            .padding(1.dp)
    ) {
        Card(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            interactionSource = interactionSource,
            shape = RoundedCornerShape(19.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161F3C)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (hasPrediction) {
                    val displayRiskLabel = riskLevel ?: when {
                        animatedProgress < 0.35f -> "Low"
                        animatedProgress < 0.65f -> "Medium"
                        else -> "High"
                    }
                    
                    val riskColor = when (displayRiskLabel.lowercase()) {
                        "high" -> Color(0xFFFF6B6B)
                        "medium" -> Color(0xFFF2B84B)
                        "low" -> Color(0xFF2BD4A4)
                        else -> Color(0xFF58E5FF)
                    }

                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(70.dp)) {
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.size(66.dp),
                            strokeWidth = 7.dp,
                            trackColor = Color(0xFF202B50),
                            color = riskColor,
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFFF2F6FF),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF1F6FF)
                        )
                        Text(
                            text = "Current Risk: $displayRiskLabel",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFFC5D1EC)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x1A52E6FF),
                        border = BorderStroke(1.dp, Color(0x5548DFFF))
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = title,
                            tint = Color(0xFF58E5FF),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                } else {
                    // No prediction data state
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1A2540),
                        modifier = Modifier.size(70.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.QueryStats,
                                contentDescription = null,
                                tint = Color(0xFF4A5E80),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF1F6FF)
                        )
                        Text(
                            text = "No prediction data available yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF7B8DAF)
                        )
                        Text(
                            text = "Run a prediction to see your risk",
                            style = MaterialTheme.typography.bodySmall,
                            color = AccentCyan.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrimaryQuickActionCard(
    item: ActionItem,
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(100),
        label = "quickActionScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        interactionSource = interactionSource,
        onClick = onTap
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF123056), Color(0xFF172549), Color(0xFF123055))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0x1F42E2FF),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0x8049DFFF))
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = Color(0xFF5CF1FF),
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFF4F9FF),
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFD0E5FF)
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0x214FE8FF),
                border = BorderStroke(1.dp, Color(0x8852E8FF))
            ) {
                Text(
                    text = item.cta,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF78F0FF),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SecondaryQuickActionCard(
    item: ActionItem,
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = tween(110),
        label = "quickSecondaryScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        interactionSource = interactionSource,
        onClick = onTap
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF132645), Color(0xFF192A50), Color(0xFF10203A))
                    )
                )
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = Color(0x1946E5FF),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0x6645DAFF))
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = Color(0xFF5EEBFF),
                    modifier = Modifier.padding(9.dp)
                )
            }
            Text(item.title, color = Color(0xFFF0F7FF), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(item.subtitle, color = Color(0xFF9AB5DE), style = MaterialTheme.typography.bodyMedium)
            Text(item.cta, color = Color(0xFF6CEEFF), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFFF2F6FF),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF9BAED7)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardPreview() {
    HealthMateAITheme {
        DashboardScreen(
            userName = "Dhanush",
            uiState = com.example.healthmateai.ui.viewmodel.PredictionUiState(),
            onQuickAction = {}
        )
    }
}
