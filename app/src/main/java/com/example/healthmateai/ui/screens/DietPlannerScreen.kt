package com.example.healthmateai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthmateai.ai.HealthPredictionSnapshot
import com.example.healthmateai.ai.diet.DietPlanPayload
import com.example.healthmateai.ai.diet.DietPlannerViewModel
import com.example.healthmateai.ai.diet.DietPlannerViewModelFactory
import com.example.healthmateai.ui.theme.BgDark

@Composable
fun DietPlannerScreen(
    contentPadding: PaddingValues,
    snapshot: HealthPredictionSnapshot,
    viewModel: DietPlannerViewModel = viewModel(factory = DietPlannerViewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsState()

    val payload = remember(snapshot) {
        DietPlanPayload(
            diabetesRisk = snapshot.diabetesRisk,
            heartRisk = snapshot.heartRisk,
            age = snapshot.age,
            bmi = snapshot.bmi,
            weight = snapshot.weight,
            lifestyle = snapshot.lifestyleSummary
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(contentPadding),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF102342), Color(0xFF18325E), Color(0xFF10314D))
                            )
                        )
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color(0xFF65EAFF))
                        Text("AI Diet Planner", style = MaterialTheme.typography.headlineSmall, color = Color(0xFFF2F8FF))
                    }
                    Text(
                        "Dynamic nutrition guidance generated from your latest diabetes and heart predictions.",
                        color = Color(0xFFBCD0F7)
                    )
                    Text(
                        "Diabetes ${toRiskPercent(snapshot.diabetesRisk)}  |  Heart ${toRiskPercent(snapshot.heartRisk)}",
                        color = Color(0xFF75E7FF),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Button(onClick = { viewModel.generatePlan(payload) }) {
                        Text("Generate Personalized Plan")
                    }
                }
            }
        }

        if (uiState.isLoading) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF152849))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(color = Color(0xFF59E9FF))
                        Text("HealthMate AI is crafting your plan...", color = Color(0xFFD1E4FF))
                    }
                }
            }
        }

        uiState.error?.let { error ->
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF4A1F2A))) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(14.dp),
                        color = Color(0xFFFFC7CF)
                    )
                }
            }
        }

        uiState.dietPlan?.let { plan ->
            item {
                DietSection(title = "Summary", entries = listOf(plan.summary))
            }
            item {
                DietSection(title = "Meal Suggestions", entries = plan.mealSuggestions)
            }
            item {
                DietSection(title = "Hydration Tips", entries = plan.hydrationTips)
            }
            item {
                DietSection(title = "Foods to Avoid", entries = plan.foodsToAvoid)
            }
            item {
                DietSection(title = "Nutrition Habits", entries = plan.healthyHabits)
            }
            item {
                DietSection(title = "Calorie Aware Guidance", entries = plan.calorieAwareAdvice)
            }
        }
    }
}

@Composable
private fun DietSection(title: String, entries: List<String>) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15203D))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFF2F7FF),
                fontWeight = FontWeight.SemiBold
            )
            if (entries.isEmpty()) {
                Text("No items generated for this section.", color = Color(0xFF8DA5D1))
            } else {
                entries.forEach { entry ->
                    Text("• $entry", color = Color(0xFFCAE0FF), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

private fun toRiskPercent(value: Float): String = "${(value * 100).toInt()}%"
