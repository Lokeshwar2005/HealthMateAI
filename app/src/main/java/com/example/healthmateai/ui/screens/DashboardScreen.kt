package com.example.healthmateai.ui.screens

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
import androidx.compose.material.icons.filled.QueryStats
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.healthmateai.ui.theme.BgDark
import com.example.healthmateai.ui.theme.HealthMateAITheme
import java.util.Calendar

enum class QuickAction { Insights, Trends, Recommendations }

private data class RiskSummary(
    val title: String,
    val value: Float
)

private data class ActionItem(
    val title: String,
    val icon: ImageVector,
    val action: QuickAction
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    userName: String,
    diabetesRisk: Float,
    heartRisk: Float,
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
        RiskSummary(title = "Diabetes Risk", value = diabetesRisk),
        RiskSummary(title = "Heart Risk", value = heartRisk)
    )

    val quickActions = listOf(
        ActionItem("View Insights", Icons.Default.QueryStats, QuickAction.Insights),
        ActionItem("Track Health Trends", Icons.Default.Timeline, QuickAction.Trends),
        ActionItem("Get Recommendations", Icons.Default.AutoGraph, QuickAction.Recommendations)
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
            RiskCard(title = item.title, progress = item.value)
        }
        item {
            SectionHeader(title = "Quick Actions", subtitle = "Shortcuts to your AI health workspace")
        }
        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                quickActions.forEach { action ->
                    QuickActionChip(
                        title = action.title,
                        icon = action.icon,
                        modifier = Modifier.weight(1f),
                        onTap = { onQuickAction(action.action) }
                    )
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
                    text = "Your health score improved today",
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
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x664CE2FF))
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
private fun RiskCard(title: String, progress: Float) {
    val targetValue = remember { mutableStateOf(0f) }
    LaunchedEffect(progress) { targetValue.value = progress.coerceIn(0f, 1f) }
    val animatedProgress by animateFloatAsState(
        targetValue = targetValue.value,
        animationSpec = tween(durationMillis = 1200),
        label = "riskRing"
    )

    val riskColor = when {
        animatedProgress < 0.35f -> Color(0xFF36E49F)
        animatedProgress < 0.65f -> Color(0xFFF8D34B)
        else -> Color(0xFFFF6B6B)
    }
    val riskLabel = when {
        animatedProgress < 0.35f -> "Low"
        animatedProgress < 0.65f -> "Medium"
        else -> "High"
    }

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
                        text = "Current Risk: $riskLabel",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFC5D1EC)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x1A52E6FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x5548DFFF))
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoGraph,
                        contentDescription = title,
                        tint = Color(0xFF58E5FF),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionChip(
    title: String,
    icon: ImageVector,
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141D39)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        interactionSource = interactionSource,
        onClick = onTap
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                color = Color(0x1F42E2FF),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x6649DFFF))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF59E6FF),
                    modifier = Modifier.padding(8.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFEAF2FF),
                    fontWeight = FontWeight.SemiBold
                )
            }
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
            diabetesRisk = 0.36f,
            heartRisk = 0.42f,
            onQuickAction = {}
        )
    }
}
