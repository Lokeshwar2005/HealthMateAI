package com.example.healthmateai.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthmateai.ui.theme.BgDark
import com.example.healthmateai.ui.theme.GradientEnd
import com.example.healthmateai.ui.theme.GradientStart
import com.example.healthmateai.ui.theme.HealthMateAITheme
import com.example.healthmateai.ui.viewmodel.PredictionViewModel
import com.example.healthmateai.ui.viewmodel.PredictionViewModelFactory
import kotlin.math.roundToInt

data class WizardField(
    val key: String,
    val label: String,
    val min: Float,
    val max: Float,
    val isToggle: Boolean = false,
    val step: Float = 1f
)

@Composable
fun PredictionScreen(
    contentPadding: PaddingValues,
    onPrediction: (disease: String, probability: Float) -> Unit,
    viewModel: PredictionViewModel = viewModel(factory = PredictionViewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsState()
    var disease by remember { mutableStateOf("diabetes") }

    val diabetesFields = remember {
        listOf(
            WizardField("Glucose", "Glucose", 70f, 250f),
            WizardField("BMI", "BMI", 15f, 50f, step = 0.5f),
            WizardField("Age", "Age", 18f, 90f),
            WizardField("Insulin", "Insulin", 0f, 300f),
            WizardField("BloodPressure", "Blood Pressure", 50f, 140f),
            WizardField("DiabetesPedigreeFunction", "Diabetes Pedigree", 0.05f, 2.50f, step = 0.01f),
        )
    }

    val heartFields = remember {
        listOf(
            WizardField("age", "Age", 40f, 95f),
            WizardField("ejection_fraction", "Ejection Fraction", 14f, 80f),
            WizardField("serum_creatinine", "Serum Creatinine", 0.5f, 9.4f, step = 0.1f),
            WizardField("creatinine_phosphokinase", "CPK", 23f, 7861f),
            WizardField("serum_sodium", "Serum Sodium", 113f, 148f),
            WizardField("anaemia", "Anaemia", 0f, 1f, isToggle = true),
            WizardField("diabetes", "Diabetes", 0f, 1f, isToggle = true),
            WizardField("high_blood_pressure", "High Blood Pressure", 0f, 1f, isToggle = true),
            WizardField("smoking", "Smoking", 0f, 1f, isToggle = true),
            WizardField("platelets", "Platelets", 25000f, 850000f)
        )
    }

    val activeFields = if (disease == "diabetes") diabetesFields else heartFields
    var currentStep by remember(disease) { mutableIntStateOf(0) }
    val values = remember(disease) {
        mutableStateMapOf<String, Float>().apply {
            activeFields.forEach { field ->
                put(field.key, (field.min + field.max) / 2f)
            }
        }
    }

    val probability = uiState.result?.probability?.toFloat() ?: 0f
    val animatedProbability by animateFloatAsState(
        targetValue = probability,
        animationSpec = tween(durationMillis = 700),
        label = "probability"
    )

    LaunchedEffect(uiState.result?.probability) {
        uiState.result?.let { result ->
            onPrediction(disease, result.probability.toFloat())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 20.dp)
            .padding(contentPadding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        PredictionHeroBar()

        DiseaseSegmentedControl(
            selectedDisease = disease,
            onDiseaseSelected = { disease = it }
        )

        val wizardProgress = (currentStep + 1f) / activeFields.size.toFloat()
        val animatedWizardProgress by animateFloatAsState(
            targetValue = wizardProgress.coerceIn(0f, 1f),
            animationSpec = tween(280),
            label = "wizardProgress"
        )
        StepProgressBar(progress = animatedWizardProgress)

        Text(
            text = "Step ${currentStep + 1} of ${activeFields.size}",
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFFB1BFDF)
        )

        AnimatedContent(
            targetState = activeFields[currentStep],
            transitionSpec = {
                (slideInHorizontally { it / 3 } + fadeIn()) togetherWith
                    (slideOutHorizontally { -it / 4 } + fadeOut())
            },
            label = "wizardStep"
        ) { field ->
            WizardFieldCard(
                field = field,
                value = values[field.key] ?: field.min,
                onValueChange = { values[field.key] = it }
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF172444),
            shadowElevation = 8.dp,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { if (currentStep > 0) currentStep -= 1 },
                    enabled = currentStep > 0,
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder,
                    contentPadding = PaddingValues(vertical = 14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Back",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                val primaryInteractionSource = remember { MutableInteractionSource() }
                val primaryPressed by primaryInteractionSource.collectIsPressedAsState()
                val primaryScale by animateFloatAsState(
                    targetValue = if (primaryPressed) 0.98f else 1f,
                    animationSpec = tween(durationMillis = 120),
                    label = "nextButtonScale"
                )

                Button(
                    onClick = {
                        if (currentStep < activeFields.lastIndex) {
                            currentStep += 1
                        } else {
                            val payload = values.mapValues { (_, value) ->
                                if (value == 0f || value == 1f) value.toInt() else String.format("%.2f", value).toDouble()
                            }
                            viewModel.predict(disease, payload)
                        }
                    },
                    interactionSource = primaryInteractionSource,
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp,
                        focusedElevation = 6.dp,
                        hoveredElevation = 6.dp,
                        disabledElevation = 0.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF031C25),
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                    modifier = Modifier
                        .weight(1.35f)
                        .height(52.dp)
                        .graphicsLayer {
                            scaleX = primaryScale
                            scaleY = primaryScale
                        }
                        .background(
                            brush = Brush.horizontalGradient(listOf(Color(0xFF24C4BF), Color(0xFF58E5FF))),
                            shape = RoundedCornerShape(14.dp)
                        )
                ) {
                    Text(
                        text = if (currentStep < activeFields.lastIndex) "Next" else "Run Prediction",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        PredictionGauge(probability = animatedProbability, risk = uiState.result?.risk ?: "-", loading = uiState.isLoading)

        uiState.result?.let { result ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Key Factors", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(result.factors.joinToString(separator = "\n• ", prefix = "• "))
                    Text("Advice", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(result.advice)
                }
            }
        }

        if (uiState.errorMessage != null) {
            Text(uiState.errorMessage ?: "Unknown error", color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun WizardFieldCard(
    field: WizardField,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    val animatedValue by animateFloatAsState(targetValue = value, animationSpec = tween(180), label = "fieldValue")
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(listOf(Color(0xFF162447), Color(0xFF1B2C55)))
                )
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(field.label, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFFEFF5FF))
            if (field.isToggle) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (value >= 0.5f) "Enabled" else "Disabled")
                    Switch(checked = value >= 0.5f, onCheckedChange = { onValueChange(if (it) 1f else 0f) })
                }
            } else {
                Text(
                    text = if (field.step < 1f) String.format("%.2f", animatedValue) else animatedValue.roundToInt().toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF4EE6E1),
                    fontWeight = FontWeight.ExtraBold
                )
                Slider(
                    value = animatedValue,
                    onValueChange = { onValueChange(it.coerceIn(field.min, field.max)) },
                    valueRange = field.min..field.max,
                )
            }
        }
    }
}

@Composable
private fun PredictionGauge(probability: Float, risk: String, loading: Boolean) {
    val ringProgress by animateFloatAsState(
        targetValue = probability.coerceIn(0f, 1f),
        animationSpec = tween(700),
        label = "ringProgress"
    )
    val infinite = rememberInfiniteTransition(label = "ringPulse")
    val pulseAlpha by infinite.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(animation = tween(1200), repeatMode = RepeatMode.Reverse),
        label = "ringGlow"
    )

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18274A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Live Probability", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFEFF5FF))

            if (loading) {
                CircularProgressIndicator(color = Color(0xFF4EE6E1), modifier = Modifier.size(44.dp))
            } else {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(152.dp)) {
                        drawCircle(
                            color = Color(0xFF3FCFE0).copy(alpha = pulseAlpha),
                            radius = size.minDimension / 2f,
                            style = Stroke(width = 10.dp.toPx())
                        )
                        drawArc(
                            color = Color(0xFF263562),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            brush = Brush.sweepGradient(listOf(Color(0xFF25C2BE), Color(0xFF58E5FF), Color(0xFF25C2BE))),
                            startAngle = -90f,
                            sweepAngle = 360f * ringProgress,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        "${(ringProgress * 100).roundToInt()}%",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFF5FAFF)
                    )
                }
            }

            Text("Risk: $risk", color = Color(0xFFB7C5E7), style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun PredictionHeroBar() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(listOf(Color(0xFF132A4F), Color(0xFF1A4360)))
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Disease Prediction",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFFF4F8FF),
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "AI-guided assessment with step-by-step inputs",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFABC0E8)
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun DiseaseSegmentedControl(
    selectedDisease: String,
    onDiseaseSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF152445),
        shadowElevation = 4.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            val indicatorWidth = (maxWidth - 4.dp) / 2f
            val offsetX by animateDpAsState(
                targetValue = if (selectedDisease == "diabetes") 0.dp else indicatorWidth,
                animationSpec = tween(240),
                label = "segmentOffset"
            )

            Box(
                modifier = Modifier
                    .offset(x = offsetX)
                    .size(width = indicatorWidth, height = 44.dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(Color(0xFF27C5C0), Color(0xFF54E4FF))),
                        shape = RoundedCornerShape(12.dp)
                    )
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                SegmentButton(
                    title = "Diabetes",
                    selected = selectedDisease == "diabetes",
                    onClick = { onDiseaseSelected("diabetes") },
                    modifier = Modifier.weight(1f)
                )
                SegmentButton(
                    title = "Heart",
                    selected = selectedDisease == "heart",
                    onClick = { onDiseaseSelected("heart") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SegmentButton(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(110),
        label = "segmentScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        color = Color.Transparent,
        modifier = modifier
            .height(44.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (selected) Color(0xFF042532) else Color(0xFFB7C5E6),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StepProgressBar(progress: Float) {
    val progressValue = progress.coerceIn(0f, 1f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF24345C)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        LinearProgressIndicator(
            progress = { progressValue },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Color(0xFF4EE6E1),
            trackColor = Color(0xFF24345C)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PredictionPreview() {
    HealthMateAITheme {
        PredictionScreen(contentPadding = PaddingValues(0.dp), onPrediction = { _, _ -> })
    }
}
